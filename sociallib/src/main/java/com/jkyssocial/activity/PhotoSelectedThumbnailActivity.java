package com.jkyssocial.activity;

import java.util.ArrayList;

import com.jkys.jkysbase.util.WidgetUtil;
import com.mintcode.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.MediaStore.Images.Media;

import cn.dreamplus.wentang.R;

/*

    选择相册（单/多选）
 */
public class PhotoSelectedThumbnailActivity extends SocialBaseScaleActivity {

	public static String TAG = "Thumbnails";

	public static final int TAKE_PHOTO = 10000;

	public static final int SELECT_PHOTO = 10001;

    public static final int BIG_IMAGE_VIEW= 10002;

    private GridView gridView;

	private GridAdapter adapter;

    private int maxImageSelectCount;

    public static ArrayList<String> selectedImages;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	DisplayImageOptions options = new DisplayImageOptions.Builder()  
	.showImageOnLoading(R.drawable.social_bg_select_image) //设置图片在下载期间显示的图片
	.cacheInMemory(false)//设置下载的图片是否缓存在内存中  
	.cacheOnDisk(false)//设置下载的图片是否缓存在SD卡中  
	.considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
	.imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
	.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
	.build();//构建完成  
	
	/**
	 * 拍照后照片存储地址
	 */
	private Uri takePhotoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_activity_photo_selected_thumbnail);

		selectedImages = new ArrayList<String>();
		maxImageSelectCount = getIntent().getIntExtra("maxImageSelectCount", 1);
		gridView = (GridView) findViewById(R.id.gridView);
		adapter = new GridAdapter();
		gridView.setAdapter(adapter);

        getRightView("完成").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent res = new Intent();
                ArrayList<String> li = new ArrayList<String>();
                for (String path : selectedImages) {
                    // 获取原图
                    if (path != null)
                        li.add(path);
                }
                res.putExtra("tu_ji", li);
                setResult(SELECT_PHOTO, res);
                finish();
            }
        });

	}

	class GridAdapter extends BaseAdapter{

		private ArrayList<String> list;

		private TakePhotoClickListener takePhotoClickListener;
		
		private PhotoClickListener photoClickListener;

		private CheckBoxListener checkBoxListener;

		public GridAdapter() {
			checkBoxListener = new CheckBoxListener();
			list = new ArrayList<String>();
			list.add(null);
			String str[] = { Media._ID,
                 Media.DISPLAY_NAME,
                 Media.DATA};
			Cursor cursor = getContentResolver().query(
                 Media.EXTERNAL_CONTENT_URI, str,
                 null, null, null);
			if(cursor == null)
				cursor = getContentResolver().query(
						Media.INTERNAL_CONTENT_URI, str,
						null, null, null);
			if(cursor != null)
				getColumnData(cursor);
			takePhotoClickListener = new TakePhotoClickListener();
			photoClickListener = new PhotoClickListener();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		public ArrayList<String> getList() {
			return list;
		}

		public void setList(ArrayList<String> list) {
			this.list = list;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			switch (getItemViewType(position)) {
			case 0:
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(
							R.layout.social_thumbnail_grid_take_photo, parent, false);
				}
				convertView.setOnClickListener(takePhotoClickListener);
				break;
			case 1:
				ViewHolder holder;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.social_thumbnail_grid,
							parent, false);
					holder = new ViewHolder();
					holder.imageView = (ImageView) convertView
							.findViewById(R.id.image);
					holder.checkBox = (CheckBox) convertView
							.findViewById(R.id.select);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.checkBox.setOnCheckedChangeListener(null);
				holder.checkBox.setTag(list.get(position));
                if(selectedImages.contains(list.get(position)))
                    holder.checkBox.setChecked(true);
                else
                    holder.checkBox.setChecked(false);
				holder.checkBox.setOnCheckedChangeListener(checkBoxListener);
                holder.imageView.setTag(position);
				holder.imageView.setOnClickListener(photoClickListener);
				WidgetUtil.expandTouchArea(holder.imageView, holder.checkBox, 20);
				imageLoader.displayImage("file://" + list.get(position), holder.imageView, options);
				break;
			}
			return convertView;
		}

		private void getColumnData(Cursor cur) {
			if (cur.moveToLast()) {
				String image_path;
				do {
					image_path = cur.getString(2);
					list.add(image_path);
				} while (cur.moveToPrevious());
			}
		}

		public void updateCheckedPartly(){
			getRightView().setText(selectedImages.size() <= 0 ? "完成" : ("完成(" + selectedImages.size() + "/" + maxImageSelectCount + ")"));
			int firstVisiblePosition = gridView.getFirstVisiblePosition();
			int lastVisiblePosition = gridView.getLastVisiblePosition();
			for(int i = firstVisiblePosition; i <= lastVisiblePosition; i++)
			{
				View view = gridView.getChildAt(i - firstVisiblePosition);
				ViewHolder holder = (ViewHolder) view.getTag();
				if(holder == null)	//第一个takePhoto item
					continue;
				holder.checkBox.setOnCheckedChangeListener(null);
				if(selectedImages.contains(list.get(i)))
					holder.checkBox.setChecked(true);
				else
					holder.checkBox.setChecked(false);
				holder.checkBox.setOnCheckedChangeListener(checkBoxListener);
			}
		}

		class CheckBoxListener implements OnCheckedChangeListener {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				String imagePath = (String) buttonView.getTag();
				if (isChecked) {
					if (selectedImages.size() >= maxImageSelectCount) {
						Toast toast = Toast
								.makeText(PhotoSelectedThumbnailActivity.this, String
										.format("最多可选 %d 张",
												maxImageSelectCount),
										Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						buttonView.setChecked(!isChecked);
					} else {
						selectedImages.add(imagePath);
						getRightView().setText(selectedImages.size() <= 0 ? "完成" : ("完成(" + selectedImages.size() + "/" + maxImageSelectCount + ")"));
					}
				} else {
					selectedImages.remove(imagePath);
					getRightView().setText(selectedImages.size() <= 0 ? "完成" : ("完成(" + selectedImages.size() + "/" + maxImageSelectCount + ")"));
				}
			}
		}

		class TakePhotoClickListener implements OnClickListener {
			
			@Override
			public void onClick(View v) {
				takePhotoUri = ImageManager.takePhoto(PhotoSelectedThumbnailActivity.this, TAKE_PHOTO);
			}

		}
		
		class PhotoClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
                Intent intent = new Intent(PhotoSelectedThumbnailActivity.this, PhotoSelectedSliderActivity.class);
                intent.putExtra("imageList", list);
				intent.putExtra("maxImageSelectCount", maxImageSelectCount);
//                intent.putStringArrayListExtra("selectedImages", selectedImages);
                intent.putExtra("index", (int) v.getTag());
                startActivityForResult(intent, BIG_IMAGE_VIEW);
			}
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		class ViewHolder {
			ImageView imageView;
			CheckBox checkBox;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 拍照返回
		if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK && takePhotoUri != null) {
			Intent res = new Intent();
			res.setData(takePhotoUri);
			setResult(TAKE_PHOTO, res);
			finish();
		}
		//查看大图返回
		else if(requestCode == BIG_IMAGE_VIEW){
			adapter.updateCheckedPartly();
		}
	}

	public GridAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(GridAdapter adapter) {
		this.adapter = adapter;
	}

}
