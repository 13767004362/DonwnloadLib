package com.xingen.download.interanl.multi.client;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.xingen.download.interanl.multi.db.bean.DownloadItemBean;
import com.xingen.download.interanl.multi.db.bean.DownloadTaskBean;
import com.xingen.download.interanl.multi.db.dao.BaseDao;
import com.xingen.download.interanl.multi.db.dao.DownloadItemImp;
import com.xingen.download.interanl.multi.db.dao.DownloadTaskImp;
import com.xingen.download.interanl.multi.db.sqlite.DownloadTaskDatabase;

import java.util.List;

/**
 * Created by ${xinGen} on 2018/1/6.
 * <p>
 * 数据库的统一管理入口
 */

public class DatabaseClient {
    public static DatabaseClient instance;
    private BaseDao<DownloadItemBean> downloadItemDao;
    private BaseDao<DownloadTaskBean> downloadTaskDao;
    private SQLiteOpenHelper sqLiteOpenHelper;
    private DatabaseClient() {
        this.downloadTaskDao = DownloadTaskImp.getInstance();
        this.downloadItemDao = DownloadItemImp.getInstance();
    }
    public static synchronized DatabaseClient getInstance() {
        if (instance == null) {
            instance = new DatabaseClient();
        }
        return instance;
    }
    public void init(Context appContext) {
        if (sqLiteOpenHelper==null){
            this.sqLiteOpenHelper=new DownloadTaskDatabase(appContext);
            this.downloadItemDao.init(this.sqLiteOpenHelper);
            this.downloadTaskDao.init(this.sqLiteOpenHelper);
        }
    }
    public void insertDownloadItem(List<DownloadItemBean> downloadItemBeans) {
        this.downloadItemDao.bulkInsert(downloadItemBeans);
    }
    public long insertDownloadTask(DownloadTaskBean downloadTaskBean) {
        return this.downloadTaskDao.insert(downloadTaskBean);
    }
    public List<DownloadTaskBean> queryDownloadTask(String select, String[] arg){
        return this.downloadTaskDao.queryAction(select,arg);
    }
    public List<DownloadItemBean> queryDownloadItem(String select, String[] arg){
        return this.downloadItemDao.queryAction(select,arg);
    }
    public  void updateDownloadItem(DownloadItemBean downloadItemBean, String select, String[] arg){
        this.downloadItemDao.update(downloadItemBean,select,arg);
    }
    public void updateDownloadTask(DownloadTaskBean downloadTaskBean, String select, String[] arg){
        this.downloadTaskDao.update(downloadTaskBean,select,arg);
    }
    public void deleteDownloadTask(String select, String[] arg){
        this.downloadTaskDao.delete(select,arg);
    };
    public void deleteDownloadItem(String select, String[] arg){
        this.downloadItemDao.delete(select,arg);
    }
}
