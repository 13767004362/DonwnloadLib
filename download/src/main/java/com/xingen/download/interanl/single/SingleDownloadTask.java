package com.xingen.download.interanl.single;


import com.xingen.download.interanl.listener.DownloadListener;
import com.xingen.download.interanl.listener.ProgressListener;

import java.util.concurrent.Executor;

/**
 * Author by ${xinGen},  Date on 2018/5/29.
 */
public class SingleDownloadTask {

    private String url;
    private String filePath;
    /**
     * 当前任务执行所在的线程
     */
    protected Thread currentThread;
    /**
     * 是否取消
     */
    protected boolean isCancel;

    private ProgressListener progressListener;
    private DownloadListener downloadListener;

    private Executor mainExecutor;
    private DownloadThread downloadThread;

    public SingleDownloadTask(String url, String filePath, ProgressListener progressListener, DownloadListener downloadListener, Executor mainExecutor) {
        this.url = url;
        this.filePath = filePath;
        this.progressListener = progressListener;
        this.downloadListener = downloadListener;
        this.mainExecutor = mainExecutor;
        this.downloadThread = new DownloadThread(this);
    }

    public synchronized boolean isCancel() {
        return isCancel;
    }

    private synchronized void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public synchronized Thread getCurrentThread() {
        return currentThread;
    }

    public synchronized void setCurrentThread(Thread currentThread) {
        this.currentThread = currentThread;
    }

    public void cancel() {
        this.setCancel(true);
        if (getCurrentThread() != null) {
            getCurrentThread().interrupt();
        }
    }

    public void deliverResult() {
        if (isCancel() || downloadListener == null) {
            return;
        }
        mainExecutor.execute(new Runnable() {
            @Override
            public void run() {
                downloadListener.finish(url, filePath);
            }
        });

    }

    public void deliverError(final Exception e) {
        if (isCancel() || downloadListener == null) {
            return;
        }
        mainExecutor.execute(new Runnable() {
            @Override
            public void run() {
                downloadListener.error(url, e);
            }
        });
    }

    public void deliverProgress(final int progress) {
        if (isCancel() || progressListener == null) {
            return;
        }
        mainExecutor.execute(new Runnable() {
            @Override
            public void run() {
                progressListener.progress(url, progress);
            }
        });
    }

    public String getUrl() {
        return url;
    }

    public String getFilePath() {
        return filePath;
    }

    public DownloadThread getDownloadThread() {
        return downloadThread;
    }
}
