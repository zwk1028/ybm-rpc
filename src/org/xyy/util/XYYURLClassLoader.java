package org.xyy.util;

import java.net.*;
import java.util.HashSet;

/**
 *	动态类加载器 
*/
public class XYYURLClassLoader extends URLClassLoader {

    public XYYURLClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public Class<?> loadClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public URL[] getURLs() {
        return super.getURLs();
    }

    public URL[] getAllURLs() {
        ClassLoader loader = this;
        HashSet<URL> set = new HashSet<>();
        do {
            String loaderName = loader.getClass().getName();
            if (loaderName.startsWith("sun.") && loaderName.contains("ExtClassLoader")) continue;
            if (loader instanceof URLClassLoader) {
                for (URL url : ((URLClassLoader) loader).getURLs()) {
                    set.add(url);
                }
            }
        } while ((loader = loader.getParent()) != null);
        return set.toArray(new URL[set.size()]);
    }
}
