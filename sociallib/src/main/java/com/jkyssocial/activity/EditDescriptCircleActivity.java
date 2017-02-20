package com.jkyssocial.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jkyssocial.common.util.EditTextShakeHelper;
import com.mintcode.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.R;

public class EditDescriptCircleActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private EditText activity_edit_circle_descript_content ;
    private int resultCode = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_descript_circle);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }
    private void initEvent() {
        activity_edit_circle_descript_content.addTextChangedListener(this);
    }

    private void initView() {
        activity_edit_circle_descript_content = (EditText) findViewById(R.id.activity_edit_circle_descript_content);

        Intent intent = getIntent();
        if (intent != null) {
            String descript = intent.getStringExtra("descript");
            boolean flag = intent.getBooleanExtra("flag", false);
            if(descript!=null && flag ){
                activity_edit_circle_descript_content .setText(descript);
                activity_edit_circle_descript_content.setSelection(descript.length());
            }
        }
    }

    @OnClick(R.id.left_rl)
    void back(View view) {
        finish();
    }

    @OnClick(R.id.right_rl)
    void confirm(View view) {
        Intent intent = new Intent() ;
        intent.putExtra("descript",activity_edit_circle_descript_content.getText()+ "") ;
        setResult(resultCode,intent);
        finish();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if(activity_edit_circle_descript_content.getText().toString().length()>=10){
//            Drawable drawable = getResources().getDrawable(R.drawable.social_send_error);
//            drawable.setBounds(0,0,10,10);
//            activity_edit_circle_descript_content.setError("最多支持100个字符!", drawable);
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(activity_edit_circle_descript_content.getText().length()>=100){
            Drawable drawable = getResources().getDrawable(R.drawable.social_send_error);
            drawable.setBounds(4,4,35,35);
            activity_edit_circle_descript_content.setError("最多输入100个字符!", drawable);
            new EditTextShakeHelper(EditDescriptCircleActivity.this).shake(activity_edit_circle_descript_content);
        }
    }
}
