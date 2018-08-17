package com.xingen.downloadtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.xingen.download.DownloadClient;
import com.xingen.download.interanl.listener.DownloadListener;
import com.xingen.download.interanl.listener.ProgressListener;
import com.xingen.download.interanl.multi.task.MultiDownLoadTask;
import com.xingen.download.interanl.single.SingleDownloadTask;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button single_download_btn, multi_download_btn;
    private static final String TAG = MainActivity.class.getSimpleName();
    private MultiDownLoadTask multiDownLoadTask;
    private SingleDownloadTask singleDownloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadClient.getInstance().init(this);
        initView();
    }

    private void initView() {
        this.single_download_btn = findViewById(R.id.main_single_download);
        this.multi_download_btn = findViewById(R.id.main_multi_download);
        this.multi_download_btn.setOnClickListener(this);
        this.single_download_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_single_download:
                if ("单文件下载".equals(single_download_btn.getText().toString())){
                    startSingleDownload();
                    single_download_btn.setText("暂停");
                }else{
                    if (singleDownloadTask!=null){
                        singleDownloadTask.cancel();
                    }
                    single_download_btn.setText("单文件下载");
                }

                break;
            case R.id.main_multi_download:
                if ("断点续传下载".equals(multi_download_btn.getText().toString())){
                    startMultiDownload();
                    multi_download_btn.setText("暂停");
                }else{
                    if (multiDownLoadTask!=null){
                        multiDownLoadTask.cancel();
                    }
                    multi_download_btn.setText("断点续传下载");
                }

                break;
        }
    }

    private void startSingleDownload() {
        //网络的url
        String url1 = "http://115.231.9.79:9096/union_adv/apk/21f2157c8070e17c25d3e5493eb45a50.apk";
        //文件存放路径
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "baidu.apk";
        singleDownloadTask = DownloadClient.getInstance().startSingleDownload(url1, filePath,
                new ProgressListener() {
                    @Override
                    public void progress(String url, int progress) {
                        Log.i(TAG, "   SingleDownload  progress " + progress);
                    }
                },
                new DownloadListener() {
                    @Override
                    public void finish(String url, String filePath) {
                        Log.i(TAG, "   SingleDownload  finish " + filePath);
                    }

                    @Override
                    public void error(String url, Exception e) {
                        Log.i(TAG, "   SingleDownload   error " + e.getMessage());
                    }
                });
    }

    private void startMultiDownload() {
        String url2 = "http://yun.aiwan.hk/1441972507.apk";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "baihewang.apk";
        multiDownLoadTask = DownloadClient.getInstance().startMultiDownLoadTask(url2, filePath,
                new ProgressListener() {
                    @Override
                    public void progress(String url, int progress) {
                        Log.i(TAG, "   MultiDownload   progress " + progress);
                    }
                },
                new DownloadListener() {
                    @Override
                    public void finish(String url, String filePath) {
                        Log.i(TAG, "   MultiDownload   finish " + filePath);
                    }

                    @Override
                    public void error(String url, Exception e) {
                        Log.i(TAG, "   MultiDownload   error " + e.getMessage());
                    }
                });
    }
}
