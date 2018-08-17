package com.xingen.download.interanl.multi.db.sqlite;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ${xinGen} on 2018/1/5.
 */

public final class DownloadTaskConstants implements BaseColumns {
    /**
     * 数据库信息
     */
    public static final String SQLITE_NAME="downloadTask.db";
    public static final int SQLITE_VERSON=1;
    /**
     * 下载表，及其字段
     */
    public static final String TABLE_NAME_DOWNLOAD_TASK="downloadTask";
    public static final String COLUMN_DOWNLOAD_URL="downloadUrl";
    public static final String COLUMN_WRITE_FILE_PATH="filePath";
    public static final String COLUMN_STATE="state";
    public static final String COLUMN_TASK_LENGTH="taskLength";
    /**
     * 模块下载，及其字段
     */
    public static final String TABLE_NAME_DOWNLOAD_ITEM="downloadItem";
    public static final String COLUMN_BIND_TASK_ID="taskId";
    public static final String COLUMN_THREAD_NAME="threadName";
    public static final String COLUMN_START_INDEX="startIndex";
    public static final String COLUMN_END_INDEX="endIndex";
    public static final String COLUMN_ITEM_STATE="itemState";
    public static final String COLUMN_CURRENT_INDEX="currentIndex";


    /**
     * ContentProvider的authorities
     */
    public static final String AUTHORITY="com.zongke.downloadservice.db.sqlite.DownloadTaskProvider";
    /**
     * Scheme
     */
    public static final String SCHEME="content";
    /**
     *  ContentProvider的URI
     */
    public static final Uri CONTENT_URI= Uri.parse(SCHEME+"://"+AUTHORITY);
    /**
     * downloadTask表的URI
     */
    public static final Uri URI_DOWNLOAD_TASK= Uri.withAppendedPath(CONTENT_URI,TABLE_NAME_DOWNLOAD_TASK);
    /**
     * downloadItem表的URI
     */
    public static final Uri URI_DOWNLOAD_ITEM= Uri.withAppendedPath(CONTENT_URI,TABLE_NAME_DOWNLOAD_ITEM);
}
