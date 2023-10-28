package com.moore.base.thread;


import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 线程池
 *
 * @author moore
 */
public class ThreadPoolUtil {

    private static final int CPU_CORE_NUM = 4;

    /**
     * cup密集型任务线程池
     */
    public static final ExecutorService CPU_EXECUTOR = new ThreadPoolExecutor(CPU_CORE_NUM + 1, CPU_CORE_NUM + 1, 1L, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50),  new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * io密集型任务线程池
     */
    private static final ExecutorService IO_EXECUTOR = new ThreadPoolExecutor(CPU_CORE_NUM * 2, CPU_CORE_NUM * 3, 1L, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 执行CPU密集型Runnable任务
     *
     * @param task 任务对象
     */
    public static void executeCpuTask(Runnable task){
        CPU_EXECUTOR.execute(Objects.requireNonNull(TtlRunnable.get(task)));
    }

    /**
     * 提交CPU密集型Runnable任务，并返回任务结果
     *
     * @param task 任务对象
     * @return     任务执行结果
     */
    public static Future<?> submitCpuTask(Runnable task){
        return CPU_EXECUTOR.submit(Objects.requireNonNull(TtlRunnable.get(task)));
    }

    /**
     * 提交CPU密集型Callable任务，并返回任务结果
     *
     * @param task 任务对象
     * @return     任务执行结果
     */
    public static <V> Future<V> submitCpuTask(Callable<V> task){
        return CPU_EXECUTOR.submit(Objects.requireNonNull(TtlCallable.get(task)));
    }

    /**
     * 提交IO密集型Runnable任务，并返回任务结果
     *
     * @param task   任务对象
     * @param result 返回结果类型
     * @return       任务执行结果
     */
    public static <T> Future<T> submitCpuTask(Runnable task, T result){
        return CPU_EXECUTOR.submit(Objects.requireNonNull(TtlRunnable.get(task)), result);
    }

    /**
     * 执行IO密集型Runnable任务
     *
     * @param task 任务对象
     */
    public static void executeIoTask(Runnable task){
        IO_EXECUTOR.execute(Objects.requireNonNull(TtlRunnable.get(task)));
    }

    /**
     * 提交IO密集型Runnable任务，并返回任务结果
     *
     * @param task 任务对象
     * @return     任务执行结果
     */
    public static Future<?> submitIoTask(Runnable task){
        return IO_EXECUTOR.submit(Objects.requireNonNull(TtlRunnable.get(task)));
    }

    /**
     * 提交IO密集型Callable任务，并返回任务结果
     *
     * @param task 任务对象
     * @return     任务执行结果
     */
    public static <V> Future<V> submitIoTask(Callable<V> task){
        return IO_EXECUTOR.submit(Objects.requireNonNull(TtlCallable.get(task)));
    }

    /**
     * 提交IO密集型Runnable任务，并返回任务结果
     *
     * @param task   任务对象
     * @param result 返回结果类型
     * @return       任务执行结果
     */
    public static <T> Future<T> submitIoTask(Runnable task, T result){
        return IO_EXECUTOR.submit(Objects.requireNonNull(TtlRunnable.get(task)), result);
    }

}
