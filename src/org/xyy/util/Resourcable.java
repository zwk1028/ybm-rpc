package org.xyy.util;

/**
 * 对象的类没有标记为&#64;Resource, 可以通过实现Resourcable接口实现动态获取Resource.name
 */
public interface Resourcable {

    public String resourceName();
}
