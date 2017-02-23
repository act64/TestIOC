package com.jkyssocial.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkysim.database.KeyValueDBService;
import com.jkys.proxy.AppImpl;
import com.jkys.proxy.MyInfoUtilProxy;
import com.jkys.proxy.ProxyClassFactory;
import com.jkys.proxy.UpLoadWxAvaTarTaskProxy;
import com.jkys.sociallib.R;
import com.jkys.sociallib.R2;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.network.UploadFileForPublishAsyncTask;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.area_patient.area_mine.MyInfoPOJO;
import com.mintcode.area_patient.entity.MyInfo;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.BitmapUtil;
import com.mintcode.util.ImageManager;
import com.mintcode.util.Keys;
import com.mintcode.util.TakePhotoDialog;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 社区糖友圈 - 个人空间编辑页
 *
 * @author yangxiaolong
 */
public class EditPersonalSpaceActivity extends BaseActivity implements View.OnClickListener, UploadFileForPublishAsyncTask.UploadFileForPublishListener, RequestManager.RequestListener<NetWorkResult> {

    public static final int EDIT_SIGNATURE = 10001;

    public static final int PHOTO_SELECTED_THUMBNAIL_ACTIVITY = 10002;

    public static final int PHOTO_CROP_ACTIVITY = 10003;

    public static final int EDIT_NICKNAME = 10004;

    private int forBiz; //选择类型：1：背景 2：头像

    private MyInfo myInfo;

    @BindView(R2.id.title_toolbar)
    TextView toolbarTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.avatar)
    CircleImageView avatar;
    @BindView(R2.id.bg)
    RoundedImageView bg;
    @BindView(R2.id.nickname)
    TextView nicknameTV;
    @BindView(R2.id.signature)
    TextView signatureTV;
    @BindView(R2.id.main_content)
    LinearLayout mainContent;
    @BindView(R2.id.rll_avatar)
    RelativeLayout rllAvatar;
    @BindView(R2.id.rll_spaceBg)
    RelativeLayout rllSpaceBg;
    @BindView(R2.id.rll_nickName)
    RelativeLayout rllNickName;
    @BindView(R2.id.rll_signature)
    RelativeLayout rllSignature;

    Buddy buddy;

    String signature;
    String nickname;

    String avatarUrl;

    String bgUrl;

    LoadToast loadToast;
    private MyInfoUtilProxy infoUtil;
    private TakePhotoDialog takePhoto = new TakePhotoDialog(this);
    ;
    public Bitmap photoBitmap;
    private boolean edit = false;
    private Handler handler;
    private MyInfoPOJO infoPOJO;

    /**
     * 修改用户信息相关
     */
    static class ModifyUserInfoRequestListener implements RequestManager.RequestListener<NetWorkResult> {

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (data != null && "0000".equals(data.getReturnCode())) {
                EventBus.getDefault().post(new ChangeUserInfoEvent());
            }
        }
    }

    static class ModifyNameRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        WeakReference<EditPersonalSpaceActivity> activityWR;

        public ModifyNameRequestListener(EditPersonalSpaceActivity activity) {
            activityWR = new WeakReference<EditPersonalSpaceActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            EditPersonalSpaceActivity activity = activityWR.get();

            if (data != null && "0000".equals(data.getReturnCode())) {
                EventBus.getDefault().post(new ChangeUserInfoEvent());
                MyInfoPOJO infoPOJO = activity.infoUtil.getMyInfo();
                if (infoPOJO != null) {
                    activity.myInfo = infoPOJO.getMyinfo();
                    if (activity.myInfo == null) {
                        activity.myInfo = new MyInfo();
                    }
                } else {
                    infoPOJO = new MyInfoPOJO();
                    activity.myInfo = new MyInfo();
                }
                activity.myInfo.setNickname(activity.nickname);
                infoPOJO.setMyinfo(activity.myInfo);
                activity.infoUtil.saveMyInfo(infoPOJO);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_edit_personal_space);
        ButterKnife.bind(this);
        buddy = (Buddy) getIntent().getSerializableExtra("myBuddy");
        if (!TextUtils.isEmpty(buddy.getImgUrl())) {
            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + buddy.getImgUrl(), this, avatar, ImageManager.avatarOptions);
        }
        if (!TextUtils.isEmpty(buddy.getBgImgUrl())) {
            ImageManager.loadImageByDefaultImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + buddy.getBgImgUrl(),
                    this, bg, R.drawable.social_personal_space_bg);
        } else {
            bg.setImageResource(R.drawable.social_personal_space_bg);
        }

        nicknameTV.setText(buddy.getUserName());
        signatureTV.setText(buddy.getSignature());
        loadToast = new LoadToast(this).setProgressColor(ContextCompat.getColor(this, R.color.social_primary)).setBackgroundColor(ContextCompat.getColor(this, R.color.black_semi_transparent)).
                setTextColor(ContextCompat.getColor(this, R.color.white)).setText("图片上传中...").setTranslationY(dp2px(100));
        infoUtil = (MyInfoUtilProxy) ProxyClassFactory.getProxyClass(AppImpl.getAppRroxy().getMyInfoProxyClazz());
        infoPOJO = infoUtil.getMyInfo();
        if (infoPOJO != null) {
            myInfo = infoPOJO.getMyinfo();
            if (myInfo == null) {
                myInfo = new MyInfo();
            }
        } else {
            infoPOJO = new MyInfoPOJO();
            myInfo = new MyInfo();
        }
        KeyValueDBService mValueDBService = KeyValueDBService.getInstance();
