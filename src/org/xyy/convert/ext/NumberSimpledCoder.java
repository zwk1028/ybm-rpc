package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * Number 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class NumberSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Number> {

    public static final NumberSimpledCoder instance = new NumberSimpledCoder();

    @Override
    public void convertTo(W out, Number value) {
        out.writeLong(value == null ? 0L : value.longValue());
    }

    @Override
    public Number convertFrom(R in) {
        return in.readLong();
    }

}
