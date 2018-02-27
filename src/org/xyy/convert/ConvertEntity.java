package org.xyy.convert;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.*;

/**
 * 用于类名的别名, 该值必须是全局唯一   <br>
 * 使用场景: 当BSON序列化为了不指定class可以使用@ConvertEntity来取个别名。关联方法: Reader.readClassName() 和 Writer.writeClassName(String value) 。
 *
 */
@Inherited
@Documented
@Target({TYPE})
@Retention(RUNTIME)
public @interface ConvertEntity {

    /**
     * 别名值
     *
     * @return String
     */
    String value();
}
