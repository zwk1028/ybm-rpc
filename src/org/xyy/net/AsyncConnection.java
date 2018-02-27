package org.xyy.net;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 异步连接抽象类
 * （1）实现了AsynchronousByteChannel接口，提供异步字节读写能力；
 * （2）实现了可自动关闭接口，用于释放连接资源
 * @author evanliux
 *
 */
public abstract class AsyncConnection implements AsynchronousByteChannel, AutoCloseable {

	//用于存储绑定在Connection上的对象集合
	protected Map<String, Object> attributes; 

	//用于存储绑定在Connection上的对象， 同attributes， 只绑定单个对象时尽量使用subobject而非attributes
    protected Object subObject; 

    //关闭数
    AtomicLong closedCounter = new AtomicLong();

    //在线数
    AtomicLong livingCounter = new AtomicLong();

    //是否为TCP连接
    public abstract boolean isTCP();

    //远端socket地址
    public abstract SocketAddress getRemoteAddress();

    //本地socket地址
    public abstract SocketAddress getLocalAddress();

    //读超时时间
    public abstract int getReadTimeoutSecond();
    public abstract void setReadTimeoutSecond(int readTimeoutSecond);
    
    //写超时时间
    public abstract int getWriteTimeoutSecond();
    public abstract void setWriteTimeoutSecond(int writeTimeoutSecond);

    //异步写数据
    public final <A> void write(ByteBuffer[] srcs, A attachment, CompletionHandler<Integer, ? super A> handler) {
        write(srcs, 0, srcs.length, attachment, handler);
    }

    public abstract <A> void write(ByteBuffer[] srcs, int offset, int length, A attachment, CompletionHandler<Integer, ? super A> handler);

