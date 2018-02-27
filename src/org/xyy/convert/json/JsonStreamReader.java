package org.xyy.convert.json;

import java.io.*;
import org.xyy.convert.*;


class JsonStreamReader extends JsonByteBufferReader {

    private InputStream in;

    protected JsonStreamReader(InputStream in) {
        super((ConvertMask) null);
        this.in = in;
    }

    @Override
    protected boolean recycle() {
        super.recycle();   // this.position 初始化值为-1
        this.in = null;
        return false;
    }

    @Override
    protected byte nextByte() {
        try {
            byte b = (byte) in.read();
            this.position++;
            return b;
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }
}
