package com.xingen.download.interanl.multi.constants;

/**
 * Created by ${xinGen} on 2017/12/22.
 */

public final  class CommonTaskConstants {
    /**
     * 停止线程
     */
    public static final int task_stop_thread = 110;
    /**
     * 任务完成
     */
    public static final int task_download_finish = 111;
    /**
     * 任务进行中
     */
    public static final int task_download_progress =112;
    /**
     * 任务失败
     */
    public  static final int task_download_failure =113;
    /**
     * 计算任务完成
     */
    public static final int task_calculate_finish=114;
    /**
     * 计算任务失败
     */
    public static final int task_calculate_failure=115;

    /**
     * 任务已经下载完成
     */
    public static final  int task_already_download=116;

    /**
     * 任务下载异常
     */
    public static final  int task_download_error=117;
    /**
     * 下载线程的个数，这里默认是3个
     */
    public static final int DOWNLOAD_THREAD_ACCOUNT=3;

    public  static final String HEADER_NAME_RANGE="Range";
    /**
     * range对应values的前缀
     */
    public static final String RANGE_VALUES_REG_BYTE="bytes=";
    /**
     * 每次请求获取最大的byte[]的长度，放置超大文件，获取过多。
     * 这里，设置2M.
     */
    public static  final  long EVERY_REQUEST_MAX_LENGTH=2*1024*1024;
    /**
     * 直接下载的模式
     */
    public static final int mode_single=1;
    /**
     * 断点续传的模式
     */
    public static  final  int mode_multi=2;

}
