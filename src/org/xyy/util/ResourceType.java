package org.xyy.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.*;

/**
 * 显式的指明资源类型。
 * 调用ResourceFactory.register(Object rs)时通常执行的是ResourceFactory.register(rs.getClass(), Object rs);
 * 若rs.getClass()的类标记了&#64;ResourceType, 则使用&#64;ResourceType.value()的class值进行注入。
 * 
 */
@Inherited
@Documented
@Target({TYPE})
@Retention(RUNTIME)
public @interface ResourceType {

    Class value();
}
