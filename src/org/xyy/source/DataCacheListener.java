package org.xyy.source;

import java.io.Serializable;

/**
 *
 *
 */
public interface DataCacheListener {

    public <T> int insertCache(Class<T> clazz, T... entitys);

    public <T> int updateCache(Class<T> clazz, T... entitys);

    public <T> int deleteCache(Class<T> clazz, Serializable... ids);
}
