package org.xyy.boot;

import java.lang.reflect.Modifier;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import org.xyy.boot.ClassFilter.FilterEntry;
import org.xyy.net.*;
import org.xyy.net.sncp.*;
import org.xyy.service.Service;
import org.xyy.util.*;
import org.xyy.util.AnyValue.DefaultAnyValue;

/**
 * SNCP Server节点的配置Server
 *
 */
@NodeProtocol({"SNCP"})
public class NodeSncpServer extends NodeServer {

    protected final SncpServer sncpServer;

    private NodeSncpServer(Application application, AnyValue serconf) {
        super(application, createServer(application, serconf));
        this.sncpServer = (SncpServer) this.server;
        this.consumer = sncpServer == null ? null : x -> sncpServer.addSncpServlet(x);
    }

    public static NodeServer createNodeServer(Application application, AnyValue serconf) {
        if (serconf != null && serconf.getAnyValue("rest") != null) {
            ((AnyValue.DefaultAnyValue) serconf).addValue("_$sncp", "true");
            return new NodeHttpServer(application, serconf);
        }
        return new NodeSncpServer(application, serconf);
    }

    private static Server createServer(Application application, AnyValue serconf) {
        return new SncpServer(application.getStartTime());
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return sncpServer == null ? null : sncpServer.getSocketAddress();
    }

    public void consumerAccept(Service service) {
        if (this.consumer != null) this.consumer.accept(service);
    }

    @Override
    public void init(AnyValue config) throws Exception {
        super.init(config);
        //-------------------------------------------------------------------
        if (sncpServer == null) return; //调试时server才可能为null
        final StringBuilder sb = logger.isLoggable(Level.FINE) ? new StringBuilder() : null;
        final String threadName = "[" + Thread.currentThread().getName() + "] ";
        List<SncpServlet> servlets = sncpServer.getSncpServlets();
        Collections.sort(servlets);
        for (SncpServlet en : servlets) {
            if (sb != null) sb.append(threadName).append(" Load ").append(en).append(LINE_SEPARATOR);
        }
        if (sb != null && sb.length() > 0) logger.log(Level.FINE, sb.toString());
    }

    @Override
    public boolean isSNCP() {
        return true;
    }

    public SncpServer getSncpServer() {
        return sncpServer;
    }

    @Override
    protected void loadFilter(ClassFilter<? extends Filter> filterFilter, ClassFilter otherFilter) throws Exception {
        if (sncpServer != null) loadSncpFilter(this.serverConf.getAnyValue("fliters"), filterFilter);
    }

    protected void loadSncpFilter(final AnyValue servletsConf, final ClassFilter<? extends Filter> classFilter) throws Exception {
        final StringBuilder sb = logger.isLoggable(Level.INFO) ? new StringBuilder() : null;
        final String threadName = "[" + Thread.currentThread().getName() + "] ";
        List<FilterEntry<? extends Filter>> list = new ArrayList(classFilter.getFilterEntrys());
        for (FilterEntry<? extends Filter> en : list) {
            Class<SncpFilter> clazz = (Class<SncpFilter>) en.getType();
            if (Modifier.isAbstract(clazz.getModifiers())) continue;
            final SncpFilter filter = clazz.newInstance();
            resourceFactory.inject(filter, this);
            DefaultAnyValue filterConf = (DefaultAnyValue) en.getProperty();
            this.sncpServer.addSncpFilter(filter, filterConf);
            if (sb != null) sb.append(threadName).append(" Load ").append(clazz.getName()).append(LINE_SEPARATOR);
        }
        if (sb != null && sb.length() > 0) logger.log(Level.INFO, sb.toString());
    }

    @Override
    protected void loadServlet(ClassFilter<? extends Servlet> servletFilter, ClassFilter otherFilter) throws Exception {
    }

    @Override
    protected ClassFilter<Filter> createFilterClassFilter() {
        return createClassFilter(null, null, SncpFilter.class, null, null, "filters", "filter");
    }

    @Override
    protected ClassFilter<Servlet> createServletClassFilter() {
        return null;
    }

}
