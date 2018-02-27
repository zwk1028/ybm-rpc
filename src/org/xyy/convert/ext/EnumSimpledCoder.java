package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * 枚举 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 * @param <E> Enum的子类
 */
public final class EnumSimpledCoder<R extends Reader, W extends Writer, E extends Enum> extends SimpledCoder<R, W, E> {

    private final Class<E> type;

    public EnumSimpledCoder(Class<E> type) {
        this.type = type;
    }

    @Override
    public void convertTo(final W out, final E value) {
        if (value == null) {
            out.writeNull();
        } else {
            out.writeSmallString(value.toString());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E convertFrom(final R in) {
        String value = in.readSmallString();
        if (value == null) return null;
        return (E) Enum.valueOf(type, value);
    }

}
