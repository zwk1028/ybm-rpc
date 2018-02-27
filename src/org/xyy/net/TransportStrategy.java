package org.xyy.net;

import java.net.SocketAddress;

/**
 * 远程请求的负载均衡策略
 *
 */
public interface TransportStrategy {

    public AsyncConnection pollConnection(SocketAddress addr, Transport transport);
}
