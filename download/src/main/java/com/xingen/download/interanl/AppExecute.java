package com.xingen.download.interanl;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Author by ${xinGen},  Date on 2018/5/29.
 * <p>
 * 线程池管理类
 */
public class AppExecute {
    private static final String TAG="AppExecute";
    /**
     * 主线程
     */
    private Executor mainExecutor;
    /**
     * 网络线程
     */
    private Executor netExecutor;
    private final int CODE_POOL_SIZE;
    private final int maxPoolSize;
    private final int KEEP_ALIVE_TIME;
    private final TimeUnit TIME_UNIT;
    /**
     * 默认情况下，单文件直接下载的线程个数
     */
    public static final int single_file_down_thread_size = 3;
    /**
     * 根据android运行的cpu运行个数
     */
    private final int NUMBER_OF_CORE;

    /**
     * 计算线程的线程池的队列
     */
    private final BlockingQueue<Runnable> calculateThreadQueue;
    /**
     * 配置线程池，计算
     */
    private ThreadPoolExecutor calculatePoolExecutor;
    private static AppExecute instance;
    static {
        instance = new AppExecute();
    }

    private AppExecute(Executor mainExecutor, Executor netExecutor) {
        this.mainExecutor = mainExecutor;
        this.netExecutor = netExecutor;
        //计算线程池
        this.CODE_POOL_SIZE = single_file_down_thread_size;
        this.maxPoolSize = single_file_down_thread_size;
        this.KEEP_ALIVE_TIME = 1;
        this.NUMBER_OF_CORE = Runtime.getRuntime().availableProcessors();
        this.TIME_UNIT = TimeUnit.SECONDS;
        this.calculateThreadQueue = new LinkedBlockingQueue<>();
        this.calculatePoolExecutor = new ThreadPoolExecutor(this.NUMBER_OF_CORE, this.NUMBER_OF_CORE, this.KEEP_ALIVE_TIME, this.TIME_UNIT, this.calculateThreadQueue);
    }
    private AppExecute() {
        this(new MainExecute(), Executors.newFixedThreadPool(3));
    }

    public static AppExecute getInstance() {
        return instance;
    }

    /**
     * 主线程的Handler
     */
    private static class MainExecute implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }

    public Executor getMainExecutor() {
        return mainExecutor;
    }

    public Executor getNetExecutor() {
        return netExecutor;
    }
    /**
     * 创建指定线程个数的线程池
     *
     * @param number
     * @return
     */
    public ExecutorService createThreadPool(int number) {
        return Executors.newFixedThreadPool(number);
    }
    /**
     * 执行计算线程的任务
     *
     * @param runnable
     */
    public  void executeCalculateTask(Runnable runnable) {
        this.calculatePoolExecutor.execute(runnable);
    }
    /**
     * 执行主线程的任务
     *
     * @param runnable
     */
    public void executorUITask(Runnable runnable) {
        this.mainExecutor.execute(runnable);
    }

    /**
     * 执行单文件的网络任务
     * @param runnable
     */
    public void executorNetTask(Runnable runnable){
        this.netExecutor.execute(runnable);
    }

}
