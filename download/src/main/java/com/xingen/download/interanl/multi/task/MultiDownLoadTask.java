package com.xingen.download.interanl.multi.task;

import android.util.Log;

import com.xingen.download.common.utils.LOG;
import com.xingen.download.common.utils.StringUtils;
import com.xingen.download.interanl.AppExecute;
import com.xingen.download.interanl.listener.DownloadListener;
import com.xingen.download.interanl.listener.ProgressListener;
import com.xingen.download.interanl.multi.client.DatabaseClient;
import com.xingen.download.interanl.multi.client.DownLoadManager;
import com.xingen.download.interanl.multi.constants.CommonTaskConstants;
import com.xingen.download.interanl.multi.db.bean.DownloadItemBean;
import com.xingen.download.interanl.multi.db.bean.DownloadTaskBean;
import com.xingen.download.interanl.multi.thread.CalculateThread;
import com.xingen.download.interanl.multi.thread.MultiDownloadThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * Created by ${xinGen} on 2017/12/22.
 */
public class MultiDownLoadTask {
    private final String TAG = MultiDownLoadTask.class.getSimpleName();
    private DownloadTaskBean downloadTaskBean;
    /**
     * 当前线程对象
     */
    private Thread currentThread;

    /**
     * 计算的Runnable接口
     */
    private CalculateThread calculateThread;
    /**
     * 线程管理类
     */
    private AppExecute threadManager;
    /**
     * 数据库操作类
     */
    private DatabaseClient databaseClient;
    /**
     * 线程个数
     */
    private int threadCount;
    /**
     * 下载线程中的下载任务列表
     */
    private List<DownloadItemBean> downloadItemList;
    /**
     * 是否重新下载的标志：先删除文件和数据库记录
     */
    private boolean isAgainTask;
    /**
     * 下载的多线程
     */
    private ExecutorService executorService;
    /**
     * 是否取消的标志
     */
    private volatile boolean isCancel;

    private ProgressListener progressListener;
    private DownloadListener downloadListener;

    public MultiDownLoadTask(AppExecute threadManager, DownLoadManager downLoadClient) {
        this.threadManager = threadManager;
        this.databaseClient = downLoadClient.getDatabaseClient();
        this.downloadItemList = new ArrayList<>();
        this.calculateThread = new CalculateThread(this);
        this.threadCount = CommonTaskConstants.DOWNLOAD_THREAD_ACCOUNT;
    }

    /**
     * @param downloadUrl
     * @param filePath
     * @param progressListener
     */
    public void init(String downloadUrl, String filePath, ProgressListener progressListener, DownloadListener downloadListener) {
        this.downloadTaskBean = new DownloadTaskBean.Builder().setDownloadUrl(downloadUrl).setFilePath(filePath).builder();
        this.progressListener = progressListener;
        this.downloadListener = downloadListener;
    }

    public long getDownloadTaskLength() {
        return this.downloadTaskBean.getDownloadTaskLength();
    }

    public void setDownloadTaskLength(long totalLength) {
        this.downloadTaskBean.setDownloadTaskLength(totalLength);
    }

    public synchronized boolean isCancel() {
        return isCancel;
    }

