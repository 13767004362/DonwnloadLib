package com.xingen.download.interanl.multi.thread;

import com.xingen.download.common.utils.LOG;
import com.xingen.download.common.utils.StringUtils;
import com.xingen.download.interanl.multi.constants.CommonTaskConstants;
import com.xingen.download.interanl.multi.db.bean.DownloadItemBean;
import com.xingen.download.interanl.multi.task.MultiDownLoadTask;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by ${xinGen} on 2017/12/22.
 * <p>
 * <p>
 * 文件多线程断点下载
 */

public class MultiDownloadThread extends BaseThread {
    private static final String TAG = MultiDownloadThread.class.getSimpleName();
    private MultiDownLoadTask downLoadTask;
    private DownloadItemBean downloadItem;

    public MultiDownloadThread(MultiDownLoadTask downLoadTask, DownloadItemBean downloadItemBean) {
        this.downLoadTask = downLoadTask;
        this.downloadItem = downloadItemBean;
    }

    @Override
    public void runTask() {
        LOG.i(TAG, " MultiDownloadThread 开始执行任务");
        OutputStream outputStream = null;
        int state = CommonTaskConstants.task_download_finish;
        RandomAccessFile randomAccessFile = null;
        try {
            if (downLoadTask.isCancel()) {
                return;
            }
            File filePath = new File(downLoadTask.getFilePath());
            randomAccessFile = new RandomAccessFile(filePath, "rwd");
            while (downloadItem.getCurrentIndex() < downloadItem.getEndIndex()) {
                if (downLoadTask.isCancel()) {
                    return;
                }
                long startIndex = downloadItem.getCurrentIndex();
                long endIndex = (startIndex + CommonTaskConstants.EVERY_REQUEST_MAX_LENGTH > downloadItem.getEndIndex()) ?
                        downloadItem.getEndIndex() : startIndex + CommonTaskConstants.EVERY_REQUEST_MAX_LENGTH;
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(downLoadTask.getDownloadUrl()).openConnection();
                httpURLConnection.addRequestProperty(CommonTaskConstants.HEADER_NAME_RANGE, StringUtils.createRangeHeader(startIndex, endIndex));
                httpURLConnection.connect();
                if (downLoadTask.isCancel()) {
                    return;
                }
                //206状态码是部分数据的标识
                if (httpURLConnection.getResponseCode() != 206) {
                    LOG.i(TAG, " MultiDownloadThread 网络执行，但失败响应");
                    state = CommonTaskConstants.task_download_failure;
                    downLoadTask.deliverResult(state);
                    httpURLConnection.disconnect();
                } else {
                    //当前一个下载模块的长度，不是总长度
                    long fileLength = httpURLConnection.getContentLength();
                    LOG.i(TAG, " MultiDownloadThread 网络执行完，开始写入磁盘中" + " 网络流的长度是: " + fileLength);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    //移动到指定位置
                    randomAccessFile.seek(downloadItem.getCurrentIndex());
                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = inputStream.read(buffer)) != -1) {
                        if (downLoadTask.isCancel()) {
                            return;
                        }
                        //断点写入下载文件中
                        randomAccessFile.write(buffer, 0, count);
                        long writeFileLength = downloadItem.getCurrentIndex() + count;
                        //写入数据库，记录下载进度
                        downloadItem.setCurrentIndex(writeFileLength);
                        //当起始位置与终点位置相同的时候，任务执行完成。
                        if (fileLength > 0) {
                            downLoadTask.handlerProgress();
                        }
                        this.downLoadTask.getDatabaseClient().updateDownloadItem(downloadItem, StringUtils.createTaskItemUpdateSQL(), new String[]{downloadItem.getThreadName()});
                        if (downLoadTask.isCancel()) {
                            return;
                        }
                    }
                    inputStream.close();
                    httpURLConnection.disconnect();
                    if (downLoadTask.isCancel()) {
                        return;
                    }
                }
            }
            if (downloadItem.getCurrentIndex() >= downloadItem.getEndIndex()) {
                LOG.i(TAG, "该模块已经下载完成了");
                downloadItem.setState(CommonTaskConstants.task_download_finish);
                this.downLoadTask.getDatabaseClient().updateDownloadItem(downloadItem, StringUtils.createTaskItemUpdateSQL(), new String[]{downloadItem.getThreadName()});
            }
            state = CommonTaskConstants.task_download_finish;
        } catch (Exception e) {
            state = CommonTaskConstants.task_download_error;
            if (e instanceof SecurityException) {
                state = CommonTaskConstants.task_stop_thread;
            }
            downLoadTask.deliverError(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.downLoadTask.handleResult(state);
        }
    }

}
