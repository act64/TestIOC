package com.jkyssocial.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/29
 * Time: 00:14
 * email: AndroidZern@163.com
 */
//解决Toast重复出现的情况
public class ZernToast {

    private static Toast mToast = null ;

    public static void showToast(Context context ,String msgInfo){
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(),msgInfo,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msgInfo);
        }
        mToast.show();
    }

    // 设置Toast显示的位置
    public static void showToast(Context context ,String msgInfo,int gravity, int xOffset, int yOffset){
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(),msgInfo,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msgInfo);
        }
        mToast.setGravity(gravity, xOffset, yOffset);
        mToast.show();
    }


    // 设置Toast显示的时间
    public static void showToastForTime(Context context ,String msgInfo,int gravity, int xOffset, int yOffset,int time){
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(),msgInfo,time);
        }else{
            mToast.setText(msgInfo);
        }
        mToast.setGravity(gravity, xOffset, yOffset);
        mToast.show();
    }

    public static void cancelToast(){
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
