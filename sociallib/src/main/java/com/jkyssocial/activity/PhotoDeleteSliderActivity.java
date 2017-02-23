package com.jkyssocial.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jkys.jkyswidget.ConfirmTipsDialog;
import com.jkys.sociallib.R;
import com.jkyssocial.pageradapter.RecyclingPagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.Serializable;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 *  本地图片大图删除
 */
public class PhotoDeleteSliderActivity extends SocialBaseScaleActivity {
    /**
     * 定义全局变量ViewPager
     */
    private ViewPager mViewPager;

    private ImagePagerLocalAdapter imagePagerAdapter;

    private List<String> imageList;

    int index = 0;

    private TextView indexView;

    @Override
    public void finish(){
        Intent intent = new Intent();
        intent.putExtra("imageList", (Serializable) imageList);
        setResult(1, intent);
        super.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_photo_selected_slider);

        imageList = (List<String>) getIntent().getSerializableExtra("imageList");

        index = getIntent().getIntExtra("index", 0);

        indexView = (TextView) findViewById(R.id.tv_title);

        /**
         * 实例化ViewPager
         */
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        /**
         * 设置滑动页变化的接口
         */
        mViewPager.setOnPageChangeListener(new MyPagerChangeListener());

        getRightView(R.drawable.social_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmTipsDialog dialog = new ConfirmTipsDialog(PhotoDeleteSliderActivity.this, "确定删除本张图片吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(imageList.size() <= 1) {
                            imageList.remove(0);
                            finish();
                            return;
                        }
//                        if (index == 0){
//                            mViewPager.setCurrentItem(1);
//                        } else{
//                            mViewPager.setCurrentItem(index-1);
//                        }
                        imageList.remove(index);
                        imagePagerAdapter.notifyDataSetChanged();
                        indexView.setText((index+1) + "/" + imageList.size());
                    }
                });
                dialog.show();
            }
        });
        indexView.setText((index+1) + "/" + imageList.size());

        /**
         * 填充ViewPager的数据适配器
         */
        imagePagerAdapter = new ImagePagerLocalAdapter(imageList);
        mViewPager.setAdapter(imagePagerAdapter);
        mViewPager.setCurrentItem(index);
    }

    /**
     * ImagePagerLocalAdapter
     *
     */
    public class ImagePagerLocalAdapter extends RecyclingPagerAdapter implements View.OnClickListener {

//        private Activity mActivity;
        private List<String> imageList;

        private ImageLoader imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(false)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(false)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .build();//构建完成

        public List<String> getImageList() {
            return imageList;
        }

        public void setImageList(List<String> imageList) {
            this.imageList = imageList;
        }

        public ImagePagerLocalAdapter(List<String> imageList) {
//            this.mActivity = activity;
            this.imageList = imageList;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.social_slider_imageview_local,
                        container, false);
                holder = new ViewHolder();
                holder.imageView = (ImageViewTouch) convertView
                        .findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageView.setOnClickListener(this);
            holder.imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            imageLoader.displayImage("file://" + imageList.get(position), holder.imageView, options);
            return convertView;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void onClick(View v) {

        }

        private class ViewHolder {
            ImageViewTouch imageView;
        }


    }

    public class MyPagerChangeListener implements OnPageChangeListener{
        @Override
        public void onPageScrollStateChanged(int arg0) {}
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageSelected(int position) {
            index = position;
            indexView.setText((position+1) + "/" + imageList.size());
        }

    }

}