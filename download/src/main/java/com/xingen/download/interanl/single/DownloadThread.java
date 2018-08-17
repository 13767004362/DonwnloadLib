package com.xingen.download.interanl.single;

import android.os.Process;

import com.xingen.download.common.net.NetWorkUtils;


/**
 * Author by ${xinGen},  Date on 2018/5/29.
 */
public class DownloadThread implements Runnable {
    private SingleDownloadTask downloadTask;
    public DownloadThread(SingleDownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        try {
            downloadTask.setCurrentThread(Thread.currentThread());
            if (Thread.interrupted()||downloadTask.isCancel()){
                 return ;
            }
            NetWorkUtils.download(downloadTask);
            if (Thread.interrupted()||downloadTask.isCancel()){
                   return ;
            }
            downloadTask.deliverResult();
        }catch (Exception e){
            downloadTask.deliverError(e);
        }finally {
            downloadTask.setCurrentThread(null);
            Thread.interrupted();
        }
    }
}
