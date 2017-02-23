package com.jkyssocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.sociallib.R;
import com.jkys.sociallib.R2;


/**
 * 个人空间页
 *
 * @author yangxiaolong
 */
public class EditSignatureActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mEditText;
    TextView countHint;

    private boolean buttonType = false;
    private boolean isEditName = false;
    private String hintStr = "/30";
    private int inputNum = 30;
    private TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_edit_signature);

//        getRightView("完成").setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra("signature", mEditText.getText().toString());
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
        initView();

        String signature = getIntent().getStringExtra("signature");
        isEditName = getIntent().getBooleanExtra("isEditName", false);
        if (isEditName) {
            hintStr = "/12";
            inputNum = 12;
            toolbarTitle.setText("修改昵称");
        }
        mEditText = (EditText) findViewById(R.id.edittext);
        mEditText.setText(signature);
        countHint = (TextView) findViewById(R.id.countHint);
        int len = signature == null ? 0 : signature.length();
        countHint.setText(len + hintStr);
        TextWatcher mTextWatcher = new TextWatcher() {
            private CharSequence temp;
            private int editStart;
            private int editEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = mEditText.getSelectionStart();
                editEnd = mEditText.getSelectionEnd();
                countHint.setText(temp.length() + hintStr);
                if (temp.length() > 0 && !buttonType) {
                    buttonType = true;
//                    getRightView("完成").setBackgroundResource(R.drawable.btn);
                }
                if (temp.length() <= 0 && buttonType) {
                    buttonType = false;
//                    getRightView("完成").setBackgroundResource(R.drawable.huisebtn);
                }
                if (temp.length() > inputNum) {
                    Toast.makeText(EditSignatureActivity.this, "你输入的字数已经超过了限制！", android.widget.Toast.LENGTH_SHORT);
                    s.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    mEditText.setText(s);
                    mEditText.setSelection(tempSelection);
                }
            }
        };
        mEditText.addTextChangedListener(mTextWatcher);

//        AppImpl.getAppRroxy().addLog(context, "page-change-record");

    }

    private void initView() {
        findViewById(R.id.left_rl).setOnClickListener(this);
        findViewById(R.id.right_rl).setOnClickListener(this);
        toolbarTitle = (TextView) findViewById(R.id.title_toolbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R2.id.right_rl:
                Intent intent = new Intent();
                intent.putExtra("signature", mEditText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R2.id.left_rl:
                finish();
                break;
        }
    }
}
