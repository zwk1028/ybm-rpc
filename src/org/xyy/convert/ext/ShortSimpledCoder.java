package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * short 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class ShortSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Short> {

    public static final ShortSimpledCoder instance = new ShortSimpledCoder();

    @Override
    public void convertTo(W out, Short value) {
        out.writeShort(value);
    }

    @Override
    public Short convertFrom(R in) {
        return in.readShort();
    }

}
