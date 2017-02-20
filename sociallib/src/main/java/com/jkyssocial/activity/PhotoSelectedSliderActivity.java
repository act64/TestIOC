package com.jkyssocial.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jkyssocial.pageradapter.RecyclingPagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import cn.dreamplus.wentang.R;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 *  本地所有图片大图查看
 */
public class PhotoSelectedSliderActivity extends SocialBaseScaleActivity {
	/**
	 * 定义全局变量ViewPager
	 */
	private ViewPager mViewPager;

    private ImagePagerLocalAdapter imagePagerAdapter;

    private List<String> imageList;

    private int maxImageSelectCount;

    private final List<String> selectedImages = PhotoSelectedThumbnailActivity.selectedImages;

    private CheckBox checkBox;

    int index = 0;

    private TextView indexView;

    private CheckBoxListener checkBoxListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_activity_photo_selected_slider);

        imageList = (List<String>) getIntent().getSerializableExtra("imageList");

        index = getIntent().getIntExtra("index", 0);

        maxImageSelectCount = getIntent().getIntExtra("maxImageSelectCount", 1);

        indexView = (TextView) findViewById(R.id.tv_title);

        /**
         * 实例化ViewPager
         */
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        /**
         * 设置滑动页变化的接口
         */
        mViewPager.setOnPageChangeListener(new MyPagerChangeListener());

        checkBox = (CheckBox) findViewById(R.id.select);
        checkBoxListener = new CheckBoxListener();
        checkBox.setOnCheckedChangeListener(checkBoxListener);

        String text = selectedImages.size() <= 0 ? "完成" : ("完成(" + selectedImages.size() + "/" + maxImageSelectCount + ")");
        getRightView(text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });;
        indexView.setText(index + "/" + imageList.size());

		/**
		 * 填充ViewPager的数据适配器
		 */
        imagePagerAdapter = new ImagePagerLocalAdapter(this, imageList);
		mViewPager.setAdapter(imagePagerAdapter);
        mViewPager.setCurrentItem(index);
	}

    /**
     * ImagePagerLocalAdapter
     *
     */
    public class ImagePagerLocalAdapter extends RecyclingPagerAdapter implements View.OnClickListener {

        private Activity mActivity;
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

        public ImagePagerLocalAdapter(Activity activity, List<String> imageList) {
            this.mActivity = activity;
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
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            indexView.setText(position + "/" + imageList.size());
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setTag(imageList.get(position));
            if(selectedImages.contains(imageList.get(position)))
                checkBox.setChecked(true);
            else
                checkBox.setChecked(false);
            checkBox.setOnCheckedChangeListener(checkBoxListener);
        }
		
	}

    class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            String imagePath = (String) buttonView.getTag();
            if (isChecked) {
                if (selectedImages.size() >= maxImageSelectCount) {
                    Toast toast = Toast
                            .makeText(PhotoSelectedSliderActivity.this, String
                                            .format("最多可选 %d 张",
                                                    maxImageSelectCount),
                                    Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    buttonView.setChecked(!isChecked);
                } else {
                    selectedImages.add(imagePath);
                    getRightView().setText("完成(" + selectedImages.size() + "/" + maxImageSelectCount + ")");
                }
            } else {
                selectedImages.remove(imagePath);
                getRightView().setText("完成(" + selectedImages.size() + "/" + maxImageSelectCount + ")");
            }
        }
    }

}