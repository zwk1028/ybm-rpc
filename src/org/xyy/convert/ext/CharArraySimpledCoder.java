package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * char[] 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class CharArraySimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, char[]> {

    public static final CharArraySimpledCoder instance = new CharArraySimpledCoder();

    @Override
    public void convertTo(W out, char[] values) {
        if (values == null) {
            out.writeNull();
            return;
        }
        out.writeArrayB(values.length);
        boolean flag = false;
        for (char v : values) {
            if (flag) out.writeArrayMark();
            out.writeChar(v);
            flag = true;
        }
        out.writeArrayE();
    }

    @Override
    public char[] convertFrom(R in) {
        int len = in.readArrayB();
        if (len == Reader.SIGN_NULL) return null;
        if (len == Reader.SIGN_NOLENGTH) {
            int size = 0;
            char[] data = new char[8];
            while (in.hasNext()) {
                if (size >= data.length) {
                    char[] newdata = new char[data.length + 4];
                    System.arraycopy(data, 0, newdata, 0, size);
                    data = newdata;
                }
                data[size++] = in.readChar();
            }
            in.readArrayE();
            char[] newdata = new char[size];
            System.arraycopy(data, 0, newdata, 0, size);
            return newdata;
        } else {
            char[] values = new char[len];
            for (int i = 0; i < values.length; i++) {
                values[i] = in.readChar();
            }
            in.readArrayE();
            return values;
        }
    }

}
