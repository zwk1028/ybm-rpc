package org.xyy.watch;

import org.xyy.service.*;

/**
 * 只给WATCH协议的Server才能加载的Service，其他协议的Server均不能自动加载WatchService
 */
public interface WatchService extends Service {

}
