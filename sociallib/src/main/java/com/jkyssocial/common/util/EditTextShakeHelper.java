package com.jkyssocial.common.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/24
 * Time: 0:11
 * email: AndroidZern@163.com
 */
// 帮助EditText进行错误提示
public class EditTextShakeHelper {
    // 震动动画
    private Animation shakeAnimation ;
    // 插值器
    private CycleInterpolator cycleInterpolator ;
    // 震动器
    private Vibrator shakeVibrator ;

    public EditTextShakeHelper(Context context){
        // 初始化震动器
        shakeVibrator = (Vibrator) context.getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        // 初始化震动动画
        shakeAnimation = new TranslateAnimation(0,10,0,0) ;
        shakeAnimation.setDuration(300);
        cycleInterpolator = new CycleInterpolator(8) ;
        shakeAnimation.setInterpolator(cycleInterpolator);
    }

    public  void shake(EditText... editTexts){
        for (EditText editText : editTexts) {
            editText.startAnimation(shakeAnimation);
        }
        // 震动时间和是否重复
//        shakeVibrator.vibrate(new long[]{0,500},-1);
    }
}
