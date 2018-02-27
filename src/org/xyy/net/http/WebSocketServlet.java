package org.xyy.net.http;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.*;
import javax.annotation.*;
import org.xyy.convert.Convert;
import org.xyy.service.*;
import org.xyy.util.*;

/**
 * <blockquote><pre>
 * 当WebSocketServlet接收一个TCP连接后，进行协议判断，如果成功就会创建一个WebSocket。
 *
 *                                    WebSocketServlet
 *                                            |
 *                                            |
 *                                   WebSocketEngine
 *                                    WebSocketNode
 *                                   /             \
 *                                 /                \
 *                               /                   \
 *                     WebSocket1                 WebSocket2
 *
 * </pre></blockquote>
 *
 *
 */
public abstract class WebSocketServlet extends HttpServlet implements Resourcable {

    @Comment("WebScoket服务器给客户端进行ping操作的间隔时间, 单位: 秒")
    public static final String WEBPARAM__LIVEINTERVAL = "liveinterval";

    @Comment("WebScoket服务器给客户端进行ping操作的默认间隔时间, 单位: 秒")
    public static final int DEFAILT_LIVEINTERVAL = 15;

    protected final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private final MessageDigest digest = getMessageDigest();

    private final BiConsumer<WebSocket, Object> restMessageConsumer = createRestOnMessageConsumer();

    protected Type messageTextType;  //RestWebSocket时会被修改

    protected boolean single = true; //是否单用户单连接

    protected int liveinterval = DEFAILT_LIVEINTERVAL;

    @Resource(name = "jsonconvert")
    protected Convert jsonConvert;

    @Resource(name = "$_textconvert")
    protected Convert textConvert;

    @Resource(name = "$_binaryconvert")
    protected Convert binaryConvert;

    @Resource(name = "$_sendconvert")
    protected Convert sendConvert;

    @Resource(name = "$")
    protected WebSocketNode node;

    protected WebSocketServlet() {
        Type msgtype = String.class;
        try {
            for (Method method : this.getClass().getDeclaredMethods()) {
                if (!method.getName().equals("createWebSocket")) continue;
                if (method.getParameterCount() > 0) continue;
                Type rt = method.getGenericReturnType();
                if (rt instanceof ParameterizedType) {
                    msgtype = ((ParameterizedType) rt).getActualTypeArguments()[1];
                }
                if (msgtype == Object.class) msgtype = String.class;
                break;
            }
        } catch (Exception e) {
            logger.warning(this.getClass().getName() + " not designate text message type on createWebSocket Method");
        }
        this.messageTextType = msgtype;
    }

    @Override
    final void preInit(HttpContext context, AnyValue conf) {
        if (this.textConvert == null) this.textConvert = jsonConvert;
        if (this.binaryConvert == null) this.binaryConvert = jsonConvert;
        if (this.sendConvert == null) this.sendConvert = jsonConvert;
        InetSocketAddress addr = context.getServerAddress();
        if (this.node == null) this.node = createWebSocketNode();
        if (this.node == null) {  //没有部署SNCP，即不是分布式
            this.node = new WebSocketNodeService();
            if (logger.isLoggable(Level.WARNING)) logger.warning("Not found WebSocketNode, create a default value for " + getClass().getName());
        }
        //存在WebSocketServlet，则此WebSocketNode必须是本地模式Service
        this.node.localEngine = new WebSocketEngine("WebSocketEngine-" + addr.getHostString() + ":" + addr.getPort() + "-[" + resourceName() + "]", this.single, context, liveinterval, this.node, this.sendConvert, logger);
        this.node.init(conf);
        this.node.localEngine.init(conf);
    }

    @Override
    final void postDestroy(HttpContext context, AnyValue conf) {
        this.node.postDestroy(conf);
        super.destroy(context, conf);
        this.node.localEngine.destroy(conf);
    }

