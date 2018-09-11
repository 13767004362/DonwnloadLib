package com.xingen.download.common.net;


import android.os.Build;

import com.xingen.download.common.utils.LOG;
import com.xingen.download.interanl.single.SingleDownloadTask;

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

    public static boolean download(SingleDownloadTask downloadTask) throws Exception {
        HttpURLConnection httpURLConnection = createConnection(downloadTask.getUrl());
        try {
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
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
        } catch (Exception e) {
            throw e;
        } finally {
            httpURLConnection.disconnect();
        }
        return true;
    }

    /**
     * 创建默认的HttURLConnection
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        //设置连接时间，60秒
        httpURLConnection.setConnectTimeout(60 * 1000);
        httpURLConnection.setReadTimeout(60 * 1000);
        //设置用户代理
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; " +
                "Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727;" +
                " .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        //设置connection的方式
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
        return httpURLConnection;
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
