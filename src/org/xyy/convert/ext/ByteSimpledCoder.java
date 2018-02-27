package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * byte 的SimpledCoder实现
 * 
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class ByteSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Byte> {

    public static final ByteSimpledCoder instance = new ByteSimpledCoder();

    @Override
    public void convertTo(W out, Byte value) {
        out.writeByte(value);
    }

    @Override
    public Byte convertFrom(R in) {
        return in.readByte();
    }

}
