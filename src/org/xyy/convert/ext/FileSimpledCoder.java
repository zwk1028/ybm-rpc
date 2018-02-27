package org.xyy.convert.ext;

import java.io.File;
import org.xyy.convert.*;

/**
 * 文件 的SimpledCoder实现
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public class FileSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, File> {

    public static final PatternSimpledCoder instance = new PatternSimpledCoder();

    @Override
    public void convertTo(W out, File value) {
        if (value == null) {
            out.writeNull();
        } else {
            out.writeString(value.getPath());
        }
    }

    @Override
    public File convertFrom(R in) {
        String value = in.readString();
        if (value == null) return null;
        return new File(value);
    }

}
