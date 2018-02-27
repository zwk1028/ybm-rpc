package org.xyy.net.http;

import static org.xyy.net.http.WebSocket.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javax.annotation.*;
import org.xyy.boot.*;
import org.xyy.service.*;
import org.xyy.source.*;
import org.xyy.util.*;

/**
 *
 *
 */
public abstract class WebSocketNode {

    @Comment("存储当前SNCP节点列表的key")
    public static final String SOURCE_SNCP_NODES_KEY = "xyy_sncpnodes";

    @Comment("存储当前用户数量的key")
    public static final String SOURCE_USER_COUNT_KEY = "xyy_usercount";

    protected final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    protected final boolean finest = logger.isLoggable(Level.FINEST);

    //"SNCP_ADDR" 如果不是分布式(没有SNCP) 值为null
    @Resource(name = Application.RESNAME_SNCP_ADDR)
    protected InetSocketAddress localSncpAddress;  //为SncpServer的服务address

    //如果不是分布式(没有SNCP) 值为null
    @RpcRemote
    protected WebSocketNode remoteNode;

    //存放所有用户分布在节点上的队列信息,Set<InetSocketAddress> 为 sncpnode 的集合， key: groupid
    //集合包含 localSncpAddress
    //如果不是分布式(没有SNCP)，sncpNodeAddresses 将不会被用到
    @Resource(name = "$_nodes")
    protected CacheSource<Serializable, InetSocketAddress> sncpNodeAddresses;

    //当前节点的本地WebSocketEngine
    protected WebSocketEngine localEngine;

    public void init(AnyValue conf) {
    }

    public void destroy(AnyValue conf) {
    }

    public final void postDestroy(AnyValue conf) {
        if (this.localEngine == null) return;
        //关掉所有本地本地WebSocket
        this.localEngine.getLocalWebSockets().forEach(g -> disconnect(g.getUserid()));
        if (sncpNodeAddresses != null && localSncpAddress != null) {
            sncpNodeAddresses.removeSetItem(SOURCE_SNCP_NODES_KEY, localSncpAddress);
        }
    }

    protected abstract CompletableFuture<List<String>> getWebSocketAddresses(@RpcTargetAddress InetSocketAddress targetAddress, Serializable userid);

    protected abstract CompletableFuture<Integer> sendMessage(@RpcTargetAddress InetSocketAddress targetAddress, Object message, boolean last, Serializable userid);

    protected abstract CompletableFuture<Integer> broadcastMessage(@RpcTargetAddress InetSocketAddress targetAddress, Object message, boolean last);

    protected abstract CompletableFuture<Void> connect(Serializable userid, InetSocketAddress addr);

    protected abstract CompletableFuture<Void> disconnect(Serializable userid, InetSocketAddress addr);

    //--------------------------------------------------------------------------------
    final CompletableFuture<Void> connect(final Serializable userid) {
        if (finest) logger.finest(localSncpAddress + " receive websocket connect event (" + userid + " on " + this.localEngine.getEngineid() + ").");
        return connect(userid, localSncpAddress);
    }

    final CompletableFuture<Void> disconnect(final Serializable userid) {
        if (finest) logger.finest(localSncpAddress + " receive websocket disconnect event (" + userid + " on " + this.localEngine.getEngineid() + ").");
        return disconnect(userid, localSncpAddress);
    }

