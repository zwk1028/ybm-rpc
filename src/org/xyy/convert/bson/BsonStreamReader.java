package org.xyy.convert.bson;

import java.io.*;
import org.xyy.convert.*;


class BsonStreamReader extends BsonByteBufferReader {

    private InputStream in;

    private byte currByte;

    protected BsonStreamReader(InputStream in) {
        super((ConvertMask) null);
        this.in = in;
    }

    @Override
    protected boolean recycle() {
        super.recycle();   // this.position 初始化值为-1
        this.in = null;
        this.currByte = 0;
        return false;
    }

    @Override
    public byte readByte() {
        try {
            byte b = (currByte = (byte) in.read());
            this.position++;
            return b;
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }

    @Override
    protected byte currentByte() {
        return currByte;
    }

    @Override
    protected byte[] read(final int len) {
        byte[] bs = new byte[len];
        try {
            in.read(bs);
            this.position += len;
        } catch (IOException e) {
            throw new ConvertException(e);
        }
        return bs;
    }
}
