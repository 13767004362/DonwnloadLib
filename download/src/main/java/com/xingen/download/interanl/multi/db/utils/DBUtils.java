package com.xingen.download.interanl.multi.db.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.xingen.download.interanl.multi.db.bean.DownloadItemBean;
import com.xingen.download.interanl.multi.db.bean.DownloadTaskBean;
import com.xingen.download.interanl.multi.db.sqlite.DownloadTaskConstants;


/**
 * Created by ${xinGen} on 2018/1/6.
 */

public class DBUtils {

    public static DownloadItemBean createDownloadItem(Cursor cursor) {
        return new DownloadItemBean.Builder()
                .setEndIndex(cursor.getInt(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_END_INDEX)))
                .setStartIndex(cursor.getInt(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_START_INDEX)))
                .setCurrentIndex(cursor.getInt(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_CURRENT_INDEX)))
                .setThreadName(cursor.getString(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_THREAD_NAME)))
                .setBindTaskId(cursor.getString(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_BIND_TASK_ID)))
                .setState(cursor.getInt(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_ITEM_STATE)))
                .builder();
    }
    public static DownloadTaskBean createDownloadTask(Cursor cursor) {
        return new DownloadTaskBean.Builder()
                .setDownloadTaskLength(Long.valueOf(cursor.getString(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_TASK_LENGTH))))
                .setDownloadUrl(cursor.getString(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_DOWNLOAD_URL)))
                .setFilePath(cursor.getString(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_WRITE_FILE_PATH)))
                .setState(cursor.getInt(cursor.getColumnIndex(DownloadTaskConstants.COLUMN_STATE)))
                .builder();
    }
    public static ContentValues createContentValues(DownloadItemBean downloadItem) {
        ContentValues contentValues = new ContentValues();
        //url需要转成特殊的字符
        contentValues.put(DownloadTaskConstants.COLUMN_BIND_TASK_ID, downloadItem.getBindTaskId());
        contentValues.put(DownloadTaskConstants.COLUMN_END_INDEX, downloadItem.getEndIndex());
        contentValues.put(DownloadTaskConstants.COLUMN_START_INDEX, downloadItem.getStartIndex());
        contentValues.put(DownloadTaskConstants.COLUMN_CURRENT_INDEX, downloadItem.getCurrentIndex());
        contentValues.put(DownloadTaskConstants.COLUMN_THREAD_NAME, downloadItem.getThreadName());
        contentValues.put(DownloadTaskConstants.COLUMN_ITEM_STATE,downloadItem.getState());
        return contentValues;
    }
    public static ContentValues createContentValues(DownloadTaskBean downLoadTask) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadTaskConstants.COLUMN_DOWNLOAD_URL, downLoadTask.getDownloadUrl());
        contentValues.put(DownloadTaskConstants.COLUMN_WRITE_FILE_PATH, downLoadTask.getFilePath());
        contentValues.put(DownloadTaskConstants.COLUMN_TASK_LENGTH, String.valueOf(downLoadTask.getDownloadTaskLength()) );
        contentValues.put(DownloadTaskConstants.COLUMN_STATE,downLoadTask.getState());
        return contentValues;
    }

}
