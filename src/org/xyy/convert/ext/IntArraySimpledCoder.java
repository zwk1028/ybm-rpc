package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * int[] 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class IntArraySimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, int[]> {

    public static final IntArraySimpledCoder instance = new IntArraySimpledCoder();

    @Override
    public void convertTo(W out, int[] values) {
        if (values == null) {
            out.writeNull();
            return;
        }
        out.writeArrayB(values.length);
        boolean flag = false;
        for (int v : values) {
            if (flag) out.writeArrayMark();
            out.writeInt(v);
            flag = true;
        }
        out.writeArrayE();
    }

    @Override
    public int[] convertFrom(R in) {
        int len = in.readArrayB();
        if (len == Reader.SIGN_NULL) return null;
        if (len == Reader.SIGN_NOLENGTH) {
            int size = 0;
            int[] data = new int[8];
            while (in.hasNext()) {
                if (size >= data.length) {
                    int[] newdata = new int[data.length + 4];
                    System.arraycopy(data, 0, newdata, 0, size);
                    data = newdata;
                }
                data[size++] = in.readInt();
            }
            in.readArrayE();
            int[] newdata = new int[size];
            System.arraycopy(data, 0, newdata, 0, size);
            return newdata;
        } else {
            int[] values = new int[len];
            for (int i = 0; i < values.length; i++) {
                values[i] = in.readInt();
            }
            in.readArrayE();
            return values;
        }
    }

}
