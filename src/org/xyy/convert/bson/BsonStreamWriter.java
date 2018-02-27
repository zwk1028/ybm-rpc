package org.xyy.convert.bson;

import java.io.*;
import org.xyy.convert.*;


class BsonStreamWriter extends BsonByteBufferWriter {

    private OutputStream out;

    protected BsonStreamWriter(boolean tiny, OutputStream out) {
        super(tiny, null);
        this.out = out;
    }

    @Override
    protected boolean recycle() {
        super.recycle();
        this.out = null;
        return false;
    }

    @Override
    public void writeTo(final byte[] chs, final int start, final int len) {
        try {
            out.write(chs, start, len);
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }

    @Override
    public void writeTo(final byte ch) {
        try {
            out.write((byte) ch);
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }
}
