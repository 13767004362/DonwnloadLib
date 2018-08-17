package com.xingen.download;

import android.content.Context;

import com.xingen.download.common.utils.FileUtils;
import com.xingen.download.interanl.listener.DownloadListener;
import com.xingen.download.interanl.listener.ProgressListener;
import com.xingen.download.interanl.multi.client.DatabaseClient;
import com.xingen.download.interanl.multi.client.DownLoadManager;
import com.xingen.download.interanl.multi.task.MultiDownLoadTask;
import com.xingen.download.interanl.single.SingleDownloadTask;


/**
 * Author by ${xinGen},  Date on 2018/5/29.
 * <p>
 * 下载文件的Client
 */
public class DownloadClient {
    private static DownloadClient instance;
    private DownLoadManager downloadManager;
    private Context appContext;

    static {
        instance = new DownloadClient();
    }

    private DownloadClient() {
        downloadManager = DownLoadManager.getInstance();
    }

    public static DownloadClient getInstance() {
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.appContext = context.getApplicationContext();
        DatabaseClient.getInstance().init(this.appContext);
    }
    /**
     * 下载单文件
     *
     * @param url
     * @param downloadListener
     * @return
     */
    public SingleDownloadTask startSingleDownload(String url, DownloadListener downloadListener) {
        String filePath = FileUtils.getCacheFile(appContext, url);
        return startSingleDownload(url, filePath, null, downloadListener);
    }

    /**
     * 下载单文件
     *
     * @param url
     * @param filePath
     * @param progressListener
     * @param downloadListener
     * @return
     */
    public SingleDownloadTask startSingleDownload(String url, String filePath, ProgressListener progressListener, DownloadListener downloadListener) {
        return downloadManager.startSingleTask(url, filePath, progressListener, downloadListener);
    }

    /**
     * 断点续传下载
     *
     * @param url
     * @param filePath
     * @param progressListener
     * @param downloadListener
     * @return
     */
    public MultiDownLoadTask startMultiDownLoadTask(String url, String filePath, ProgressListener progressListener, DownloadListener downloadListener) {
        return downloadManager.startMultiDownLoadThread(url, filePath, progressListener, downloadListener,false);
    }

    /**
     * 删除旧文件，重新断点下载
     *
     * @param url
     * @param filePath
     * @param progressListener
     * @param downloadListener
     * @return
     */
    public MultiDownLoadTask againStartMultiDownLoadTask(String url, String filePath, ProgressListener progressListener, DownloadListener downloadListener) {
        return downloadManager.startMultiDownLoadThread(url, filePath, progressListener, downloadListener,true);
    }
}
