package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * String 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class StringSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, String> {

    public static final StringSimpledCoder instance = new StringSimpledCoder();

    @Override
    public void convertTo(W out, String value) {
        out.writeString(value);
    }

    @Override
    public String convertFrom(R in) {
        return in.readString();
    }

}
