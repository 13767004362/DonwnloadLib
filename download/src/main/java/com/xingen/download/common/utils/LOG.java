package com.xingen.download.common.utils;

import android.util.Log;

/**
 * Author by {xinGen}
 * Date on 2018/7/23 18:41
 */
public class LOG {
    public  static final  int level_i=1;
    public  static final  int level_nothing=-1;
    public static int  current_level=level_nothing;
    public static void i(String tag,String content){
        if (current_level>level_nothing){
            Log.i(tag,content);
        }
    }
}
