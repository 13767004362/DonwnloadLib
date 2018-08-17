package com.xingen.download.interanl.listener;

/**
 * Author by ${xinGen},  Date on 2018/5/29.
 */
public interface DownloadListener {
     /**
      * 下载成功的回调
      * @param url
      * @param filePath
      */
     void finish(String url, String filePath);
     /**
      * 下载失败的回调
      * @param url
      * @param e
      */
     void error(String url, Exception e);
}
