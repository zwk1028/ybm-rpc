package org.xyy.boot;

import java.lang.annotation.Annotation;
import org.xyy.net.*;
import org.xyy.net.http.*;
import org.xyy.service.Service;
import org.xyy.util.AnyValue;
import org.xyy.watch.*;

@NodeProtocol({"WATCH"})
public class NodeWatchServer extends NodeHttpServer {

    public NodeWatchServer(Application application, AnyValue serconf) {
        super(application, serconf);
    }

    @Override
    protected ClassFilter<Service> createServiceClassFilter() {
        return createClassFilter(this.sncpGroup, null, WatchService.class, null, Annotation.class, "services", "service");
    }

    @Override
    protected ClassFilter<Filter> createFilterClassFilter() {
        return createClassFilter(null, null, WatchFilter.class, null, null, "filters", "filter");
    }

    @Override
    protected ClassFilter<Servlet> createServletClassFilter() {
        return createClassFilter(null, WebServlet.class, WatchServlet.class, null, null, "servlets", "servlet");
    }

    @Override
    protected ClassFilter createOtherClassFilter() {
        return null;
    }

    @Override
    public boolean isWATCH() {
        return true;
    }
}
