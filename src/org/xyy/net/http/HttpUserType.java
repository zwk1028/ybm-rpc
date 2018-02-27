package org.xyy.net.http;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 配合 HttpServlet 使用。
 * 用于指定HttpRequest.currentUser的数据类型。<br>
 * 注意： 数据类型是JavaBean，则必须要用javax.persistence.Id标记主键字段，用于确定用户ID
 */
@Inherited
@Documented
@Target({TYPE})
@Retention(RUNTIME)
public @interface HttpUserType {

    Class value();

}
