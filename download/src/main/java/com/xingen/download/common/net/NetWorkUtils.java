package com.xingen.download.common.net;


import com.xingen.download.interanl.single.SingleDownloadTask;
import com.xingen.download.common.utils.LOG;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author by ${xinGen},  Date on 2018/5/29.
 */
public class NetWorkUtils {
    private static final String TAG = "NetWorkUtils";

    public static boolean download(SingleDownloadTask downloadTask) throws IOException, Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(downloadTask.getUrl()).openConnection();
        //设置连接时间，10秒
        httpURLConnection.setConnectTimeout(10 * 1000);
        httpURLConnection.setReadTimeout(10 * 1000);
        httpURLConnection.connect();
        if (httpURLConnection.getResponseCode() == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            File file = new File(downloadTask.getFilePath());
            FileOutputStream outputStream = new FileOutputStream(file);
            int fileLength = httpURLConnection.getContentLength();
            byte[] buffer = new byte[4096];
            int count;
            long total = 0;
            while ((count = inputStream.read(buffer)) != -1) {
                if (downloadTask.isCancel()) {
                    outputStream.close();
                    inputStream.close();
                    file.deleteOnExit();
                    throw new Exception("SingleDownloadTask cancel 被取消");
                }
                outputStream.write(buffer, 0, count);
                total += count;
                if (fileLength > 0) {
                    int progress = (int) ((float) total * 100 / fileLength);
                    downloadTask.deliverProgress(progress);
                }
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
            LOG.i(TAG, " 下载完成： " + downloadTask.getUrl() + " " + downloadTask.getFilePath());
        } else {
            InputStream inputStream = httpURLConnection.getErrorStream();
            String error = streamToString(inputStream);
            LOG.i(TAG, " 下载异常： " + downloadTask.getUrl());
            throw new Exception(error);
        }
        httpURLConnection.disconnect();
        return true;
    }

    /**
     * 将stream转成String
     *
     * @param inputStream
     * @return
     */
    private static String streamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        String result = null;
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int length;
            while ((length = bufferedInputStream.read(b)) > 0) {
                byteArrayOutputStream.write(b, 0, length);
            }
            result = byteArrayOutputStream.toString("utf-8");
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
