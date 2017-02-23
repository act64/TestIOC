package com.jkyssocial.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.jkys.common.widget.CustomToolbar;
import com.jkys.jkysbase.BaseCommonUtil;
import com.jkys.jkysim.chat.emoji.MsgFaceUtils;
import com.jkys.jkyswidget.ProgressBarDialog;
import com.jkys.proxy.AppImpl;
import com.jkys.sociallib.R;
import com.jkys.sociallib.R2;
import com.jkyshealth.tool.CommonDialog;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.util.EditTextShakeHelper;
import com.jkyssocial.common.util.ZernToast;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.data.Topic;
import com.jkyssocial.data.TopicListResult;
import com.jkyssocial.event.PrePublishDynamicEvent;
import com.jkyssocial.event.PublishFinishEvent;
import com.jkyssocial.handler.PublishDynamicManager;
import com.jkyssocial.pageradapter.MessageBoxPagerAdapter;
import com.mintcode.base.BaseActivity;
import com.mintcode.widget.MyGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * @author xiaolong.yxl
 */
public class NewPublishDynamicActivity extends BaseActivity implements TextWatcher, RequestManager.RequestListener<TopicListResult> {

    private static final int THUMBNAIL_ACTIVITY = 0;
    private static final int DELETE_ACTIVITY = 1;
    private static final int REQUEST_MY_INFO = 2;

    private static final int PERSONAL_TYPE = 0;
    private static final int CIRCLE_TYPE = 1;

