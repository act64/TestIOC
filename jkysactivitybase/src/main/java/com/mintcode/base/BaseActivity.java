package com.mintcode.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.jkys.data.BaseResult;
import com.jkys.jkysim.chat.widget.CustomProgressDialog;
import com.jkys.jkysim.database.KeyValueDBService;
import com.jkys.jkysim.model.BasePOJO;
import com.jkys.proxy.AppImpl;
import com.jkyssocial.common.util.ZernToast;
import com.mintcode.network.OnResponseListener;
import com.mintcode.util.Const;
import com.mintcode.util.Keys;
import com.mintcode.util.Utils;


import java.util.ArrayList;

import cn.dreamplus.wentang.R;

/**
 * Created by wuweixiang on 16/8/22.
 */
public class BaseActivity extends BaseTopActivity implements OnResponseListener, View.OnClickListener {
    public static Gson GSON = new Gson();

    /**
     * 建议文哲封装的时候把这个考虑进去
     */
    private CustomProgressDialog mdialog;

    private CustomProgressDialog mNewdialog;

    /**
     * 建议文哲封装的时候把这个考虑进去
     */
    private TaskToast taskDialogView;

    //用户相关信息，暂时保留，后续了解清楚逻辑之后，再考虑修改，每次OnResume的时候会获取最新值
    public String uid;
    public String token;

    public void showTaskTip(String str) {
        if (taskDialogView == null) {
            taskDialogView = new TaskToast(this);
        } else {
            taskDialogView.cancel();
        }
        taskDialogView.show(str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.haveInternet(context)) {
            Toast("网络不稳定，请稍后重试");
        }

        // // SDK初始化，第三方程序启动时，都要进行SDK初始化工作
        // Log.d("GetuiSdkDemo", "initializing sdk...");

    }

    /**
     * 游客登录
     * 这个个人建议可以放到登录相关的封装，放在这里不是很合适
     */
    public void guestLogin() {
        //LogUtil.addLog(context, "event-guest-login");
        Const.setUidToGuest(context);
    }

    public void showLoadDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mdialog == null) {
                        mdialog = CustomProgressDialog.creatDialog(BaseActivity.this, R.style.ImageloadingDialogStyle);
                    } else {
                        mdialog.cancel();
                    }
                    mdialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void hideLoadDialog() {
        if (mdialog != null && mdialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mdialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public void showNewLoadDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mNewdialog == null) {
                        mNewdialog = CustomProgressDialog.creatDialog(BaseActivity.this, R.style.ImageloadingDialogStyle);
                    } else {
                        mNewdialog.cancel();
                    }
                    mNewdialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void hideNewLoadDialog() {
        if (mNewdialog != null && mNewdialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mNewdialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public void showResponseErrorToast(BasePOJO pojo) {
        String msg = (pojo == null ? getString(R.string.network_error) : pojo.getMessage());
        hideLoadDialog();
        if (msg != null && msg.contains("过期") && !(context.getClass().getName().contains("LoginActivity"))) {
            KeyValueDBService.getInstance().put(Keys.TOKEN, null);
            KeyValueDBService.getInstance().put(Keys.NEW_TOKEN, null);
            msg = "您的账号在另一台设备上登录，您被迫下线";
            AppImpl.getAppRroxy().showLoginActivity(this);
        }
        if (TextUtils.isEmpty(msg)) {
            msg = getString(R.string.network_error);
        }
        Toast(msg);
    }

    public void showResponseErrorToast(BaseResult pojo) {
        String msg = (pojo == null ? getString(R.string.network_error) : (pojo.getMessage()));

        if (msg != null && msg.contains("过期")) {
            Toast("您的账号在另一台手机上登录，您被迫下线");
            AppImpl.getAppRroxy().showLoginActivity(this);
        }
        if (TextUtils.isEmpty(msg)) {
            msg = getString(R.string.network_error);
        }
        Toast(msg);
    }

    public void Toast(String msg) {
        ZernToast.showToast(context, msg);
//        com.mintcode.util.BaseUtil.Toast(msg, context);
    }

    /**
     * 跳转原生或web
     *
     * @param type3
     * @param url3
     */
    public void goTo(String type3, String url3) {
        if (!TextUtils.isEmpty(type3) && !TextUtils.isEmpty(url3)) {
            if ("WEB_PAGE".equals(type3)) {//跳转到webview页面
                Intent intent2 = null;
                try {
                    intent2 = new Intent(this, Class.forName("com.mintcode.area_patient.area_home.BannerActivity"));
                    intent2.putExtra("pageToUrl", url3);
                    startActivity(intent2);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } else if ("NATIVE".equals(type3)) {//跳转到原生界面
                //游客不准进入的界面要检测是否是游客，若是提醒登录
                if ("page-record".equals(url3)) {
//                    if (checkUidForVisitor()) return;
                    AppImpl.getAppRroxy().showLoginActivity(this)) return;
                }
               AppImpl.getAppRroxy().startIntent(url3, BaseActivity.this, null);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        App.fixInputMethodManagerLeakTest(this);
        // app从后台重新返回到前台

        KeyValueDBService mValueDBService = KeyValueDBService.getInstance();
        token = mValueDBService.findValue(Keys.TOKEN);
        uid = mValueDBService.findValue(Keys.UID);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        Log.e("onDestroy", "" + this.toString());

        // 防止Activity has leaked window，程序崩溃，
        try {
            if (mdialog != null && mdialog.isShowing())
                mdialog.dismiss();
            mdialog = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onDestroy();

        // Toast("强制关闭");
    }

//    public DisplayMetrics getDM() {
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        return dm;
//    }


    /**
     * 暂时放在BaseActivity中
     * 响应失败的统一处理
     *
     * @param response the data send by server.
     * @param taskId   taskId is the check code.
     * @param rawData
     */
    @Override
    public void onResponse(Object response, String taskId, boolean rawData) {
        if (response == null) {
            hideLoadDialog();
            Toast(Const.NET_CHECK_SHOW);
        } else if (response instanceof BasePOJO) {
            BasePOJO pojo = (BasePOJO) response;
            if (pojo != null && !pojo.isResultSuccess()) {
                // 3170 是提示绑定手机号码。前端已经提示对话框。不需要Toast冗余提示。
                if (pojo.getCode() != 3170) {
                    showResponseErrorToast(pojo);
                    if (pojo.isLogout()) {
                        AppImpl.getAppRroxy().socialLogout();
                    }
                }
            }
        }
    }

    @Override
    public boolean isDisable() {
        return false;
    }

    @Override
    public void onClick(View view) {

    }
}

