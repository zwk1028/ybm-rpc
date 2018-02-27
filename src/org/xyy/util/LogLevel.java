package org.xyy.util;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 被标记的日志级别以上的才会被记录
 *
*/
@Inherited
@Documented
@Target({TYPE})
@Retention(RUNTIME)
public @interface LogLevel {

    String value();
}
