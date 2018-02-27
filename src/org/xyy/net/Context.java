package org.xyy.net;

import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.logging.*;
import org.xyy.convert.bson.*;
import org.xyy.convert.json.*;
import org.xyy.util.*;

/**
 * 服务器上下文对象
 * @author evanliux
 *
 */
public class Context {

    private static final Charset UTF8 = Charset.forName("UTF-8");//默认utf-8编码

    //服务启动时间
    protected final long serverStartTime;

    //Server的线程池
    protected final ExecutorService executor;

    //ByteBuffer的容量，默认8K
    protected final int bufferCapacity;

    //ByteBuffer对象池
    protected final ObjectPool<ByteBuffer> bufferPool;

    //Response对象池
    protected final ObjectPool<Response> responsePool;

    //服务的根Servlet
    protected final PrepareServlet prepare;

    //服务的监听地址
    private final InetSocketAddress address;

    //字符集
    protected final Charset charset;

    //请求内容的大小上限, 默认64K
    protected final int maxbody;

    //IO读取的超时时间
    protected final int readTimeoutSecond;

    //IO写入的超时时间
    protected final int writeTimeoutSecond;

    //日志Logger
    protected final Logger logger;

    //BSON操作工厂
    protected final BsonFactory bsonFactory;

    //JSON操作工厂
    protected final JsonFactory jsonFactory;

    public Context(long serverStartTime, Logger logger, ExecutorService executor, int bufferCapacity, ObjectPool<ByteBuffer> bufferPool, ObjectPool<Response> responsePool,
        final int maxbody, Charset charset, InetSocketAddress address, final PrepareServlet prepare, final int readTimeoutSecond, final int writeTimeoutSecond) {
        this.serverStartTime = serverStartTime;
        this.logger = logger;
        this.executor = executor;
        this.bufferCapacity = bufferCapacity;
        this.bufferPool = bufferPool;
        this.responsePool = responsePool;
        this.maxbody = maxbody;
        this.charset = UTF8.equals(charset) ? null : charset;
        this.address = address;
        this.prepare = prepare;
        this.readTimeoutSecond = readTimeoutSecond;
        this.writeTimeoutSecond = writeTimeoutSecond;
        this.jsonFactory = JsonFactory.root();
        this.bsonFactory = BsonFactory.root();
    }

    public int getMaxbody() {
        return maxbody;
    }

    public InetSocketAddress getServerAddress() {
        return address;
    }

    public long getServerStartTime() {
        return serverStartTime;
    }

    public Charset getCharset() {
        return charset;
    }

    public Future<?> submitAsync(Runnable r) {
        return executor.submit(r);
    }

    public void runAsync(Runnable r) {
        executor.execute(r);
    }

    public int getBufferCapacity() {
        return bufferCapacity;
    }

    public Supplier<ByteBuffer> getBufferSupplier() {
        return bufferPool;
    }

    public ByteBuffer pollBuffer() {
        return bufferPool.get();
    }

    public void offerBuffer(ByteBuffer buffer) {
        bufferPool.offer(buffer);
    }

    public void offerBuffer(ByteBuffer... buffers) {
        if (buffers == null) return;
        for (ByteBuffer buffer : buffers) {
            bufferPool.offer(buffer);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public int getReadTimeoutSecond() {
        return readTimeoutSecond;
    }

    public int getWriteTimeoutSecond() {
        return writeTimeoutSecond;
    }

    public JsonConvert getJsonConvert() {
        return jsonFactory.getConvert();
    }

    public BsonConvert getBsonConvert() {
        return bsonFactory.getConvert();
    }
}
