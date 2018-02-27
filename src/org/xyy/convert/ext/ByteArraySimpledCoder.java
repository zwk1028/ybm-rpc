package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * byte[] 的SimpledCoder实现
 * 
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class ByteArraySimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, byte[]> {

    public static final ByteArraySimpledCoder instance = new ByteArraySimpledCoder();

    @Override
    public void convertTo(W out, byte[] values) {
        if (values == null) {
            out.writeNull();
            return;
        }
        out.writeArrayB(values.length);
        boolean flag = false;
        for (byte v : values) {
            if (flag) out.writeArrayMark();
            out.writeByte(v);
            flag = true;
        }
        out.writeArrayE();
    }

    @Override
    public byte[] convertFrom(R in) {
        int len = in.readArrayB();
        if (len == Reader.SIGN_NULL) return null;
        if (len == Reader.SIGN_NOLENGTH) {
            int size = 0;
            byte[] data = new byte[8];
            while (in.hasNext()) {
                if (size >= data.length) {
                    byte[] newdata = new byte[data.length + 4];
                    System.arraycopy(data, 0, newdata, 0, size);
                    data = newdata;
                }
                data[size++] = in.readByte();
            }
            in.readArrayE();
            byte[] newdata = new byte[size];
            System.arraycopy(data, 0, newdata, 0, size);
            return newdata;
        } else {
            byte[] values = new byte[len];
            for (int i = 0; i < values.length; i++) {
                values[i] = in.readByte();
            }
            in.readArrayE();
            return values;
        }
    }

}
