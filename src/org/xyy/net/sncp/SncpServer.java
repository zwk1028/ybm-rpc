package org.xyy.net.sncp;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.xyy.convert.bson.*;
import org.xyy.net.*;
import org.xyy.service.Service;
import org.xyy.util.*;

/**
 * Service Node Communicate Protocol
 *
 */
@SuppressWarnings("unchecked")
public class SncpServer extends Server<DLong, SncpContext, SncpRequest, SncpResponse, SncpServlet> {

    public SncpServer() {
        this(System.currentTimeMillis());
    }

    public SncpServer(long serverStartTime) {
        super(serverStartTime, "TCP", new SncpPrepareServlet());
    }

    @Override
    public void init(AnyValue config) throws Exception {
        super.init(config);
    }

    public List<SncpServlet> getSncpServlets() {
        return this.prepare.getServlets();
    }

    public List<SncpFilter> getSncpFilters() {
        return this.prepare.getFilters();
    }

    /**
     * 删除SncpFilter
     *
     * @param <T>         泛型
     * @param filterClass SncpFilter类
     *
     * @return SncpFilter
     */
    public <T extends SncpFilter> T removeSncpFilter(Class<T> filterClass) {
        return (T) this.prepare.removeFilter(filterClass);
    }

    /**
     * 添加SncpFilter
     *
     * @param filter SncpFilter
     * @param conf   AnyValue
     *
     * @return SncpServer
     */
    public SncpServer addSncpFilter(SncpFilter filter, AnyValue conf) {
        this.prepare.addFilter(filter, conf);
        return this;
    }

    /**
     * 删除SncpServlet
     *
     * @param sncpService Service
     *
     * @return SncpServlet
     */
    public SncpServlet removeSncpServlet(Service sncpService) {
        return ((SncpPrepareServlet) this.prepare).removeSncpServlet(sncpService);
    }

    public void addSncpServlet(Service sncpService) {
        SncpDynServlet sds = new SncpDynServlet(BsonFactory.root().getConvert(), Sncp.getResourceName(sncpService), Sncp.getResourceType(sncpService), sncpService);
        this.prepare.addServlet(sds, null, Sncp.getConf(sncpService));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SncpContext createContext() {
        final int port = this.address.getPort();
        AtomicLong createBufferCounter = new AtomicLong();
        AtomicLong cycleBufferCounter = new AtomicLong();
        final int rcapacity = Math.max(this.bufferCapacity, 4 * 1024);
        ObjectPool<ByteBuffer> bufferPool = new ObjectPool<>(createBufferCounter, cycleBufferCounter, this.bufferPoolSize,
            (Object... params) -> ByteBuffer.allocateDirect(rcapacity), null, (e) -> {
                if (e == null || e.isReadOnly() || e.capacity() != rcapacity) return false;
                e.clear();
                return true;
            });
        AtomicLong createResponseCounter = new AtomicLong();
        AtomicLong cycleResponseCounter = new AtomicLong();
        ObjectPool<Response> responsePool = SncpResponse.createPool(createResponseCounter, cycleResponseCounter, this.responsePoolSize, null);
        SncpContext sncpcontext = new SncpContext(this.serverStartTime, this.logger, executor, rcapacity, bufferPool, responsePool,
            this.maxbody, this.charset, this.address, this.prepare, this.readTimeoutSecond, this.writeTimeoutSecond);
        responsePool.setCreator((Object... params) -> new SncpResponse(sncpcontext, new SncpRequest(sncpcontext)));
        return sncpcontext;
    }

}
