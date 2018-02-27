package org.xyy.convert.ext;

import java.util.regex.*;
import org.xyy.convert.*;

/**
 *  Pattern 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public class PatternSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Pattern> {

    public static final PatternSimpledCoder instance = new PatternSimpledCoder();

    @Override
    public void convertTo(W out, Pattern value) {
        if (value == null) {
            out.writeNull();
        } else {
            out.writeString(value.flags() + "," + value.pattern());
        }
    }

    @Override
    public Pattern convertFrom(R in) {
        String value = in.readString();
        if (value == null) return null;
        int pos = value.indexOf(',');
        return Pattern.compile(value.substring(pos + 1), Integer.parseInt(value.substring(0, pos)));
    }

}
