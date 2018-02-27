package org.xyy.convert;

import java.lang.reflect.Type;

/**
 * 对不明类型的对象进行序列化； BSON序列化时将对象的类名写入Writer，JSON则不写入。
 *
 * @param <T> 序列化的泛型类型
 */
public final class AnyEncoder<T> implements Encodeable<Writer, T> {

    final ConvertFactory factory;

    AnyEncoder(ConvertFactory factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void convertTo(final Writer out, final T value) {
        if (value == null) {
            out.writeClassName(null);
            out.writeNull();
        } else {
            if (out.needWriteClassName()) out.writeClassName(factory.getEntityAlias(value.getClass()));
            factory.loadEncoder(value.getClass()).convertTo(out, value);
        }
    }

    @Override
    public Type getType() {
        return Object.class;
    }

}
