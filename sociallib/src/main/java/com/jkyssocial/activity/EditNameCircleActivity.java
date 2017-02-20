package com.jkyssocial.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jkyssocial.common.util.EditTextShakeHelper;
import com.mintcode.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.R;


public class EditNameCircleActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private EditText activity_edit_circle_name_content ;
    private int resultCode = 997;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name_circle);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        activity_edit_circle_name_content.addTextChangedListener(this);
    }

    private void initView() {
        activity_edit_circle_name_content = (EditText) findViewById(R.id.activity_edit_circle_name_content);
        Intent intent = getIntent();
        String title = intent.getStringExtra("circleTitle");
        if (title != null && !title.equals("")) {
            activity_edit_circle_name_content.setText(title);
            activity_edit_circle_name_content.setSelection(title.length());
        }

    }

    @OnClick(R.id.left_rl)
    void back(View view) {
        finish();
    }

    @OnClick(R.id.right_rl)
    void confirm(View view) {
        String text = activity_edit_circle_name_content.getText().toString();
        if (text == null || text.equals("")) {
            showError("圈子的名称不能为空!!");
            Toast.makeText(this,"圈子的名称不能为空!!",Toast.LENGTH_LONG).show();
        }else{
            Intent intent = new Intent() ;
            intent.putExtra("name",text) ;
            setResult(resultCode, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(activity_edit_circle_name_content.getText().length() >= 15){
            showError("最多输入15个字符!");
        }
    }

    // 进行错误提示
    private void showError(String errorMsg) {
        Drawable drawable = getResources().getDrawable(R.drawable.social_send_error);
        drawable.setBounds(4,4,35,35);
        activity_edit_circle_name_content.setError(errorMsg, drawable);
        new EditTextShakeHelper(EditNameCircleActivity.this).shake(activity_edit_circle_name_content);
    }
}
