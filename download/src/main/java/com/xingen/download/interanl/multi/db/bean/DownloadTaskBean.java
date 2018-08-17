package com.xingen.download.interanl.multi.db.bean;

/**
 * Created by ${xinGen} on 2018/1/6.
 */

public class DownloadTaskBean {
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 文件存储路径
     */
    private String filePath;
    /**
     * 下载任务文件的总长度
     */
    private long downloadTaskLength;
    /**
     * 是否完成
     */
    private int state;
    public int getState() {
        return state;
    }
    public String getDownloadUrl() {
        return downloadUrl;
    }
    public String getFilePath() {
        return filePath;
    }
    public long getDownloadTaskLength() {
        return downloadTaskLength;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDownloadTaskLength(long downloadTaskLength) {
        this.downloadTaskLength = downloadTaskLength;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static class  Builder {
        private DownloadTaskBean downloadTaskBean;
        public Builder(){
            this.downloadTaskBean=new DownloadTaskBean();
        }
        public Builder setState(int state) {
            this.downloadTaskBean.state = state;
            return this;
        }
        public Builder setDownloadUrl(String downloadUrl) {
            this.downloadTaskBean.downloadUrl = downloadUrl;
            return this;
        }
        public Builder setFilePath(String filePath) {
            this.downloadTaskBean.filePath = filePath;
            return this;
        }
        public Builder setDownloadTaskLength(long downloadTaskLength) {
            this.downloadTaskBean.downloadTaskLength = downloadTaskLength;
            return this;
        }
        public  DownloadTaskBean builder(){
            return this.downloadTaskBean;
        }
    }
}
