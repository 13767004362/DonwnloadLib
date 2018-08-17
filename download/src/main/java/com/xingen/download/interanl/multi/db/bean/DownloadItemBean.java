package com.xingen.download.interanl.multi.db.bean;

/**
 * Created by ${xinGen} on 2018/1/5.
 * <p>
 * 每个模块下载信息的实体
 */

public class DownloadItemBean {
    private long startIndex;
    private long endIndex;
    private String threadName;
    private String bindTaskId;
    /**
     * 上传的长度
     */
    private long uploadLength;
    /**
     * 当前进度
     */
    private long currentIndex;
    /**
     * 任务完成度
     */
    private volatile int state;

    public  DownloadItemBean(){
    }

    public String getBindTaskId() {
        return bindTaskId;
    }
    public String getThreadName() {
        return threadName;
    }
    public long getStartIndex() {
        return startIndex;
    }
    public long getEndIndex() {
        return endIndex;
    }
    public  void setStartIndex(long startIndex) {
        this.startIndex = startIndex;

    }
    public synchronized long getUploadLength() {
        return uploadLength;
    }
    public  void setUploadLength(long uploadLength) {
        this.uploadLength = uploadLength;
    }
    public  int getState() {
        return state;
    }
    public synchronized void setState(int state) {
        this.state = state;
    }

    public long getCurrentIndex() {
        return currentIndex;
    }
    public void setCurrentIndex(long currentIndex) {
        this.currentIndex = currentIndex;
        setUploadLength(currentIndex-startIndex);
    }

    public  static class Builder {
        private DownloadItemBean downloadItem;
        public Builder() {
            this.downloadItem = new DownloadItemBean();
        }
        public Builder setThreadName(String threadName) {
            this.downloadItem.threadName = threadName;
            return this;
        }
        public Builder setStartIndex(long startIndex) {
            this.downloadItem.startIndex = startIndex;
            return this;
        }
        public Builder setBindTaskId(String bindTaskId) {
            this.downloadItem.bindTaskId = bindTaskId;
            return this;
        }
        public Builder setEndIndex(long endIndex) {
            this.downloadItem.endIndex = endIndex;
            return this;
        }
        public Builder setCurrentIndex(long currentIndex) {
            this.downloadItem.setCurrentIndex(currentIndex);
            return this;
        }
        public Builder setState(int state) {
            this.downloadItem.state = state;
            return this;
        }
        public DownloadItemBean builder() {
            return this.downloadItem;
        }
    }
}
