package org.xyy.service;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * SNCP协议中标记为目标地址参数, 该注解只能标记在类型为SocketAddress或InetSocketAddress的参数上。
 *
 */
@Inherited
@Documented
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface RpcTargetAddress {
    
}
