package org.xyy.convert.ext;

import org.xyy.convert.*;

/**
 *  CharSequence 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public class CharSequenceSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, CharSequence> {

    public static final CharSequenceSimpledCoder instance = new CharSequenceSimpledCoder();

    @Override
    public void convertTo(W out, CharSequence value) {
        out.writeString(value == null ? null : value.toString());
    }

    @Override
    public CharSequence convertFrom(R in) {
        return in.readString();
    }
}
