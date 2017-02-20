package com.jkyssocial.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.tools.MainSelector;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.network.UpLoadCircleAvatarAsyncTask;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.CircleResult;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.jkyssocial.event.CircleChangeEvent;
import com.jkyssocial.event.FollowCircleEvent;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.jsoup.helper.StringUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class SetUpCircleActivity extends BaseActivity implements View.OnClickListener, UpLoadCircleAvatarAsyncTask.UploadCircleAvatarListener {
    private TextView activity_detail_circle_members_more,
            activity_detail_circle_descript_more,
            activity_detail_circle_head_more,
            activity_detail_circle_name,
            activity_detail_circle_type,
            activity_detail_circle_descript;
    private FancyButton activity_detail_circle_disband;
    private CircleImageView activity_detail_circle_members_img0, activity_detail_circle_members_img1, activity_detail_circle_members_img2;
    private RoundedImageView activity_detail_circle_head_img;
    private RelativeLayout rl_avatar, rl_descript, rl_memeber;
    private int localButtonType = -1;

    private static final int RequestCodeToAvatar = 1;
    private static final int RequestCodeToDescript = 4;
    private Circle circle;
    private Integer status;
    private Buddy userInfo;
    private ProgressBar activity_detail_circle_progressBar;

    @Bind(R.id.right_rl)
    View rightRL;

    static class EditCirleRequestListener implements RequestManager.RequestListener<CircleResult> {
        private WeakReference<SetUpCircleActivity> activityWR;

        public EditCirleRequestListener(SetUpCircleActivity activity) {
            activityWR = new WeakReference<SetUpCircleActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, CircleResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            SetUpCircleActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    Toast.makeText(activity.getApplicationContext(), "修改成功!", Toast.LENGTH_SHORT).show();
                    // TODO 按下保存之后的处理,到底是去哪个界面
                    activity.activity_detail_circle_progressBar.setVisibility(View.GONE);
                }
            } else {
                activity.activity_detail_circle_progressBar.setVisibility(View.GONE);
                Toast.makeText(activity.getApplicationContext(), data.getReturnMsg(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class GetUserInfoRequestListener implements RequestManager.RequestListener<GetUserInfoResult> {
        private WeakReference<SetUpCircleActivity> activityWR;

        public GetUserInfoRequestListener(SetUpCircleActivity activity) {
            activityWR = new WeakReference<SetUpCircleActivity>(activity);
        }


        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            SetUpCircleActivity activity = activityWR.get();
            if (data != null && data.getBuddy() != null && !TextUtils.isEmpty(data.getBuddy().getBuddyId())) {
                activity.hideLoadDialog();
                activity.userInfo = data.getBuddy();
                activity.UpdateCircleData();
            } else {
                activity.hideLoadDialog();
                Toast.makeText(activity.getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class FollowCirleRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<SetUpCircleActivity> activityWR;
        private String msg;
        private int follow;

        public FollowCirleRequestListener(SetUpCircleActivity activity, String msg, int follow) {
            activityWR = new WeakReference<SetUpCircleActivity>(activity);
            this.msg = msg;
            this.follow = follow;
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            SetUpCircleActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    if (activity.circle != null) {
                        Toast.makeText(activity.getApplicationContext(), msg + "[" + activity.circle.getTitle() + "]", Toast.LENGTH_SHORT).show();
                    }
                    EventBus.getDefault().post(new FollowCircleEvent(activity.circle.getId(), follow));
                    EventBus.getDefault().post(new ChangeUserInfoEvent());
                    // 没时间维护前面的事件处理。目前已增量的形式去完成新的需求。
                    EventBus.getDefault().post(new CircleChangeEvent());
                    activity.finish();
                }
            } else {
                Toast.makeText(activity.getApplicationContext(), data.getReturnMsg() + "", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class RemoveCirleRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<SetUpCircleActivity> activityWR;

        public RemoveCirleRequestListener(SetUpCircleActivity activity) {
            activityWR = new WeakReference<SetUpCircleActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            SetUpCircleActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    // 无动态
                    if (activity.circle.getStat() != null && activity.circle.getStat().getDynamicCount() == 0) {
                        Toast.makeText(activity.getApplicationContext(), "已解散[" + activity.circle.getTitle() + "]", Toast.LENGTH_SHORT).show();
                    } else { //有动态
                        Toast.makeText(activity.getApplicationContext(), "已提交解散的申请.", Toast.LENGTH_SHORT).show();
                    }
                    // TODO  需要明天来商量
                    activity.startActivity(MainSelector.getMainIntent(activity));
                }
            } else {
                Toast.makeText(activity, data.getReturnMsg(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_circle);
        ButterKnife.bind(this);
        initView();
        initEvent();
        // 小龙之前页面给了circle 此也面不需要getData
//        getData(null);
        LogUtil.addLog(this, "page-forum-circle-detail");
    }

    //网络请求数据之后的数据填充
    private void getData(String baseLine) {
        circleimageUrl = circle.getAvatar();
        if (!TextUtils.isEmpty(circle.getAvatar())) {
            ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + circle.getAvatar(), this,
                    activity_detail_circle_head_img, ImageManager.circleAvatarOptions);
        }

        activity_detail_circle_name.setText(circle.getTitle() + "");
        activity_detail_circle_type.setText(circle.getClassName() + "");
        List<Buddy> buddyList = circle.getBuddyList();
        if (buddyList == null || buddyList.isEmpty()) {

        } else if (buddyList.size() >= 3) {
            if (!TextUtils.isEmpty(circle.getBuddyList().get(0).getImgUrl())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH
                                + circle.getBuddyList().get(0).getImgUrl(),
                        this, activity_detail_circle_members_img0, R.drawable.social_new_avatar);
            } else {
                activity_detail_circle_members_img0.setImageResource(R.drawable.social_new_avatar);
            }
            if (!TextUtils.isEmpty(circle.getBuddyList().get(1).getImgUrl())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH
                                + circle.getBuddyList().get(1).getImgUrl(), this,
                        activity_detail_circle_members_img1, R.drawable.social_new_avatar);
            } else {
                activity_detail_circle_members_img1.setImageResource(R.drawable.social_new_avatar);
            }
            if (!TextUtils.isEmpty(circle.getBuddyList().get(2).getImgUrl())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH
                                + circle.getBuddyList().get(2).getImgUrl(), this,
                        activity_detail_circle_members_img2, R.drawable.social_new_avatar);
            } else {
                activity_detail_circle_members_img2.setImageResource(R.drawable.social_new_avatar);
            }

        } else if (buddyList.size() == 2) {
            activity_detail_circle_members_img0.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(circle.getBuddyList().get(0).getImgUrl())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH
                                + circle.getBuddyList().get(0).getImgUrl(), this,
                        activity_detail_circle_members_img1, R.drawable.social_new_avatar);
            } else {
                activity_detail_circle_members_img1.setImageResource(R.drawable.social_new_avatar);
            }
            if (!TextUtils.isEmpty(circle.getBuddyList().get(1).getImgUrl())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH
                                + circle.getBuddyList().get(1).getImgUrl(), this,
                        activity_detail_circle_members_img2, R.drawable.social_new_avatar);
            } else {
                activity_detail_circle_members_img2.setImageResource(R.drawable.social_new_avatar);
            }

        } else if (buddyList.size() == 1) {
            activity_detail_circle_members_img0.setVisibility(View.INVISIBLE);
            activity_detail_circle_members_img1.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(circle.getBuddyList().get(0).getImgUrl())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH
                                + circle.getBuddyList().get(0).getImgUrl(), this,
                        activity_detail_circle_members_img2, R.drawable.social_new_avatar);
            }else{
                activity_detail_circle_members_img2.setImageResource(R.drawable.social_new_avatar);
            }

        }

        if (circle.getSummary() != null) {
            activity_detail_circle_descript.setText(circle.getSummary() + "");
        } else {
            activity_detail_circle_descript.setText("");
        }
    }

    private void initEvent() {
        activity_detail_circle_disband.setOnClickListener(this);
        rl_avatar.setOnClickListener(this);
        rl_memeber.setOnClickListener(this);
        rl_descript.setOnClickListener(this);
        activity_detail_circle_head_img.setOnClickListener(this);
    }

    private void initView() {
        // 圈子成员的查看按钮
        activity_detail_circle_members_more = (TextView) findViewById(R.id.activity_detail_circle_members_more);
        // 解散按钮和退出按钮
        activity_detail_circle_disband = (FancyButton) findViewById(R.id.activity_detail_circle_disband);
        // 圈子的头像
        activity_detail_circle_head_img = (RoundedImageView) findViewById(R.id.activity_detail_circle_head_img);
        // 圈子的头像设置按钮,即图片右侧的按钮
        activity_detail_circle_head_more = (TextView) findViewById(R.id.activity_detail_circle_head_more);
        // 圈子的描述修改
        activity_detail_circle_descript_more = (TextView) findViewById(R.id.activity_detail_circle_descript_more);
        // 圈子的名字
        activity_detail_circle_name = (TextView) findViewById(R.id.activity_detail_circle_name);
        // 圈子的类型
        activity_detail_circle_type = (TextView) findViewById(R.id.activity_detail_circle_type);
        // 圈子成员头像
        activity_detail_circle_members_img0 = (CircleImageView) findViewById(R.id.activity_detail_circle_members_img0);
        activity_detail_circle_members_img1 = (CircleImageView) findViewById(R.id.activity_detail_circle_members_img1);
        activity_detail_circle_members_img2 = (CircleImageView) findViewById(R.id.activity_detail_circle_members_img2);
        // 圈子描述
        activity_detail_circle_descript = (TextView) findViewById(R.id.activity_detail_circle_descript);
        rl_avatar = (RelativeLayout) findViewById(R.id.rl_avatar);
        rl_memeber = (RelativeLayout) findViewById(R.id.rl_member);
        rl_descript = (RelativeLayout) findViewById(R.id.rl_descript);
        // 加载框:
        activity_detail_circle_progressBar = (ProgressBar) findViewById(R.id.activity_detail_circle_progressBar);

        // 用来填充数据 得到传进来的圈子 根据该圈子的状态来加载当前界面
        Intent intent = getIntent();
        //TODO 跳进来的时候将,进行判断当前圈子和用户我的关系,确定---显示 加入,退出,解散,
        circle = (Circle) intent.getSerializableExtra("circle");
        status = circle.getStatus();
        userInfo = CommonInfoManager.getInstance().getUserInfo(this);
        // 填充数据
        getData(null);
        if (userInfo != null) {
            if (circle != null) {
                UpdateCircleData();
            } else {
                Toast.makeText(SetUpCircleActivity.this, "未得到当前圈子的信息!", Toast.LENGTH_SHORT).show();
            }
        } else {
            showLoadDialog();
            ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, new GetUserInfoRequestListener(this), 1, context, null);
//            Toast.makeText(SetUpCircleActivity.this, "请先进入社区首页加载下信息.", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateCircleData() {
        // 判断是否是圈主 是圈主:
        if (userInfo.getBuddyId() == circle.getOwnerId() || userInfo.getBuddyId().equals(circle.getOwnerId())) {
            // 加载公共的代码
            CircleLordCommandCode();
            switch (status) {
                // 第一种:圈子正常
                case 0:
                    activity_detail_circle_disband.setText("解散圈子");
                    localButtonType = DISBAND;
                    break;
                // 第二种:圈子待审核(1和3代码相同,防止以后代码添加需求)
                case 1:
                    activity_detail_circle_disband.setText("待审核");
                    activity_detail_circle_disband.setEnabled(false);
                    localButtonType = WAIT_CHECK;
                    WaitForAudit();
                    break;
                // 第三种:待解散:
                case 3:
                    activity_detail_circle_disband.setText("解散审核中");
                    activity_detail_circle_disband.setEnabled(false);
                    localButtonType = DISBAND_WAIT_CHECK;
                    WaitForAudit();
                    break;
            }
        }
        // 非圈主: 就只有加入和退出的情况 就不用管圈子的状态 只要能进入这个界面,就只有两种情况 加入或者退出
        else {
            //非圈主的共有代码段
            NormalMemberCommandCode();
            // 我是否是圈子成员判断: 我已加入:
            if (circle.getHasMe() == 1) {
                activity_detail_circle_disband.setText("退出圈子");
                localButtonType = OUT;
            } else if (circle.getHasMe() == 0) { // 我尚未加入
                activity_detail_circle_disband.setText("加入圈子");
                localButtonType = ENTER;
            }
        }
    }

    private void WaitForAudit() {
        activity_detail_circle_disband.setTextColor(Color.parseColor("#FFFFFF"));
        activity_detail_circle_disband.setBackgroundColor(Color.parseColor("#CCCCCC"));
    }

    private void NormalMemberCommandCode() {
        activity_detail_circle_head_more.setVisibility(View.GONE);
        activity_detail_circle_descript_more.setVisibility(View.GONE);
        rightRL.setVisibility(View.INVISIBLE);
    }

    // 用来唯一标识当前界面显示的加入,退出,解散.
    private final int ENTER = 0;
    private final int OUT = 1;
    // 表示 圈主
    private final int DISBAND = 2;

    private final int WAIT_CHECK = 3;

    private final int DISBAND_WAIT_CHECK = 4;

    private void CircleLordCommandCode() {
        activity_detail_circle_head_more.setVisibility(View.VISIBLE);
        rightRL.setVisibility(View.VISIBLE);
        activity_detail_circle_descript_more.setVisibility(View.VISIBLE);
    }

    private String circleimageUrl;

    @OnClick(R.id.right_rl)
    void confirm(View v) {
        if (tempFile != null) {
            // 先上传头像
            UpLoadCircleAvatarAsyncTask task = new UpLoadCircleAvatarAsyncTask(0, 4, SetUpCircleActivity.this);
            task.execute(tempFile);
            activity_detail_circle_progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(SetUpCircleActivity.this, "正在进行后台的申请中...请稍后", Toast.LENGTH_SHORT).show();
        } else { //不修改头像就直接上传圈子信息
            changeCircleInfo();
        }
    }

    @OnClick(R.id.left_rl)
    void back(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            // 按钮的点击事件情况:
            case R.id.activity_detail_circle_disband:
                if (localButtonType == DISBAND) {
                    // 解散圈子的情况
                    switch (status) {
                        // 第一种:解散圈子
                        case 0:
                            makeDialog("是否需要提出申请!", localButtonType);
                            break;
                        // 第二种:圈子待审核(1和3代码相同,防止以后代码添加需求)暂不处理
                        case 1:
                            break;
                        // 第三种:待解散:暂不处理
                        case 3:
                            break;
                    }
                } else if (localButtonType == OUT) {
                    makeDialog("是否退出该圈子!", localButtonType);
                } else if (localButtonType == ENTER) {
                    EnterOrOutCircle("成功加入", 1, new Intent(this, CircleMainActivity.class));
                }
                break;
            case R.id.rl_avatar:
                // TODO 本地设置圈子头像的 界面
                if (localButtonType == DISBAND) {
                    Intent i_rl_avatar = new Intent(this, PhotoSelectedThumbnailActivity.class);
                    i_rl_avatar.putExtra("maxImageSelectCount", 1);
                    startActivityForResult(i_rl_avatar, RequestCodeToAvatar);
                }
                break;

            case R.id.rl_member:
                Intent i_rl_member = new Intent(this, CircleMemberActivity.class);
                i_rl_member.putExtra("circle", circle);
                startActivity(i_rl_member);
                break;

            case R.id.rl_descript:
                if (localButtonType == DISBAND) {
                    Intent i_rl_descript = new Intent(this, EditDescriptCircleActivity.class);
                    String desStr = activity_detail_circle_descript.getText().toString();
                    if (desStr != null) {
                        i_rl_descript.putExtra("descript", desStr);
                        i_rl_descript.putExtra("flag", true);
                    }
                    startActivityForResult(i_rl_descript, RequestCodeToDescript);
                }
                break;

            // 点击头像 放大
            case R.id.activity_detail_circle_head_img:
                Intent intent = new Intent(this, ImageShowerActivity.class);
                intent.putExtra("imgUrl", circle.getAvatar());
                startActivity(intent);
                break;
        }

    }

    // 编辑圈子 上传回调
    private void changeCircleInfo() {
        ApiManager.editCircle(new EditCirleRequestListener(this), 1, SetUpCircleActivity.this,
                circle.getId(), circleimageUrl, circle.getTitle(), circle.getClassCode(),
                activity_detail_circle_descript.getText().toString() + "");
    }

    private static final int PHOTO_CROP_ACTIVITY = 10003;

    // 加入或者退出圈子的回调:
    private void EnterOrOutCircle(final String msg, final int follow, final Intent intent) {

        ApiManager.followCircle(new FollowCirleRequestListener(this, msg, follow), 1,
                SetUpCircleActivity.this, circle.getId(), follow);

    }

    public void makeDialog(String message, final int localButtonType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage(message); //设置内容
        builder.setCancelable(true); // 点击对话框外面可以取消
//        builder.setIcon(R.drawable.social_zern);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (localButtonType == DISBAND) {
                    // 如果当前圈子是没有动态,那就直接解散,不需要显示 解散审核中的状态
                    ApiManager.removeCircle(new RemoveCirleRequestListener(SetUpCircleActivity.this),
                            1, SetUpCircleActivity.this, circle.getId());
                } else if (localButtonType == OUT) { // TODO 这里面是退出圈子,
                    if (circle != null && !TextUtils.isEmpty(circle.getTitle())) {
                        EnterOrOutCircle("已退出", 0, new Intent());
                    } else {
                        EnterOrOutCircle("已退出", 0, new Intent());
                    }
                }
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    //通过反射来findViewById来找寻控件的id
    public int findId(String fieldName) {
        int id = -1;
        if (fieldName != null) {
            Class<R.id> idClass = R.id.class;
            try {
                Field field = idClass.getDeclaredField(fieldName);
                id = field.getInt(idClass);
                //通过静态的常量,获取到view
//                ret = container.findViewById(id);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    private Uri imageUri;
    private DisplayImageOptions localOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.social_bg_select_image) //设置图片在下载期间显示的图片
            .cacheInMemory(false)//设置下载的图片是否缓存在内存中
            .cacheOnDisk(false)//设置下载的图片是否缓存在SD卡中
            .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
            .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
            .build();//构建完成;

    private File tempFile;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 修改头像
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

            // 圈子描述
            case RequestCodeToDescript:
                if (resultCode == 999 && data != null) {
                    String descript = data.getStringExtra("descript");
                    activity_detail_circle_descript.setText(descript);
                }
                break;

            // 对头像进行裁剪
            case PHOTO_CROP_ACTIVITY:
                if (resultCode != RESULT_OK || data == null) {
                    ImageLoader.getInstance().displayImage(imageUri + "", activity_detail_circle_head_img, localOptions);
                    String filePath = Uri.decode(imageUri.getPath());
                    tempFile = new File(filePath);
                    return;
                }
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    ImageLoader.getInstance().displayImage(imageUri + "", activity_detail_circle_head_img, localOptions);
                    tempFile = ImageManager.getCropImageTemp();
                }
                break;
        }

    }

    // 按下新建按钮上传头像之后的回调
    @Override
    public void onUploadCircleAvatarAsyncResult(int position, int forBiz, String picUrl, String returnCode, String returnMsg) {
        if (!TextUtils.isEmpty(picUrl)) {
//            Toast.makeText(SetUpCircleActivity.this, "头像审核完毕!", Toast.LENGTH_SHORT).show();
            if (forBiz == 4) {
                circleimageUrl = picUrl;
                changeCircleInfo();
            }
        } else {
            Toast.makeText(SetUpCircleActivity.this, returnMsg, Toast.LENGTH_LONG).show();
            activity_detail_circle_progressBar.setVisibility(View.GONE);
        }
    }
}



















