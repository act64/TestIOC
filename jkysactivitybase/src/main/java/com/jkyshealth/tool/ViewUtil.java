package com.jkyshealth.tool;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by jinzifu on 16/1/17.
 */
public class ViewUtil {

    private static long mLastClickTime = 0;

    //为图片添加闪烁功能
    public static void setFlickerAnimation(Animation animation, ImageView iv_chat_head) {
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        iv_chat_head.setAnimation(animation);
    }

    /**
     * 防止重复点击
     *为false 说明点击太快
     * @return
     */
    public static boolean singleClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 600){
            mLastClickTime = SystemClock.elapsedRealtime();
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public static boolean singleClickFlex() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 600){
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    /**
     * 软键盘删除键监听处理事件
     *
     * @param keyCode
     * @param editText
     * @return true 消费监听,删除无效 ;false 捕获监听,执行事件
     */
    public static boolean deleteKey(int keyCode, EditText editText) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            String content = editText.getText().toString();
            if (TextUtils.isEmpty(content)) {
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

}