//		//微信openid判断是否为微信登录
        String openid = mValueDBService.findValue(Keys.WX_OPENID);
        if (!TextUtils.isEmpty(openid)) {
            url = myInfo.getAvatar();
            if (TextUtils.isEmpty(url)) {
                url = mValueDBService.find(Keys.WX_HEADIMGURL);
            }
        }

        if (!TextUtils.isEmpty(myInfo.getAvatar()) && myInfo.getAvatar().startsWith("/avatar")) {
            url =  AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + myInfo.getAvatar();
        } else if (!TextUtils.isEmpty(myInfo.getAvatar()) && myInfo.getAvatar().contains("http")) {
            url = myInfo.getAvatar();
            isWXUploadAvartar = true;
        }
        handler = new MyHandler(this);
    }

    @OnClick(R2.id.left_rl)
    void back(View view) {
        finish();
    }

    @OnClick(R2.id.right_rl)
    void onConfirmClick(View view) {
        if (!TextUtils.isEmpty(signature))
            ApiManager.modifyMood(new ModifyUserInfoRequestListener(), 1, EditPersonalSpaceActivity.this, signature);

        if (!TextUtils.isEmpty(nickname))
            ApiManager.modifyName(new ModifyNameRequestListener(this), 1, EditPersonalSpaceActivity.this, nickname);

        if (!TextUtils.isEmpty(bgUrl)) {
            ApiManager.modifySpaceBg(new ModifyUserInfoRequestListener(), 1, this, bgUrl);
        }

        if (!TextUtils.isEmpty(avatarUrl)) {
            ApiManager.modifyAvatar(new ModifyUserInfoRequestListener(), 1, this, avatarUrl);
        }
        finish();
    }

    @OnClick(R2.id.nickname)
    void onNicknameClick(View view) {
        Intent intent = new Intent(EditPersonalSpaceActivity.this, EditSignatureActivity.class);
        intent.putExtra("signature", buddy.getUserName());
        intent.putExtra("isEditName", true);
        startActivityForResult(intent, EDIT_NICKNAME);
    }

    @OnClick(R2.id.rll_nickName)
    void onRllNickNameClick(View view) {
        Intent intent = new Intent(EditPersonalSpaceActivity.this, EditSignatureActivity.class);
        intent.putExtra("signature", buddy.getUserName());
        intent.putExtra("isEditName", true);
        startActivityForResult(intent, EDIT_NICKNAME);
    }

    @OnClick(R2.id.signature)
    void onSignatureClick(View view) {
        Intent intent = new Intent(EditPersonalSpaceActivity.this, EditSignatureActivity.class);
        intent.putExtra("signature", buddy.getSignature());
        startActivityForResult(intent, EDIT_SIGNATURE);
    }

    @OnClick(R2.id.rll_signature)
    void onRllSignatureClick(View view) {
        Intent intent = new Intent(EditPersonalSpaceActivity.this, EditSignatureActivity.class);
        intent.putExtra("signature", buddy.getSignature());
        startActivityForResult(intent, EDIT_SIGNATURE);
    }

    @OnClick(R2.id.avatar)
    void onAvatarClick(View view) {
        if (buddy.getCertStatus() == 1) {
            Toast.makeText(EditPersonalSpaceActivity.this, "认证中无法修改头像", Toast.LENGTH_SHORT).show();
            return;
        }
        forBiz = 2;
        takePhoto.showTakePhotoDialog();
//        Intent intent = new Intent(EditPersonalSpaceActivity.this,
//                PhotoSelectedThumbnailActivity.class);
//        intent.putExtra("maxImageSelectCount", 1);
//        startActivityForResult(intent, PHOTO_SELECTED_THUMBNAIL_ACTIVITY);
    }

    @OnClick(R2.id.rll_avatar)
    void onRllAvatarClick(View view) {
        if (buddy.getCertStatus() == 1) {
            Toast.makeText(EditPersonalSpaceActivity.this, "认证中无法修改头像", Toast.LENGTH_SHORT).show();
            return;
        }
        forBiz = 2;
        takePhoto.showTakePhotoDialog();
//        Intent intent = new Intent(EditPersonalSpaceActivity.this,
//                PhotoSelectedThumbnailActivity.class);
//        intent.putExtra("maxImageSelectCount", 1);
//        startActivityForResult(intent, PHOTO_SELECTED_THUMBNAIL_ACTIVITY);
    }

    @OnClick(R2.id.bg)
    void onBgClick(View view) {
        if (buddy.getCertStatus() == 1) {
            Toast.makeText(EditPersonalSpaceActivity.this, "认证中无法修改背景", Toast.LENGTH_SHORT).show();
            return;
        }
        forBiz = 1;
//        takePhoto.showTakePhotoDialog();
        Intent intent = new Intent(EditPersonalSpaceActivity.this,
                PhotoSelectedThumbnailActivity.class);
        intent.putExtra("maxImageSelectCount", 1);
        startActivityForResult(intent, PHOTO_SELECTED_THUMBNAIL_ACTIVITY);
    }

    @OnClick(R2.id.rll_spaceBg)
    void onRllBgClick(View view) {
        if (buddy.getCertStatus() == 1) {
            Toast.makeText(EditPersonalSpaceActivity.this, "认证中无法修改背景", Toast.LENGTH_SHORT).show();
            return;
        }
        forBiz = 1;
//        takePhoto.showTakePhotoDialog();
        Intent intent = new Intent(EditPersonalSpaceActivity.this,
                PhotoSelectedThumbnailActivity.class);
        intent.putExtra("maxImageSelectCount", 1);
        startActivityForResult(intent, PHOTO_SELECTED_THUMBNAIL_ACTIVITY);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
    }

    private Uri imageUri;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_SIGNATURE:
                if (data == null)
                    return;
                signature = data.getStringExtra("signature");
                if (signature != null)
                    signatureTV.setText(signature);
                break;
            case EDIT_NICKNAME:
                if (data == null)
                    return;
                nickname = data.getStringExtra("signature");
                if (nickname != null)
                    nicknameTV.setText(nickname);
                break;
            case PHOTO_SELECTED_THUMBNAIL_ACTIVITY:
                if (data == null)
                    return;
                if (resultCode == PhotoSelectedThumbnailActivity.SELECT_PHOTO) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> res = (ArrayList<String>) data
                            .getSerializableExtra("tu_ji");
                    String picPath = null;
                    if (res != null && res.size() > 0) {
                        picPath = res.get(0);
                        imageUri = Uri.fromFile(new File(picPath));
                        ImageManager.cropImage(this, imageUri, 500, 500, PHOTO_CROP_ACTIVITY);
                    }
                } else if (resultCode == PhotoSelectedThumbnailActivity.TAKE_PHOTO) {
                    imageUri = data.getData();
                    ImageManager.cropImage(this, imageUri, 500, 500, PHOTO_CROP_ACTIVITY);
                }
                break;
            case PHOTO_CROP_ACTIVITY:
                if (resultCode != RESULT_OK || data == null) {
//                    loadToast.show();
//                    UploadFileForPublishAsyncTask task = new UploadFileForPublishAsyncTask(
//                            0, forBiz, this);
//                    String filePath = Uri.decode(imageUri.getPath());
//                    task.execute(new File(filePath));
                    return;
                }
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    loadToast.show();
                    File tempFile = ImageManager.getCropImageTemp();
                    UploadFileForPublishAsyncTask task = new UploadFileForPublishAsyncTask(
                            0, forBiz, this);
                    task.execute(tempFile);
                }
                break;
            case TakePhotoDialog.PHOTO_CODE:
                Intent intentPhoto = null;
                try {
                    intentPhoto = new Intent(this, Class.forName("com.mintcode.widget.cropview.CropActivity"));
                    intentPhoto.putExtra("bitmap", TakePhotoDialog.mPhotoPath);
                    startActivityForResult(intentPhoto, TakePhotoDialog.CROP_CODE);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case TakePhotoDialog.IMAG_CODE:
                if (data != null) {
                    Uri uri = data.getData();
                    String path = BitmapUtil.uri2StringPath(context, uri);
                    try {
                        Intent intentImage = new Intent(this, Class.forName("com.mintcode.widget.cropview.CropActivity"));
                        intentImage.putExtra("bitmap", path);
                        startActivityForResult(intentImage, TakePhotoDialog.CROP_CODE);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TakePhotoDialog.CROP_CODE:
                if (data != null) {
                    takePhoto.mPhotoPath = data.getStringExtra("path");
                    if (!TextUtils.isEmpty(takePhoto.mPhotoPath)) {
                        byte[] b = BitmapUtil.decodeBitmap(takePhoto.mPhotoPath);
                        if (b != null) {
                            photoBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                            if (forBiz == 2) {
                                avatar.setImageBitmap(photoBitmap);
                            } else if (forBiz == 1) {
                                bg.setImageBitmap(photoBitmap);
                            }
                            b = null;
                        }
                        save();
                    }
                }

                break;
            default:
        }
    }

    private boolean isWXUploadAvartar = false;
    /**
     * 微信登录微信昵称和头像
     */
    private String url = "";

    private void save() {
        edit = true;
//        showLoadDialog();
        loadToast.show();
        String headIconPath = BitmapUtil.saveBitmapAsJpeg(photoBitmap,
                context);
        if (headIconPath != null) {
//            UploadAvatarUtil.uploadAvatar(context, handler, headIconPath);
            AppImpl.getAppRroxy().uploadAvatar(context,handler,headIconPath);
            avatarUrl = headIconPath;
        } else if (isWXUploadAvartar) {
            UpLoadWxAvaTarTaskProxy upLoadWXAvatarTask = ProxyClassFactory.getProxyClass(AppImpl.getAppRroxy().getUpLoadWxAvaTarTaskProxy(),context, url, handler);
            upLoadWXAvatarTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            handler.sendEmptyMessage(0);
        }

    }

    private static class MyHandler extends Handler {
        private WeakReference<EditPersonalSpaceActivity> mActivityWR;

        public MyHandler(EditPersonalSpaceActivity activity) {
            mActivityWR = new WeakReference<EditPersonalSpaceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final EditPersonalSpaceActivity activity = mActivityWR.get();
            if (activity == null)
                return;
            if (!activity.edit) {
                return;
            }
            activity.edit = false;
//            activity.hideLoadDialog();
            EventBus.getDefault().post(new ChangeUserInfoEvent());
            activity.loadToast.success();
        }
    }

    @Override
    public void onUploadFileForPublishAsyncResult(int position, int forBiz, final String picUrl) {
        if (!TextUtils.isEmpty(picUrl)) {
            if (forBiz == 1) {
                loadToast.success();
                bgUrl = picUrl;
                if (!TextUtils.isEmpty(bgUrl)) {
                    bg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + bgUrl, null, bg);
                        }
                    }, 3000);
                }

            } else if (forBiz == 2) {
                loadToast.success();
                avatarUrl = picUrl;
                if (!TextUtils.isEmpty(avatarUrl)) {
                    avatar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + avatarUrl, null, avatar);
                        }
                    }, 3000);
                }

                MyInfoPOJO infoPOJO = infoUtil.getMyInfo();
                if (infoPOJO != null) {
                    myInfo = infoPOJO.getMyinfo();
                    if (myInfo == null) {
                        myInfo = new MyInfo();
                    }
                } else {
                    infoPOJO = new MyInfoPOJO();
                    myInfo = new MyInfo();
                }
                myInfo.setAvatar(avatarUrl);
                infoPOJO.setMyinfo(myInfo);
                infoUtil.saveMyInfo(infoPOJO);
            }
        }
    }

    @Override
    public void processResult(int requestCode, int resultCode, NetWorkResult data) {

    }
}
