package org.xyy.service;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 本地模式注解。
 * 声明为Local的Service只能以本地模式存在， 即使配置文件中配置成远程模式也将被忽略。
 *
 */
@Inherited
@Documented
@Target({TYPE, METHOD, PARAMETER})
@Retention(RUNTIME)
public @interface Local {

    String comment() default ""; //备注描述
}
