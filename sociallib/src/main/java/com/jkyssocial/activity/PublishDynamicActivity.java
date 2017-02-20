package com.jkyssocial.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkys.common.widget.CustomToolbar;
import com.jkys.jkysbase.BaseCommonUtil;
import com.jkys.jkysbase.ThreadPoolTools;
import com.jkys.jkysim.chat.emoji.MsgFaceUtils;
import com.jkys.jkyswidget.ProgressBarDialog;
import com.jkyshealth.tool.CommonDialog;
import com.jkyssocial.Fragment.SocialMainFragment;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.util.EditTextShakeHelper;
import com.jkyssocial.common.util.ZernToast;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.data.ListCircleForUserResult;
import com.jkyssocial.event.PrePublishDynamicEvent;
import com.jkyssocial.handler.PublishDynamicManager;
import com.jkyssocial.pageradapter.MessageBoxPagerAdapter;
import com.jkyssocial.widget.ListPopupWindow;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.LogUtil;
import com.mintcode.util.MIUIUtil;
import com.mintcode.widget.MyGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.CirclePageIndicator;

import org.jsoup.helper.StringUtil;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;


/**
 * @author xiaolong.yxl
 */
public class PublishDynamicActivity extends BaseActivity implements TextWatcher, AdapterView.OnItemClickListener, RequestManager.RequestListener<ListCircleForUserResult> {

    private static final int THUMBNAIL_ACTIVITY = 0;
    private static final int DELETE_ACTIVITY = 1;
    private static final int REQUEST_MY_INFO = 2;

    private static final int PERSONAL_TYPE = 0;
    private static final int CIRCLE_TYPE = 1;

//    @Bind(R.id.back)
//    ImageView back;
//    @Bind(R.id.toolbarTitle)
//    TextView toolbarTitle;
//    @Bind(R.id.publishBtn)
//    TextView publishBtn;
    @Bind(R.id.toolbar)
    CustomToolbar toolbar;
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.content)
    EditText content;
    @Bind(R.id.gridView)
    MyGridView gridView;
    @Bind(R.id.switchImage)
    ImageView switchImage;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.indicator)
    CirclePageIndicator indicator;
    @Bind(R.id.hideBox)
    TextView hideBox;
    @Bind(R.id.messagebox)
    RelativeLayout messagebox;
    @Bind(R.id.editLinear)
    LinearLayout editLinear;
    @Bind(R.id.titleDivider)
    View titleDivider;
    @Bind(R.id.select_circle_name)
    TextView selectCircleNameTv;
    @Bind(R.id.select_circle_rl)
    RelativeLayout selectCircleRl;

    private int type = PERSONAL_TYPE;

    private Circle circle;

    private PublishNewsPicAdapter adapter;


    /**
     * 是否正在发布
     */
    private boolean isPublishing;

    private EditText contentEditText;

    int curStatus = 0;  //0 键盘；1 表情框

    EditText curEditText;

    Buddy myBuddy;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    ProgressBarDialog dialog;

    static class GetUserInfoResquestListener implements RequestManager.RequestListener<GetUserInfoResult> {
        private WeakReference<PublishDynamicActivity> activityWR;

        public GetUserInfoResquestListener(PublishDynamicActivity activity) {
            activityWR = new WeakReference<PublishDynamicActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (activityWR == null || activityWR.get() == null) {
                return;
            }
            PublishDynamicActivity activity = activityWR.get();
            if (data != null && data.getBuddy() != null) {
                if (requestCode == REQUEST_MY_INFO) {
                    activity.myBuddy = data.getBuddy();
                }
            }
        }
    }

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.social_bg_select_image) //设置图片在下载期间显示的图片
            .cacheInMemory(false)//设置下载的图片是否缓存在内存中
            .cacheOnDisk(false)//设置下载的图片是否缓存在SD卡中
            .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
            .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
            .build();//构建完成

    MessageBoxPagerAdapter.OnFaceOprateListener mOnFaceOprateListener = new MessageBoxPagerAdapter.OnFaceOprateListener() {

        @Override
        public void onFaceSelected(String unicode) {
            if (null != unicode) {
                int selection = curEditText.getSelectionStart();
                curEditText.getText().insert(selection, unicode);
            }
        }

        @Override
        public void onFaceDeleted() {
            int selection = curEditText.getSelectionStart();
            String text = curEditText.getText().toString();
            if (selection > 0) {
                if (selection > 1) {
                    String text2 = text.substring(selection - 2);
                    if (MsgFaceUtils.faceImgUnicodes.contains(text2)) {
                        curEditText.getText().delete(selection - 2, selection);
                        return;
                    }
                }
                curEditText.getText().delete(selection - 1, selection);
            }
        }

    };

    @OnClick(R.id.left_rl)
    void onBackClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (dismissPop()) return;
