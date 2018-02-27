package org.xyy.source;

import java.io.*;
import java.lang.reflect.*;
import org.xyy.util.*;

/**
 *
 * @param <T> Entity类的类型
 * @param <F> 字段的类型
 */
public final class DataCallArrayAttribute<T, F> implements Attribute<T[], F> {

    public static final DataCallArrayAttribute instance = new DataCallArrayAttribute();

    @Override
    public Class<? extends F> type() {
        return (Class<F>) Object.class;
    }

    @Override
    public Class<T[]> declaringClass() {
        return (Class<T[]>) (Class) Object[].class;
    }

    @Override
    public String field() {
        return "";
    }

    @Override
    public F get(final T[] objs) {
        if (objs == null || objs.length == 0) return null;
        final Attribute<T, Serializable> attr = DataCallAttribute.load(objs[0].getClass());
        final Object keys = Array.newInstance(attr.type(), objs.length);
        for (int i = 0; i < objs.length; i++) {
            Array.set(keys, i, attr.get(objs[i]));
        }
        return (F) keys;
    }

    @Override
    public void set(final T[] objs, final F keys) {
        if (objs == null || objs.length == 0) return;
        final Attribute<T, Serializable> attr = DataCallAttribute.load(objs[0].getClass());
        for (int i = 0; i < objs.length; i++) {
            attr.set(objs[i], (Serializable) Array.get(keys, i));
        }
    }

}
