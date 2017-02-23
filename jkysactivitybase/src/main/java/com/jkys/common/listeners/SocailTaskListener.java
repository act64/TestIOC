package com.jkys.common.listeners;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Created by ylei on 2017/2/22.
 */

public class SocailTaskListener {

    private final String mStr;
    WeakReference<Activity> mActivityRef;
    public SocailTaskListener(Activity activity, String str){
        this.mActivityRef=new WeakReference<Activity>(activity);
        this.mStr=str;
    }
}
