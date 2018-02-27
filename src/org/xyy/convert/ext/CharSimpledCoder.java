package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * char 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class CharSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Character> {

    public static final CharSimpledCoder instance = new CharSimpledCoder();

    @Override
    public void convertTo(W out, Character value) {
        out.writeChar(value);
    }

    @Override
    public Character convertFrom(R in) {
        return in.readChar();
    }

}