//        ConfirmTipsDialog dialog = new ConfirmTipsDialog(this, "退出本次编辑？", new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                finish();
//            }
//        });
//        dialog.show();
        showExitDialog();
    }

    private CommonDialog exitEditDialog;

    private void showExitDialog() {
        if (exitEditDialog == null) {
            exitEditDialog = new CommonDialog.Builder()
                    .setDes("")
                    .setTwobutton(true)
                    .setCheckable(false)
                    .setImportantPosLeftOrRight(false)
                    .setButtonText("取消", "确定").setTitle("退出本次编辑？")
                    .setImportantPosLeftOrRight(true)
                    .setLy(600).setClickListenerfirtst(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exitEditDialog.dissmiss();
                        }
                    }).setClickListenersecond(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exitEditDialog.dissmiss();
                            finish();
                        }
                    }).build(this);
        }
        exitEditDialog.show();
    }

    @OnClick(R.id.right_rl)
    void onPublishClick(View view) {
        // 判断是否是社区首页进来的 而且没有点击选择圈子或者我的空间。
        if (SocialMainFragment.from.equals(from) && !isClickDialogItem) {
            ZernToast.showToast(this, "请选择发布动态的圈子", Gravity.CENTER, 0, 0);
            return;
        }
        List<String> picList = adapter.getPicList();
        if (circle != null) {
            if (TextUtils.isEmpty(title.getText().toString())) {
//                Toast toast = Toast.makeText(NewPublishDynamicActivity.this,
//                        "快写个响亮的标题吧！", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
                ZernToast.showToast(this, "快写个响亮的标题吧！", Gravity.CENTER, 0, 0);
                return;
            }
//            if(title.getText().length() < 5){
//                Toast toast = Toast.makeText(NewPublishDynamicActivity.this,
//                        "标题不能少于5个字", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//            }
            if (title.getText().length() > 25) {
//                Toast toast = Toast.makeText(NewPublishDynamicActivity.this,
//                        "标题不能超过25个字", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
                ZernToast.showToast(this, "标题不能超过25个字", Gravity.CENTER, 0, 0);
                return;
            }
        }

        if (picList.size() <= 1 && TextUtils.isEmpty(contentEditText.getText().toString())) {
//            Toast toast = Toast.makeText(NewPublishDynamicActivity.this,
//                    "调皮，至少发一张图啦", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
            ZernToast.showToast(this, "调皮，至少发一张图啦", Gravity.CENTER, 0, 0);
            return;
        }
        if (circle != null) {
            LogUtil.addLog(PublishDynamicActivity.this, "event-forum-new-circletopic");
        } else {
            LogUtil.addLog(PublishDynamicActivity.this, "event-forum-new-topic");
        }
        if (!isPublishing) {
            isPublishing = true;
            Dynamic dynamic = new Dynamic();
            dynamic.setStatusAndroid(1);
            dynamic.setCreatedTime(System.currentTimeMillis());
            dynamic.setContent(contentEditText.getText().toString());
            if (circle != null) {
                dynamic.setTitle(title.getText().toString());
                dynamic.setCircle(circle);
            }
            dynamic.setOwner(myBuddy);
            picList.remove(picList.size() - 1);
            dynamic.setImages(picList);

            EventBus.getDefault().post(new PrePublishDynamicEvent(dynamic));

            PublishDynamicManager.sendingDynamicList.add(0, dynamic);
            PublishDynamicManager publishDynamicManager = new PublishDynamicManager(dynamic, PublishDynamicActivity.this);
            publishDynamicManager.start();
            // 判断是否是社区首页进来的
            if (SocialMainFragment.from.equals(from)) {
                Intent intent = new Intent();
                // 如果是非圈子动态 即我的空间动态
                if (circle == null) {
                    intent.setClass(this, NewPersonalSpaceActivity.class);
                    intent.putExtra("myBuddy", myBuddy);
                } else {
                    intent.setClass(this, CircleMainActivity.class);
                    intent.putExtra("circle", circle);
                }
                startActivity(intent);
            }
            finish();
        }
    }

    private String from;
    // 请求码
    private final int requestCode = 1;
    // 标记是否已经请求成功过了圈子信息。
    private boolean isHasCircleData;
    private Object obj = new Object();

    @OnClick(R.id.select_circle_rl)
    void onSelectCircleOnClick(View view) {
        // 第一次选择我的圈子 则去请求我的圈子,改成提前获取
//        if (myBuddy != null && !isHasCircleData) {
//            ApiManager.listCircleForUserV2(this, requestCode, context.getApplicationContext(), myBuddy.getBuddyId());
//            return;
//        }
        if (!isHasCircleData) {
            //异步获取圈子未完成
            ZernToast.showToast(this, "圈子获取中");
            ThreadPoolTools.getInstance().postWorkerTask(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    //等待圈子列表获取完成,最多等待10s
                    while (!isHasCircleData && count < 100) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count++;
                    }
                    ThreadPoolTools.getInstance().postMainTask(new Runnable() {
                        @Override
                        public void run() {
                            if (isHasCircleData) {
                                showSelectDialog();
                            } else {
                                ZernToast.showToast(getApplicationContext(), "圈子获取失败");
                            }
                        }
                    });
                }
            });

        } else {
            // 我的圈子已获取完成
            showSelectDialog();
        }
    }

    @Override
    public void processResult(int requestCode, int resultCode, ListCircleForUserResult data) {
        if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
            if (data.getReturnCode().equals("0000") && requestCode == this.requestCode) {
//                circles.clear();
                if (data.getCircleList() != null && !data.getCircleList().isEmpty()) {
                    circles.addAll(data.getCircleList());
                }
                // 因为有一个是我的空间。数据类型不一致。暂时当做圈子处理。
                circles.add(new Circle("我的空间", null, null, null, null));
//                showSelectDialog();
                isHasCircleData = true;
            }
        }
    }

    private List<Circle> circles = new LinkedList<>();
    private ListPopupWindow listPopupWindow;
    private ListPopupWindow.Builder builder;

    private void showSelectDialog() {
        if (builder == null || listPopupWindow == null) {
            builder = new ListPopupWindow.Builder(this, circles, this);
            listPopupWindow = builder.create(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPop();
                }
            });
        }
        listPopupWindow.showDefaultAlpha(selectCircleRl);
    }

    private boolean isClickDialogItem = false;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ImageView img = (ImageView) view.findViewById(R.id.duihao_img);
