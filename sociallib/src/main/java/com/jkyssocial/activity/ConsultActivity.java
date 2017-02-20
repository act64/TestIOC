package com.jkyssocial.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jkys.common.widget.CustomToolbar;
import com.jkyshealth.manager.MedicalApi;
import com.jkyshealth.manager.MedicalApiManager;
import com.jkyshealth.manager.MedicalVolleyListener;
import com.jkyshealth.tool.CommonDialog;
import com.jkysshop.util.HandlerH5Utils;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.util.ZernToast;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.ConsumeResult;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.data.SubmitAskResult;
import com.jkyssocial.data.SumbitQuestionBean;
import com.jkyssocial.event.PublishFinishEvent;
import com.jkyssocial.handler.UpLoadConsultManager;
import com.mintcode.area_patient.area_home.BannerActivity;
import com.mintcode.area_patient.area_task.TaskAPI;
import com.mintcode.area_patient.area_task.TaskListener;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.LogUtil;
import com.mintcode.util.MIUIUtil;
import com.mintcode.widget.MyGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

/**
 * 咨询页面
 * Created by xiaoke on 16/11/15.
 */

public class ConsultActivity extends BaseActivity implements TextWatcher, AdapterView.OnItemClickListener {

    private static final int THUMBNAIL_ACTIVITY = 0;
    private static final int DELETE_ACTIVITY = 1;
    private static final int REQUEST_MY_INFO = 2;

