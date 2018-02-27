package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * long 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class LongSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Long> {

    public static final LongSimpledCoder instance = new LongSimpledCoder();

    @Override
    public void convertTo(W out, Long value) {
        out.writeLong(value);
    }

    @Override
    public Long convertFrom(R in) {
        return in.readLong();
    }

}
