package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * boolean 的SimpledCoder实现
 * 
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class BoolSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Boolean> {

    public static final BoolSimpledCoder instance = new BoolSimpledCoder();

    @Override
    public void convertTo(W out, Boolean value) {
        out.writeBoolean(value);
    }

    @Override
    public Boolean convertFrom(R in) {
        return in.readBoolean();
    }

}
