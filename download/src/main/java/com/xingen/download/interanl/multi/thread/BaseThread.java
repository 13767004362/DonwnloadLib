package com.xingen.download.interanl.multi.thread;


import android.os.Process;

/**
 * Created by ${xinGen} on 2018/2/1.
 *
 * 一个线程的超类
 *
 */

public abstract class BaseThread implements Runnable {
    @Override
    public void run() {
        //设置线程的优先级，这里设置后台线程。
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        runTask();
    }
    public abstract  void runTask();
}
