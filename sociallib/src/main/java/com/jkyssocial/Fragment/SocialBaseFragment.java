package com.jkyssocial.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkyssocial.handler.PublishDynamicManager;
import com.mintcode.area_patient.area_login.LoginActivity;
import com.jkys.jkysim.database.KeyValueDBService;
import com.mintcode.util.Keys;

import java.util.ArrayList;

import cn.dreamplus.wentang.R;

/**
 * Created by yangxiaolong on 15/8/28.
 */
public class SocialBaseFragment extends Fragment{

    public View customToolBar;

    // variable to track event time
    private long mLastClickTime = 0;

    public KeyValueDBService mValueDBService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValueDBService = KeyValueDBService.getInstance();

    }

    public boolean singleClick(){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
            return false;
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public void initialCustomToolBar(View view){
        customToolBar = view.findViewById(R.id.social_tool_bar);
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

    public void setTitle(String title){
        ((TextView) customToolBar.findViewById(R.id.tv_title)).setText(title);
    }

    // 之前需求是弹出对话框，目前改成直接跳出登录界面，方法名字引用地方太多，暂时不该，只改变其内容，之前对话框代码暂时注释
    public boolean showLoginDialog() {
        String uid = mValueDBService.findValue(Keys.UID);
        if (uid == null || "-1000".equals(uid)) {
            PublishDynamicManager.sendingDynamicList = new ArrayList<>();
            KeyValueDBService.getInstance().delete();
            Intent intent = new Intent(getActivity(),
                    LoginActivity.class);
            //强制登录
            intent.putExtra("forceLogin", true);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_to_b_bottom, R.anim.slide_in_from_a_bottom);
//            final AlertDialog myDialog = new AlertDialog.Builder(getActivity()).create();
//            myDialog.show();
//            myDialog.getWindow().setContentView(R.layout.dialog_login);
//            myDialog.getWindow().findViewById(R.id.tv_login_ok)
//                    .setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            myDialog.dismiss();
//                            PublishDynamicManager.sendingDynamicList = new ArrayList<>();
//                            KeyValueDBService.getInstance().delete();
//                            Intent intent = new Intent(getActivity(),
//                                    LoginActivity.class);
//                            //强制登录
//                            intent.putExtra("forceLogin", true);
//                            startActivity(intent);
//                            getActivity().overridePendingTransition(R.anim.slide_in_to_b_bottom, R.anim.slide_in_from_a_bottom);
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
