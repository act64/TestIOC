package com.jkyssocial.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.dreamplus.wentang.R;

/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/14
 * Time: 17:33
 * email: AndroidZern@163.com
 */
public class FootView extends LinearLayout {

    private ProgressBar progressBar ;
    private TextView tv_info ;

    public FootView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.zern_footer_load_more, this, true) ;
        initView() ;
    }

    public FootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.zern_footer_load_more, this, true) ;
        initView() ;
    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.zern_google_progress);
        tv_info = (TextView) findViewById(R.id.zern_foot_tip);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getTv_info() {
        return tv_info;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setTv_info(String str_info) {
        this.tv_info.setText(str_info+"");
    }
}

















