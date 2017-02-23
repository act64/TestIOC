package com.mintcode.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;

import com.jkys.jkysactivitybase.R;

import java.io.File;
import java.io.IOException;


/**
 * Created by wuweixiang on 16/8/24.
 * 获取照片Dialog
 */
public class TakePhotoDialog {
    public static final int IMAG_CODE = 0x101;
    public static final int PHOTO_CODE = 0x102;
    public static final int CROP_CODE = 0x103;

    private AlertDialog dialog = null;
    private Activity activity;
    public static String mPhotoPath;

    public TakePhotoDialog(Activity activity) {
        this.activity = activity;
    }

    public void showTakePhotoDialog() {
        // 选择图片对话框
        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity).create();
        } else {
            dialog.dismiss();
        }
        dialog.show();
//		showLoadDialog();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_take_photo);
        window.findViewById(R.id.tv_photo).setOnClickListener(listener);
        window.findViewById(R.id.tv_album).setOnClickListener(listener);
        window.findViewById(R.id.tv_cancle).setOnClickListener(listener);
    }

    public void dismissTakePhotoDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.tv_photo) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mPhotoPath = Environment.getExternalStorageDirectory() + "/"
                        + Const.getCurrentTime() + ".jpg";
                try {
                    File mPhotoFile = new File(mPhotoPath);
                    if (!mPhotoFile.getParentFile().exists()) {
                        mPhotoFile.getParentFile().mkdirs();
                    }

                    if (!mPhotoFile.exists()) {
                        mPhotoFile.createNewFile();
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(mPhotoFile));
                    activity.startActivityForResult(intent, PHOTO_CODE);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (i == R.id.tv_album) {
                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                activity.startActivityForResult(in, IMAG_CODE);

            } else if (i == R.id.tv_cancle) {
            } else {
            }
            dismissTakePhotoDialog();
        }
    };

}
