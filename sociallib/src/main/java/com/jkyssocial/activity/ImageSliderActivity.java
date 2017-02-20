package com.jkyssocial.activity;

import java.util.List;

import com.jkyssocial.pageradapter.ImagePagerAdapter;
import com.mintcode.base.BaseActivity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import cn.dreamplus.wentang.R;

/**
 * 查看大图滑动页
 */
public class ImageSliderActivity extends BaseActivity {
	/**
	 * 定义全局变量ViewPager
	 */
	private ViewPager mViewPager;

    private ImagePagerAdapter imagePagerAdapter;
	
	/** 小圆点的父控件 */
    private LinearLayout llDot;
    /** 上一个被选中的小圆点的索引，默认值为0 */
    private int preDotPosition = 0;

    int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.scale_enter, R.anim.scale_exit);
		setContentView(R.layout.social_activity_image_slider);

		/**
		 * 实例化ViewPager
		 */
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		llDot = (LinearLayout) findViewById(R.id.ll_dot);
		
		/**
		 * 设置滑动页变化的接口
		 */
		mViewPager.setOnPageChangeListener(new MyPagerChangeListener());

        List<String> imageList = (List<String>) getIntent().getSerializableExtra("imageList");

        index = getIntent().getIntExtra("index", 0);

        if (imageList.size()>1) {
            //设置indicator
            for (int i = 0; i < imageList.size(); ++i) {
                View dot = new View(this);
                dot.setBackgroundResource(R.drawable.dot_bg_bigbg_selector);
                LayoutParams params = new LayoutParams(10, 10);
                params.leftMargin = 15;
                dot.setEnabled(false);
                dot.setLayoutParams(params);
                llDot.addView(dot); // 向线性布局中添加"点"
            }
            llDot.getChildAt(index).setEnabled(true);
        }

		/**
		 * 填充ViewPager的数据适配器
		 */
        imagePagerAdapter = new ImagePagerAdapter(this, imageList);
		mViewPager.setAdapter(imagePagerAdapter);
        mViewPager.setCurrentItem(index);
	}

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scale_enter, R.anim.scale_exit);
    }

    public class MyPagerChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
        public void onPageSelected(int position) {
        	// 取余后的索引，得到新的page的索引
//            int newPositon = position % bannerList.size();
            // 把上一个点设置为被选中
        	llDot.getChildAt(preDotPosition).setEnabled(false);
            // 根据索引设置那个点被选中
            llDot.getChildAt(position).setEnabled(true);
            // 新索引赋值给上一个索引的位置
            preDotPosition = position;
        }
		
	}

}