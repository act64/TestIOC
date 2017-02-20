package com.jkyssocial.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;

public class ImageShowerActivity extends BaseActivity {

    @Bind(R.id.activity_image_shower)
    ImageView activityImageShower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shower);
        ButterKnife.bind(this);
        String imgUrl = getIntent().getStringExtra("imgUrl");
        if (!TextUtils.isEmpty(imgUrl)) {
            ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + imgUrl, this, activityImageShower,
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
