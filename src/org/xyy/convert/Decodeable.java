package org.xyy.convert;

import java.lang.reflect.Type;

/**
 * 反序列化操作类
 *
 * @param <R> Reader输入的子类
 * @param <T> 反解析的数据类型
 */
public interface Decodeable<R extends Reader, T> {

    public T convertFrom(final R in);

    /**
     * 泛型映射接口
     *
     * @return 反解析的数据类型
     */
    public Type getType();

}
