package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.Writer;
import org.xyy.convert.SimpledCoder;
import org.xyy.util.*;

/**
 * Dlong 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class DLongSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, DLong> {

    private static final ByteArraySimpledCoder bsSimpledCoder = ByteArraySimpledCoder.instance;

    public static final DLongSimpledCoder instance = new DLongSimpledCoder();

    @Override
    public void convertTo(final W out, final DLong value) {
        if (value == null) {
            out.writeNull();
        } else {
            bsSimpledCoder.convertTo(out, value.directBytes());
        }
    }

    @Override
    public DLong convertFrom(R in) {
        byte[] bs = bsSimpledCoder.convertFrom(in);
        if (bs == null) return null;
        return DLong.create(bs);
    }

    /**
     * DLong 的JsonSimpledCoder实现
     *
     * @param <R> Reader输入的子类型
     * @param <W> Writer输出的子类型
     */
    public static class DLongJsonSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, DLong> {

        public static final DLongJsonSimpledCoder instance = new DLongJsonSimpledCoder();

        @Override
        public void convertTo(final Writer out, final DLong value) {
            if (value == null) {
                out.writeNull();
            } else {
                out.writeSmallString(value.toString());
            }
        }

        @Override
        public DLong convertFrom(Reader in) {
            final String str = in.readSmallString();
            if (str == null) return null;
            return DLong.create(Utility.hexToBin(str));
        }
    }
}
