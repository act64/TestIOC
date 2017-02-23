package com.jkyssocial.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.jkys.proxy.AppImpl;
import com.jkys.sociallib.R;
import com.jkys.sociallib.R2;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageMengcengActivity extends BaseActivity {

    @BindView(R2.id.activity_image_shower)
    ImageView activityImageShower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_mengceng);
        ButterKnife.bind(this);
        String imgUrl = getIntent().getStringExtra("imgUrl");
        if (!TextUtils.isEmpty(imgUrl)) {
            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + imgUrl, this, activityImageShower,
                    ImageManager.circleAvatarOptions);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }
}