    /**
     * 释放资源，它调用close关闭实际的资源
     */
    public void dispose() {
        try {
            this.close();
        } catch (IOException io) {
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void close() throws IOException {
        if (closedCounter != null) {
            closedCounter.incrementAndGet();
            closedCounter = null;
        }
        if (livingCounter != null) {
            livingCounter.decrementAndGet();
            livingCounter = null;
        }
        if (attributes == null) 
        	return;
        //尝试关闭绑定在attributes上的可关闭对象
        try {
            for (Object obj : attributes.values()) {
                if (obj instanceof AutoCloseable) ((AutoCloseable) obj).close();
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public final <T> T getSubObject() {
        return (T) this.subObject;
    }

    public void setSubObject(Object value) {
        this.subObject = value;
    }

    public void setAttribute(String name, Object value) {
        if (this.attributes == null) this.attributes = new HashMap<>();
        this.attributes.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public final <T> T getAttribute(String name) {
        return (T) (this.attributes == null ? null : this.attributes.get(name));
    }

    public final void removeAttribute(String name) {
        if (this.attributes != null) this.attributes.remove(name);
    }

    public final Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public final void clearAttribute() {
        if (this.attributes != null) this.attributes.clear();
    }

   
    /**
     * 静态工厂函数，用于创建异步连接对象
     * @param protocol
     * 		协议：TCP or UDP
     * @param group
     * 		异步通道组
     * @param address
     * 		连接地址
     * @return
     * @throws IOException
     */
    public static AsyncConnection create(final String protocol, final AsynchronousChannelGroup group, final SocketAddress address) throws IOException {
        return create(protocol, group, address, 0, 0);//创建客户端的连接
    }

    /**
     * 创建客户端连接
     *
     * @param protocol            连接类型 只能是TCP或UDP
     * @param address             连接点子
     * @param group               连接AsynchronousChannelGroup
     * @param readTimeoutSecond0  读取超时秒数
     * @param writeTimeoutSecond0 写入超时秒数
     *
     * @return 连接
     * @throws java.io.IOException 异常
     */
    public static AsyncConnection create(final String protocol, final AsynchronousChannelGroup group, final SocketAddress address,
        final int readTimeoutSecond0, final int writeTimeoutSecond0) throws IOException {
        if ("TCP".equalsIgnoreCase(protocol)) {
        	//开启一个异步socket通道
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open(group);
            try {
            	//连接到通道，如果3秒内没有连接成功，则连接失败
                channel.connect(address).get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new IOException("AsyncConnection connect " + address, e);
            }
            //实际创建TCP异步连接通道对象：AIOTCPAsyncConnection
            return create(channel, address, readTimeoutSecond0, writeTimeoutSecond0);
        } else if ("UDP".equalsIgnoreCase(protocol)) {
        	//打开UDP通道
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(true);//配置为阻塞模式
            channel.connect(address);
            return create(channel, address, true, readTimeoutSecond0, writeTimeoutSecond0);
        } else {
            throw new RuntimeException("AsyncConnection not support protocol " + protocol);
        }
    }

    /**
     * 基于阻塞bio模式的BIOUDPAsyncConnection连接对象，用于UDP连接
     * @author evanliux
     *
     */
    private static class BIOUDPAsyncConnection extends AsyncConnection {

        private int readTimeoutSecond;

        private int writeTimeoutSecond;

        private final DatagramChannel channel;

        private final SocketAddress remoteAddress;

        private final boolean client;

        public BIOUDPAsyncConnection(final DatagramChannel ch, SocketAddress addr,
            final boolean client0, final int readTimeoutSecond0, final int writeTimeoutSecond0) {
            this.channel = ch;
            this.client = client0;
            this.readTimeoutSecond = readTimeoutSecond0;
            this.writeTimeoutSecond = writeTimeoutSecond0;
            this.remoteAddress = addr;
        }

        @Override
        public void setReadTimeoutSecond(int readTimeoutSecond) {
            this.readTimeoutSecond = readTimeoutSecond;
        }

        @Override
        public void setWriteTimeoutSecond(int writeTimeoutSecond) {
            this.writeTimeoutSecond = writeTimeoutSecond;
        }

        @Override
        public int getReadTimeoutSecond() {
            return this.readTimeoutSecond;
        }

        @Override
        public int getWriteTimeoutSecond() {
            return this.writeTimeoutSecond;
        }

        @Override
        public final SocketAddress getRemoteAddress() {
            return remoteAddress;
        }

        @Override
        public SocketAddress getLocalAddress() {
            try {
                return channel.getLocalAddress();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public <A> void write(ByteBuffer[] srcs, int offset, int length, A attachment, CompletionHandler<Integer, ? super A> handler) {
            try {
                int rs = 0;
                for (int i = offset; i < offset + length; i++) {
                    rs += channel.send(srcs[i], remoteAddress);
                    if (i != offset) Thread.sleep(10);
                }
                if (handler != null) handler.completed(rs, attachment);
            } catch (Exception e) {
                if (handler != null) handler.failed(e, attachment);
            }
        }

        @Override
        public <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler) {
            try {
                int rs = channel.read(dst);
                if (handler != null) handler.completed(rs, attachment);
            } catch (IOException e) {
                if (handler != null) handler.failed(e, attachment);
            }
        }

        @Override
        public Future<Integer> read(ByteBuffer dst) {
            try {
                int rs = channel.read(dst);
                return CompletableFuture.completedFuture(rs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <A> void write(ByteBuffer src, A attachment, CompletionHandler<Integer, ? super A> handler) {
            try {
                int rs = channel.send(src, remoteAddress);
                if (handler != null) handler.completed(rs, attachment);
            } catch (IOException e) {
                if (handler != null) handler.failed(e, attachment);
            }
        }

        @Override
        public Future<Integer> write(ByteBuffer src) {
            try {
                int rs = channel.send(src, remoteAddress);
                return CompletableFuture.completedFuture(rs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public final void close() throws IOException {
            super.close();
            if (client) channel.close();
        }

        @Override
        public final boolean isOpen() {
            return channel.isOpen();
        }

        @Override
        public final boolean isTCP() {
            return false;
        }
    }

    public static AsyncConnection create(final DatagramChannel ch, SocketAddress addr,
        final boolean client0, final int readTimeoutSecond0, final int writeTimeoutSecond0) {
    	//BIOUDPAsyncConnection
        return new BIOUDPAsyncConnection(ch, addr, client0, readTimeoutSecond0, writeTimeoutSecond0);
    }

    /**
     * 基于同步BIOTCPAsyncConnection连接对象，用于ssl连接
     * @author evanliux
     *
     */
    private static class BIOTCPAsyncConnection extends AsyncConnection {

        private int readTimeoutSecond;

        private int writeTimeoutSecond;

        private final Socket socket;

        private final ReadableByteChannel readChannel;

        private final WritableByteChannel writeChannel;

        private final SocketAddress remoteAddress;

        public BIOTCPAsyncConnection(final Socket socket, final SocketAddress addr0, final int readTimeoutSecond0, final int writeTimeoutSecond0) {
            this.socket = socket;
            ReadableByteChannel rc = null;
            WritableByteChannel wc = null;
            try {
                socket.setSoTimeout(Math.max(readTimeoutSecond0, writeTimeoutSecond0));
                rc = Channels.newChannel(socket.getInputStream());//获取socket绑定的读通道
                wc = Channels.newChannel(socket.getOutputStream());//获取socket绑定的写通道
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.readChannel = rc;
            this.writeChannel = wc;
            this.readTimeoutSecond = readTimeoutSecond0;
            this.writeTimeoutSecond = writeTimeoutSecond0;
            SocketAddress addr = addr0;
            if (addr == null) {
                try {
                    addr = socket.getRemoteSocketAddress();
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
            this.remoteAddress = addr;
        }

        @Override
        public boolean isTCP() {
            return true;
        }

        @Override
        public SocketAddress getRemoteAddress() {
            return remoteAddress;
        }

        @Override
        public SocketAddress getLocalAddress() {
            return socket.getLocalSocketAddress();
        }

        @Override
        public int getReadTimeoutSecond() {
            return readTimeoutSecond;
        }

        @Override
        public int getWriteTimeoutSecond() {
            return writeTimeoutSecond;
        }

        @Override
        public void setReadTimeoutSecond(int readTimeoutSecond) {
            this.readTimeoutSecond = readTimeoutSecond;
        }

        @Override
        public void setWriteTimeoutSecond(int writeTimeoutSecond) {
            this.writeTimeoutSecond = writeTimeoutSecond;
        }

        @Override
        public <A> void write(ByteBuffer[] srcs, int offset, int length, A attachment, CompletionHandler<Integer, ? super A> handler) {
            try {
                int rs = 0;
                for (int i = offset; i < offset + length; i++) {
                    rs += writeChannel.write(srcs[i]);
                }
                if (handler != null) handler.completed(rs, attachment);
            } catch (IOException e) {
                if (handler != null) handler.failed(e, attachment);
            }
        }

        @Override
        public <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler) {
            try {
                int rs = readChannel.read(dst);
                if (handler != null) handler.completed(rs, attachment);
            } catch (IOException e) {
                if (handler != null) handler.failed(e, attachment);
            }
        }

        @Override
        public Future<Integer> read(ByteBuffer dst) {
            try {
                int rs = readChannel.read(dst);
                return CompletableFuture.completedFuture(rs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <A> void write(ByteBuffer src, A attachment, CompletionHandler<Integer, ? super A> handler) {
            try {
                int rs = writeChannel.write(src);
                if (handler != null) handler.completed(rs, attachment);
            } catch (IOException e) {
                if (handler != null) handler.failed(e, attachment);
            }
        }

        @Override
        public Future<Integer> write(ByteBuffer src) {
            try {
                int rs = writeChannel.write(src);
                return CompletableFuture.completedFuture(rs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.socket.close();
        }

        @Override
        public boolean isOpen() {
            return !socket.isClosed();
        }
    }

    /**
     * 通常用于 ssl socket,基于同步方式
     * 
     * @param socket Socket对象
     * @return 连接对象
     */
    public static AsyncConnection create(final Socket socket) {
        //BIOTCPAsyncConnection
    	return create(socket, null, 0, 0);
    }

    public static AsyncConnection create(final Socket socket, final SocketAddress addr0, final int readTimeoutSecond0, final int writeTimeoutSecond0) {
        return new BIOTCPAsyncConnection(socket, addr0, readTimeoutSecond0, writeTimeoutSecond0);
    }

    /**
     * 基于AIOTCP的连接异步连接对象
     * @author evanliux
     *
     */
    private static class AIOTCPAsyncConnection extends AsyncConnection {

        private int readTimeoutSecond;

        private int writeTimeoutSecond;

        private final AsynchronousSocketChannel channel;

        private final SocketAddress remoteAddress;

        public AIOTCPAsyncConnection(final AsynchronousSocketChannel ch, final SocketAddress addr0, final int readTimeoutSecond0, final int writeTimeoutSecond0) {
            this.channel = ch;
            this.readTimeoutSecond = readTimeoutSecond0;
            this.writeTimeoutSecond = writeTimeoutSecond0;
            SocketAddress addr = addr0;
            if (addr == null) {
                try {
                    addr = ch.getRemoteAddress();
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
            this.remoteAddress = addr;
        }

        @Override
        public <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler) {
            if (readTimeoutSecond > 0) {
                channel.read(dst, readTimeoutSecond, TimeUnit.SECONDS, attachment, handler);
            } else {
                channel.read(dst, attachment, handler);
            }
        }

        @Override
        public <A> void write(ByteBuffer src, A attachment, CompletionHandler<Integer, ? super A> handler) {
            if (writeTimeoutSecond > 0) {
                channel.write(src, writeTimeoutSecond, TimeUnit.SECONDS, attachment, handler);
            } else {
                channel.write(src, attachment, handler);
            }
        }

        @Override
        public <A> void write(ByteBuffer[] srcs, int offset, int length, A attachment, final CompletionHandler<Integer, ? super A> handler) {
            channel.write(srcs, offset, length, writeTimeoutSecond > 0 ? writeTimeoutSecond : 60, TimeUnit.SECONDS,
                attachment, new CompletionHandler<Long, A>() {

                @Override
                public void completed(Long result, A attachment) {
                    handler.completed(result.intValue(), attachment);
                }

                @Override
                public void failed(Throwable exc, A attachment) {
                    handler.failed(exc, attachment);
                }

            });
        }

        @Override
        public void setReadTimeoutSecond(int readTimeoutSecond) {
            this.readTimeoutSecond = readTimeoutSecond;
        }

        @Override
        public void setWriteTimeoutSecond(int writeTimeoutSecond) {
            this.writeTimeoutSecond = writeTimeoutSecond;
        }

        @Override
        public int getReadTimeoutSecond() {
            return this.readTimeoutSecond;
        }

        @Override
        public int getWriteTimeoutSecond() {
            return this.writeTimeoutSecond;
        }

        @Override
        public final SocketAddress getRemoteAddress() {
            return remoteAddress;
        }

        @Override
        public SocketAddress getLocalAddress() {
            try {
                return channel.getLocalAddress();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public final Future<Integer> read(ByteBuffer dst) {
            return channel.read(dst);
        }

        @Override
        public final Future<Integer> write(ByteBuffer src) {
            return channel.write(src);
        }

        @Override
        public final void close() throws IOException {
            super.close();
            channel.close();
        }

        @Override
        public final boolean isOpen() {
            return channel.isOpen();
        }

        @Override
        public final boolean isTCP() {
            return true;
        }

    }

    public static AsyncConnection create(final AsynchronousSocketChannel ch) {
        return create(ch, null, 0, 0);
    }

    public static AsyncConnection create(final AsynchronousSocketChannel ch, final SocketAddress addr0, final int readTimeoutSecond0, final int writeTimeoutSecond0) {
        return new AIOTCPAsyncConnection(ch, addr0, readTimeoutSecond0, writeTimeoutSecond0);
    }

}
