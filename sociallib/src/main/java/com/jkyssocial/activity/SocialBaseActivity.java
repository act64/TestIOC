package com.jkyssocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkyssocial.handler.PublishDynamicManager;
import com.mintcode.area_patient.area_login.LoginActivity;
import com.mintcode.base.BaseTopActivity;
import com.jkys.jkysim.database.KeyValueDBService;
import com.mintcode.util.Keys;
import com.mintcode.util.MIUIUtil;

import java.util.ArrayList;

import cn.dreamplus.wentang.R;

/**
 * Created by yangxiaolong on 15/8/28.
 * 暂不继承BaseFragmentActivity
 */
public class SocialBaseActivity extends BaseTopActivity {

    public View customToolBar;

    public ImageView img_back;

    public ImageView rightIV;

    // variable to track event time
    private long mLastClickTime = 0;

    private TextView rightTV;

    public KeyValueDBService mValueDBService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValueDBService = KeyValueDBService.getInstance();
        if (MIUIUtil.isMIUIV6()) {

//            MIUIUtil.setBarBlackText(this);
        }
//        this.setTheme(R.style.Animation_RightInRightOutActivity);
//        overridePendingTransition(R.anim.translucent_enter, R.anim.translucent_exit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MIUIUtil.isMIUIV6()) {
//            MIUIUtil.setMIUITopViewVisible(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.translucent_enter, R.anim.translucent_exit);
    }

    public boolean singleClick(){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
            return false;
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        customToolBar = findViewById(R.id.social_tool_bar);
        img_back = (ImageView) customToolBar.findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rightTV = (TextView) customToolBar.findViewById(R.id.tv_right);
        rightIV = (ImageView) customToolBar.findViewById(R.id.iv_right);
    }

    public ImageView getRightView2(int resId){
        ImageView view = (ImageView) customToolBar.findViewById(R.id.iv_right_2);
        view.setVisibility(View.VISIBLE);
        view.setImageResource(resId);
        return view;
    }

    public ImageView getRightView(int resId){
        ImageView view = (ImageView) customToolBar.findViewById(R.id.iv_right);
        view.setVisibility(View.VISIBLE);
        view.setImageResource(resId);
        return view;
    }

    public TextView getRightView(String text){
        TextView view = (TextView) customToolBar.findViewById(R.id.tv_right);
        view.setVisibility(View.VISIBLE);
        view.setText(text);
        return view;
    }

    public TextView getRightView(){
        return rightTV;
    }

    public void setTitle(String title){
        ((TextView) customToolBar.findViewById(R.id.tv_title)).setText(title);
    }

    public void setCustomToolBarBackground(int resId){
        customToolBar.setBackgroundResource(resId);
    }

    public void ShowToastCenter(String s, int duration){
        Toast toast = Toast.makeText(this, s, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    // 之前需求是弹出对话框，目前改成直接跳出登录界面，方法名字引用地方太多，暂时不该，只改变其内容，之前对话框代码暂时注释
    public boolean showLoginDialog() {
        if ("-1000".equals(mValueDBService.findValue(Keys.UID))) {
            PublishDynamicManager.sendingDynamicList = new ArrayList<>();
            KeyValueDBService.getInstance().delete();
            Intent intent = new Intent(SocialBaseActivity.this,
                    LoginActivity.class);
            //强制登录
            intent.putExtra("forceLogin", true);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_to_b_bottom, R.anim.slide_in_from_a_bottom);
//            final AlertDialog myDialog = new AlertDialog.Builder(this).create();
//            myDialog.show();
//            myDialog.getWindow().setContentView(R.layout.dialog_login);
//            myDialog.getWindow().findViewById(R.id.tv_login_ok)
//                    .setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            myDialog.dismiss();
//                            PublishDynamicManager.sendingDynamicList = new ArrayList<>();
//                            KeyValueDBService.getInstance().delete();
//                            Intent intent = new Intent(SocialBaseActivity.this,
//                                    LoginActivity.class);
//                            //强制登录
//                            intent.putExtra("forceLogin", true);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.slide_in_to_b_bottom, R.anim.slide_in_from_a_bottom);
////                        finish();
//                        }
//                    });
//            myDialog.getWindow().findViewById(R.id.tv_login_cancel)
//                    .setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            myDialog.dismiss();
//
//                        }
//                    });
            return true;
        }
        return false;
    }



}
