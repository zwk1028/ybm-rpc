package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.Writer;
import org.xyy.convert.SimpledCoder;

/**
 * Type 的SimpledCoder实现 只支持Type的子类Class
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public class TypeSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Class> {

    public static final TypeSimpledCoder instance = new TypeSimpledCoder();

    @Override
    public void convertTo(final W out, final Class value) {
        if (value == null) {
            out.writeNull();
        } else {
            out.writeSmallString(value.getName());
        }
    }

    @Override
    public Class convertFrom(R in) {
        String str = in.readSmallString();
        if (str == null) return null;
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(str);
        } catch (Throwable e) {
            return null;
        }
    }

}
