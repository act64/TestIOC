package com.jkyssocial.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.jkysbase.BaseCommonUtil;
import com.jkys.jkysbase.TimeUtil;
import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkysim.chat.emoji.MsgFaceUtils;
import com.jkys.jkyswidget.ActionSheetDialog;
import com.jkys.jkyswidget.ConfirmTipsDialog;
import com.jkys.jkyswidget.MyListView;
import com.jkys.proxy.AppImpl;
import com.jkys.sociallib.R;
import com.jkys.tools.DeviceUtil;
import com.jkys.tools.ImageUtil;
import com.jkysshop.model.ShareStatus;
import com.jkyssocial.adapter.CommentListAdapter;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.AddCommentResult;
import com.jkyssocial.data.AddReplyResult;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.Comment;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.GetDynamicResult;
import com.jkyssocial.data.Reply;
import com.jkyssocial.event.DeleteDynamicEvent;
import com.jkyssocial.pageradapter.MessageBoxPagerAdapter;
import com.mintcode.area_patient.area_share.ShareUtil;
import com.mintcode.area_patient.area_task.TaskRewardPOJO;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * 动态详情页
 *
 * @author yangxiaolong
 */
public class DynamicDetailActivity extends BaseActivity implements CommentListAdapter.RefreshListener,
        CommentListAdapter.OpenReplyListener, View.OnTouchListener, IWeiboHandler.Response {

    private static final int COMMENT_LIST_REQUEST_CODE = 1;

    private static final int ZAN_REQUEST_CODE = 2;
    private static final int ADD_COMMENT = 3;
    private static final int REPORT_REQUEST_CODE = 10001;

    View emptyView;

    MyListView mListView;

    DynamicDetailAdapter mAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    // 最大显示多少行
    protected final int maxImageRowCount = 3;

    // 最大显示多少列
    protected final int maxImageColumnCount = 3;

    protected final int maxImageCount = maxImageRowCount * maxImageColumnCount;

//    FavorLayout mFavorLayout;

    LinearLayout editLinear;

    CommentListener commentListener;

    Dynamic dynamic;

    TextView zanTextView;

    TextView commentTextView;

    EditText editText;

    ImageView switchMessageBox;
    int curStatus = 0;  //0 键盘；1 表情框

    TextView sendComment;

    View messageBox;

    View footView;
    //     private  ImageView likeView;
//    private TextView likeListView;
    private TextView commentCount;
    private TextView zanCount;
    private View moreView;
    private LinearLayout favorLayout;
    OnImageClickListener onImageClickListener;
    OnCommentLongClickListener onCommentClickListener;
    OnReplyLongClickListener onReplyLongClickListener;
    GoPersonalSpaceListener goPersonalSpaceListener;

    private boolean zanChanged = false;

    private Buddy myBuddy;

    Intent resultData = new Intent();

    private boolean sendCommentFlag = false;

    long startTime, endTime;

    /**
     * 举报监听,暂时removeComment\removeReply也公用这个监听，但是对响应暂不做处理
     */
    static class NetWorkResultRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        WeakReference<DynamicDetailActivity> activityWR;

        public NetWorkResultRequestListener(DynamicDetailActivity activity) {
            activityWR = new WeakReference<DynamicDetailActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            DynamicDetailActivity activity = activityWR.get();
            if (requestCode == REPORT_REQUEST_CODE) {
                Toast toast = Toast.makeText(activity.getApplicationContext(), "举报成功", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    /**
     * 获取动态监听
     */
    static class GetDynamicRequestListener implements RequestManager.RequestListener<GetDynamicResult> {
        WeakReference<DynamicDetailActivity> activityWR;
        private FrameLayout headerFrame;

        public GetDynamicRequestListener(DynamicDetailActivity activity, FrameLayout headerFrame) {
            activityWR = new WeakReference<DynamicDetailActivity>(activity);
            this.headerFrame = headerFrame;
        }

        @Override
        public void processResult(int requestCode, int resultCode, GetDynamicResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            DynamicDetailActivity activity = activityWR.get();
            if (data != null && data.getDynamic() != null) {
                activity.dynamic = data.getDynamic();
                if (activity.dynamic.getLikeFlag() == 1) {
                    activity.zanTextView.setCompoundDrawablesWithIntrinsicBounds(activity.getApplicationContext().getResources().getDrawable(R.drawable.social_zan_new_selected), null, null, null);
                } else {
                    activity.zanTextView.setCompoundDrawablesWithIntrinsicBounds(activity.getApplicationContext().getResources().getDrawable(R.drawable.social_zan_new), null, null, null);
                }
                View headerView = activity.getHeaderView(activity.dynamic);
                headerFrame.removeAllViews();
                headerFrame.addView(headerView);
            }
        }
    }

    /**
     * 增加评论监听
     */
    static class AddCommentRequestListener implements RequestManager.RequestListener<AddCommentResult> {
        private WeakReference<DynamicDetailActivity> activityWR;

        public AddCommentRequestListener(DynamicDetailActivity activity) {
            activityWR = new WeakReference<DynamicDetailActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, AddCommentResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            DynamicDetailActivity activity = activityWR.get();
            if (data != null && data.getComment() != null) {
                activity.mAdapter.addItem(data.getComment());
                activity.dynamic.incrCommentCount();
                activity.commentCount.setText("评论 " + activity.dynamic.getCommentCount());
//                                    commentTextView.setText(dynamic.getCommentCount() > 0 ? String.valueOf(dynamic.getCommentCount()) : "评论");
                if (activity.emptyView.getVisibility() == View.VISIBLE) {
                    activity.emptyView.setVisibility(View.GONE);
                }
//                TaskAPI.getInstance(activity.getApplicationContext()).getTaskReward(new TaskListener(activity, "每天第一次评论"), "article/reply");
//                TaskAPI.getInstance(activity).CallAPITaskReward(activity, "首次回复动态", "每日回复动态", "answer_post/first", "article/reply");
                AppImpl.getAppRroxy().CallAPITaskReward(activity, "首次回复动态", "每日回复动态", "answer_post/first", "article/reply");
            }
            activity.resetSendComment();
        }
    }

    /**
     * 增加回复监听
     */
    static class AddReplyRequestListener implements RequestManager.RequestListener<AddReplyResult> {
        private WeakReference<DynamicDetailActivity> activityWR;

        public AddReplyRequestListener(DynamicDetailActivity activity) {
            activityWR = new WeakReference<DynamicDetailActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, AddReplyResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            DynamicDetailActivity activity = activityWR.get();
            if (data != null && data.getReply() != null) {
                activity.mAdapter.addReply(data.getReply());
                activity.dynamic.incrCommentCount();
                activity.commentCount.setText("评论 " + activity.dynamic.getCommentCount());
//                TaskAPI.getInstance(activity.getApplicationContext()).getTaskReward(new TaskListener(activity, "每天第一次回复"), "article/reply");
                AppImpl.getAppRroxy().CallAPITaskReward(activity, "首次回复动态", "每日回复动态", "answer_post/first", "article/reply");
//                                    commentTextView.setText(dynamic.getCommentCount() > 0 ? String.valueOf(dynamic.getCommentCount()) : "评论");
            }
            activity.resetSendComment();
        }
    }

    /**
     * 移除动态监听
     */
    static class RemoveDynamicRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<DynamicDetailActivity> activityWR;

        public RemoveDynamicRequestListener(DynamicDetailActivity activity) {
            activityWR = new WeakReference<DynamicDetailActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            DynamicDetailActivity activity = activityWR.get();
            EventBus.getDefault().post(new DeleteDynamicEvent(activity.dynamic));
        }
    }


    MessageBoxPagerAdapter.OnFaceOprateListener mOnFaceOprateListener = new MessageBoxPagerAdapter.OnFaceOprateListener() {

        @Override
        public void onFaceSelected(String unicode) {
            if (null != unicode) {
                int selection = editText.getSelectionStart();
                editText.getText().insert(selection, unicode);
            }
        }

        @Override
        public void onFaceDeleted() {
            int selection = editText.getSelectionStart();
            String text = editText.getText().toString();
            if (selection > 0) {
                if (selection > 1) {
                    String text2 = text.substring(selection - 2);
                    if (MsgFaceUtils.faceImgUnicodes.contains(text2)) {
                        editText.getText().delete(selection - 2, selection);
                        return;
                    }
                }
                editText.getText().delete(selection - 1, selection);
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_dynamic_detail);
        dynamic = (Dynamic) getIntent().getSerializableExtra("dynamic");
        if (dynamic == null) {
            dynamic = new Dynamic();
            dynamic.setDynamicId(getIntent().getStringExtra("dynamicID").trim());
//            updateDynamicByNetWork();
        }
        myBuddy = CommonInfoManager.getInstance().getUserInfo(this);

        findViewById(R.id.right_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBuddy == null)
                    return;
                if (dynamic.getOwner().getBuddyId().equals(myBuddy.getBuddyId())) {
                    RemoveDialog myDialog = new RemoveDialog(DynamicDetailActivity.this, dynamic.getDynamicId(), null, null);
                    myDialog.show();
                } else {
                    ReportDialog myDialog = new ReportDialog(DynamicDetailActivity.this, 1, dynamic.getDynamicId());
                    myDialog.show();
                }
            }
        });

        findViewById(R.id.left_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        emptyView = findViewById(R.id.empty);
        mListView = (MyListView) findViewById(R.id.listView);

        // Set the adapter
        mListView.setOnTouchListener(this);

        onImageClickListener = new OnImageClickListener();
        onCommentClickListener = new OnCommentLongClickListener();
        onReplyLongClickListener = new OnReplyLongClickListener();
        goPersonalSpaceListener = new GoPersonalSpaceListener();

        zanTextView = (TextView) findViewById(R.id.zan);
        commentTextView = (TextView) findViewById(R.id.comment);

        commentListener = new CommentListener();

        zanTextView.setOnClickListener(new ZanListener());
        commentTextView.setOnClickListener(commentListener);
        editLinear = (LinearLayout) findViewById(R.id.editLinear);
        editText = (EditText) editLinear.findViewById(R.id.commentEdit);
        editText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (messageBox.getVisibility() == View.VISIBLE) {
                    curStatus = 0;
                    switchMessageBox.setImageResource(R.drawable.social_smile);
                    messageBox.setVisibility(View.GONE);
                }
                return false;
            }
        });
        switchMessageBox = (ImageView) editLinear.findViewById(R.id.switchImage);
        sendComment = (TextView) editLinear.findViewById(R.id.sendComment);
        messageBox = editLinear.findViewById(R.id.messagebox);
        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new MessageBoxPagerAdapter(this, mOnFaceOprateListener));
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        circlePageIndicator.setViewPager(pager);

        final FrameLayout headerFrame = new FrameLayout(this);
        //TODO
        ApiManager.getDynamic(new GetDynamicRequestListener(this, headerFrame), 0, this, dynamic.getDynamicId());
        headerFrame.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View headerView = getHeaderView(dynamic);
        headerFrame.addView(headerView);
        mListView.addHeaderView(headerFrame);
