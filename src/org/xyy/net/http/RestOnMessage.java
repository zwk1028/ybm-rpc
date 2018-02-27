package org.xyy.net.http;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记在RestWebSocket的接收消息方法上
 *
 *
 */
@Inherited
@Documented
@Target({METHOD})
@Retention(RUNTIME)
public @interface RestOnMessage {

    /**
     * 请求的方法名, 不能含特殊字符,不能以数字开头(能作为变量名)
     *
     * @return String
     */
    String name();

    /**
     * 备注描述
     *
     * @return String
     */
    String comment() default "";
}
