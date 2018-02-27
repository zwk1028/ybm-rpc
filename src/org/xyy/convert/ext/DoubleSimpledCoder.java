package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * double 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class DoubleSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Double> {

    public static final DoubleSimpledCoder instance = new DoubleSimpledCoder();

    @Override
    public void convertTo(W out, Double value) {
        out.writeDouble(value);
    }

    @Override
    public Double convertFrom(R in) {
        return in.readDouble();
    }

}
