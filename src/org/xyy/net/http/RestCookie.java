package org.xyy.net.http;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 只能注解于RestService类的方法的String参数或参数内的String字段
 *
 */
@Inherited
@Documented
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface RestCookie {

    /**
     * cookie名
     *
     * @return String
     */
    String name();

    /**
     * 转换数字byte/short/int/long时所用的进制数， 默认10进制
     *
     * @return int
     */
    int radix() default 10;

    /**
     * 备注描述
     *
     * @return String
     */
    String comment() default "";
}
