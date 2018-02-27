package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * float[] 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class FloatArraySimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, float[]> {

    public static final FloatArraySimpledCoder instance = new FloatArraySimpledCoder();

    @Override
    public void convertTo(W out, float[] values) {
        if (values == null) {
            out.writeNull();
            return;
        }
        out.writeArrayB(values.length);
        boolean flag = false;
        for (float v : values) {
            if (flag) out.writeArrayMark();
            out.writeFloat(v);
            flag = true;
        }
        out.writeArrayE();
    }

    @Override
    public float[] convertFrom(R in) {
        int len = in.readArrayB();
        if (len == Reader.SIGN_NULL) return null;
        if (len == Reader.SIGN_NOLENGTH) {
            int size = 0;
            float[] data = new float[8];
            while (in.hasNext()) {
                if (size >= data.length) {
                    float[] newdata = new float[data.length + 4];
                    System.arraycopy(data, 0, newdata, 0, size);
                    data = newdata;
                }
                data[size++] = in.readFloat();
            }
            in.readArrayE();
            float[] newdata = new float[size];
            System.arraycopy(data, 0, newdata, 0, size);
            return newdata;
        } else {
            float[] values = new float[len];
            for (int i = 0; i < values.length; i++) {
                values[i] = in.readFloat();
            }
            in.readArrayE();
            return values;
        }
    }

}
