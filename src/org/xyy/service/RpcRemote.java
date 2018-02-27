package org.xyy.service;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于在 Service 中创建自身远程模式的对象
 *
 */
@Inherited
@Documented
@Target({FIELD})
@Retention(RUNTIME)
public @interface RpcRemote {

}
