package org.xyy.service;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.*;

/**
 * SNCP协议中用于CompletionHandler回调函数中的attach字段。
 *
 */
@Inherited
@Documented
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface RpcAttachment {

}
