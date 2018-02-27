package org.xyy.convert.ext;

import org.xyy.convert.*;
import org.xyy.util.AsyncHandler;

/**
 * AsyncHandlerSimpledCoder 的SimpledCoder实现, 只输出null
 *
 * @param <R> Reader输入的子类型
 * @param <W> Writer输出的子类型
 */
public final class AsyncHandlerSimpledCoder<R extends Reader, W extends Writer> extends SimpledCoder<R, W, AsyncHandler> {

    public static final AsyncHandlerSimpledCoder instance = new AsyncHandlerSimpledCoder();

    @Override
    public void convertTo(W out, AsyncHandler value) {
        out.writeObjectNull(AsyncHandler.class);
    }

    @Override
    public AsyncHandler convertFrom(R in) {
        in.readObjectB(AsyncHandler.class);
        return null;
    }

}