    private synchronized void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public void cancel() {
        setCancel(true);
        try {
            if (getCurrentThread() != null) {
                getCurrentThread().interrupt();
            }
            if (executorService != null) {
                executorService.shutdownNow();
                executorService = null;
            }
            releaseResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DownloadTaskBean getDownloadTaskBean() {
        return this.downloadTaskBean;
    }

    public Thread getCurrentThread() {
        synchronized (this) {
            return currentThread;
        }
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public String getDownloadUrl() {
        return this.downloadTaskBean.getDownloadUrl();
    }

    public String getFilePath() {
        return this.downloadTaskBean.getFilePath();
    }

    public CalculateThread getCalculateThread() {
        return calculateThread;
    }

    public List<DownloadItemBean> getDownloadItemList() {
        return downloadItemList;
    }

    public void setDownloadItemList(List<DownloadItemBean> downloadItemList) {
        this.downloadItemList = downloadItemList;
    }

    public int getItemSize() {
        return this.downloadItemList.size();
    }

    public boolean isAgainTask() {
        return isAgainTask;
    }

    public void setAgainTask(boolean againTask) {
        isAgainTask = againTask;
    }

    /**
     * 记住当前执行线程的Id
     *
     * @param currentThread
     */
    public void setCurrentThread(Thread currentThread) {
        synchronized (this) {
            this.currentThread = currentThread;
        }
    }

    /**
     * 释放资源
     */
    public void releaseResource() {
        this.downloadItemList.clear();
        this.downloadTaskBean = null;
        this.isAgainTask = false;
        this.progressListener = null;
        this.downloadListener = null;
    }

    /**
     * 处理 结果
     */
    public void handleResult(int state) {
        if (isCancel()) {
            return;
        }
        switch (state) {
            case CommonTaskConstants.task_download_finish:
                handlerResult();
                break;
            case CommonTaskConstants.task_already_download:
                releaseResource();
                break;
            case CommonTaskConstants.task_calculate_finish:
                startMultiDownloadThread();
                break;
            case CommonTaskConstants.task_download_failure:
            case CommonTaskConstants.task_calculate_failure:
            case  CommonTaskConstants.task_stop_thread:
            default:
                releaseResource();
                break;
        }

    }

    public void handlerProgress() {
        int total = 0;
        for (DownloadItemBean downloadItemBean : downloadItemList) {
            total += downloadItemBean.getUploadLength();
        }
        if (isCancel()) {
            return;
        }
        //计算出剩余还剩下多少，然后100- 剩余进度=上传进度
        final int progress = (int) (((total) * 100) / downloadTaskBean.getDownloadTaskLength());
        LOG.i(TAG, " 当前线程 " + Thread.currentThread().getName() + " 下载的进度： " + progress);
       deliverProgress(progress);
    }
    public void deliverResult(int state){
              if (isCancel()){
                  return ;
              }
              switch (state){
                  case  CommonTaskConstants.task_already_download:
                      handlerResult();
                      break;
                  case CommonTaskConstants.task_calculate_failure:
                      deliverError(new Exception("计算线程中，计算文件大小失败"));
                  case CommonTaskConstants.task_download_failure:
                      deliverError(new Exception("网络线程中，下载文件模块失败"));
                      break;
              }
    }

    private void handlerResult() {
        int finish = 0;
        for (DownloadItemBean downloadItemBean : downloadItemList) {
            if (downloadItemBean.getState() == CommonTaskConstants.task_download_finish) {
                finish++;
            }
        }
        if (isCancel()) {
            return;
        }
        LOG.i(TAG, " 当前线程" + Thread.currentThread().getName() + "handlerResult " + finish + " 任务的个数 " + downloadItemList.size());
        if (finish == downloadItemList.size()) {
           deliverResult();
            //更新task的记录
            getDownloadTaskBean().setState(CommonTaskConstants.task_download_finish);
            getDatabaseClient().updateDownloadTask(getDownloadTaskBean(), StringUtils.createTaskQuerySQL(), new String[]{getDownloadUrl()});
            //删除对应的item记录
            getDatabaseClient().deleteDownloadItem(StringUtils.createTaskItemQuerySQL(), new String[]{getDownloadUrl()});
            Log.i(TAG, " MultiDownloadThread 写入磁盘操作完成");
            releaseResource();
        }
    }
    private void deliverProgress(final int progress){
        this.threadManager.executorUITask(new Runnable() {
            @Override
            public void run() {
                if (progressListener!=null&&!isCancel()){
                    progressListener.progress(getDownloadUrl(),progress);
                }
            }
        });
    }
    private void deliverResult(){
        this.threadManager.executorUITask(new Runnable() {
            @Override
            public void run() {
                if (downloadListener!=null&&!isCancel()){
                    downloadListener.finish(getDownloadUrl(),getFilePath());
                }
            }
        });
    }
public  void deliverError(final Exception e){
        this.threadManager.executorUITask(new Runnable() {
            @Override
            public void run() {
                if (downloadListener!=null&&!isCancel()){
                    downloadListener.error(getDownloadUrl(),e);
                }
            }
        });
    }

    public DatabaseClient getDatabaseClient() {
        return databaseClient;
    }

    /**
     * 开启下载任务
     */
    private void startMultiDownloadThread() {
        if (isCancel()) {
            return;
        }
        executorService = threadManager.createThreadPool(AppExecute.single_file_down_thread_size);
        for (DownloadItemBean downloadItemBean : downloadItemList) {
            if (downloadItemBean.getState() != CommonTaskConstants.task_download_finish) {
                executorService.execute(new MultiDownloadThread(this, downloadItemBean));
            }
        }
    }
}
