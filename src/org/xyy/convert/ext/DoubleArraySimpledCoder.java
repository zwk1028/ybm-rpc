package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;

/**
 * double[] 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class DoubleArraySimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, double[]> {

    public static final DoubleArraySimpledCoder instance = new DoubleArraySimpledCoder();

    @Override
    public void convertTo(W out, double[] values) {
        if (values == null) {
            out.writeNull();
            return;
        }
        out.writeArrayB(values.length);
        boolean flag = false;
        for (double v : values) {
            if (flag) out.writeArrayMark();
            out.writeDouble(v);
            flag = true;
        }
        out.writeArrayE();
    }

    @Override
    public double[] convertFrom(R in) {
        int len = in.readArrayB();
        if (len == Reader.SIGN_NULL) return null;
        if (len == Reader.SIGN_NOLENGTH) {
            int size = 0;
            double[] data = new double[8];
            while (in.hasNext()) {
                if (size >= data.length) {
                    double[] newdata = new double[data.length + 4];
                    System.arraycopy(data, 0, newdata, 0, size);
                    data = newdata;
                }
                data[size++] = in.readDouble();
            }
            in.readArrayE();
            double[] newdata = new double[size];
            System.arraycopy(data, 0, newdata, 0, size);
            return newdata;
        } else {
            double[] values = new double[len];
            for (int i = 0; i < values.length; i++) {
                values[i] = in.readDouble();
            }
            in.readArrayE();
            return values;
        }
    }

}
