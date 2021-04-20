package com.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author Evan
 * @create 2021/2/19 15:16
 */
@Slf4j
public class AsyncQueue {

    private static final int THREAD_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int QUEUE_SIZE = 10000;

    private static ExecutorService executorService =
            new ThreadPoolExecutor(THREAD_SIZE, THREAD_SIZE, 60L,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_SIZE),
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r);
                            t.setName("rabbitmq_async");
                            return t;
                        }
                    },
                    new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            log.error("reject error");
                        }
                    });

    public static void submit(Runnable runnable){
        executorService.submit(runnable);
    }

}