    //--------------------------------------------------------------------------------
    /**
     * 获取目标地址 <br>
     * 该方法仅供内部调用
     *
     * @param targetAddress InetSocketAddress
     * @param userid        Serializable
     *
     * @return 客户端地址列表
     */
    protected CompletableFuture<List<String>> remoteWebSocketAddresses(@RpcTargetAddress InetSocketAddress targetAddress, Serializable userid) {
        if (remoteNode == null) return CompletableFuture.completedFuture(null);
        try {
            return remoteNode.getWebSocketAddresses(targetAddress, userid);
        } catch (Exception e) {
            logger.log(Level.WARNING, "remote " + targetAddress + " websocket getOnlineRemoteAddresses error", e);
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * 获取用户在线的SNCP节点地址列表，不是分布式则返回元素数量为1，且元素值为null的列表<br>
     * InetSocketAddress 为 SNCP节点地址
     *
     * @param userid Serializable
     *
     * @return 地址列表
     */
    public CompletableFuture<Collection<InetSocketAddress>> getRpcNodeAddresses(final Serializable userid) {
        if (this.sncpNodeAddresses != null) return this.sncpNodeAddresses.getCollectionAsync(userid);
        List<InetSocketAddress> rs = new ArrayList<>();
        rs.add(this.localSncpAddress);
        return CompletableFuture.completedFuture(rs);
    }

    /**
     * 获取在线用户的详细连接信息 <br>
     * Map.key 为 SNCP节点地址, 含值为null的key表示没有分布式
     * Map.value 为 用户客户端的IP
     *
     * @param userid Serializable
     *
     * @return 地址集合
     */
    public CompletableFuture<Map<InetSocketAddress, List<String>>> getRpcNodeWebSocketAddresses(final Serializable userid) {
        CompletableFuture<Collection<InetSocketAddress>> sncpFuture = getRpcNodeAddresses(userid);
        return sncpFuture.thenCompose((Collection<InetSocketAddress> addrs) -> {
            if (finest) logger.finest("websocket found userid:" + userid + " on " + addrs);
            if (addrs == null || addrs.isEmpty()) return CompletableFuture.completedFuture(new HashMap<>());
            CompletableFuture<Map<InetSocketAddress, List<String>>> future = null;
            for (final InetSocketAddress nodeAddress : addrs) {
                CompletableFuture<Map<InetSocketAddress, List<String>>> mapFuture = getWebSocketAddresses(nodeAddress, userid)
                    .thenCompose((List<String> list) -> CompletableFuture.completedFuture(Utility.ofMap(nodeAddress, list)));
                future = future == null ? mapFuture : future.thenCombine(mapFuture, (a, b) -> Utility.merge(a, b));
            }
            return future == null ? CompletableFuture.completedFuture(new HashMap<>()) : future;
        });
    }

    /**
     * 判断指定用户是否WebSocket在线
     *
     * @param userid
     *
     * @return boolean
     */
    public CompletableFuture<Boolean> existsWebSocket(final Serializable userid) {
        if (this.localEngine != null && this.sncpNodeAddresses == null) {
            return CompletableFuture.completedFuture(this.localEngine.existsLocalWebSocket(userid));
        }
        return this.sncpNodeAddresses.existsAsync(userid);
    }

    /**
     * 获取在线用户总数
     *
     *
     * @return boolean
     */
    public CompletableFuture<Integer> getUserSize() {
        if (this.localEngine != null && this.sncpNodeAddresses == null) {
            return CompletableFuture.completedFuture(this.localEngine.getLocalUserSize());
        }
        return this.sncpNodeAddresses.getKeySizeAsync().thenCompose(count -> {
            return sncpNodeAddresses.existsAsync(SOURCE_SNCP_NODES_KEY).thenApply(exists -> exists ? (count - 1) : count);
        });
    }

    //--------------------------------------------------------------------------------
    /**
     * 获取本地的WebSocketEngine，没有则返回null
     *
     *
     * @return WebSocketEngine
     */
    public final WebSocketEngine getLocalWebSocketEngine() {
        return this.localEngine;
    }

    /**
     * 向指定用户发送消息，先发送本地连接，再发送远程连接  <br>
     * 如果当前WebSocketNode是远程模式，此方法只发送远程连接
     *
     * @param message 消息内容
     * @param userids Serializable[]
     *
     * @return 为0表示成功， 其他值表示部分发送异常
     */
    public final CompletableFuture<Integer> sendMessage(Object message, final Serializable... userids) {
        return sendMessage(message, true, userids);
    }

    /**
     * 向指定用户发送消息，先发送本地连接，再发送远程连接  <br>
     * 如果当前WebSocketNode是远程模式，此方法只发送远程连接
     *
     * @param message 消息内容
     * @param last    是否最后一条
     * @param userids Serializable[]
     *
     * @return 为0表示成功， 其他值表示部分发送异常
     */
    public final CompletableFuture<Integer> sendMessage(final Object message, final boolean last, final Serializable... userids) {
        if (userids == null || userids.length < 1) return CompletableFuture.completedFuture(RETCODE_GROUP_EMPTY);
        if (this.localEngine != null && this.sncpNodeAddresses == null) { //本地模式且没有分布式
            return this.localEngine.sendMessage(message, last, userids);
        }
        CompletableFuture<Integer> future = null;
        for (Serializable userid : userids) {
            future = future == null ? sendOneMessage(message, last, userid)
                : future.thenCombine(sendOneMessage(message, last, userid), (a, b) -> a | b);
        }
        return future == null ? CompletableFuture.completedFuture(RETCODE_GROUP_EMPTY) : future;
    }

    /**
     * 广播消息， 给所有人发消息
     *
     * @param message 消息内容
     *
     * @return 为0表示成功， 其他值表示部分发送异常
     */
    public final CompletableFuture<Integer> broadcastMessage(final Object message) {
        return broadcastMessage(message, true);
    }

    /**
     * 广播消息， 给所有人发消息
     *
     * @param message 消息内容
     * @param last    是否最后一条
     *
     * @return 为0表示成功， 其他值表示部分发送异常
     */
    public final CompletableFuture<Integer> broadcastMessage(final Object message, final boolean last) {
        if (this.localEngine != null && this.sncpNodeAddresses == null) { //本地模式且没有分布式
            return this.localEngine.broadcastMessage(message, last);
        }
        CompletableFuture<Integer> localFuture = this.localEngine == null ? null : this.localEngine.broadcastMessage(message, last);
        CompletableFuture<Collection<InetSocketAddress>> addrsFuture = sncpNodeAddresses.getCollectionAsync("xyy_sncpnodes");
        CompletableFuture<Integer> remoteFuture = addrsFuture.thenCompose((Collection<InetSocketAddress> addrs) -> {
            if (finest) logger.finest("websocket broadcast message on " + addrs);
            if (addrs == null || addrs.isEmpty()) return CompletableFuture.completedFuture(0);
            CompletableFuture<Integer> future = null;
            for (InetSocketAddress addr : addrs) {
                if (addr == null || addr.equals(localSncpAddress)) continue;
                future = future == null ? remoteNode.broadcastMessage(addr, message, last)
                    : future.thenCombine(remoteNode.broadcastMessage(addr, message, last), (a, b) -> a | b);
            }
            return future == null ? CompletableFuture.completedFuture(0) : future;
        });
        return localFuture == null ? remoteFuture : localFuture.thenCombine(remoteFuture, (a, b) -> a | b);
    }

    private CompletableFuture<Integer> sendOneMessage(final Object message, final boolean last, final Serializable userid) {
        if (finest) logger.finest("websocket want send message {userid:" + userid + ", content:'" + message + "'} from locale node to locale engine");
        CompletableFuture<Integer> localFuture = null;
        if (this.localEngine != null) localFuture = localEngine.sendMessage(message, last, userid);
        if (this.sncpNodeAddresses == null || this.remoteNode == null) {
            if (finest) logger.finest("websocket remote node is null");
            //没有CacheSource就不会有分布式节点
            return localFuture == null ? CompletableFuture.completedFuture(RETCODE_GROUP_EMPTY) : localFuture;
        }
        //远程节点发送消息
        CompletableFuture<Collection<InetSocketAddress>> addrsFuture = sncpNodeAddresses.getCollectionAsync(userid);
        CompletableFuture<Integer> remoteFuture = addrsFuture.thenCompose((Collection<InetSocketAddress> addrs) -> {
            if (finest) logger.finest("websocket found userid:" + userid + " on " + addrs);
            if (addrs == null || addrs.isEmpty()) return CompletableFuture.completedFuture(0);
            CompletableFuture<Integer> future = null;
            for (InetSocketAddress addr : addrs) {
                if (addr == null || addr.equals(localSncpAddress)) continue;
                future = future == null ? remoteNode.sendMessage(addr, message, last, userid)
                    : future.thenCombine(remoteNode.sendMessage(addr, message, last, userid), (a, b) -> a | b);
            }
            return future == null ? CompletableFuture.completedFuture(0) : future;
        });
        return localFuture == null ? remoteFuture : localFuture.thenCombine(remoteFuture, (a, b) -> a | b);
    }
}