//        if (img != null) {
//            img.setVisibility(View.VISIBLE);
//        }
//
        for (int i = 0; i < circles.size(); i++) {
            circles.get(i).setChecked(false);
        }
        circles.get(position).setChecked(true);
        builder.getAdapter().notifyDataSetChanged();

        // 判断是否选的是我的空间
        if (position == circles.size() - 1) {
            // TODO 设置标题栏是否可编辑
            title.setTextColor(Color.parseColor("#CCCCCC"));
            title.setEnabled(false);
//            title.setText("");
//            title.setHint("标题");
            circle = null;
        } else {
            title.setTextColor(Color.parseColor("#333333"));
            title.setEnabled(true);
            circle = circles.get(position);
        }
        isClickDialogItem = true;
        selectCircleNameTv.setText(circle == null ? "我的空间" : circle.getTitle() + "");
        Log.d("Zern-publish_dy", circle == null ? "我的空间" : circle.getTitle() + "");
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissPop();
            }
        }, 150);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_publish_dynamic);
        ButterKnife.bind(this);
        toolbar.setBackVisble(true, this);
        from = getIntent().getStringExtra("from");
        // 是否是从社区首页进来的。
        if (SocialMainFragment.from.equals(from)) {
            title.setVisibility(View.VISIBLE);
//            title.setText("发布圈子动态");
            titleDivider.setVisibility(View.VISIBLE);
            selectCircleRl.setVisibility(View.VISIBLE);
        } else {
            circle = (Circle) getIntent().getSerializableExtra("circle");
            if (circle != null) {
                type = CIRCLE_TYPE;
                toolbar.setTitle("发布动态");
//                toolbarTitle.setText("发布动态");
            }
            title.setVisibility(type == CIRCLE_TYPE ? View.VISIBLE : View.GONE);
            titleDivider.setVisibility(type == CIRCLE_TYPE ? View.VISIBLE : View.GONE);
        }

        ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_WITH_CACHE, new GetUserInfoResquestListener(this),
                REQUEST_MY_INFO, this, null);
        if (MIUIUtil.isMIUIV6()) {
//            MIUIUtil.setBarBlackText(this);
        }
        contentEditText = (EditText) findViewById(R.id.content);
        curEditText = contentEditText;
        isPublishing = false;
        adapter = new PublishNewsPicAdapter();
        gridView.setAdapter(adapter);
        initEvent();
        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new MessageBoxPagerAdapter(this, mOnFaceOprateListener));
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        circlePageIndicator.setViewPager(pager);
        content.addTextChangedListener(this);
        LogUtil.addLog(this, "page-forum-new-topic");

        //提前获取圈子列表,防止卡顿
        if (myBuddy != null)
            ApiManager.listCircleForUserV2(this, requestCode, context.getApplicationContext(), myBuddy.getBuddyId());
    }

    private void initEvent() {
        title.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    curEditText = title;
                    return openCommentLinear();
                }
                return false;
            }
        });

        contentEditText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    curEditText = contentEditText;
                    return openCommentLinear();
                }
                return false;
            }
        });
        switchImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curStatus == 0) {
                    curStatus = 1;
                    switchImage.setImageResource(R.drawable.social_keyborad);
                    messagebox.setVisibility(View.VISIBLE);
                    BaseCommonUtil.hideKeyBoard(PublishDynamicActivity.this);
                } else {
                    curStatus = 0;
                    switchImage.setImageResource(R.drawable.social_smile);
                    messagebox.setVisibility(View.GONE);
                    curEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            curEditText.setFocusable(true);
                            curEditText.requestFocus();
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }, 300);
                }
            }
        });

        hideBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editLinear.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPause() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        super.onPause();
    }

    private boolean openCommentLinear() {
        editLinear.setVisibility(View.VISIBLE);
        switchImage.setImageResource(R.drawable.social_smile);
        messagebox.setVisibility(View.GONE);
        curStatus = 0;
        return false;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (content.getText().length() >= 1000) {
            showError("最多输入1000个字符!");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // 进行错误提示
    private void showError(String errorMsg) {
        Drawable drawable = getResources().getDrawable(R.drawable.social_send_error);
        drawable.setBounds(4, 4, 35, 35);
        content.setError(errorMsg, drawable);
        new EditTextShakeHelper(PublishDynamicActivity.this).shake(content);
    }

    public class PublishNewsPicAdapter extends BaseAdapter {

        private ArrayList<String> picList;

        private PicClickListener picClickListener;

        private TakePhotoClickListener takePhotoClickListener;

        PhotoClickListener photoClickListener;

        public int maxImageSelectCount = 9;

        public PublishNewsPicAdapter() {
            picClickListener = new PicClickListener();
            takePhotoClickListener = new TakePhotoClickListener();
            photoClickListener = new PhotoClickListener();
            picList = new ArrayList<String>();
            picList.add(null);
        }

        public void setPicList(ArrayList<String> picList) {
            this.picList = picList;
            this.picList.add(null);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case 0:
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(
                                R.layout.social_publish_dynamic_grid_take_photo, parent,
                                false);
                        convertView.setOnClickListener(takePhotoClickListener);
                    }
                    if (picList.size() - 1 >= maxImageSelectCount)
                        convertView.setVisibility(View.INVISIBLE);
                    else
                        convertView.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    ViewHolder holder;
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(
                                R.layout.social_publish_dynamic_grid, parent, false);
                        holder = new ViewHolder();
                        holder.image = (ImageView) convertView
                                .findViewById(R.id.image);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    imageLoader.displayImage("file://" + picList.get(position), holder.image, options);
                    holder.image.setTag(position);
                    holder.image.setOnClickListener(photoClickListener);
                    convertView.setOnClickListener(picClickListener);
                    break;
            }
            return convertView; // 返回ImageView
        }

        @Override
        public Object getItem(int position) {
            return picList.get(position);
        }

        @Override
        public int getCount() {
            return picList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == picList.size() - 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public List<String> getPicList() {
            return picList;
        }

        class PicClickListener implements OnClickListener {
            @Override
            public void onClick(View v) {
            }
        }

        class TakePhotoClickListener implements OnClickListener {
            @Override
            public void onClick(View v) {
                if (maxImageSelectCount - picList.size() + 1 <= 0) {
//                    Toast.makeText(NewPublishDynamicActivity.this, "最多只能选择" + maxImageSelectCount + "张图片",
//                            Toast.LENGTH_SHORT).show();
                    ZernToast.showToast(PublishDynamicActivity.this, "最多只能选择" + maxImageSelectCount + "张图片", Gravity.CENTER, 0, 0);
                    return;
                }
                Intent intent = new Intent(PublishDynamicActivity.this,
                        PhotoSelectedThumbnailActivity.class);
                intent.putExtra("maxImageSelectCount", maxImageSelectCount - picList.size() + 1);
                startActivityForResult(intent, THUMBNAIL_ACTIVITY);
            }
        }

        class PhotoClickListener implements OnClickListener {

            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Intent intent = new Intent(PublishDynamicActivity.this, PhotoDeleteSliderActivity.class);
                ArrayList<String> imageList = new ArrayList<String>(picList.subList(0, picList.size() - 1));
                intent.putExtra("imageList", (Serializable) imageList);
                intent.putExtra("index", position);
                startActivityForResult(intent, DELETE_ACTIVITY);
            }
        }

        public void addPic(String path) {
            picList.add(picList.size() - 1, path);
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        class ViewHolder {
            ImageView image;
        }

        public void removePic(int position) {
            picList.remove(position);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // thumbnailActivity返回
        if (requestCode == THUMBNAIL_ACTIVITY) {
            // 选择图片返回
            if (resultCode == PhotoSelectedThumbnailActivity.SELECT_PHOTO) {
                if (data != null) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> res = (ArrayList<String>) data
                            .getSerializableExtra("tu_ji");
                    for (String path : res) {
                        adapter.addPic(path);
                    }
                }
            } else if (resultCode == PhotoSelectedThumbnailActivity.TAKE_PHOTO) {
                if (data != null) {
                    adapter.addPic(data.getData().getPath());
                }
            }
        } else if (requestCode == DELETE_ACTIVITY) {
            if (data != null) {
                ArrayList<String> li = (ArrayList<String>) data.getSerializableExtra("imageList");
                if (li.size() < adapter.getPicList().size() - 1)
                    adapter.setPicList(li);
            }
        }
        curEditText.clearFocus();
    }

    // 销毁popupWindow 成功与否
    public boolean dismissPop() {
        if (listPopupWindow != null && listPopupWindow.isShowing()) {
            listPopupWindow.dismiss();
//            listPopupWindow.hide(this);
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPop();
        ZernToast.cancelToast();
    }

}
