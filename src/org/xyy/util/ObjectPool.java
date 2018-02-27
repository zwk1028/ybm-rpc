package org.xyy.util;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;

/**
 * 
 * @author evanliux
 *
 * @param <T> 对象池元素的数据类型
 */
public final class ObjectPool<T> implements Supplier<T> {

    private static final Logger logger = Logger.getLogger(ObjectPool.class.getSimpleName());

    private final boolean debug;//是否调试模式

    private final Queue<T> queue;

    //创建者接口
    private Creator<T> creator;

    //预处理接口
    private final Consumer<T> prepare;

    //判断是否循环的接口
    private final Predicate<T> recycler;

    //创建计数器
    private final AtomicLong creatCounter;

    //循环计数器
    private final AtomicLong cycleCounter;

    public ObjectPool(Class<T> clazz, Consumer<T> prepare, Predicate<T> recycler) {
        this(2, clazz, prepare, recycler);
    }

    public ObjectPool(int max, Class<T> clazz, Consumer<T> prepare, Predicate<T> recycler) {
        this(max, Creator.create(clazz), prepare, recycler);
    }

    public ObjectPool(Creator<T> creator, Consumer<T> prepare, Predicate<T> recycler) {
        this(2, creator, prepare, recycler);
    }

    public ObjectPool(int max, Creator<T> creator, Consumer<T> prepare, Predicate<T> recycler) {
        this(null, null, max, creator, prepare, recycler);
    }

    public ObjectPool(AtomicLong creatCounter, AtomicLong cycleCounter, int max, Creator<T> creator, Consumer<T> prepare, Predicate<T> recycler) {
        this.creatCounter = creatCounter;
        this.cycleCounter = cycleCounter;
        this.creator = creator;
        this.prepare = prepare;
        this.recycler = recycler;
        this.queue = new LinkedBlockingQueue<>(Math.max(Runtime.getRuntime().availableProcessors() * 2, max));
        this.debug = logger.isLoggable(Level.FINEST);
    }

    public void setCreator(Creator<T> creator) {
        this.creator = creator;
    }

    //获取队列头部对象
    @Override
    public T get() {
        T result = queue.poll();
        if (result == null) {
            if (creatCounter != null) creatCounter.incrementAndGet();
            result = this.creator.create();//没有则创建
        }
        if (prepare != null) prepare.accept(result);//预处理对象
        return result;
    }

    //添加
    public void offer(final T e) {
        if (e != null && recycler.test(e)) {//测试是否该对象已经在对象池中了
            if (cycleCounter != null) 
            	cycleCounter.incrementAndGet();
            if (debug) {//调试模式下信息输出
                for (T t : queue) {
                    if (t == e) {
                        logger.log(Level.WARNING, "[" + Thread.currentThread().getName() + "] repeat offer the same object(" + e + ")", new Exception());
                        return;
                    }
                }
            }
            queue.offer(e);//压到队列中
        }
    }

    public long getCreatCount() {
        return creatCounter.longValue();
    }

    public long getCycleCount() {
        return cycleCounter.longValue();
    }
}
