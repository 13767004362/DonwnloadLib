package com.xingen.download.interanl.listener;

/**
 * Author by ${xinGen},  Date on 2018/5/29.
 */
public interface ProgressListener {
    /**
     * 下载文件的进度回调
     * @param url
     * @param progress
     */
    void progress(String url, int progress);
}
