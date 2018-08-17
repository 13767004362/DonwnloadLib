package com.xingen.download.interanl.multi.client;


import android.util.Log;

import com.xingen.download.interanl.AppExecute;
import com.xingen.download.interanl.listener.DownloadListener;
import com.xingen.download.interanl.listener.ProgressListener;
import com.xingen.download.interanl.multi.task.MultiDownLoadTask;
import com.xingen.download.interanl.single.SingleDownloadTask;


/**
 * Created by ${xinGen} on 2017/12/21.
 * 下载的客户端
 */

public class DownLoadManager {
    private static DownLoadManager instance;
    private final AppExecute threadManager;
    private DatabaseClient databaseClient;
    private static final String TAG = DownLoadManager.class.getSimpleName();
    private DownLoadManager() {
        this.threadManager = AppExecute.getInstance();
        this.databaseClient = DatabaseClient.getInstance();
    }

    public static synchronized DownLoadManager getInstance() {
        if (instance == null) {
            instance = new DownLoadManager();
        }
        return instance;
    }

    /**
     * 断点下载
     * @param url
     * @param filePath
     * @param progressListener
     * @param downloadListener
     * @param isAgain
     * @return
     */

  public  MultiDownLoadTask startMultiDownLoadThread(String url, String filePath, ProgressListener progressListener, DownloadListener downloadListener, boolean isAgain) {
        Log.i(TAG, "start download task 下载地址是：" + url);
        MultiDownLoadTask downLoadTask = new MultiDownLoadTask(threadManager, this);
        downLoadTask.setAgainTask(isAgain);
        downLoadTask.init(url, filePath, progressListener, downloadListener);
        threadManager.executeCalculateTask(downLoadTask.getCalculateThread());
        return downLoadTask;
    }

    /**
     * 单文件下载
     * @param url
     * @param filePath
     * @param progressListener
     * @param downloadListener
     * @return
     */
    public SingleDownloadTask startSingleTask(String url, String filePath, ProgressListener progressListener, DownloadListener downloadListener) {
        SingleDownloadTask downloadTask = new SingleDownloadTask(url, filePath, progressListener, downloadListener, threadManager.getMainExecutor());
        threadManager.executorNetTask(downloadTask.getDownloadThread());
        return downloadTask;
    }


    public DatabaseClient getDatabaseClient() {
        return databaseClient;
    }
}
