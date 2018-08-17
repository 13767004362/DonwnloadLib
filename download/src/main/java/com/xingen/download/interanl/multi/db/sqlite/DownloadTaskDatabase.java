package com.xingen.download.interanl.multi.db.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ${xinGen} on 2018/1/5.
 */
public class DownloadTaskDatabase extends SQLiteOpenHelper {
    private static final String TAG=DownloadTaskDatabase.class.getSimpleName();
    /**
     * 创建下载任务的表 的sql语句
     */
    public static final String CREATE_DOWNLOAD_TASK = "create table " +
            DownloadTaskConstants.TABLE_NAME_DOWNLOAD_TASK + "(" +
            DownloadTaskConstants._ID + " integer primary key autoincrement," +
            DownloadTaskConstants.COLUMN_DOWNLOAD_URL + " text," +
            DownloadTaskConstants.COLUMN_WRITE_FILE_PATH + " text," +
            DownloadTaskConstants.COLUMN_TASK_LENGTH + " text," +
            DownloadTaskConstants.COLUMN_STATE + " integer"
            + ")";
    /**
     * 创建多部分下载的表的sql语句
     */
    public static final String CREATE_DOWNLOAD_ITEM = "create table " +
            DownloadTaskConstants.TABLE_NAME_DOWNLOAD_ITEM + "(" +
            DownloadTaskConstants._ID + " integer primary key autoincrement," +
            DownloadTaskConstants.COLUMN_START_INDEX + " integer," +
            DownloadTaskConstants.COLUMN_END_INDEX + " integer," +
            DownloadTaskConstants.COLUMN_CURRENT_INDEX + " integer," +
            DownloadTaskConstants.COLUMN_ITEM_STATE + " integer," +
            DownloadTaskConstants.COLUMN_THREAD_NAME + " text," +
            DownloadTaskConstants.COLUMN_BIND_TASK_ID + " text"
            + ")";
    public DownloadTaskDatabase(Context context) {
        super(context, DownloadTaskConstants.SQLITE_NAME, null, DownloadTaskConstants.SQLITE_VERSON);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
      //  Log.i(TAG, "下载任务的数据库执行 onCreate()");
        db.execSQL(CREATE_DOWNLOAD_TASK);
        db.execSQL(CREATE_DOWNLOAD_ITEM);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
