package com.jkyssocial.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.common.widget.CustomToolbar;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.network.UpLoadCircleAvatarAsyncTask;
import com.jkyssocial.common.util.ZernToast;
import com.jkyssocial.data.CircleResult;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.jsoup.helper.StringUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

public class BuildCircleActivity extends BaseActivity implements View.OnClickListener, UpLoadCircleAvatarAsyncTask.UploadCircleAvatarListener {
    private TextView build_circle_name, build_circle_descript, build_circle_avatar_more, build_circle_name_more, build_circle_type, build_circle_type_more, build_circle_descript_more;
    private RelativeLayout relativeLayout_avatar, relativeLayout_name, relativeLayout_type, relativeLayout_destript;
    private ProgressBar activity_build_circle_progressBar;
    private static final int RequestCodeToAvatar = 1;
    private static final int RequestCodeToName = 2;
    private static final int RequestCodeToType = 3;
    private static final int RequestCodeToDescript = 4;
    private static final int FINISH = 5;
    private RoundedImageView build_circle_avatar;
    private DisplayImageOptions localOptions;
    private Uri imageUri;
    private String CircleType;
    private String name;
    private String circleClassCode;
    private File tempFile;

    @Bind(R.id.toolbar)
    CustomToolbar toolbar;

    private int circleNum = 0;

    /**
     * 新建圈子
     */
    static class AddCircleRequestListener implements RequestManager.RequestListener<CircleResult> {
        WeakReference<BuildCircleActivity> activityWR;

