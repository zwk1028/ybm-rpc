package org.xyy.net.http;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 只能注解于RestService类的方法的String/byte[]/JavaBean参数或参数内的String/byte[]/JavaBean字段
 * <p>
 * 用于获取HTTP请求端的请求内容UTF-8编码字符串、byte[]、JavaBean
 *
 *
 */
@Inherited
@Documented
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface RestBody {

    /**
     * 备注描述, 对应&#64;HttpParam.comment
     *
     * @return String
     */
    String comment() default "";
}