//        updateDynamicByNetWork();

        mAdapter = new DynamicDetailAdapter(this, mListView, dynamic, this, this, emptyView, commentListener);
        mListView.setAdapter(mAdapter);
        mListView.setOnLoadListener(mAdapter);
        swipeRefreshLayout.setOnRefreshListener(mAdapter);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        if (getIntent().getBooleanExtra("comment", false)) {
            if (dynamic.getCommentCount() > 0) {
                mAdapter.setSetSelectionAfterHeaderView();
            } else {
                openCommentLinear(null);
            }
        }
        if (AppImpl.getAppRroxy().isNeedNewMain() )
            AppImpl.getAppRroxy().addLog(this, "page-forum-topic-detail-trump-" + dynamic.getDynamicId());
        else
            AppImpl.getAppRroxy().addLog(this, "page-forum-topic-detail-" + dynamic.getDynamicId());
        EventBus.getDefault().register(this);
    }

    @Override
    public void finish() {
        resultData.putExtra("dynamic", dynamic);
        setResult(RESULT_OK, resultData);
        super.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        commitZanChanged();
        if (AppImpl.getAppRroxy().isNeedNewMain() ) {
            endTime = System.currentTimeMillis();
            AppImpl.getAppRroxy().addLog(this, "page-forum-topic-detail-trump-" + dynamic.getDynamicId() + "-" + (startTime - endTime));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        editLinear.setVisibility(View.GONE);
        BaseCommonUtil.hideKeyBoard(this);
        return false;
    }

    private View getHeaderView(Dynamic dynamic) {
        List<String> imageList = dynamic.getImages();
        if (imageList == null || imageList.isEmpty()) {
            return getTextView(dynamic);
        } else if (imageList.size() == 1) {
            return getOneImageView(dynamic);
        } else if (imageList.size() == 4) {
            return getFourImageView(dynamic);
        } else {
            return getMultiImageView(dynamic);
        }
    }

    protected void setSameView(View view, final Dynamic dynamic) {
        View headView = view.findViewById(R.id.header);
        ImageView avatar = (ImageView) headView
                .findViewById(R.id.avatar);
        TextView nickname = (TextView) headView
                .findViewById(R.id.nickname);
        TextView createTime = (TextView) headView
                .findViewById(R.id.createTime);
        TextView title = (TextView) view
                .findViewById(R.id.title);
        TextView content = (TextView) view
                .findViewById(R.id.content);

        footView = view.findViewById(R.id.footer);
        if (dynamic.getCircle() != null && !AppImpl.getAppRroxy().isNeedNewMain() ) {
            TextView comeFrom = (TextView) footView
                    .findViewById(R.id.comeFrom);
            comeFrom.setText(dynamic.getCircle().getTitle());
            comeFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dynamic.getStatusAndroid() != 0) {
                        return;
                    }
                    String error = Circle.getErrorMessage(dynamic.getCircle());
                    if (error != null) {
                        Toast.makeText(DynamicDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(DynamicDetailActivity.this, CircleMainActivity.class);
                    intent.putExtra("circle", dynamic.getCircle());
                    startActivity(intent);
                }
            });
        } else {
            View come = footView.findViewById(R.id.come);
            come.setVisibility(View.GONE);
        }
