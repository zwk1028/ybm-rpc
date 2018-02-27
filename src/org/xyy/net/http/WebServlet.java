package org.xyy.net.http;

import java.lang.annotation.*;

/**
 * 功能同JSR 315 (java-servlet 3.0) 规范中的 @WebServlet
 *
 *
 *
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebServlet {

    /**
     * HttpServlet资源名
     *
     * @return String
     */
    String name() default "";

    /**
     * 是否自动添加url前缀, 对应application.xml中servlets节点的path属性
     *
     * @return boolean
     */
    boolean repair() default true;

    /**
     * url匹配规则
     *
     * @return String[]
     */
    String[] value() default {};

    /**
     * 模块ID，一个HttpServlet尽量只有提供一个模块的服务
     *
     * @return int
     */
    int moduleid() default 0;

    /**
     * 备注描述
     *
     * @return String
     */
    String comment() default "";
}
