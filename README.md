# DonwnloadLib
一个网络下载库，支持单文件下载，断点续传下载，支持暂停，恢复，进度监听.

#### **前期工作**

在项目的build.gradle中依赖：
```
compile 'com.xingen:download:1.0.0'
```


**添加相关的权限**：

添加联网权限
```java
    <uses-permission android:name="android.permission.INTERNET"></uses-permission>
```
若是文件存放到sdcard需要添加，读写权限：
```java
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
```



#### **使用介绍**

**1.初始化：**
```
   DownloadClient.getInstance().init(this);
```
例如：在Application子类中，进行初始化操作。


**2. 在单线程中单文件的下载：**

调用startSingleDownload(),传入下载地址，磁盘文件存放地址，进度监听器，下载状态监听器。

```java
    private void startSingleDownload() {
        //网络的url
        String url1 = "http://imtt.dd.qq.com/16891/08637F2F36C0225E9C9BE8EAFE668B59.apk?fsname=com.shoujiduoduo.ringtone_8.7.15.0_60087150.apk";
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
```

**3. 在多线程中文件的断点续传下载：**

调用startMultiDownLoadTask(),传入下载地址，磁盘文件存放地址，进度监听器，下载状态监听器。
```java
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
```




License
-------

    Copyright 2018 HeXinGen.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
