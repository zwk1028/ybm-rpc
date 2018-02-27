package org.xyy.service;

import java.lang.annotation.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import org.xyy.util.*;

/**
 * 参数回写, 当Service的方法需要更改参数对象内部的数据时，需要使用RpcCall
 *
 */
@Inherited
@Documented
@Target({ElementType.PARAMETER})
@Retention(RUNTIME)
public @interface RpcCall {

    Class<? extends Attribute> value();
}
