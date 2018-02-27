package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * float 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class FloatSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Float> {

    public static final FloatSimpledCoder instance = new FloatSimpledCoder();

    @Override
    public void convertTo(W out, Float value) {
        out.writeFloat(value);
    }

    @Override
    public Float convertFrom(R in) {
        return in.readFloat();
    }

}
