package com.abu.xbase.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author abu
 *         2017/12/6    16:56
 *         ..
 */

public class ThreadPool {
    private static ExecutorService executorService;
    private static final int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;
    /**
     * 核心保活线程数即最大正在运行数
     */
    private static final int CORE_POOL_SIZE = 10;


    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ABUThreadPool #" + mCount.getAndIncrement());
        }
    };

    public static ExecutorService getPool() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    1000L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), sThreadFactory);
        }
        return executorService;
    }
}
