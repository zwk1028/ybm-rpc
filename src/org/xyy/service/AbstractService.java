package org.xyy.service;

import java.util.concurrent.*;
import org.xyy.net.WorkThread;
/**
 * 抽象服务支持类
 * @author evanliux
 *
 */
public abstract class AbstractService implements Service {

    protected void runAsync(Runnable runner) {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread) {
            ((WorkThread) thread).runAsync(runner);
        } else {
            ForkJoinPool.commonPool().execute(runner);
        }
    }

    protected ExecutorService getExecutor() {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread) {
            return ((WorkThread) thread).getExecutor();
        }
        return ForkJoinPool.commonPool();
    }
}
