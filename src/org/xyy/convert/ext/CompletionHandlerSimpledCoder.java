package org.xyy.convert.ext;

import java.nio.channels.*;
import org.xyy.convert.*;

/**
 * java.nio.channels.CompletionHandler 的SimpledCoder实现, 只输出null
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class CompletionHandlerSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, CompletionHandler> {

    public static final CompletionHandlerSimpledCoder instance = new CompletionHandlerSimpledCoder();

    @Override
    public void convertTo(W out, CompletionHandler value) {
        out.writeObjectNull(CompletionHandler.class);
    }

    @Override
    public CompletionHandler convertFrom(R in) {
        in.readObjectB(CompletionHandler.class);
        return null;
    }

}
