package org.xyy.net;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 协议底层Server
 *
 */
public abstract class ProtocolServer {

    //创建数
    protected final AtomicLong createCounter = new AtomicLong();

    //关闭数
    protected final AtomicLong closedCounter = new AtomicLong();

    //在线数
    protected final AtomicLong livingCounter = new AtomicLong();

    public abstract void open() throws IOException;

    public abstract void bind(SocketAddress local, int backlog) throws IOException;

    public abstract <T> Set<SocketOption<?>> supportedOptions();

    public abstract <T> void setOption(SocketOption<T> name, T value) throws IOException;

    public abstract void accept();

    public abstract void close() throws IOException;

    public abstract AsynchronousChannelGroup getChannelGroup();

    public long getCreateCount() {
        return createCounter.longValue();
    }

    public long getClosedCount() {
        return closedCounter.longValue();
    }

    public long getLivingCount() {
        return livingCounter.longValue();
    }

    //---------------------------------------------------------------------
    public static ProtocolServer create(String protocol, Context context) {
        if ("TCP".equalsIgnoreCase(protocol)) return new ProtocolTCPServer(context);
        if ("UDP".equalsIgnoreCase(protocol)) return new ProtocolUDPServer(context);
        throw new RuntimeException("ProtocolServer not support protocol " + protocol);
    }

    private static final class ProtocolUDPServer extends ProtocolServer {

        private boolean running;

        private final Context context;

        private DatagramChannel serverChannel;

        public ProtocolUDPServer(Context context) {
            this.context = context;
        }

        @Override
        public void open() throws IOException {
            DatagramChannel ch = DatagramChannel.open();
            ch.configureBlocking(true);
            this.serverChannel = ch;
        }

        @Override
        public void bind(SocketAddress local, int backlog) throws IOException {
            this.serverChannel.bind(local);
        }

        @Override
        public <T> void setOption(SocketOption<T> name, T value) throws IOException {
            this.serverChannel.setOption(name, value);
        }

        @Override
        public <T> Set<SocketOption<?>> supportedOptions() {
            return this.serverChannel.supportedOptions();
        }

        @Override
        public void accept() {
            final DatagramChannel serchannel = this.serverChannel;
            final int readTimeoutSecond = this.context.readTimeoutSecond;
            final int writeTimeoutSecond = this.context.writeTimeoutSecond;
            final CountDownLatch cdl = new CountDownLatch(1);
            this.running = true;
            new Thread() {
                @Override
                public void run() {
                    cdl.countDown();
                    while (running) {
                        final ByteBuffer buffer = context.pollBuffer();
                        try {
                            SocketAddress address = serchannel.receive(buffer);
                            buffer.flip();
                            AsyncConnection conn = AsyncConnection.create(serchannel, address, false, readTimeoutSecond, writeTimeoutSecond);
                            context.runAsync(new PrepareRunner(context, conn, buffer));
                        } catch (Exception e) {
                            context.offerBuffer(buffer);
                        }
                    }
                }
            }.start();
            try {
                cdl.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() throws IOException {
            this.running = false;
            this.serverChannel.close();
        }

        @Override
        public AsynchronousChannelGroup getChannelGroup() {
            return null;
        }

        @Override
        public long getCreateCount() {
            return -1;
        }

        @Override
        public long getClosedCount() {
            return -1;
        }

        @Override
        public long getLivingCount() {
            return -1;
        }
    }

    private static final class ProtocolTCPServer extends ProtocolServer {

        private final Context context;

        private AsynchronousChannelGroup group;

        private AsynchronousServerSocketChannel serverChannel;

        public ProtocolTCPServer(Context context) {
            this.context = context;
        }

        @Override
        public void open() throws IOException {
            group = AsynchronousChannelGroup.withCachedThreadPool(context.executor, 1);
            this.serverChannel = AsynchronousServerSocketChannel.open(group);
        }

        @Override
        public void bind(SocketAddress local, int backlog) throws IOException {
            this.serverChannel.bind(local, backlog);
        }

        @Override
        public <T> void setOption(SocketOption<T> name, T value) throws IOException {
            this.serverChannel.setOption(name, value);
        }

        @Override
        public <T> Set<SocketOption<?>> supportedOptions() {
            return this.serverChannel.supportedOptions();
        }

        @Override
        public void accept() {
            final AsynchronousServerSocketChannel serchannel = this.serverChannel;
            serchannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

                @Override
                public void completed(final AsynchronousSocketChannel channel, Void attachment) {
                    serchannel.accept(null, this);
                    createCounter.incrementAndGet();
                    livingCounter.incrementAndGet();
                    AsyncConnection conn = AsyncConnection.create(channel, null, context.readTimeoutSecond, context.writeTimeoutSecond);
                    conn.livingCounter = livingCounter;
                    conn.closedCounter = closedCounter;
                    context.submitAsync(new PrepareRunner(context, conn, null));
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    serchannel.accept(null, this);
                    //if (exc != null) context.logger.log(Level.FINEST, AsynchronousServerSocketChannel.class.getSimpleName() + " accept erroneous", exc);
                }
            });
        }

        @Override
        public void close() throws IOException {
            this.serverChannel.close();
        }

        @Override
        public AsynchronousChannelGroup getChannelGroup() {
            return this.group;
        }
    }

}