//        TextView renqi = (TextView) footView.findViewById(R.id.renqi);
//        if(dynamic.getViewCount() >= 10000){
//            renqi.setText("人气 " + String.format("%.1f", ((float)dynamic.getViewCount()) / 10000) + "万");
//        }else{
//            renqi.setText("人气 " + dynamic.getViewCount());
//        }

        updateLikerList();
        if (dynamic.getOwner() != null) {
            if (!TextUtils.isEmpty(dynamic.getOwner().getImgUrl())) {
                ImageManager.loadImageByDefaultImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + dynamic.getOwner().getImgUrl(), null,
                        avatar, R.drawable.social_new_avatar);
            } else {
                avatar.setImageResource(R.drawable.social_new_avatar);
            }

            nickname.setText(dynamic.getOwner().getUserName());
            ImageView vFlag = (ImageView) headView.findViewById(R.id.vFlag);
            ImageManager.setVFlag(vFlag, dynamic.getOwner());
        }
        createTime.setText(TimeUtil.getInterval(dynamic.getCreatedTime()));
        title.setTextIsSelectable(true);
        title.setVisibility(TextUtils.isEmpty(dynamic.getTitle()) ? View.GONE : View.VISIBLE);
        title.setText(dynamic.getTitle());
        if (!TextUtils.isEmpty(dynamic.getContent())) {
            content.setVisibility(View.VISIBLE);
            content.setTextIsSelectable(true);
            content.setText(dynamic.getContent());
            content.setTag(dynamic);
        } else {
            content.setVisibility(View.GONE);
        }

        headView.setTag(dynamic.getOwner());
