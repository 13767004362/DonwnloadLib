package com.xingen.download.interanl.multi.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xingen.download.interanl.multi.db.bean.DownloadTaskBean;
import com.xingen.download.interanl.multi.db.sqlite.DownloadTaskConstants;
import com.xingen.download.interanl.multi.db.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${xinGen} on 2018/1/6.
 */

public class DownloadTaskImp implements BaseDao<DownloadTaskBean>{

    private static DownloadTaskImp instance;
   private SQLiteOpenHelper dataHelper;
    private DownloadTaskImp(){}
    public static synchronized  DownloadTaskImp getInstance(){
        if (instance==null){
            instance=new DownloadTaskImp();
        }
        return instance;
    }
    @Override
    public void init(SQLiteOpenHelper sqLiteOpenHelper) {
        this.dataHelper=sqLiteOpenHelper;
    }
    @Override
    public List<DownloadTaskBean> queryAll() {
        return null;
    }
    @Override
    public List<DownloadTaskBean> queryAction(String select, String[] selectArg) {
        List<DownloadTaskBean> downloadItemList = new ArrayList<>();
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DownloadTaskConstants.TABLE_NAME_DOWNLOAD_TASK, null, select, selectArg,null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    downloadItemList.add(DBUtils.createDownloadTask(cursor));
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
    public long insert(DownloadTaskBean downloadTaskBean) {
        SQLiteDatabase sqLiteDatabase = dataHelper.getWritableDatabase();
        long rowId = sqLiteDatabase.insert(DownloadTaskConstants.TABLE_NAME_DOWNLOAD_TASK, null, DBUtils.createContentValues(downloadTaskBean));
        return rowId ;
    }

    @Override
    public int bulkInsert(List<DownloadTaskBean> list) {
        return 0;
    }

    @Override
    public int update(DownloadTaskBean downloadTaskBean, String select, String[] selectArg) {
        SQLiteDatabase sqLiteDatabase = dataHelper.getWritableDatabase();
        int updateRow = 0;
        updateRow = sqLiteDatabase.update(DownloadTaskConstants.TABLE_NAME_DOWNLOAD_TASK,DBUtils.createContentValues(downloadTaskBean),select,selectArg);
        return updateRow;
    }
    @Override
    public int delete(String select, String[] selectArg) {
        SQLiteDatabase sqLiteDatabase = this.dataHelper.getWritableDatabase();
        int deleteRow = 0;
        deleteRow = sqLiteDatabase.delete(DownloadTaskConstants.TABLE_NAME_DOWNLOAD_TASK, select,selectArg);
        return  deleteRow;
    }
    @Override
    public void deleteAll() {


    }
}
