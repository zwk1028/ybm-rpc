package org.xyy.convert.bson;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.util.function.*;
import org.xyy.convert.*;
import org.xyy.util.*;

/**
 * <blockquote><pre>
 * BSON协议格式:
 *  1) 基本数据类型: 直接转换成byte[]
 *  2) SmallString(无特殊字符且长度小于256的字符串): length(1 byte) + byte[](utf8); 通常用于类名、字段名、枚举。
 *  3) String: length(4 bytes) + byte[](utf8);
 *  4) 数组: length(4 bytes) + byte[]...
 *  5) Object:
 *      1、 realclass (SmallString) (如果指定格式化的class与实体对象的class不一致才会有该值, 该值可以使用@ConvertEntity给其取个别名)
 *      2、 空字符串(SmallString)
 *      3、 SIGN_OBJECTB 标记位，值固定为0xBB (short)
 *      4、 循环字段值:
 *          4.1 SIGN_HASNEXT 标记位，值固定为1 (byte)
 *          4.2 字段类型; 1-9为基本类型和字符串; 101-109为基本类型和字符串的数组; 127为Object
 *          4.3 字段名 (SmallString)
 *          4.4 字段的值Object
 *      5、 SIGN_NONEXT 标记位，值固定为0 (byte)
 *      6、 SIGN_OBJECTE 标记位，值固定为0xEE (short)
 *
 * </pre></blockquote>
 */
public final class BsonConvert extends Convert<BsonReader, BsonWriter> {

    private static final ObjectPool<BsonReader> readerPool = BsonReader.createPool(Integer.getInteger("convert.bson.pool.size", 16));

    private static final ObjectPool<BsonWriter> writerPool = BsonWriter.createPool(Integer.getInteger("convert.bson.pool.size", 16));

    private final boolean tiny;

    protected BsonConvert(ConvertFactory<BsonReader, BsonWriter> factory, boolean tiny) {
        super(factory);
        this.tiny = tiny;
    }

    @Override
    public BsonFactory getFactory() {
        return (BsonFactory) factory;
    }

    public static BsonConvert root() {
        return BsonFactory.root().getConvert();
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    //------------------------------ reader -----------------------------------------------------------
    public BsonReader pollBsonReader(final ByteBuffer... buffers) {
        return new BsonByteBufferReader((ConvertMask) null, buffers);
    }

    public BsonReader pollBsonReader(final InputStream in) {
        return new BsonStreamReader(in);
    }

    public BsonReader pollBsonReader() {
        return readerPool.get();
    }

    public void offerBsonReader(final BsonReader in) {
        if (in != null) readerPool.offer(in);
    }

    //------------------------------ writer -----------------------------------------------------------
    public BsonByteBufferWriter pollBsonWriter(final Supplier<ByteBuffer> supplier) {
        return new BsonByteBufferWriter(tiny, supplier);
    }

    public BsonWriter pollBsonWriter(final OutputStream out) {
        return new BsonStreamWriter(tiny, out);
    }

    public BsonWriter pollBsonWriter() {
        return writerPool.get().tiny(tiny);
    }

    public void offerBsonWriter(final BsonWriter out) {
        if (out != null) writerPool.offer(out);
    }

    //------------------------------ convertFrom -----------------------------------------------------------
    public <T> T convertFrom(final Type type, final byte[] bytes) {
        if (bytes == null) return null;
        return convertFrom(type, bytes, 0, bytes.length);
    }

    public <T> T convertFrom(final Type type, final byte[] bytes, final int start, final int len) {
        if (type == null) return null;
        final BsonReader in = readerPool.get();
        in.setBytes(bytes, start, len);
        @SuppressWarnings("unchecked")
        T rs = (T) factory.loadDecoder(type).convertFrom(in);
        readerPool.offer(in);
        return rs;
    }

    public <T> T convertFrom(final Type type, final InputStream in) {
        if (type == null || in == null) return null;
        return (T) factory.loadDecoder(type).convertFrom(new BsonStreamReader(in));
    }

    @Override
    public <T> T convertFrom(final Type type, final ByteBuffer... buffers) {
        if (type == null || buffers.length < 1) return null;
        return (T) factory.loadDecoder(type).convertFrom(new BsonByteBufferReader((ConvertMask) null, buffers));
    }

    @Override
    public <T> T convertFrom(final Type type, final ConvertMask mask, final ByteBuffer... buffers) {
        if (type == null || buffers.length < 1) return null;
        return (T) factory.loadDecoder(type).convertFrom(new BsonByteBufferReader(mask, buffers));
    }

    public <T> T convertFrom(final Type type, final BsonReader reader) {
        if (type == null) return null;
        @SuppressWarnings("unchecked")
        T rs = (T) factory.loadDecoder(type).convertFrom(reader);
        return rs;
    }

    //------------------------------ convertTo -----------------------------------------------------------
    public byte[] convertTo(final Object value) {
        if (value == null) {
            final BsonWriter out = writerPool.get().tiny(tiny);
            out.writeNull();
            byte[] result = out.toArray();
            writerPool.offer(out);
            return result;
        }
        return convertTo(value.getClass(), value);
    }

    public byte[] convertTo(final Type type, final Object value) {
        if (type == null) return null;
        final BsonWriter out = writerPool.get().tiny(tiny);
        factory.loadEncoder(type).convertTo(out, value);
        byte[] result = out.toArray();
        writerPool.offer(out);
        return result;
    }

    public void convertTo(final OutputStream out, final Object value) {
        if (value == null) {
            new BsonStreamWriter(tiny, out).writeNull();
        } else {
            factory.loadEncoder(value.getClass()).convertTo(new BsonStreamWriter(tiny, out), value);
        }
    }

    public void convertTo(final OutputStream out, final Type type, final Object value) {
        if (type == null) return;
        if (value == null) {
            new BsonStreamWriter(tiny, out).writeNull();
        } else {
            factory.loadEncoder(type).convertTo(new BsonStreamWriter(tiny, out), value);
        }
    }

    @Override
    public ByteBuffer[] convertTo(final Supplier<ByteBuffer> supplier, final Object value) {
        if (supplier == null) return null;
        BsonByteBufferWriter out = new BsonByteBufferWriter(tiny, supplier);
        if (value == null) {
            out.writeNull();
        } else {
            factory.loadEncoder(value.getClass()).convertTo(out, value);
        }
        return out.toBuffers();
    }

    @Override
    public ByteBuffer[] convertTo(final Supplier<ByteBuffer> supplier, final Type type, final Object value) {
        if (supplier == null || type == null) return null;
        BsonByteBufferWriter out = new BsonByteBufferWriter(tiny, supplier);
        if (value == null) {
            out.writeNull();
        } else {
            factory.loadEncoder(type).convertTo(out, value);
        }
        return out.toBuffers();
    }

    public void convertTo(final BsonWriter writer, final Object value) {
        if (value == null) {
            writer.writeNull();
        } else {
            factory.loadEncoder(value.getClass()).convertTo(writer, value);
        }
    }

    public void convertTo(final BsonWriter writer, final Type type, final Object value) {
        if (type == null) return;
        factory.loadEncoder(type).convertTo(writer, value);
    }

    public BsonWriter convertToWriter(final Object value) {
        if (value == null) return null;
        return convertToWriter(value.getClass(), value);
    }

    public BsonWriter convertToWriter(final Type type, final Object value) {
        if (type == null) return null;
        final BsonWriter out = writerPool.get().tiny(tiny);
        factory.loadEncoder(type).convertTo(out, value);
        return out;
    }

}
