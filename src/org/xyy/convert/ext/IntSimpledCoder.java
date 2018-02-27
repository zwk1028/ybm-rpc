package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * int 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class IntSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Integer> {

    public static final IntSimpledCoder instance = new IntSimpledCoder();

    @Override
    public void convertTo(W out, Integer value) {
        out.writeInt(value);
    }

    @Override
    public Integer convertFrom(R in) {
        return in.readInt();
    }

}