//        headView.setOnClickListener(goPersonalSpaceListener);
        avatar.setTag(dynamic.getOwner());
        avatar.setOnClickListener(goPersonalSpaceListener);
        nickname.setTag(dynamic.getOwner());
        nickname.setOnClickListener(goPersonalSpaceListener);
        nickname.setTextColor(getResources().getColor(R.color.social_primary));
    }

    private void updateLikerList() {
        favorLayout = (LinearLayout) footView.findViewById(R.id.favor_count_layout);
        commentCount = (TextView) favorLayout.findViewById(R.id.commentCount);
        commentCount.setText("评论" + dynamic.getCommentCount());
        zanCount = (TextView) favorLayout.findViewById(R.id.zanCount);
        zanCount.setText("赞 " + dynamic.getLikeCount());
        moreView = favorLayout.findViewById(R.id.more);
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DynamicDetailActivity.this, ListZanActivity.class);
                intent.putExtra("dynamicId", dynamic.getDynamicId());
                intent.putExtra("zanCount", dynamic.getLikeCount());
                startActivity(intent);
            }
        });
    }

    private View getTextView(Dynamic dynamic) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(
                R.layout.social_detail_text_dynamic, mListView, false);
        setSameView(headerView, dynamic);
        return headerView;
    }

    private View getOneImageView(Dynamic dynamic) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(
                R.layout.social_detail_one_image_dynamic, mListView, false);
        setSameView(headerView, dynamic);
        ImageView imageView = (ImageView) headerView
                .findViewById(R.id.image);
        int dp = DeviceUtil.getDensity();
        String imageUrl = dynamic.getImages().get(0);
        try {
            String wh = imageUrl.substring(imageUrl.lastIndexOf('_') + 1, imageUrl.lastIndexOf('.'));
            String[] s = wh.split("x");
            int mWidth = Integer.parseInt(s[0]) * dp, mHeight = Integer.parseInt(s[1]) * dp;
            int maxWidth = 200 * dp, maxHeight = 200 * dp;
            ImageUtil.ImageParam imageParam = ImageUtil.getRatioImageParam(mWidth, mHeight, maxWidth, maxHeight);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageParam.width, imageParam.height);
            params.setMargins(10 * dp, 0, 10 * dp, 10 * dp);
            imageView.setLayoutParams(params);
        } catch (Exception e) {

        }
        String url = dynamic.getImages().get(0);
        if (!TextUtils.isEmpty(url)) {
            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + ImageUtil.getSmallImageUrl(url), null, imageView);
        }

        imageView.setTag(R.id.tag_first, dynamic.getImages());
        imageView.setTag(R.id.tag_second, 0);
        imageView.setOnClickListener(onImageClickListener);

        return headerView;
    }

    private View getFourImageView(Dynamic dynamic) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(
                R.layout.social_detail_multi_image_dynamic, mListView, false);
        setSameView(headerView, dynamic);
        ViewGroup contentView = (ViewGroup) headerView
                .findViewById(R.id.imageContent);
        LinearLayout[] imageLinearLayouts = new LinearLayout[3];
        for (int i = 0; i < 2; ++i) {
            imageLinearLayouts[i] = (LinearLayout) inflater
                    .inflate(R.layout.social_include_listitem_content_2_line,
                            contentView, false);
            setImageView(3,
                    imageLinearLayouts[i], inflater, R.color.white);
            contentView.addView(imageLinearLayouts[i]);
        }
        List<String> picUrls = dynamic.getImages();
        int index = 0;
        for (int i = 0; i < 2; ++i) {
            imageLinearLayouts[i].setVisibility(View.VISIBLE);
            for (int j = 0; j < 2; ++j) {
                ImageView imageView = (ImageView) imageLinearLayouts[i]
                        .getChildAt(j);
                imageView.setTag(R.id.tag_second, index);
                imageView.setTag(R.id.tag_first, dynamic.getImages());
                if (!TextUtils.isEmpty(picUrls.get(index))) {
                    ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + picUrls.get(index), null, imageView);
                }
                index++;

                imageView.setOnClickListener(onImageClickListener);
                imageView.setVisibility(View.VISIBLE);
            }
        }
        return headerView;
    }

    private View getMultiImageView(Dynamic dynamic) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(
                R.layout.social_detail_multi_image_dynamic, mListView, false);
        setSameView(headerView, dynamic);
        ViewGroup contentView = (ViewGroup) headerView
                .findViewById(R.id.imageContent);
        LinearLayout[] imageLinearLayouts = new LinearLayout[maxImageRowCount];
        for (int i = 0; i < maxImageRowCount; ++i) {
            imageLinearLayouts[i] = (LinearLayout) inflater
                    .inflate(R.layout.social_include_listitem_content_2_line,
                            contentView, false);
            setImageView(maxImageColumnCount,
                    imageLinearLayouts[i], inflater, R.color.bg_page);
            contentView.addView(imageLinearLayouts[i]);
        }
        int imageCount = dynamic.getImages().size() < maxImageCount ? dynamic.getImages().size() : maxImageCount;
        int imageRowCount = (imageCount == 0) ? 0 : (imageCount - 1) / maxImageColumnCount + 1;
        int index = 0;
        List<String> picUrls = dynamic.getImages();
        for (int i = 0; i < maxImageRowCount; ++i) {
            if (i < imageRowCount) {
                imageLinearLayouts[i].setVisibility(View.VISIBLE);
                if (i != imageRowCount - 1) {
                    for (int j = 0; j < maxImageColumnCount; ++j) {
                        ImageView imageView = (ImageView) imageLinearLayouts[i]
                                .getChildAt(j);
                        imageView.setTag(R.id.tag_first, dynamic.getImages());
                        imageView.setTag(R.id.tag_second, index);
                        imageView.setOnClickListener(onImageClickListener);
                        if (!TextUtils.isEmpty(ImageUtil.getSmallImageUrl(picUrls.get(index)))) {
                            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() +
                                    ImageUtil.getSmallImageUrl(picUrls.get(index)), null, imageView);
                        }
                        index++;
                        imageView.setVisibility(View.VISIBLE);
                    }
                } else {
                    int count = (imageCount - 1) % maxImageColumnCount + 1;
                    for (int j = 0; j < maxImageColumnCount; ++j) {
                        if (j < count) {
                            ImageView imageView = (ImageView) imageLinearLayouts[i]
                                    .getChildAt(j);
                            imageView.setTag(R.id.tag_first, dynamic.getImages());
                            imageView.setTag(R.id.tag_second, index);
                            imageView.setOnClickListener(onImageClickListener);
                            String picUrl = picUrls.get(index++);
                            if (!TextUtils.isEmpty(picUrl)) {
                                ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH()
                                        + ImageUtil.getSmallImageUrl(picUrl), null, imageView);
                            }
                            imageView.setVisibility(View.VISIBLE);
                        } else {
                            ImageView imageView = (ImageView) imageLinearLayouts[i]
                                    .getChildAt(j);
                            imageView.setImageResource(R.color.white);
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            } else {
                imageLinearLayouts[i].setVisibility(View.GONE);
            }
        }
        return headerView;
    }

    protected void setImageView(int imageCount, LinearLayout imageLinearLayout,
                                LayoutInflater inflater, int colorId) {
        for (int j = 0; j < imageCount; ++j) {
            ImageView imageView = (ImageView) inflater.inflate(
                    R.layout.social_include_listitem_content_image, imageLinearLayout,
                    false);
            imageView.setBackgroundResource(colorId);
            if (j != 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                int marginLeft = 5 * DeviceUtil.getDensity();
                layoutParams.setMargins(marginLeft, 0, 0, 0);
                imageView.setLayoutParams(layoutParams);
            }
            imageLinearLayout.addView(imageView);
        }
    }

    @Override
    public void stopRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void openReply(CommentListAdapter.AddCommentParam param) {
        openCommentLinear(param);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        editLinear.setVisibility(View.GONE);
        BaseCommonUtil.hideKeyBoard(this);
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
//        shareUtil.getmWeiboShareAPI().handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                AppImpl.getAppRroxy().addLog(this, "event-topic-share-" + dynamic.getDynamicId() + "-新浪微博");
                Toast.makeText(this, "微博分享成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "微博分享成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    class UserNameClickableSpan extends ClickableSpan {

        private Buddy buddy;

        UserNameClickableSpan(Buddy buddy) {
            this.buddy = buddy;
        }

        @Override
        public void onClick(View v) {
            if (AppImpl.getAppRroxy().isNeedNewMain() )
                return;
            Intent intent = new Intent(DynamicDetailActivity.this, NewPersonalSpaceActivity.class);
            intent.putExtra("otherBuddy", buddy);
            startActivity(intent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.social_primary));
        }

    }

    class ZanListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            zanChanged = !zanChanged;
            if (dynamic.getLikeFlag() == 1) {
                AppImpl.getAppRroxy().addLog(DynamicDetailActivity.this, "event-forum-cancel-praise");
                dynamic.setLikeFlag(0);
//                List<Buddy> li = dynamic.getLikerList();
//                if (li == null) return;
//                for (int i = 0; i < li.size(); ++i) {
//                    if (li.get(i).getBuddyId().equals(myBuddy.getBuddyId())) {
//                        dynamic.getLikerList().remove(i);
//                        updateLikerList();
//                        break;
//                    }
//                }
//                zanTextView.setText(String.valueOf(dynamic.decrLikeCount()));
                dynamic.decrLikeCount();
                zanCount.setText("赞  " + dynamic.getLikeCount());
                zanTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.social_zan_new), null, null, null);
            } else {
//                mFavorLayout.addFavor();
                AppImpl.getAppRroxy().addLog(DynamicDetailActivity.this, "event-forum-praise");
//                zanTextView.setText(String.valueOf(dynamic.incrLikeCount()));
                zanTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.social_zan_new_selected), null, null, null);
                dynamic.setLikeFlag(1);
