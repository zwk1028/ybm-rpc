package org.xyy.convert.ext;

import org.xyy.convert.Reader;
import org.xyy.convert.SimpledCoder;
import org.xyy.convert.Writer;
import java.util.Date;

/**
 * Date 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class DateSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, Date> {

    public static final DateSimpledCoder instance = new DateSimpledCoder();

    @Override
    public void convertTo(W out, Date value) {
        out.writeLong(value == null ? 0L : value.getTime());
    }

    @Override
    public Date convertFrom(R in) {
        long t = in.readLong();
        return t == 0 ? null : new Date();
    }

}
