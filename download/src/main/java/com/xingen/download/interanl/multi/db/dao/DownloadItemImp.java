package com.xingen.download.interanl.multi.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xingen.download.interanl.multi.db.bean.DownloadItemBean;
import com.xingen.download.interanl.multi.db.sqlite.DownloadTaskConstants;
import com.xingen.download.interanl.multi.db.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${xinGen} on 2018/1/6.
 */

public class DownloadItemImp implements BaseDao<DownloadItemBean> {
    private static DownloadItemImp instance;

    private SQLiteOpenHelper dataHelper;

    private DownloadItemImp() {
    }

    public static synchronized DownloadItemImp getInstance() {
        if (instance == null) {
            instance = new DownloadItemImp();
        }
        return instance;
    }

    @Override
    public void init(SQLiteOpenHelper sqLiteOpenHelper) {
        this.dataHelper = sqLiteOpenHelper;
    }

    @Override
    public List<DownloadItemBean> queryAll() {
        return null;
    }

    @Override
    public List<DownloadItemBean> queryAction(String select, String[] selectArg) {
        List<DownloadItemBean> downloadItemList = new ArrayList<>();
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DownloadTaskConstants.TABLE_NAME_DOWNLOAD_ITEM, null, select, selectArg, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    downloadItemList.add(DBUtils.createDownloadItem(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return downloadItemList;
    }

    @Override
    public long insert(DownloadItemBean downloadItem) {
        return 0;
    }

    @Override
    public int bulkInsert(List<DownloadItemBean> list) {
        ContentValues[] contentValueArray = new ContentValues[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            contentValueArray[i] = DBUtils.createContentValues(list.get(i));
        }
        return bulkInsert( DownloadTaskConstants.TABLE_NAME_DOWNLOAD_ITEM, contentValueArray);
    }

    @Override
    public int update(DownloadItemBean downloadItem, String select, String[] selectArg) {
        ContentValues contentValues = DBUtils.createContentValues(downloadItem);
        SQLiteDatabase sqLiteDatabase = dataHelper.getWritableDatabase();
        int updateRow = 0;
        updateRow = sqLiteDatabase.update(DownloadTaskConstants.TABLE_NAME_DOWNLOAD_ITEM, contentValues, select, selectArg);
        return updateRow;
    }

    @Override
    public int delete(String select, String[] selectArg) {
        SQLiteDatabase sqLiteDatabase = this.dataHelper.getWritableDatabase();
        int deleteRow = 0;
        deleteRow = sqLiteDatabase.delete(DownloadTaskConstants.TABLE_NAME_DOWNLOAD_ITEM,  select, selectArg);
        return deleteRow;
    }

    @Override
    public void deleteAll() {

    }
    /**
     * 循环批量插入的动作
     * @param tableName
     * @param values
     * @return
     */
    private int bulkInsert( String tableName, ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = dataHelper.getWritableDatabase();
        int numValues = 0;
        try {
            sqLiteDatabase.beginTransaction();
            for (int i = 0; i < values.length; ++i) {
                sqLiteDatabase.insert(tableName, null, values[i]);
            }
            sqLiteDatabase.setTransactionSuccessful();
            numValues = values.length;
        } catch (Exception e) {
            e.printStackTrace();
            numValues = 0;
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return numValues;
    }
}
