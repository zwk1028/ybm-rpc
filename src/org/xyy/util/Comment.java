package org.xyy.util;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记注释，备注
 *
 */
@Inherited
@Documented
@Target({TYPE, METHOD, FIELD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, ANNOTATION_TYPE, TYPE_PARAMETER})
@Retention(RUNTIME)
public @interface Comment {

    String name() default "";

    String value();
}
