package org.xyy.net.sncp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import org.xyy.net.*;
import org.xyy.util.ObjectPool;

/**
 *
 */
public class SncpContext extends Context {

    public SncpContext(long serverStartTime, Logger logger, ExecutorService executor, int bufferCapacity, ObjectPool<ByteBuffer> bufferPool,
        ObjectPool<Response> responsePool, int maxbody, Charset charset, InetSocketAddress address, PrepareServlet prepare,
        int readTimeoutSecond, int writeTimeoutSecond) {
        super(serverStartTime, logger, executor, bufferCapacity, bufferPool, responsePool, maxbody, charset,
            address, prepare, readTimeoutSecond, writeTimeoutSecond);
    }
}
