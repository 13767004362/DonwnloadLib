package com.xingen.download.common.utils;


import com.xingen.download.interanl.multi.constants.CommonTaskConstants;
import com.xingen.download.interanl.multi.db.sqlite.DownloadTaskConstants;

/**
 * Created by ${xinGen} on 2018/1/5.
 */

public class StringUtils {
    /**
     * 创建RangeHeader:
     *
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static String createRangeHeader(long startIndex, long endIndex){
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(CommonTaskConstants.RANGE_VALUES_REG_BYTE);
        stringBuffer.append(startIndex);
        stringBuffer.append("-");
        stringBuffer.append(endIndex);
        return stringBuffer.toString();
    }
    public static String createTaskQuerySQL(){
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(DownloadTaskConstants.COLUMN_DOWNLOAD_URL);
        stringBuffer.append("=?");
        return stringBuffer.toString();
    }
    public static String createTaskItemQuerySQL(){
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(DownloadTaskConstants.COLUMN_BIND_TASK_ID);
        stringBuffer.append("=?");
        return stringBuffer.toString();
    }
    public static String createTaskItemUpdateSQL(){
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(DownloadTaskConstants.COLUMN_THREAD_NAME);
        stringBuffer.append("=?");
        return stringBuffer.toString();
    }

    /**
     * 转换特殊字符
     * @return
     */
    public static String conversionString(String origin){
        return  String.format("'%s'",origin);
    }
}
