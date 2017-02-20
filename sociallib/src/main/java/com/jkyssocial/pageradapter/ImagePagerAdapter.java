package com.jkyssocial.pageradapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jkys.jkyswidget.ActionSheetDialog;
import com.jkys.tools.ImageUtil;
import com.mintcode.util.ImageManager;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * com.jkys.activity.home.ImagePagerAdapter
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter implements OnClickListener, View.OnLongClickListener {

    private Activity mActivity;
    private Context context;
    private List<String> imageList;

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public ImagePagerAdapter(Activity activity, List<String> imageList) {
        this.mActivity = activity;
        this.context = activity.getApplicationContext();
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        ViewHolder holder;
        String imageShow = imageList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    R.layout.social_slider_imageview, container, false);
            holder = new ViewHolder();
            holder.littleImage = (ImageView) convertView.findViewById(R.id.littleImage);
            holder.bigImage = (ImageViewTouch) convertView.findViewById(R.id.bigImage);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
            convertView.setTag(R.id.tag_first, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_first);
        }
        if (!TextUtils.isEmpty(imageShow)) {
            ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + ImageUtil.getSmallImageUrl(imageShow),
                    context, holder.littleImage);
        }

//        Matrix m = holder.bigImage.getImageMatrix();
//        RectF drawableRect = new RectF(0, 0, imageWidth, imageHeight);
//        RectF viewRect = new RectF(0, 0, holder.bigImage.getWidth(), holder.bigImage.getHeight());
//        m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
//        holder.bigImage.setImageMatrix(m);
        holder.bigImage.setScrollEnabled(true);
        holder.bigImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        holder.bigImage.setTag(holder);
        holder.bigImage.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                mActivity.finish();
            }
        });
        holder.bigImage.setOnClickListener(this);
        if (!TextUtils.isEmpty(imageShow)) {
            ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + imageShow, context,
                    holder.bigImage, new MyImageLoaderListener());
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        mActivity.finish();
    }

    @Override
    public boolean onLongClick(View v) {

        return true;
    }

    private class ViewHolder {
        ImageView littleImage;
        ImageViewTouch bigImage;
        ProgressBar progressBar;
    }

    class MyImageLoaderListener implements ImageLoadingListener {

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.progressBar.setVisibility(View.GONE);
            holder.bigImage.setOnLongClickListener(new MyLongClickListener(holder.bigImage));
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.progressBar.setVisibility(View.GONE);
        }
    }

    public class MyLongClickListener implements View.OnLongClickListener {

        private ImageView img;

        public MyLongClickListener(ImageView imageView) {
            img = imageView;
        }

        @Override
        public boolean onLongClick(View v) {
            final ActionSheetDialog dialog = new ActionSheetDialog(mActivity);
            dialog.setContentView(R.layout.dialog_save_prcture);
            dialog.findViewById(R.id.savePhone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Drawable drawable = img.getDrawable();
//                                BitmapDrawable bg = (BitmapDrawable) img.getDrawable();
                        Bitmap bitmap = drawableToBitmap(drawable);
                        String url = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "KS_Photo", "km");
//                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + url)));
                        MediaScannerConnection.scanFile(context, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + url}, null, null);
                        if (!url.isEmpty()) {
                            Toast.makeText(context, "已保存到手机相册", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(context, "图片格式不支持,保存失败", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });

            dialog.findViewById(R.id.cancelSave).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }
    }

    // drawable转换为bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

}
