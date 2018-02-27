package org.xyy.net.http;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 只能注解于Service类的方法的String参数或参数内的String字段
 * <p>
 * 用于获取HTTP请求URL HttpRequest.getRequestURI
 *
 */
@Inherited
@Documented
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface RestURI {

    /**
     * 备注描述, 对应&#64;HttpParam.comment
     *
     * @return String
     */
    String comment() default "";
}