        public AddCircleRequestListener(BuildCircleActivity activity) {
            activityWR = new WeakReference<BuildCircleActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, final CircleResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final BuildCircleActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    activity.activity_build_circle_progressBar.setVisibility(View.GONE);
                    new AlertDialog.Builder(activity)
                            .setTitle("恭喜!")
                            .setMessage("恭喜你申请成功,需要审核,我们会尽快处理!")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(activity, CircleMainActivity.class);
                                    intent.putExtra("circle", data.getCircle());
                                    activity.startActivityForResult(intent, FINISH);
                                }
                            }).create().show();
                    EventBus.getDefault().post(new ChangeUserInfoEvent());
                } else {
                    Toast.makeText(activity, data.getReturnMsg(), Toast.LENGTH_LONG).show();
                    activity.activity_build_circle_progressBar.setVisibility(View.GONE);
                }
            } else {
                activity.activity_build_circle_progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_circle);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        build_circle_type_more.setOnClickListener(this);
        build_circle_name_more.setOnClickListener(this);
        build_circle_avatar_more.setOnClickListener(this);
        build_circle_descript_more.setOnClickListener(this);
        relativeLayout_avatar.setOnClickListener(this);
        relativeLayout_name.setOnClickListener(this);
        relativeLayout_type.setOnClickListener(this);
        relativeLayout_destript.setOnClickListener(this);

    }

    @OnClick(R.id.left_rl)
    void onBackClick(View view) {
        finish();
    }

    @OnClick(R.id.right_rl)
    void onRightRLClick(View view) {
        // TODO 将新建的圈子的信息上传至服务器 进行审核
        // 先判断圈子的头像和名字和类型是否是空的
        CharSequence name = build_circle_name.getText();
        CharSequence type = build_circle_type.getText();
        String tempStr = new String();
        if (imageUri == null || imageUri.equals("") && tempFile == null) {
//                    Toast.makeText(BuildCircleActivity.this, "请设置圈子的头像", Toast.LENGTH_LONG).show();
            tempStr += "圈子头像";
        } else if (name == null || name.equals("")) {
//                    Toast.makeText(BuildCircleActivity.this, "请设置圈子的名称", Toast.LENGTH_LONG).show();
            if (TextUtils.isEmpty(tempStr)) tempStr += "圈子名称";
            else tempStr += ",圈子名称";
        } else if (circleClassCode == null || circleClassCode.equals("")) {
//                    Toast.makeText(BuildCircleActivity.this, "请设置圈子的类型", Toast.LENGTH_LONG).show();
            if (TextUtils.isEmpty(tempStr)) tempStr += "圈子分类!";
            else tempStr += ",圈子分类!";
        }
        if (imageUri != null && !TextUtils.isEmpty(name + "") && !TextUtils.isEmpty(circleClassCode) && tempFile != null) {
            // 先上传头像
            UpLoadCircleAvatarAsyncTask task = new UpLoadCircleAvatarAsyncTask(0, 4, BuildCircleActivity.this);
            task.execute(tempFile);
            activity_build_circle_progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(BuildCircleActivity.this, "正在进行后台的申请中...请稍后", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(BuildCircleActivity.this)
                    .setTitle("提示!")
                    .setMessage("请填写: " + tempStr)
                    .setCancelable(true)
                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    private void initView() {
        getUserInfoCircleNum();
        if (circleNum == 2) {
            // 用户已经建立了两个 则显示一个对话框 提示用户
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示")
                    .setMessage("您已经建立了2个圈子，建立第3个圈子后无法再建新圈子了!解散圈子不会增加剩余可建圈子的数量!")
                    .setCancelable(true)
                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        // 进度条
        activity_build_circle_progressBar = (ProgressBar) findViewById(R.id.activity_build_circle_progressBar);
        activity_build_circle_progressBar.setVisibility(View.GONE);
        // 圈子名称设置
        build_circle_name_more = (TextView) findViewById(R.id.build_circle_name_more);
        // 圈子类型设置
        build_circle_type_more = (TextView) findViewById(R.id.build_circle_type_more);
        // 头像设置按钮
        build_circle_avatar_more = (TextView) findViewById(R.id.build_circle_avatar_more);
        // 圈子描述
        build_circle_descript_more = (TextView) findViewById(R.id.build_circle_descript_more);
        // 圈子类型显示的textview
        build_circle_type = (TextView) findViewById(R.id.build_circle_type);
        build_circle_name = (TextView) findViewById(R.id.build_circle_name);
        build_circle_avatar = (RoundedImageView) findViewById(R.id.build_circle_avatar);
        build_circle_descript = (TextView) findViewById(R.id.build_circle_descript);


        relativeLayout_avatar = (RelativeLayout) findViewById(R.id.relativeLayout_avatar);
        relativeLayout_name = (RelativeLayout) findViewById(R.id.relativeLayout_name);
        relativeLayout_type = (RelativeLayout) findViewById(R.id.relativeLayout_type);
        relativeLayout_destript = (RelativeLayout) findViewById(R.id.relativeLayout_descript);

        localOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(false)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(false)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .build();


    }

    private void getUserInfoCircleNum() {
        Integer hasCircles = CommonInfoManager.getInstance().getUserInfo(this).getHasCircles();
        if (hasCircles != null) {
            circleNum = hasCircles;
        } else {
            circleNum = 0;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        int requestcode = 0;
        switch (id) {
            // 头像设置
            case R.id.build_circle_avatar_more:
                intent.setClass(this, PhotoSelectedThumbnailActivity.class);
                intent.putExtra("maxImageSelectCount", 1);
                requestcode = RequestCodeToAvatar;
                break;
            case R.id.relativeLayout_avatar:
                intent.setClass(this, PhotoSelectedThumbnailActivity.class);
                intent.putExtra("maxImageSelectCount", 1);
                requestcode = RequestCodeToAvatar;
                break;

            // 圈子的名字设置
            case R.id.build_circle_name_more:
                intent.setClass(this, EditNameCircleActivity.class);
                String textStr = build_circle_name.getText().toString();
                if (textStr != null) {
                    intent.putExtra("circleTitle", textStr);
                }
                requestcode = RequestCodeToName;
                break;
            case R.id.relativeLayout_name:
                intent.setClass(this, EditNameCircleActivity.class);
                String textStr1 = build_circle_name.getText().toString();
                if (textStr1 != null) {
                    intent.putExtra("circleTitle", textStr1);
                }
                requestcode = RequestCodeToName;
                break;

            // 圈子的类型设置
            case R.id.build_circle_type_more:
                intent.setClass(this, ChooseTypeActivity.class);
                intent.putExtra("CircleType", CircleType);
                requestcode = RequestCodeToType;
                break;
            case R.id.relativeLayout_type:
                intent.setClass(this, ChooseTypeActivity.class);
                intent.putExtra("CircleType", CircleType);
                requestcode = RequestCodeToType;
                break;

            // 圈子的描述设置
            case R.id.build_circle_descript_more:
                intent.setClass(this, EditDescriptCircleActivity.class);
                String desStr = build_circle_descript.getText().toString();
                if (desStr != null) {
                    intent.putExtra("flag", flag);
                    intent.putExtra("descript", desStr);
                }
                requestcode = RequestCodeToDescript;
                break;

            case R.id.relativeLayout_descript:
                intent.setClass(this, EditDescriptCircleActivity.class);
                String desStr1 = build_circle_descript.getText().toString();
                if (desStr1 != null) {
                    intent.putExtra("flag", flag);
                    intent.putExtra("descript", desStr1);
                }
                requestcode = RequestCodeToDescript;
                break;
        }
        startActivityForResult(intent, requestcode);
    }

    private boolean flag = false;
    private static final int PHOTO_CROP_ACTIVITY = 10003;
    private static final int MAIN_CIRCLE_RESULTCODE = 10000;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodeToAvatar:
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
//                        ImageLoader.getInstance().displayImage(imageUri + "", build_circle_avatar, localOptions);
                        ImageManager.cropImage(this, imageUri, 500, 500, PHOTO_CROP_ACTIVITY);
                    }
                } else if (resultCode == PhotoSelectedThumbnailActivity.TAKE_PHOTO) {
                    imageUri = data.getData();
//                    ImageLoader.getInstance().displayImage(imageUri + "", build_circle_avatar, localOptions);
                    ImageManager.cropImage(this, imageUri, 500, 500, PHOTO_CROP_ACTIVITY);
                }
                break;
            // 圈子名字设置
            case RequestCodeToName:
                if (resultCode == 997 && data != null) {
                    String name = data.getStringExtra("name");
                    build_circle_name.setText(name);
                }
                break;
            // 圈子类型
            case RequestCodeToType:
                if (resultCode == 998 && data != null) {
                    CircleType = data.getStringExtra("CircleType");
                    circleClassCode = data.getStringExtra("circleClassCode");
                    if (CircleType != null) {
                        build_circle_type.setText(CircleType);
                    }
                }
                break;
            // 圈子描述
            case RequestCodeToDescript:
                if (resultCode == 999 && data != null) {
                    String descript = data.getStringExtra("descript");
                    build_circle_descript.setText(descript);
                    flag = true;
                }
                break;

            // 对头像进行裁剪
            case PHOTO_CROP_ACTIVITY:
                if (resultCode != RESULT_OK || data == null) {
                    ImageLoader.getInstance().displayImage(imageUri + "", build_circle_avatar, localOptions);
                    String filePath = Uri.decode(imageUri.getPath());
                    tempFile = new File(filePath);
                    return;
                }
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    ImageLoader.getInstance().displayImage(imageUri + "", build_circle_avatar, localOptions);
                    tempFile = ImageManager.getCropImageTemp();
                }
                break;

            case FINISH:
                if (resultCode == MAIN_CIRCLE_RESULTCODE)
                    finish();
                break;
        }

    }

    // 按下新建按钮上传头像之后的回调
    @Override
    public void onUploadCircleAvatarAsyncResult(int position, int forBiz, String picUrl, String returnCode, String returnMsg) {
        if (!TextUtils.isEmpty(picUrl)) {
            Toast.makeText(BuildCircleActivity.this, "头像审核通过!", Toast.LENGTH_SHORT).show();
            if (forBiz == 4) {
                ApiManager.addCircle(
                        new AddCircleRequestListener(this), 1, BuildCircleActivity.this,
                        picUrl,
                        build_circle_name.getText().toString(),
                        circleClassCode,
                        build_circle_descript.getText().toString() + "");
            }
        } else {
            Toast.makeText(BuildCircleActivity.this, "创建失败: " + returnMsg, Toast.LENGTH_LONG).show();
            activity_build_circle_progressBar.setVisibility(View.GONE);
        }

    }
}