    private static final int PERSONAL_TYPE = 0;
    private static final int CIRCLE_TYPE = 1;
    @Bind(R.id.toolbar)
    CustomToolbar toolbar;
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.content)
    EditText content;
    @Bind(R.id.gridView)
    MyGridView gridView;
    @Bind(R.id.titleDivider)
    View titleDivider;
    @Bind(R.id.ask_doctor_item_ly)
    View ask_doctor_item_ly;
    @Bind(R.id.image_add)
    View image_add;
    @Bind(R.id.ask_btn)
    Button ask_btn;
    @Bind(R.id.question_cosume_noticetv)
    TextView question_cosume_noticetv;
    @Bind(R.id.nospare_tv)
    TextView nospare_tv;
    @Bind(R.id.add_photo_tv)
    TextView add_photo_tv;

    private int type = PERSONAL_TYPE;


    private ConsultActivity.PublishNewsPicAdapter adapter;


    /**
     * 是否正在发布
     */
    private boolean isPublishing;


    private int curStatus = 0;  //0 键盘；1 表情框

    private EditText curEditText;

    private  Buddy myBuddy;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private int countQuery;//刚开始进入需要查询一个用户账户和糖币接口。如果＝＝2就表示两个接口都调成功了。


    static class GetUserInfoResquestListener implements RequestManager.RequestListener<GetUserInfoResult> {
        private WeakReference<ConsultActivity> activityWR;

        public GetUserInfoResquestListener(ConsultActivity activity) {
            activityWR = new WeakReference<ConsultActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (activityWR == null || activityWR.get() == null) {
                return;
            }
            ConsultActivity activity = activityWR.get();
            activity.countQuery++;
            if (activity.countQuery==2){
                activity.hideLoadDialog();
            }
            if (data != null && data.getBuddy() != null) {
                if (requestCode == REQUEST_MY_INFO) {
                    activity.myBuddy = data.getBuddy();
                }
            }
        }
    }


    public static class MedicalAskDoctorImpl implements MedicalVolleyListener {
        private WeakReference<ConsultActivity> activityWR;

        MedicalAskDoctorImpl(ConsultActivity activity) {
            activityWR = new WeakReference<ConsultActivity>(activity);
        }

        @Override
        public void successResult(String result, String url) {
            if (activityWR == null || activityWR.get() == null) {
                if (url.equals(MedicalApi.COMMIT_QUESTION)) {
                    HandlerH5Utils.currentCallId = null;
                    HandlerH5Utils.currentXWalkView = null;
                }
                return;
            }
            ConsultActivity context = activityWR.get();
            context.isPublishing = false;
            if (url.equals(MedicalApi.CONSUME_COIN)) {
                context.countQuery++;
                if (context.countQuery==2){
                    context.hideLoadDialog();
                }
                ConsumeResult consumeResult = context.GSON.fromJson(result, new TypeToken<ConsumeResult>() {
                }.getType());
                if (consumeResult.getCount() == 0) {
                    context.question_cosume_noticetv.setText(context.getString(R.string.first_time_consult)+Math.abs(consumeResult.getCount())+context.getString(R.string.free_doctor_notice));
                    context.ask_btn.setBackgroundResource(R.drawable.blue_solid_retangle_bg);
                    context.ask_btn.setText("提问");
                    context.ask_btn.setClickable(true);
                    context.nospare_tv.setVisibility(View.GONE);
                    context.add_photo_tv.setTextColor(Color.parseColor("#999999"));
                } else if (consumeResult.getCount() > 0) {
                    context.question_cosume_noticetv.setText(context.getString(R.string.every_time_consult)+Math.abs(consumeResult.getCount())+context.getString(R.string.take_consume_coin_notice));
                    context.ask_btn.setBackgroundResource(R.drawable.blue_solid_retangle_bg);
                    context.ask_btn.setText("提问");
                    context.ask_btn.setClickable(true);
                    context.nospare_tv.setVisibility(View.GONE);
                    context.add_photo_tv.setTextColor(Color.parseColor("#999999"));
                } else if (consumeResult.getCount() < 0) {
                    context.question_cosume_noticetv.setText(context.getString(R.string.every_time_consult)+Math.abs(consumeResult.getCount())+context.getString(R.string.take_consume_coin_notice));
                    context.ask_btn.setBackgroundResource(R.drawable.gray_solid_retangle_bg);
                    context.ask_btn.setText("糖币不足");
                    context.ask_btn.setClickable(false);
                    context.nospare_tv.setVisibility(View.VISIBLE);
                    context.add_photo_tv.setTextColor(Color.parseColor("#e6e6e6"));

                }

            } else if (url.equals(MedicalApi.COMMIT_QUESTION)) {

                SubmitAskResult consumeResult = context.GSON.fromJson(result, new TypeToken<SubmitAskResult>() {
                }.getType());
                if (consumeResult.getBizCode() > 0) {
                    TaskAPI.getInstance(context).getTaskReward(new TaskListener(context, "首次提问"),"consult/first");
                } else {
                    context.hideLoadDialog();
                    Toast.makeText(context, "系统错误，请联系管理员", Toast.LENGTH_LONG).show();
                    HandlerH5Utils.jsOnFailCallBack(HandlerH5Utils.currentCallId, context, HandlerH5Utils.currentXWalkView, "");
                    HandlerH5Utils.currentCallId = null;
                    HandlerH5Utils.currentXWalkView = null;
                }


            }


        }

        @Override
        public void errorResult(String result, String url) {
            if (activityWR == null || activityWR.get() == null) {
                if (url.equals(MedicalApi.COMMIT_QUESTION)) {
                    HandlerH5Utils.currentCallId = null;
                    HandlerH5Utils.currentXWalkView = null;
                }
                return;
            }
            ConsultActivity baseActivity = activityWR.get();
            baseActivity.isPublishing = false;
            baseActivity.hideLoadDialog();
            if (url.equals(MedicalApi.COMMIT_QUESTION)) {
                Toast.makeText(baseActivity, "网络不稳定，请重新提交", Toast.LENGTH_LONG).show();
                HandlerH5Utils.jsOnFailCallBack(HandlerH5Utils.currentCallId, baseActivity, HandlerH5Utils.currentXWalkView, "");
                HandlerH5Utils.currentCallId = null;
                HandlerH5Utils.currentXWalkView = null;
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

    @OnClick(R.id.image_add)
    void onImageClick(View view) {
        if (adapter.maxImageSelectCount - adapter.picList.size() + 1 <= 0) {
            ZernToast.showToast(ConsultActivity.this, "最多只能选择" + adapter.maxImageSelectCount + "张图片", Gravity.CENTER, 0, 0);
            return;
        }
        Intent intent = new Intent(ConsultActivity.this,
                PhotoSelectedThumbnailActivity.class);
        intent.putExtra("maxImageSelectCount", adapter.maxImageSelectCount - adapter.picList.size() + 1);
        startActivityForResult(intent, THUMBNAIL_ACTIVITY);
    }

    @OnClick(R.id.left_rl)
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

    @OnClick(R.id.ask_btn)
    void onSubmit(View view) {
        List<String> picList = adapter.getPicList();

        if (!(title.getText().length() >= 5 && title.getText().length() <= 30)) {
            ZernToast.showToastForTime(this, "问题描述不得少于5个字或超过30个字，请调整您的问题内容", Gravity.CENTER, 0, 0,3000);
            return;
        }

        if (!(content.getText().length() >= 10)) {
            ZernToast.showToastForTime(this, "问题描述少于10个字，请您详细描述", Gravity.CENTER, 0, 0,3000);
            return;
        }


        if (!isPublishing) {
            isPublishing = true;
            SumbitQuestionBean sumbitAskResult = new SumbitQuestionBean();
            sumbitAskResult.setUser_id(myBuddy.getUserId());
            sumbitAskResult.setTitle(title.getText().toString());
            sumbitAskResult.setDescription(content.getText().toString());
            sumbitAskResult.setImages(picList);
            UpLoadConsultManager consultManager = new UpLoadConsultManager(sumbitAskResult, new MedicalAskDoctorImpl(this));
            consultManager.start();
            showLoadDialog();
        }

    }

    @OnClick(R.id.right_rl)
    void onPublishClick(View view) {
        Intent intent = new Intent(this,BannerActivity.class);
        intent.putExtra(HandlerH5Utils.PageToUrl,"http://static.91jkys.com/htmls/1480473509869zixunshuoming.html");
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult);
        ButterKnife.bind(this);
        toolbar.setBackVisble(true, this);
        showLoadDialog();
        ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_WITH_CACHE, new ConsultActivity.GetUserInfoResquestListener(this),
                REQUEST_MY_INFO, this, null);
        MedicalApiManager.getInstance().getConsumeCoin(new MedicalAskDoctorImpl(this));
        if (MIUIUtil.isMIUIV6()) {
//            MIUIUtil.setBarBlackText(this);
        }
        curEditText = content;
        isPublishing = false;
        adapter = new ConsultActivity.PublishNewsPicAdapter();
        gridView.setAdapter(adapter);
        initEvent();
        content.addTextChangedListener(this);
        title.addTextChangedListener(this);
        LogUtil.addLog(this, "page-new-ask-trump");
        ask_btn.setClickable(false);
        EventBus.getDefault().register(this);

    }

    private long lastCurrentMillons;

    @Override
    protected void onResume() {
        super.onResume();
        lastCurrentMillons = System.currentTimeMillis();
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

        content.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    curEditText = content;
                    return openCommentLinear();
                }
                return false;
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        long time = System.currentTimeMillis() - lastCurrentMillons;
        LogUtil.addLog(this, "page-new-ask-"+time+"-trump");
    }

    private boolean openCommentLinear() {
        curStatus = 0;
        return false;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public class PublishNewsPicAdapter extends BaseAdapter {

        private ArrayList<String> picList;

        private ConsultActivity.PublishNewsPicAdapter.PicClickListener picClickListener;

        private ConsultActivity.PublishNewsPicAdapter.TakePhotoClickListener takePhotoClickListener;

        ConsultActivity.PublishNewsPicAdapter.PhotoClickListener photoClickListener;

        public int maxImageSelectCount = 5;

        public PublishNewsPicAdapter() {
            picClickListener = new ConsultActivity.PublishNewsPicAdapter.PicClickListener();
            takePhotoClickListener = new ConsultActivity.PublishNewsPicAdapter.TakePhotoClickListener();
            photoClickListener = new ConsultActivity.PublishNewsPicAdapter.PhotoClickListener();
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
                    ConsultActivity.PublishNewsPicAdapter.ViewHolder holder;
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(
                                R.layout.social_publish_dynamic_grid, parent, false);
                        holder = new ConsultActivity.PublishNewsPicAdapter.ViewHolder();
                        holder.image = (ImageView) convertView
                                .findViewById(R.id.image);
                        convertView.setTag(holder);
                    } else {
                        holder = (ConsultActivity.PublishNewsPicAdapter.ViewHolder) convertView.getTag();
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

        class PicClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
            }
        }

        class TakePhotoClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                if (maxImageSelectCount - picList.size() + 1 <= 0) {
//                    Toast.makeText(NewPublishDynamicActivity.this, "最多只能选择" + maxImageSelectCount + "张图片",
//                            Toast.LENGTH_SHORT).show();
                    ZernToast.showToast(ConsultActivity.this, "最多只能选择" + maxImageSelectCount + "张图片", Gravity.CENTER, 0, 0);
                    return;
                }
                Intent intent = new Intent(ConsultActivity.this,
                        PhotoSelectedThumbnailActivity.class);
                intent.putExtra("maxImageSelectCount", maxImageSelectCount - picList.size() + 1);
                startActivityForResult(intent, THUMBNAIL_ACTIVITY);
            }
        }

        class PhotoClickListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Intent intent = new Intent(ConsultActivity.this, PhotoDeleteSliderActivity.class);
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

        if (adapter.getCount() == 1) {
            ask_doctor_item_ly.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        } else {
            ask_doctor_item_ly.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
        curEditText.clearFocus();
    }

    public void onEventMainThread(PublishFinishEvent event){
        hideLoadDialog();
        if (event.getIsSuccess()) {
            HandlerH5Utils.jsOnSuccessCallBackLogin(HandlerH5Utils.currentCallId, this, HandlerH5Utils.currentXWalkView, "");
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZernToast.cancelToast();
        EventBus.getDefault().unregister(this);
    }



    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }

}