//                if (dynamic.getLikerList() == null) {
//                    dynamic.setLikerList(new ArrayList<Buddy>());
//                }
//                dynamic.getLikerList().add(0, myBuddy);
                dynamic.incrLikeCount();
                zanCount.setText("赞  " + dynamic.getLikeCount());
            }
        }
    }

    public class CommentListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            openCommentLinear(null);
        }
    }

    private void openCommentLinear(CommentListAdapter.AddCommentParam param) {
        editLinear.setVisibility(View.VISIBLE);
        if (param != null && param.targetUserName != null) {
            editText.setHint("回复 " + param.targetUserName);
        }
        if (param == null && editText.getHint() != null) {
            editText.setText(null);
        }
        if (param != null && param.targetUserName != null && editText.getHint() != null && !param.targetUserName.equals(editText.getHint())) {
            editText.setText(null);
        }
        if (curStatus == 0) {
            switchMessageBox.setImageResource(R.drawable.social_smile);
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setFocusable(true);
                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }, 300);
            messageBox.setVisibility(View.GONE);
        } else {
            switchMessageBox.setImageResource(R.drawable.social_keyborad);
            messageBox.setVisibility(View.VISIBLE);
            BaseCommonUtil.hideKeyBoard(this);
        }
        switchMessageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curStatus == 0) {
                    curStatus = 1;
                    switchMessageBox.setImageResource(R.drawable.social_keyborad);
                    messageBox.setVisibility(View.VISIBLE);
                    BaseCommonUtil.hideKeyBoard(DynamicDetailActivity.this);
                } else {
                    curStatus = 0;
                    switchMessageBox.setImageResource(R.drawable.social_smile);
                    messageBox.setVisibility(View.GONE);
                    editText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editText.setFocusable(true);
                            editText.requestFocus();
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }, 300);
                }
            }
        });
        sendComment.setTag(param);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sendCommentFlag) {
                    sendCommentFlag = true;
                    sendComment.setClickable(true);
                } else {
                    return;
                }
                CommentListAdapter.AddCommentParam param = (CommentListAdapter.AddCommentParam) v.getTag();
                if (param == null) {
                    String content = editText.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        ApiManager.addComment(new AddCommentRequestListener(DynamicDetailActivity.this), ADD_COMMENT, DynamicDetailActivity.this, dynamic.getDynamicId(),
                                content);
                        AppImpl.getAppRroxy().addLog(DynamicDetailActivity.this, "event-forum-new-comment");
                    }
                } else {
                    String content = editText.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        ApiManager.addReply(new AddReplyRequestListener(DynamicDetailActivity.this), ADD_COMMENT, DynamicDetailActivity.this, dynamic.getDynamicId(),
                                param.commentId, param.targetBuddyId, content);
                        v.setTag(null);
                        AppImpl.getAppRroxy().addLog(DynamicDetailActivity.this, "event-forum-new-reply");
                    }
                }
            }
        });
    }

    private void resetSendComment() {
        editLinear.setVisibility(View.GONE);
        editText.setText(null);
        sendCommentFlag = false;
        sendComment.setClickable(false);
        BaseCommonUtil.hideKeyBoard(this);
    }

    public class OnImageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            List<String> imageList = (List<String>) v.getTag(R.id.tag_first);
            int index = (int) v.getTag(R.id.tag_second);
            Intent intent = new Intent(DynamicDetailActivity.this, ImageSliderActivity.class);
            intent.putExtra("imageList", (Serializable) imageList);
            intent.putExtra("index", index);
            DynamicDetailActivity.this.startActivity(intent);
        }
    }

    public class OnCommentLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            v.setTag(R.id.tag_third, "longClick");
            if (myBuddy == null)
                return true;
            Comment comment = (Comment) v.getTag();
            if (comment.getOwner().getBuddyId().equals(myBuddy.getBuddyId())) {
                RemoveDialog myDialog = new RemoveDialog(DynamicDetailActivity.this, comment.getDynamicId(), comment.getCommentId(), null);
                myDialog.show();
            } else {
                ReportDialog myDialog = new ReportDialog(DynamicDetailActivity.this, 2, comment.getCommentId());
                myDialog.show();
            }
            return true;
        }
    }

    public class OnReplyLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            v.setTag(R.id.tag_third, "longClick");

            if (myBuddy == null)
                return true;
            Reply reply = (Reply) v.getTag();
            if (reply.getOwner().getBuddyId().equals(myBuddy.getBuddyId())) {
                RemoveDialog myDialog = new RemoveDialog(DynamicDetailActivity.this, dynamic.getDynamicId(), reply.getCommentId(), reply.getReplyId());
                myDialog.show();
            } else {
                ReportDialog myDialog = new ReportDialog(DynamicDetailActivity.this, 3, reply.getReplyId());
                myDialog.show();
            }
            return true;
        }
    }

