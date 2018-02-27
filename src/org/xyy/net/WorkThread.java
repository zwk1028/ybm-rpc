package org.xyy.net;

import java.util.concurrent.*;

/**
 * 协议处理的自定义线程类
 *
 */
public class WorkThread extends Thread {

    private final ExecutorService executor;

    /**
     *构造函数
     * @param executor
     * @param runner
     */
    public WorkThread(ExecutorService executor, Runnable runner) {
        super(runner);
        this.executor = executor;
        this.setDaemon(true);
    }

    /**
     * 异步执行
     * @param runner
     */
    public void runAsync(Runnable runner) {
        executor.execute(runner);
    }

    
    public ExecutorService getExecutor() {
        return executor;
    }
}
