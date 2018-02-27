package org.xyy.convert.ext;

import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;
import org.xyy.convert.Reader;
import java.math.BigInteger;

/**
 * BigInteger 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class BigIntegerSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, BigInteger> {

    public static final BigIntegerSimpledCoder instance = new BigIntegerSimpledCoder();

    @Override
    public void convertTo(W out, BigInteger value) {
        if (value == null) {
            out.writeNull();
            return;
        }
        ByteArraySimpledCoder.instance.convertTo(out, value.toByteArray());
    }

    @Override
    public BigInteger convertFrom(R in) {
        byte[] bytes = ByteArraySimpledCoder.instance.convertFrom(in);
        return bytes == null ? null : new BigInteger(bytes);
    }

    /**
     * BigInteger 的JsonSimpledCoder实现
     *
     * @param <R> Reader输入的子类型
     * @param <W> Writer输出的子类型
     */
    public static class BigIntegerJsonSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, BigInteger> {

        public static final BigIntegerJsonSimpledCoder instance = new BigIntegerJsonSimpledCoder();

        @Override
        public void convertTo(final Writer out, final BigInteger value) {
            if (value == null) {
                out.writeNull();
            } else {
                out.writeString(value.toString());
            }
        }

        @Override
        public BigInteger convertFrom(Reader in) {
            final String str = in.readString();
            if (str == null) return null;
            return new BigInteger(str);
        }
    }
}
