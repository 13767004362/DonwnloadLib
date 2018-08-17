package com.xingen.download.common.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Author by ${xinGen},  Date on 2018/5/29.
 */
public class FileUtils {
    public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取缓存路径
     *
     * @param context
     * @return 返回缓存文件路径
     */
    public static File getCacheDir(Context context) {
        File cache;
        boolean permission=false;
        boolean isExistSDCard = hasExternalStorage();
        if (isExistSDCard) {
            permission = hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission) {
                cache = getExternalCacheDir(context);
            } else {
                cache = getInternalCacheDir(context);
            }
        } else {
            cache = getInternalCacheDir(context);
        }
        if (!cache.exists()) {
            cache.mkdirs();
        }
        LOG.i("Download File", " 是否存在sdcard : " + isExistSDCard + " 是否具备写入sdcard的权限: "+permission + " 缓存磁盘的路径是: " + cache.getAbsolutePath());
        return cache;
    }


    /**
     * 获取外部sdcard中缓存目录
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 获取内部存储
     *
     * @param context
     * @return
     */
    public static File getInternalCacheDir(Context context) {
        return context.getCacheDir();
    }

    public static File getInternalCacheFile(Context context, String fileName) {
        File dir = getInternalCacheDir(context);
        return new File(dir + File.separator + fileName);
    }

    /**
     * 检查权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(Context context, String permission) {
        boolean isHasPermission = false;
        if (Build.VERSION.SDK_INT < 23) {
            isHasPermission = (PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission(permission, context.getPackageName()));
        } else {
            //app的目标版本大于23，才使用Context.checkSelfPermission()
            if (getAppTargetVersion(context) >= Build.VERSION_CODES.M) {
                int permissionCode = context.checkSelfPermission(permission);
                isHasPermission = (permissionCode == PackageManager.PERMISSION_GRANTED);
            } else {
                try {
                    Class ContextCompat = Class.forName("android.support.v4.content.ContextCompat");
                    Method checkSelfPermission = ContextCompat.getDeclaredMethod("checkSelfPermission", Context.class, String.class);
                    checkSelfPermission.setAccessible(true);
                    int permissionCode = (int) checkSelfPermission.invoke(null, context, permission);
                    isHasPermission = (permissionCode == PackageManager.PERMISSION_GRANTED);
                } catch (Exception e) {
                   // e.printStackTrace();
                }
            }

        }
        return isHasPermission;
    }


    /**
     * 获取app的目标版本
     *
     * @param context
     * @return
     */
    public static int getAppTargetVersion(Context context) {
        int version = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            version = applicationInfo.targetSdkVersion;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 从url中获取到最后的文件名，包含文件的后缀名
     *
     * @param url
     * @return
     */
    public static String obtainFileName(String url) {
        String[] s = url.split("/");
        return s[s.length - 1];
    }

    public static String getCacheFile(Context context, String url) {
        File dir = FileUtils.getCacheDir(context);
        File file = new File(dir + File.separator + "download");
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = new File(file + File.separator + FileUtils.obtainFileName(url)).getAbsolutePath();
        return filePath;
    }

    /**
     * 删除文件
     * @param file
     * @return
     */
    public static boolean deleteFile(File file){
        boolean finish=false;
        try {
            if (file!=null&&file.exists()){
                if (file.isFile()){
                    file.delete();
                    finish=true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return finish;
    }

}