//    private void updateDynamicByNetWork() {
//        ApiManager.getDynamic(new RequestManager.RequestListener<GetDynamicResult>() {
//            @Override
//            public void processResult(int requestCode, int resultCode, GetDynamicResult data) {
//                if (data != null && data.getDynamic() != null) {
//                    dynamic = data.getDynamic();
//                    zanTextView.setOnClickListener(new ZanListener());
////                    zanTextView.setText(dynamic.getLikeCount() > 0 ? String.valueOf(dynamic.getLikeCount()) : "点赞");
//                    if (dynamic.getLikeFlag() == 1) {
//                        zanTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.social_zan_new_selected), null, null, null);
//                    } else {
//                        zanTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.social_zan_new), null, null, null);
//                    }
//                    commentTextView.setOnClickListener(commentListener);
////                    commentTextView.setText(dynamic.getCommentCount() > 0 ? String.valueOf(dynamic.getCommentCount()) : "评论");
//                    updateLikerList();
//                }
//            }
//        }, 1, DynamicDetailActivity.this, dynamic.getDynamicId());
//    }

    public void commitZanChanged() {
        if (zanChanged) {
            if (dynamic.getLikeFlag() == 1)
                ApiManager.like(null, ZAN_REQUEST_CODE, DynamicDetailActivity.this, dynamic.getDynamicId(), 1);
            else
                ApiManager.like(null, ZAN_REQUEST_CODE, DynamicDetailActivity.this, dynamic.getDynamicId(), 0);
            zanChanged = false;
        }
    }

    public class DynamicDetailAdapter extends CommentListAdapter {

        public DynamicDetailAdapter(Activity context, MyListView myListView, Dynamic dynamic, RefreshListener refreshListener, OpenReplyListener openReplyListener, View emptyView, CommentListener commentListener) {
            super(context, myListView, dynamic, refreshListener, openReplyListener, emptyView, commentListener);
        }

        @Override
        protected void getData(Long baseTime) {
            int requestCode = baseTime == null ? REFRESH_CODE : LOAD_MORE_CODE;
            if (requestCode == REFRESH_CODE) {
                mListView.forbidLoad("", true);
                commitZanChanged();
//                updateDynamicByNetWork();
            }
            ApiManager.listComment(this, requestCode, DynamicDetailActivity.this, dynamic.getDynamicId(), baseTime, COUNT);
        }

        @Override
        protected void addCommentLongClick(View commentView) {
            commentView.setOnLongClickListener(onCommentClickListener);
        }

        @Override
        protected void addReplyLongClick(View replyView) {
            replyView.setOnLongClickListener(onReplyLongClickListener);
        }


    }

    class DynamicDetailDialog extends ActionSheetDialog {

        public DynamicDetailDialog(Activity context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.social_dynamic_detail_bottom_dialog);
            findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // AppImpl.getAppRroxy().addLog(DynamicDetailActivity.this, "event-topic-share-" + dynamic.getDynamicId());
                    String url = AppImpl.getAppRroxy().getH5_PATH() + "events/dynamicDetail/index2.html?dynamicId=" + dynamic.getDynamicId();
                    String content = null;
                    if (dynamic.getContent() != null)
                        content = dynamic.getContent().length() < 100 ? dynamic.getContent() : dynamic.getContent().substring(0, 97) + "...";
//                    Bitmap bitmap = null;
                    String imgUrl = null;
                    if (dynamic.getImages() != null && dynamic.getImages().size() > 0) {
//                        bitmap = ImageManager.getBitmap( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + ImageUtil.getSmallImageUrl(dynamic.getImages().get(0)), new ImageSize(50, 50));
                        imgUrl =  AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + ImageUtil.getSmallImageUrl(dynamic.getImages().get(0));
                    }
                    shareUtil = new ShareUtil(DynamicDetailActivity.this, url, "糖友圈好文推荐", content, imgUrl);
                    shareUtil.setMaiDianCanSu("event-topic-share-" + dynamic.getDynamicId() + "-");
                    shareUtil.showSharePop(mListView);
//                    shareUtil.getmWeiboShareAPI().handleWeiboResponse(getIntent(), DynamicDetailActivity.this);
                    dismiss();
                }
            });
        }

    }

    ShareUtil shareUtil;

    class ReportDialog extends DynamicDetailDialog {

        int targetType;
        String targetId;

        public ReportDialog(Activity context, int targetType, String targetId) {
            super(context);
            this.targetType = targetType;
            this.targetId = targetId;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiManager.report(new NetWorkResultRequestListener(DynamicDetailActivity.this), REPORT_REQUEST_CODE, DynamicDetailActivity.this, targetType, targetId, (String) v.getTag());
                dismiss();
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ((TextView) findViewById(R.id.first)).setText("举报");
            findViewById(R.id.first).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.reportLinear).setVisibility(View.VISIBLE);
                    findViewById(R.id.type1).setOnClickListener(listener);
                    findViewById(R.id.type1).setTag("0");
                    findViewById(R.id.type2).setOnClickListener(listener);
                    findViewById(R.id.type2).setTag("1");
                    findViewById(R.id.type3).setOnClickListener(listener);
                    findViewById(R.id.type3).setTag("2");
                    findViewById(R.id.type4).setOnClickListener(listener);
                    findViewById(R.id.type4).setTag("3");
                    findViewById(R.id.cancelReport).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                    findViewById(R.id.first).setVisibility(View.GONE);
                    findViewById(R.id.cancel).setVisibility(View.GONE);
                    findViewById(R.id.share).setVisibility(View.GONE);
                }
            });
        }
    }

    class RemoveDialog extends DynamicDetailDialog {

        String dynamicId;
        String commentId;
        String replyId;

        public RemoveDialog(Activity context, String dynamicId, String commentId, String replyId) {
            super(context);
            this.dynamicId = dynamicId;
            this.commentId = commentId;
            this.replyId = replyId;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ((TextView) findViewById(R.id.first)).setText("删除");
            findViewById(R.id.first).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmTipsDialog dialog = new ConfirmTipsDialog(DynamicDetailActivity.this, "确认删除本条信息吗？", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(commentId) && TextUtils.isEmpty(replyId)) {
                                AppImpl.getAppRroxy().addLog(DynamicDetailActivity.this, "event-forum-delete-topic");
                                ApiManager.removeDynamic(new RemoveDynamicRequestListener(DynamicDetailActivity.this), 1, DynamicDetailActivity.this, dynamicId);
//                                resultData.putExtra("delDynamicId", dynamicId);
//                                setResult(RESULT_OK, resultData);
                                finish();
                            } else if (TextUtils.isEmpty(replyId)) {
                                AppImpl.getAppRroxy().addLog(DynamicDetailActivity.this, "event-forum-delete-comment");
                                ApiManager.removeComment(new NetWorkResultRequestListener(DynamicDetailActivity.this), 1, DynamicDetailActivity.this, dynamicId, commentId);
                                dynamic.decrCommentCount();
                                commentCount.setText("评论 " + dynamic.getCommentCount());
                                mAdapter.removeItem(commentId);
                            } else {
                                AppImpl.getAppRroxy().addLog(DynamicDetailActivity.this, "event-forum-delete-reply");
                                ApiManager.removeReply(new NetWorkResultRequestListener(DynamicDetailActivity.this), 1, DynamicDetailActivity.this, dynamicId, commentId, replyId);
                                mAdapter.removeReply(commentId, replyId);
                            }
                        }
                    });
                    dialog.show();
                    dismiss();
                }
            });

        }

    }

    class GoPersonalSpaceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (null != v.getTag() && !AppImpl.getAppRroxy().isNeedNewMain() ) {
                Intent intent = new Intent(DynamicDetailActivity.this, NewPersonalSpaceActivity.class);
                intent.putExtra("otherBuddy", (Buddy) v.getTag());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onResponse(Object response, String taskId, boolean rawData) {
        super.onResponse(response, taskId, rawData);
        if (AppImpl.getAppRroxy().getTASKD_GET_REWARD().equals(taskId)) {
            if (response instanceof TaskRewardPOJO) {
                TaskRewardPOJO pojo = (TaskRewardPOJO) response;
                if (pojo.getCode() == 2000 && pojo.getCoin() > 0) {

//                    TaskRewardUtils.handleTask(this, pojo, "article/reply");
                    AppImpl.getAppRroxy().handleTask(this, pojo, "article/reply");
                }

            }
        }
    }


    // 该通知用来监听分享之后的回调
    public void onEventMainThread(ShareStatus event) {
        int status = event.getStatus();
        switch (status) {
            case ShareStatus.ShareSuccess:
                AppImpl.getAppRroxy().CallAPITaskReward(this, "首次分享动态", "每日分享动态", "share_post/first", "share_post/daily");
                break;
        }
    }

}

