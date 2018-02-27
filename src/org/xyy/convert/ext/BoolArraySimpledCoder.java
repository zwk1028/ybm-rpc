package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * boolean[] 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class BoolArraySimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, boolean[]> {

    public static final BoolArraySimpledCoder instance = new BoolArraySimpledCoder();

    @Override
    public void convertTo(W out, boolean[] values) {
        if (values == null) {
            out.writeNull();
            return;
        }
        out.writeArrayB(values.length);
        boolean flag = false;
        for (boolean v : values) {
            if (flag) out.writeArrayMark();
            out.writeBoolean(v);
            flag = true;
        }
        out.writeArrayE();
    }

    @Override
    public boolean[] convertFrom(R in) {
        int len = in.readArrayB();
        if (len == Reader.SIGN_NULL) return null;
        if (len == Reader.SIGN_NOLENGTH) {
            int size = 0;
            boolean[] data = new boolean[8];
            while (in.hasNext()) {
                if (size >= data.length) {
                    boolean[] newdata = new boolean[data.length + 4];
                    System.arraycopy(data, 0, newdata, 0, size);
                    data = newdata;
                }
                data[size++] = in.readBoolean();
            }
            in.readArrayE();
            boolean[] newdata = new boolean[size];
            System.arraycopy(data, 0, newdata, 0, size);
            return newdata;
        } else {
            boolean[] values = new boolean[len];
            for (int i = 0; i < values.length; i++) {
                values[i] = in.readBoolean();
            }
            in.readArrayE();
            return values;
        }
    }

}
