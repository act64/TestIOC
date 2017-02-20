package com.jkyssocial.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mintcode.base.BaseTopActivity;
import com.mintcode.util.MIUIUtil;

import cn.dreamplus.wentang.R;

/**
 * Created by yangxiaolong on 15/8/28.
 * 暂不继承BaseFragmentActivity
 */
public class SocialBaseScaleActivity extends BaseTopActivity {

    public View customToolBar;

    private View img_back;

    // variable to track event time
    private long mLastClickTime = 0;

    private TextView rightTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.scale_enter, R.anim.scale_exit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MIUIUtil.isMIUIV6()) {
//            MIUIUtil.setMIUITopViewVisible(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scale_enter, R.anim.scale_exit);
    }

    public boolean singleClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
            return false;
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        customToolBar = findViewById(R.id.social_tool_bar);
        img_back = customToolBar.findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rightTV = (TextView) customToolBar.findViewById(R.id.tv_right);
    }

    public ImageView getRightView2(int resId) {
        ImageView view = (ImageView) customToolBar.findViewById(R.id.iv_right_2);
        view.setVisibility(View.VISIBLE);
        view.setImageResource(resId);
        return view;
    }

    public ImageView getRightView(int resId) {
        ImageView view = (ImageView) customToolBar.findViewById(R.id.iv_right);
        view.setVisibility(View.VISIBLE);
        view.setImageResource(resId);
        return view;
    }

    public TextView getRightView(String text) {
        TextView view = (TextView) customToolBar.findViewById(R.id.tv_right);
        view.setVisibility(View.VISIBLE);
        view.setText(text);
        return view;
    }

    public TextView getRightView() {
        return rightTV;
    }

    public void setTitle(String title) {
        ((TextView) customToolBar.findViewById(R.id.tv_title)).setText(title);
    }

    public void setCustomToolBarBackground(int resId) {
        customToolBar.setBackgroundResource(resId);
    }
}