    @Override
    public String resourceName() {
        return this.getClass().getSimpleName().replace("_Dyn", "").toLowerCase().replaceAll("websocket.*$", "").replaceAll("servlet.*$", "");
    }

    @Override
    public final void execute(final HttpRequest request, final HttpResponse response) throws IOException {
        final boolean debug = logger.isLoggable(Level.FINEST);
        if (!request.isWebSocket()) {
            if (debug) logger.finest("WebSocket connect abort, (Not GET Method) or (Connection != Upgrade) or (Upgrade != websocket). request=" + request);
            response.finish(true);
            return;
        }
        final String key = request.getHeader("Sec-WebSocket-Key");
        if (key == null) {
            if (debug) logger.finest("WebSocket connect abort, Not found Sec-WebSocket-Key header. request=" + request);
            response.finish(true);
            return;
        }
        final WebSocket webSocket = this.createWebSocket();
        webSocket._engine = this.node.localEngine;
        webSocket._messageTextType = this.messageTextType;
        webSocket._textConvert = textConvert;
        webSocket._binaryConvert = binaryConvert;
        webSocket._sendConvert = sendConvert;
        webSocket._remoteAddress = request.getRemoteAddress();
        webSocket._remoteAddr = request.getRemoteAddr();
        initRestWebSocket(webSocket);
        CompletableFuture<String> sessionFuture = webSocket.onOpen(request);
        if (sessionFuture == null) {
            if (debug) logger.finest("WebSocket connect abort, Not found sessionid. request=" + request);
            response.finish(true);
            return;
        }
        sessionFuture.whenComplete((sessionid, ex) -> {
            if (sessionid == null || ex != null) {
                if (debug || ex != null) logger.log(ex == null ? Level.FINEST : Level.FINE, "WebSocket connect abort, Not found sessionid or occur error. request=" + request, ex);
                response.finish(true);
                return;
            }
            webSocket._sessionid = sessionid;
            request.setKeepAlive(true);
            byte[] bytes = (key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes();
            synchronized (digest) {
                bytes = digest.digest(bytes);
            }
            response.setStatus(101);
            response.setHeader("Connection", "Upgrade");
            response.addHeader("Upgrade", "websocket");
            response.addHeader("Sec-WebSocket-Accept", Base64.getEncoder().encodeToString(bytes));
            response.sendBody((ByteBuffer) null, null, new AsyncHandler<Integer, Void>() {

                @Override
                public void completed(Integer result, Void attachment) {
                    HttpContext context = response.getContext();
                    CompletableFuture<Serializable> userFuture = webSocket.createUserid();
                    if (userFuture == null) {
                        if (debug) logger.finest("WebSocket connect abort, Create userid abort. request = " + request);
                        response.finish(true);
                        return;
                    }
                    userFuture.whenComplete((userid, ex2) -> {
                        if (userid == null || ex2 != null) {
                            if (debug || ex2 != null) logger.log(ex2 == null ? Level.FINEST : Level.FINE, "WebSocket connect abort, Create userid abort. request = " + request, ex2);
                            response.finish(true);
                            return;
                        }
                        webSocket._userid = userid;
                        WebSocketServlet.this.node.localEngine.add(webSocket);
                        WebSocketRunner runner = new WebSocketRunner(context, webSocket, restMessageConsumer, response.removeChannel());
                        webSocket._runner = runner;
                        context.runAsync(runner);
                        response.finish(true);
                    });
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    logger.log(Level.FINEST, "WebSocket connect abort, Response send abort. request = " + request, exc);
                    response.finish(true);
                }
            });
        });
    }

    protected abstract <G extends Serializable, T> WebSocket<G, T> createWebSocket();

    protected WebSocketNode createWebSocketNode() {
        return null;
    }

    protected void initRestWebSocket(WebSocket websocket) { //设置WebSocket中的@Resource资源
    }

    protected BiConsumer<WebSocket, Object> createRestOnMessageConsumer() {
        return null;
    }

    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
