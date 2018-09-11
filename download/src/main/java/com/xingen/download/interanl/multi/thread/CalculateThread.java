package com.xingen.download.interanl.multi.thread;

import com.xingen.download.common.net.NetWorkUtils;
import com.xingen.download.common.utils.FileUtils;
import com.xingen.download.common.utils.LOG;
import com.xingen.download.common.utils.StringUtils;
import com.xingen.download.interanl.multi.constants.CommonTaskConstants;
import com.xingen.download.interanl.multi.db.bean.DownloadItemBean;
import com.xingen.download.interanl.multi.db.bean.DownloadTaskBean;
import com.xingen.download.interanl.multi.db.sqlite.DownloadTaskConstants;
import com.xingen.download.interanl.multi.task.MultiDownLoadTask;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.UUID;


/**
 * Created by ${xinGen} on 2018/1/5.
 * <p>
 * 文件的计算线程，用于计算多线程断点下载的数据。
 */

public class CalculateThread extends BaseThread {
    private static final String TAG = CalculateThread.class.getSimpleName();
    private MultiDownLoadTask downLoadTask;

    public CalculateThread(MultiDownLoadTask downLoadTask) {
        this.downLoadTask = downLoadTask;
    }

    @Override
    public void runTask() {
        this.downLoadTask.setCurrentThread(Thread.currentThread());
        RandomAccessFile randomAccessFile = null;
        int state = 0;
        try {
            if (downLoadTask.isCancel()) {
                return;
            }
            String[] args = new String[]{downLoadTask.getDownloadUrl()};
            List<DownloadTaskBean> downloadTaskBeanList = downLoadTask.getDatabaseClient()
                    .queryDownloadTask(StringUtils.createTaskQuerySQL(), args);
            LOG.i(TAG, "数据库中记录 的个数" + downloadTaskBeanList.size());
            if (downLoadTask.isAgainTask()) {
                deleteOldDownLoadTask(downloadTaskBeanList);
            }
            if (downLoadTask.isCancel()){
                return;
            }
            if (downloadTaskBeanList.size() > 0) {
                // 已经下载完成过了。
                if (downloadTaskBeanList.get(0).getState() == CommonTaskConstants.task_download_finish) {
                    LOG.i(TAG, "数据库中查询到任务已经完成： : " + downLoadTask.getDownloadUrl());
                    state = CommonTaskConstants.task_already_download;
                    downLoadTask.getDownloadTaskBean().setFilePath(downloadTaskBeanList.get(0).getFilePath());
                    this.downLoadTask.deliverResult(state);
                } else {
                    List<DownloadItemBean> downloadItemBeanList = downLoadTask.getDatabaseClient().queryDownloadItem(StringUtils.createTaskItemQuerySQL(), args);
                    long fileLength = downloadTaskBeanList.get(0).getDownloadTaskLength();
                    downLoadTask.setDownloadTaskLength(downloadTaskBeanList.get(0).getDownloadTaskLength());
                    this.downLoadTask.getDownloadItemList().addAll(downloadItemBeanList);
                    state = CommonTaskConstants.task_calculate_finish;
                    LOG.i(TAG, "数据库中查询到任务对应的多部分下载的数据 : " + downloadItemBeanList.size() + " 文件长度" + fileLength);
                }
            } else {
                if (Thread.interrupted()) {
                    return;
                }
                HttpURLConnection httpURLConnection = NetWorkUtils.createConnection(downLoadTask.getDownloadUrl());
                if (Thread.interrupted()) {
                    return;
                }
                httpURLConnection.connect();
                if (downLoadTask.isCancel()){
                    httpURLConnection.disconnect();
                    return  ;
                }
                if (httpURLConnection.getResponseCode() == 200) {
                    // rwd,实时写到底层设备
                    randomAccessFile = new RandomAccessFile(new File(this.downLoadTask.getFilePath()), "rwd");
                    long totalLength = httpURLConnection.getContentLength();
                    // 设置文件的大小
                    randomAccessFile.setLength(totalLength);
                    int threadCount = this.downLoadTask.getThreadCount();
                    //计算每个线程需要下载的数据大小,下一个下载起点是上一个下载的终点
                    long averageCount = (totalLength % threadCount == 0) ? (totalLength / threadCount) : (totalLength / threadCount + 1);

                    LOG.i(TAG, "数据库中记录当前的下载任务  : " + this.downLoadTask.getDownloadUrl());
                    for (int i = 0; i < threadCount; ++i) {
                        String threadName = UUID.randomUUID().toString();
                        long startIndex = i * averageCount;
                        long endIndex = (i + 1) * averageCount - 1;
                        if (i == threadCount - 1) {
                            //实际上，最后一个下载模块，结束尾部位置》=文件总长度
                            endIndex = totalLength - 1;
                        }
                        DownloadItemBean downloadItemBean = new DownloadItemBean.Builder()
                                .setThreadName(threadName)
                                .setStartIndex(startIndex)
                                .setCurrentIndex(startIndex)
                                .setEndIndex(endIndex)
                                .setBindTaskId(downLoadTask.getDownloadUrl())
                                .builder();
                        this.downLoadTask.getDownloadItemList().add(downloadItemBean);
                        LOG.i(TAG, "计算线程中计算多模块的下载数据  : " + (i + 1) + " " + startIndex + " " + endIndex + " 文件总长度 " + totalLength);
                    }
                    this.downLoadTask.setDownloadTaskLength(totalLength);
                    this.downLoadTask.getDatabaseClient().insertDownloadTask(downLoadTask.getDownloadTaskBean());
                    this.downLoadTask.getDatabaseClient().insertDownloadItem(downLoadTask.getDownloadItemList());
                    state = CommonTaskConstants.task_calculate_finish;
                } else {
                    LOG.i(TAG, "计算线程中的网络请求失败  : " + this.downLoadTask.getDownloadUrl());
                    state = CommonTaskConstants.task_calculate_failure;
                    this.downLoadTask.deliverResult(state);
                }
                httpURLConnection.getInputStream().close();
                httpURLConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            state = CommonTaskConstants.task_stop_thread;
            downLoadTask.deliverError(e);
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.downLoadTask.setCurrentThread(null);
            this.downLoadTask.handleResult(state);
            Thread.interrupted();
        }
    }

    private void deleteOldDownLoadTask(List<DownloadTaskBean> downloadTaskBeanList) {
        if (downloadTaskBeanList.size() > 0) {
            DownloadTaskBean downloadTaskBean = downloadTaskBeanList.get(0);
            File oldFile = new File(downloadTaskBean.getFilePath());
            FileUtils.deleteFile(oldFile);
            String[] args = new String[]{downloadTaskBean.getDownloadUrl()};
            //删除下载任务中记录
            downLoadTask.getDatabaseClient().deleteDownloadTask(DownloadTaskConstants.COLUMN_DOWNLOAD_URL + "=?", args);
            //删除下载模块中记录
            downLoadTask.getDatabaseClient().deleteDownloadItem(DownloadTaskConstants.COLUMN_BIND_TASK_ID + "=?", args);
            downloadTaskBeanList.clear();
        }
    }
}
