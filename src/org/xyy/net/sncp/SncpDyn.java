package org.xyy.net.sncp;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 修饰由SNCP协议动态生成的class、和method
 * 本地模式：动态生成的_DynLocalXXXXService类其带有&#64;RpcMultiRun方法均会打上@SncpDyn(remote = false, index=N) 的注解
 * 远程模式：动态生成的_DynRemoteXXXService类会打上&#64;SncpDyn(remote = true) 的注解
 *
 */
@Inherited
@Documented
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface SncpDyn {

    boolean remote();

    int index() default 0;  //排列顺序， 主要用于Method
}