    //    @BindView(R2.id.back)
//    ImageView back;
//    @BindView(R2.id.toolbarTitle)
//    TextView toolbarTitle;
//    @BindView(R2.id.publishBtn)
//    TextView publishBtn;
    @BindView(R2.id.toolbar)
    CustomToolbar toolbar;
    @BindView(R2.id.title)
    EditText title;
    @BindView(R2.id.content)
    EditText content;
    @BindView(R2.id.gridView)
    MyGridView gridView;
    @BindView(R2.id.switchImage)
    ImageView switchImage;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.indicator)
    CirclePageIndicator indicator;
    @BindView(R2.id.hideBox)
    TextView hideBox;
    @BindView(R2.id.messagebox)
    RelativeLayout messagebox;
    @BindView(R2.id.editLinear)
    LinearLayout editLinear;
    @BindView(R2.id.titleDivider)
    View titleDivider;
    @BindView(R2.id.select_tags_fl)
    FlexboxLayout selectTagsFl;

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
    private boolean isH5Operation;//是和H5交互的，是发布成功或者失败之后才关掉当前页面

    static class GetUserInfoResquestListener implements RequestManager.RequestListener<GetUserInfoResult> {
        private WeakReference<NewPublishDynamicActivity> activityWR;

        public GetUserInfoResquestListener(NewPublishDynamicActivity activity) {
            activityWR = new WeakReference<NewPublishDynamicActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (activityWR == null || activityWR.get() == null) {
                return;
            }
            NewPublishDynamicActivity activity = activityWR.get();
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

    @OnClick(R2.id.left_rl)
    void onBackClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
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
                    .setLy(600).setClickListenerfirtst(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exitEditDialog.dissmiss();
                        }
                    }).setClickListenersecond(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exitEditDialog.dissmiss();
                            finish();
                        }
                    }).build(this);
        }
        exitEditDialog.show();
    }

    @OnClick(R2.id.right_rl)
    void onPublishClick(View view) {
        List<String> picList = adapter.getPicList();
        if ( TextUtils.isEmpty(title.getText().toString())) {
            ZernToast.showToast(this, "快写个响亮的标题吧！", Gravity.CENTER, 0, 0);
            return;
        }
        if (title.getText().length() > 25) {
            ZernToast.showToast(this, "标题不能超过25个字", Gravity.CENTER, 0, 0);
            return;
        }

        if (selectedTagFB == null) {
            ZernToast.showToast(this, "请选择一个话题", Gravity.CENTER, 0, 0);
            return;
        }

        if (picList.size() <= 1 && TextUtils.isEmpty(contentEditText.getText().toString())) {
            ZernToast.showToast(this, "调皮，至少发一张图啦", Gravity.CENTER, 0, 0);
            return;
        }
        AppImpl.getAppRroxy().addLog(NewPublishDynamicActivity.this, "event-forum-new-topic");
        if (!isPublishing) {
            isPublishing = true;
            Dynamic dynamic = new Dynamic();
            dynamic.setStatusAndroid(1);
            dynamic.setCreatedTime(System.currentTimeMillis());
            dynamic.setContent(contentEditText.getText().toString());
            dynamic.setTitle(title.getText().toString());
            dynamic.setOwner(myBuddy);
            dynamic.setTopic(topicList.get((Integer) selectedTagFB.getTag()));
            if (picList.size()>0) {
                picList.remove(picList.size() - 1);
            }
            dynamic.setImages(picList);

            EventBus.getDefault().post(new PrePublishDynamicEvent(dynamic));

            PublishDynamicManager.sendingDynamicList.add(0, dynamic);
            PublishDynamicManager publishDynamicManager = new PublishDynamicManager(dynamic, NewPublishDynamicActivity.this);
            publishDynamicManager.start();
            if (isH5Operation){
                showLoadDialog();
            }else{
                finish();
            }

        }
    }

    List<Topic> topicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_new_publish_dynamic);
        ButterKnife.bind(this);
        toolbar.setBackVisble(true, this);

        ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_WITH_CACHE, new GetUserInfoResquestListener(this),
                REQUEST_MY_INFO, this, null);
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
        AppImpl.getAppRroxy().addLog(this,"page-forum-new-topic", "page-forum-new-topic-trump");

        ApiManager.listTopic(this, context, null, 100, 1);
        isH5Operation = getIntent().getBooleanExtra("isH5",false);
        EventBus.getDefault().register(this);

    }

    @Override
    public void processResult(int requestCode, int resultCode, TopicListResult data) {
        if (data==null)return;
        topicList = data.getTopicList();
        int index = 0;
        for(Topic topic : topicList){
            addTagToSelectView("#" + topic.getName(), index);
            index++;
        }
    }

    FancyButton selectedTagFB;

    private void addTagToSelectView(String tag, int index) {
        FancyButton fancyButton = (FancyButton) LayoutInflater.from(this).inflate(R.layout.item_selected_tag, selectTagsFl, false);
        fancyButton.setText(tag);
        fancyButton.setTag(index);
        fancyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < selectTagsFl.getChildCount(); ++i){
                    FancyButton unselectTagFB = (FancyButton) selectTagsFl.getChildAt(i);
                    unselectTagFB.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                    unselectTagFB.setBorderColor(ContextCompat.getColor(getContext(), R.color.health_grayCC));
                    unselectTagFB.setTextColor(ContextCompat.getColor(getContext(), R.color.text_999999));
                }
                selectedTagFB = (FancyButton) v;
                selectedTagFB.getTag();
                selectedTagFB.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.social_primary));
                selectedTagFB.setBorderColor(ContextCompat.getColor(getContext(), R.color.social_primary));
                selectedTagFB.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            }
        });
        selectTagsFl.addView(fancyButton);
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
                    BaseCommonUtil.hideKeyBoard(NewPublishDynamicActivity.this);
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
        new EditTextShakeHelper(NewPublishDynamicActivity.this).shake(content);
    }

    public void onEventMainThread(PublishFinishEvent event){
        hideLoadDialog();
        if (event.getIsSuccess()) {
            this.finish();
        }else{
            isPublishing = false;
            Toast.makeText(this, "网络不稳定，请重新提交", Toast.LENGTH_LONG).show();
        }
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
                    ZernToast.showToast(NewPublishDynamicActivity.this, "最多只能选择" + maxImageSelectCount + "张图片", Gravity.CENTER, 0, 0);
                    return;
                }
                Intent intent = new Intent(NewPublishDynamicActivity.this,
                        PhotoSelectedThumbnailActivity.class);
                intent.putExtra("maxImageSelectCount", maxImageSelectCount - picList.size() + 1);
                startActivityForResult(intent, THUMBNAIL_ACTIVITY);
            }
        }

        class PhotoClickListener implements OnClickListener {

            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Intent intent = new Intent(NewPublishDynamicActivity.this, PhotoDeleteSliderActivity.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ZernToast.cancelToast();
    }

}
