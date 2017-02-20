package com.mintcode.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;

import com.jkys.jkysbase.BaseCommonUtil;
import com.jkys.proxy.AppImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//import com.zxinsight.MLink;
//import com.zxinsight.MagicWindowSDK;
//import com.zxinsight.Session;
//import com.zxinsight.mlink.YYBCallback;

/**
 * Created by wuweixiang on 16/8/24.
 * 最顶层activity，放置所有activity的公用处理
 */
public class BaseTopActivity extends AppCompatActivity {
    private static boolean isActive = false;
    protected Context context;
    protected boolean isMIUI = true;
    // 新增一个标志，默认是false，true 表示后台重新激活到前台。
//    private boolean isActiveFlag = false;

    protected Context getContext() {
        return context;
    }

    public static List<Activity> getsActivities() {
        return sActivities;
    }

    public static List<Activity> sActivities = new LinkedList<Activity>();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        sActivities.add(this);
    }

    @Override
    protected void onDestroy() {
        sActivities.remove(this);
//        unregisterReceiver(mHomeKeyEventReceiver);
//        mHomeKeyEventReceiver = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("Zern-Receiver-", "BaseTop-onPause");
        BaseCommonUtil.fixInputMethodManagerLeak(this);
        // 魔窗
//        Session.onPause(this);
//        unregisterAllReceiver();
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onResume() {

//        HashMap<String, String> info = new HashMap<>();
//        info.put("nickName", "wuweixiang");
//        info.put("avatar", "http://www.baidu.com");
//        String mInfo = IMGlobal.gson.toJson(info);
        //如果未注册,则注册
        AppImpl.getAppRroxy().registerReceiverByNeed();
        if (!BaseCommonUtil.isAppOnForeground) { // 从后台回到前台
//            if (App.isAppStartTimeCount) { // 如果是App中已经start过了 那么就不再启动
//                App.isAppStartTimeCount = false;
//            } else {
//                if (App.timeCount != null) {
//                    App.timeCount.start();
//                } else {
//                    App.getInstence().initHeartbeat();
//                }
//            }
            AppImpl.getAppRroxy().timeCountistart();
        }
        BaseCommonUtil.isAppOnForeground = true;
//        if (isActiveFlag) {
//            if (App.timeCount != null)
//                App.timeCount.start();
////            App.getInstence().getTimeCount().start();
//            isActiveFlag = false;
//            Log.d("LogUtil app restart", "--" + isActiveFlag + "--");
//        }
        // 魔窗
//        Session.onResume(this);
        super.onResume();
    }

    public static Activity getTopActivity() {
        if (sActivities.size() > 0) {
            return sActivities.get(sActivities.size() - 1);
        } else {
            return null;
        }
    }

    public static boolean isExistedMain() {
        boolean isNeedNewMain = AppImpl.getAppRroxy().isNeedNewMain();
        for (int i = sActivities.size() - 1; i >= 0; i--) {
            Activity a = sActivities.get(i);
            if (isNeedNewMain) {
                //新主页
                if (a.getClass().getName().contains("NewMainTabActivity") ) {
                    return true;
                }
            } else {
                //老主页
                if (a.getClass().getName().contains("MainActivity_pt_new") ) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 将dp转成px
     *
     * @param dp
     * @return
     */
    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }


    /**
     * finish指定activity之后加入的所有activity
     *
     * @param tClass
     */
    public static void backTo(Class<?> tClass) {
        int i = -1;
        for (Activity a : sActivities) {
            if (a.getClass().equals(tClass)) {
                i = sActivities.indexOf(a);

            }
        }
        if (i >= 0) {
            ArrayList<Activity> as = new ArrayList<>();
            for (int k = i + 1; k < sActivities.size(); k++) {
                as.add(sActivities.get(k));
            }
            for (Activity a : as) {
                if (a instanceof Activity && !a.isFinishing()) {
                    a.finish();
                    a.overridePendingTransition(0, 0);
                }
            }
        }
    }


    /**
     * finish所有activity，除了主页面
     */
    public static void finishAllExceptMainActivity() {
        Log.e("action", "finishAllExceptMainActivity");
        for (int i = sActivities.size() - 1; i >= 0; i--) {
            Activity a = sActivities.get(i);
            if (a instanceof Activity && !a.isFinishing()) {
                boolean isNewMain =AppImpl.getAppRroxy().isNeedNewMain();
                if (!isNewMain && !(a.getClass().getName().contains("MainActivity_pt_new"))) {
                    //需要老首页,除了老首页之外的finish
                    a.finish();
                    a.overridePendingTransition(0, 0);
                } else if (isNewMain && !(a.getClass().getName().contains("NewMainTabActivity"))) {
                    //需要新首页,除了新首页之外的finish
                    a.finish();
                    a.overridePendingTransition(0, 0);
                }
            }
        }
    }

    // 关闭登录流程所涉及的界面。
    public static void finishLoginLogicActivity() {
        Log.e("action", "finishLoginLogicActivity");
        for (int i = 0; i < sActivities.size(); i++) {
            Activity a = sActivities.get(i);
            if (!a.isFinishing() && isLoginLogicActivity(a)) {
                a.finish();
                a.overridePendingTransition(0, 0);
            }
        }
    }

    private static boolean isLoginLogicActivity(Activity a) {
        if (a.getClass().getName().contains("LoginActivity")
                || a.getClass().getName().contains( "RegistActivity")
                || a.getClass().getName().contains( "InputVerifyCodeActivity")
                || a.getClass().getName().contains( "PasswordLoginActivity")
                || a.getClass().getName().contains( "ChooseGenderActivity")
                || a.getClass().getName().contains( "ChooseDiabetesTypeActivity")
                || a.getClass().getName().contains( "ChooseAgeActivity")) {
            return true;
        }
        return false;
    }

    public static void finishAllExceptMainActivity(boolean isNewMain) {
        Log.e("action", "finishAllExceptMainActivity");
        for (int i = sActivities.size() - 1; i >= 0; i--) {
            Activity a = sActivities.get(i);
            if (a instanceof Activity && !a.isFinishing()) {
                if (!isNewMain && !(a.getClass().getName().contains("MainActivity_pt_new"))) {
                    //需要老首页,除了老首页之外的finish
                    a.finish();
                    a.overridePendingTransition(0, 0);
                } else if (isNewMain && !(a .getClass().getName().contains("NewMainTabActivity"))) {
                    //需要新首页,除了新首页之外的finish
                    a.finish();
                    a.overridePendingTransition(0, 0);
                }
            }
        }
    }

    public static void finishAllExceptHelloActivity() {
        Log.e("action", "finishAllExceptHelloActivity");
        for (int i = sActivities.size() - 1; i >= 0; i--) {
            Activity a = sActivities.get(i);
            if (a instanceof Activity && !a.isFinishing() && !(a .getClass().getName().contains("NewHelloActivity"))) {
//                sActivities.remove(i);
                a.finish();
                a.overridePendingTransition(0, 0);
            }
        }
    }

    public static void finishAll() {
        Log.e("action", "finishAll");
        for (int i = sActivities.size() - 1; i >= 0; i--) {
            Activity a = sActivities.get(i);
            if (a instanceof Activity && !a.isFinishing()) {
//                sActivities.remove(i);
                a.finish();
                a.overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        // 如果是魔窗启动的则返回统一到首页
//        if (getIntent().getBooleanExtra("MWflag", false)) {
//            startActivity(new Intent(this, MainActivity_pt_new.class));
//            finish();
//        }
    }

    /**
     * finish所有exceptList和主页面之外的activity
     *
     * @param exceptList
     */
    public static void finishAll(List<Activity> exceptList) {
        Log.e("action", "finishAll");
        if (AppImpl.getAppRroxy().isNeedNewMain()) {
            for (int i = sActivities.size() - 1; i >= 0; i--) {
                Activity a = sActivities.get(i);
                if (a instanceof Activity && !a.isFinishing()
                        && !(a .getClass().getName().contains("NewMainTabActivity"))
                        && !exceptList.contains(a)) {
//                sActivities.remove(i);
                    a.finish();
                    a.overridePendingTransition(0, 0);
                }
            }
        } else {
            for (int i = sActivities.size() - 1; i >= 0; i--) {
                Activity a = sActivities.get(i);
                if (a instanceof Activity && !a.isFinishing()
                        && !(a .getClass().getName().contains("MainActivity_pt_new"))
                        && !exceptList.contains(a)) {
//                sActivities.remove(i);
                    a.finish();
                    a.overridePendingTransition(0, 0);
                }
            }
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("ZernShopTrumpMMM", "----onRestoreInstanceState");
        try {
            startActivity(new Intent(this, Class.forName("com.jkyshealth.activity.other.NewHelloActivity")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finish();
    }

}
