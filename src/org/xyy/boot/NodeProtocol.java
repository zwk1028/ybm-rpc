package org.xyy.boot;

import java.lang.annotation.*;

/**
 * 根据application.xml中的server节点中的protocol值来适配Server的加载逻辑, 
 * 只能注解在NodeServer子类上
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NodeProtocol {

    String[] value();
}
