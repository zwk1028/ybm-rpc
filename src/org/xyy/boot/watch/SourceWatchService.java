package org.xyy.boot.watch;

import javax.annotation.Resource;
import org.xyy.boot.Application;
import org.xyy.net.TransportFactory;
import org.xyy.net.http.RestService;

/**
 *
 */
@RestService(name = "source", catalog = "watch", repair = false)
public class SourceWatchService extends AbstractWatchService {

    @Resource
    private Application application;

    @Resource
    private TransportFactory transportFactory;

}
