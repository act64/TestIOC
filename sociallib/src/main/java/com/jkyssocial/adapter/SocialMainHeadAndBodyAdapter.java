package com.jkyssocial.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.jkysbase.TimeUtil;
import com.jkys.jkyswidget.ConfirmTipsDialog;
import com.jkys.jkyswidget.CustomDialog;
import com.jkys.tools.DeviceUtil;
import com.jkys.tools.ImageUtil;
import com.jkys.tools.MainSelector;
import com.jkyshealth.tool.ViewUtil;
import com.jkyslogin.LoginHelper;
import com.jkyssocial.Fragment.HotRecommendFragment;
import com.jkyssocial.activity.CircleMainActivity;
import com.jkyssocial.activity.DynamicDetailActivity;
import com.jkyssocial.activity.MyEnterCircleActivity;
import com.jkyssocial.activity.NewPersonalSpaceActivity;
import com.jkyssocial.activity.SugarControlStarActivity;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.DynamicListResult;
import com.jkyssocial.data.SocialMainHeadAndBodyData;
import com.jkyssocial.data.SocialMainHeadData;
import com.jkyssocial.handler.PublishDynamicManager;
import com.jkyssocial.listener.ListUIListener;
import com.mintcode.base.BaseActivity;
import com.mintcode.base.BaseTopActivity;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.jsoup.helper.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/2/4
 * Time: 16:03
 * Email:AndroidZern@163.com
 */

public class SocialMainHeadAndBodyAdapter extends RecyclerView.Adapter implements RequestManager.RequestListener<DynamicListResult> {

    public static final int CREATE_CODE = 1;

    public static final int REFRESH_CODE = 2;

    public static final int LOAD_MORE_CODE = 3;

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_ONE_IMAGE = 1;
    public static final int TYPE_TWO_IMAGE = 2;
    public static final int TYPE_THREE_IMAGE = 3;
    public static final int TYPE_HEAD = 4;
    public static final int PAGING_COUNT = 20;

    protected final LayoutInflater mLayoutInflater;
    protected final ImageLoader imageLoader;
    //    protected final Activity mContext;
    protected WeakReference<Activity> activityWR;
    protected Context context;
    protected final ListUIListener mListUIListener;
    protected List<SocialMainHeadAndBodyData> socialMainHeadAndBodyList;
    private List<Circle> circles;

    String baseLine;

    String source;

    public List<SocialMainHeadAndBodyData> getSocialMainHeadAndBodyList() {
        return socialMainHeadAndBodyList;
    }

    DisplayImageOptions localOptions = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .cacheInMemory(false)//设置下载的图片是否缓存在内存中
            .cacheOnDisk(false)//设置下载的图片是否缓存在SD卡中
            .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
            .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
            .build();//构建完成

    public SocialMainHeadAndBodyAdapter(Activity activity, ListUIListener listUIListener, String source) {
        this.context = BaseTopActivity.getTopActivity();
        this.source = source;
        activityWR = new WeakReference<Activity>(activity);
        mLayoutInflater = LayoutInflater.from(context.getApplicationContext());
        imageLoader = ImageLoader.getInstance();
        socialMainHeadAndBodyList = new ArrayList<>();
        mListUIListener = listUIListener;
        circles = new ArrayList<>();
        mAdapter = new SugarFriendCirclesAdapter(BaseTopActivity.getTopActivity(), circles);
    }

    public SocialMainHeadAndBodyAdapter(Activity activity, ListUIListener listUIListener) {
        this.context = BaseTopActivity.getTopActivity();
        activityWR = new WeakReference<Activity>(activity);
        mLayoutInflater = LayoutInflater.from(context.getApplicationContext());
        imageLoader = ImageLoader.getInstance();
        socialMainHeadAndBodyList = new ArrayList<>();
        mListUIListener = listUIListener;
        circles = new ArrayList<>();
        mAdapter = new SugarFriendCirclesAdapter(BaseTopActivity.getTopActivity(), circles);
    }

    DynamicTextViewHolder.DynamicTextViewHolderClicks dynamicTextViewHolderClicks = new DynamicTextViewHolder.DynamicTextViewHolderClicks() {
        @Override
        public void onUserArea(View caller, int position) {
            if (activityWR == null || activityWR.get() == null)
                return;
            Activity activity = activityWR.get();
            if (activity instanceof BaseActivity) {
                BaseTopActivity socialBase = (BaseTopActivity) activity;
//                if(socialBase.showLoginDialogNew())
//                    return;
                if (LoginHelper.getInstance().showLoginActivity(socialBase))
                    return;
            }
            SocialMainHeadAndBodyData socialMainHeadAndBodyData = socialMainHeadAndBodyList.get(position);
            if (socialMainHeadAndBodyData instanceof Dynamic) {
                Dynamic dynamic = (Dynamic) socialMainHeadAndBodyData;
                if (dynamic.getStatusAndroid() != 0) {
                    return;
                }
                if (activity instanceof NewPersonalSpaceActivity) return;
                if (AppImpl.getAppRroxy().isNeedNewMain() )
                    return;
                Intent intent = new Intent(activity, NewPersonalSpaceActivity.class);
                intent.putExtra("otherBuddy", dynamic.getOwner());
                activity.startActivity(intent);
            }
        }

        @Override
        public void onComeFrom(View caller, int position) {
            if (activityWR == null || activityWR.get() == null)
                return;
            Activity activity = activityWR.get();
            if (activity instanceof BaseActivity) {
                BaseTopActivity socialBase = (BaseTopActivity) activity;
//                if(socialBase.showLoginDialogNew())
//                    return;
                if (LoginHelper.getInstance().showLoginActivity(socialBase))
                    return;
            }
            SocialMainHeadAndBodyData socialMainHeadAndBodyData = socialMainHeadAndBodyList.get(position);
            if (socialMainHeadAndBodyData instanceof Dynamic) {
                Dynamic dynamic = (Dynamic) socialMainHeadAndBodyData;
                if (dynamic.getStatusAndroid() != 0) {
                    return;
                }
                String error = Circle.getErrorMessage(dynamic.getCircle());
                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(activity, CircleMainActivity.class);
                intent.putExtra("circle", dynamic.getCircle());
                activity.startActivity(intent);
            }
        }

        @Override
        public void onSend(View caller, SocialMainHeadAndBodyAdapter.DynamicTextViewHolder viewHolder) {
            if (activityWR == null || activityWR.get() == null)
                return;
            Activity activity = activityWR.get();
            if (activity instanceof BaseActivity) {
                BaseTopActivity socialBase = (BaseTopActivity) activity;
//                if(socialBase.showLoginDialogNew())
//                    return;
                if (LoginHelper.getInstance().showLoginActivity(socialBase))
                    return;
            }
            SocialMainHeadAndBodyData socialMainHeadAndBodyData = socialMainHeadAndBodyList.get(viewHolder.getAdapterPosition());
            if (socialMainHeadAndBodyData instanceof Dynamic) {
                Dynamic dynamic = (Dynamic) socialMainHeadAndBodyData;
                if (dynamic.getStatusAndroid() != 2) {
                    return;
                }
                SocialMainHeadAndBodyAdapter.SendDynamicDialog dialog = new SocialMainHeadAndBodyAdapter.SendDynamicDialog(activity, viewHolder);
                dialog.show();
            }
        }

        @Override
        public void onItemView(View caller, int position) {
            if (activityWR == null || activityWR.get() == null)
                return;
            Activity activity = activityWR.get();
            if (activity instanceof BaseActivity) {
                BaseTopActivity socialBase = (BaseTopActivity) activity;
//                if(socialBase.showLoginDialogNew())
//                    return;
                if (LoginHelper.getInstance().showLoginActivity(socialBase))
                    return;
            }
            SocialMainHeadAndBodyData socialMainHeadAndBodyData = socialMainHeadAndBodyList.get(position);
            if (socialMainHeadAndBodyData instanceof Dynamic) {
                Dynamic dynamic = (Dynamic) socialMainHeadAndBodyData;
                if (dynamic.getStatusAndroid() != 0) {
                    return;
                }
                if (!TextUtils.isEmpty(source))
                    AppImpl.getAppRroxy().addLog(context, "event-topic-detail-trump-" + source + dynamic.getDynamicId());
                Intent intent = new Intent(activity, DynamicDetailActivity.class);
                intent.putExtra("dynamic", dynamic);
                activity.startActivity(intent);
            }
        }
    };

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD)
            return new SocialMainHeadHolder(mLayoutInflater.inflate(R.layout.zern_zern_zern, parent, false));
        else if (viewType == TYPE_TEXT)
            return new DynamicTextViewHolder(mLayoutInflater.inflate(R.layout.social_listitem_dynamic_text, parent, false),
                    dynamicTextViewHolderClicks);
        else if (viewType == TYPE_ONE_IMAGE)
            return new DynamicOneImageViewHolder(mLayoutInflater.inflate(R.layout.social_listitem_dynamic_one_image, parent, false),
                    dynamicTextViewHolderClicks);
        else if (viewType == TYPE_TWO_IMAGE)
            return new DynamicTwoImageViewHolder(mLayoutInflater.inflate(R.layout.social_listitem_dynamic_two_image, parent, false),
                    dynamicTextViewHolderClicks);
        else if (viewType == TYPE_THREE_IMAGE)
            return new DynamicThreeImageViewHolder(mLayoutInflater.inflate(R.layout.social_listitem_dynamic_three_image, parent, false),
                    dynamicTextViewHolderClicks);
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    private SugarFriendCirclesAdapter mAdapter;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SocialMainHeadAndBodyData item = getItem(position);
        if (item instanceof Dynamic && holder instanceof DynamicTextViewHolder) {
            DynamicTextViewHolder dynamicTextViewHolder = (DynamicTextViewHolder) holder;
            Dynamic dynamic = (Dynamic) item;
            dynamicTextViewHolder.renqiTV.setVisibility(View.GONE);
            if (position == 1) {
                dynamicTextViewHolder.headerLine.setVisibility(View.GONE);
                dynamicTextViewHolder.headerPad.setVisibility(View.GONE);
            } else if (position > 1) {
                dynamicTextViewHolder.headerLine.setVisibility(View.VISIBLE);
                dynamicTextViewHolder.headerPad.setVisibility(View.VISIBLE);
            }
            if (dynamic.getOwner() != null) {
                if (!TextUtils.isEmpty(dynamic.getOwner().getImgUrl())) {
                    ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + dynamic.getOwner().getImgUrl(),
                            null, dynamicTextViewHolder.avatarIV, ImageManager.avatarOptions);
                }

                ImageManager.setVFlag(dynamicTextViewHolder.vFlag, dynamic.getOwner());
                dynamicTextViewHolder.userNameTV.setText(dynamic.getOwner().getUserName());
            }
            dynamicTextViewHolder.createdTimeTV.setText(TimeUtil.getInterval(dynamic.getCreatedTime()));
            dynamicTextViewHolder.titleTV.setVisibility(TextUtils.isEmpty(dynamic.getTitle()) ? View.GONE : View.VISIBLE);
            dynamicTextViewHolder.titleTV.setText(dynamic.getTitle());
            if (TextUtils.isEmpty(dynamic.getTitle()))
                dynamicTextViewHolder.contentTV.setTextColor(Color.parseColor("#333333"));
            else dynamicTextViewHolder.contentTV.setTextColor(Color.parseColor("#999999"));
            dynamicTextViewHolder.contentTV.setVisibility(TextUtils.isEmpty(dynamic.getContent()) ? View.GONE : View.VISIBLE);
            dynamicTextViewHolder.contentTV.setText(dynamic.getContent());
            if (dynamic.getCircle() != null) {
                dynamicTextViewHolder.comeVG.setVisibility(View.VISIBLE);
                dynamicTextViewHolder.comeFromTV.setText(dynamic.getCircle().getTitle());
            } else {
                dynamicTextViewHolder.comeVG.setVisibility(View.GONE);
            }
            dynamicTextViewHolder.zanTV.setText("" + dynamic.getLikeCount());
//        if (dynamic.getViewCount() >= 10000) {
//            holder.renqiTV.setText("人气 " + String.format("%.1f", ((float) dynamic.getViewCount()) / 10000) + "万");
//        } else {
//            holder.renqiTV.setText("人气 " + dynamic.getViewCount());
//        }
            dynamicTextViewHolder.commentTV.setText("" + dynamic.getCommentCount());
            dynamicTextViewHolder.sendingBar.setVisibility((dynamic.getStatusAndroid() == 1) ? View.VISIBLE : View.GONE);
            dynamicTextViewHolder.sendError.setVisibility((dynamic.getStatusAndroid() == 2) ? View.VISIBLE : View.GONE);
            dynamicTextViewHolder.sendStatus.setVisibility((dynamic.getStatusAndroid() == 2) ? View.VISIBLE : View.GONE);

            if (dynamic.getStatusAndroid() == 1) {
                long interval = System.currentTimeMillis() - dynamic.getCreatedTime();
                if (interval > 300000)
                    dynamic.setStatusAndroid(2);
            }

            if (holder instanceof SocialMainHeadAndBodyAdapter.DynamicOneImageViewHolder) {
                SocialMainHeadAndBodyAdapter.DynamicOneImageViewHolder dynamicOneImageViewHolder = (SocialMainHeadAndBodyAdapter.DynamicOneImageViewHolder) holder;
                int dp = DeviceUtil.getDensity();
                String imageUrl = dynamic.getImages().get(0);
                int mWidth = 0, mHeight = 0;
                try {
                    String wh = imageUrl.substring(imageUrl.lastIndexOf('_') + 1, imageUrl.lastIndexOf('.'));
                    String[] s = wh.split("x");
                    mWidth = Integer.parseInt(s[0]) * dp;
                    mHeight = Integer.parseInt(s[1]) * dp;
                } catch (Exception e) {

                }
                PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) dynamicOneImageViewHolder.firstImage.getLayoutParams();
                PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
                if (mWidth > mHeight) {
                    info.widthPercent = 0.6f;
                    info.aspectRatio = 1.5f;
                } else {
                    info.widthPercent = 0.5f;
                    info.aspectRatio = 0.66f;
                }
                dynamicOneImageViewHolder.firstImage.setLayoutParams(params);
                loadDynamicContentImage(dynamic, dynamicOneImageViewHolder.firstImage, 0);
            } else if (holder instanceof SocialMainHeadAndBodyAdapter.DynamicTwoImageViewHolder) {
                SocialMainHeadAndBodyAdapter.DynamicTwoImageViewHolder dynamicTwoImageViewHolder = (SocialMainHeadAndBodyAdapter.DynamicTwoImageViewHolder) holder;
                loadDynamicContentImage(dynamic, dynamicTwoImageViewHolder.firstImage, 0);
                loadDynamicContentImage(dynamic, dynamicTwoImageViewHolder.secondImage, 1);
            } else if (holder instanceof SocialMainHeadAndBodyAdapter.DynamicThreeImageViewHolder) {
                SocialMainHeadAndBodyAdapter.DynamicThreeImageViewHolder dynamicThreeImageViewHolder = (SocialMainHeadAndBodyAdapter.DynamicThreeImageViewHolder) holder;
                loadDynamicContentImage(dynamic, dynamicThreeImageViewHolder.firstImage, 0);
                loadDynamicContentImage(dynamic, dynamicThreeImageViewHolder.secondImage, 1);
                loadDynamicContentImage(dynamic, dynamicThreeImageViewHolder.thirdImage, 2);
                if (dynamic.getImages().size() > 3) {
                    dynamicThreeImageViewHolder.moreImageTag.setVisibility(View.VISIBLE);
                } else {
                    dynamicThreeImageViewHolder.moreImageTag.setVisibility(View.GONE);
                }
            }
        } else if (item instanceof SocialMainHeadData) {
            SocialMainHeadData socialMainHeadData = (SocialMainHeadData) item;
            SocialMainHeadHolder headHolder = (SocialMainHeadHolder) holder;
            headHolder.mGridView.setAdapter(mAdapter);
            circles.clear();
            circles.addAll(socialMainHeadData.getCircleList());
            mAdapter.notifyDataSetChanged();

            headHolder.headerLinear.removeAllViews();
            int size = socialMainHeadData.getBuddyList().size() < 4 ? socialMainHeadData.getBuddyList().size() : 4;
            for (int i = 0; i < size; ++i) {
                final Buddy buddy1 = socialMainHeadData.getBuddyList().get(i);
                View view = mLayoutInflater.inflate(R.layout.social_include_listitem_social_dynamic_header, headHolder.headerLinear, false);
                ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
                ImageView vFlag = (ImageView) view.findViewById(R.id.vFlag);
                TextView userTitle = (TextView) view.findViewById(R.id.userTitle);
                TextView userName = (TextView) view.findViewById(R.id.userName);
                ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + buddy1.getImgUrl(), null, avatar, ImageManager.avatarOptions);
                ImageManager.setVFlag(vFlag, buddy1);
                userName.setText(buddy1.getUserName());
                userTitle.setText(buddy1.getUserType() == 1 ? "专家医生" : "资深糖友");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LoginHelper.getInstance().showLoginActivity((Activity) context)) return;
                        if (AppImpl.getAppRroxy().isNeedNewMain() )
                            return;
                        AppImpl.getAppRroxy().addLog(context, "event-forum-recommend-expert-" + buddy1.getBuddyId());
                        Intent intent = new Intent(context, NewPersonalSpaceActivity.class);
                        intent.putExtra("otherBuddy", buddy1);
                        context.startActivity(intent);
                    }
                });
                headHolder.headerLinear.addView(view);
            }
        }
    }

    private void loadDynamicContentImage(Dynamic dynamic, ImageView imageView, int position) {
        if (dynamic.getStatusAndroid() == 0) {
            String url = dynamic.getImages().get(position);
            if (!TextUtils.isEmpty(url)) {
                ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH()
                                + ImageUtil.getSmallImageUrl(url), null,
                        imageView, new Reloadlistener());
            }

        } else {
            imageLoader.displayImage("file://" + dynamic.getImages().get(position), imageView, localOptions);
        }
    }

    protected SocialMainHeadAndBodyData getItem(int position) {
        return socialMainHeadAndBodyList.get(position);
    }

    @Override
    public int getItemCount() {
        return socialMainHeadAndBodyList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("SocialHeadBody", "-position-" + position);
        SocialMainHeadAndBodyData socialMainHeadAndBodyData = socialMainHeadAndBodyList.get(position);
        if (socialMainHeadAndBodyData instanceof Dynamic) {
            Dynamic dynamic = (Dynamic) socialMainHeadAndBodyData;
            List<String> imageList = dynamic.getImages();
            int size = (imageList == null) ? 0 : imageList.size();
            if (size == 0)
                return TYPE_TEXT;
            else if (size == 1)
                return TYPE_ONE_IMAGE;
            else if (size == 2)
                return TYPE_TWO_IMAGE;
            else
                return TYPE_THREE_IMAGE;
        } else {
            return TYPE_HEAD;
        }
    }

    @Override
    public void processResult(int requestCode, int resultCode, DynamicListResult data) {
        switch (requestCode) {
            case CREATE_CODE:
                if (isSuccessData(data)) {
                    baseLine = data.getBaseLine();
                    socialMainHeadAndBodyList.addAll(PublishDynamicManager.sendingDynamicList);
                    socialMainHeadAndBodyList.addAll(data.getDynamicList());
                    mListUIListener.afterCreateUI(ListUIListener.SUCCESS_CODE);
                } else {
                    socialMainHeadAndBodyList.addAll(PublishDynamicManager.sendingDynamicList);
                    if (socialMainHeadAndBodyList.isEmpty()) {
                        mListUIListener.afterCreateUI(ListUIListener.NULL_LIST_CODE);
                    } else {
                        mListUIListener.afterCreateUI(ListUIListener.NETWORK_NULL_LIST_CODE);
                    }
                }
                notifyDataSetChanged();
                break;
            case REFRESH_CODE:
                if (isSuccessData(data)) {
                    baseLine = data.getBaseLine();
                    socialMainHeadAndBodyList.addAll(PublishDynamicManager.sendingDynamicList);
                    socialMainHeadAndBodyList.addAll(data.getDynamicList());
                    mListUIListener.afterRefreshUI(ListUIListener.SUCCESS_CODE);
                } else {
//                    dynamicList = new ArrayList<Dynamic>();
                    socialMainHeadAndBodyList.addAll(PublishDynamicManager.sendingDynamicList);
                    if (socialMainHeadAndBodyList.isEmpty()) {
                        mListUIListener.afterRefreshUI(ListUIListener.NULL_LIST_CODE);
                    } else {
                        mListUIListener.afterRefreshUI(ListUIListener.NETWORK_NULL_LIST_CODE);
                    }
                }
                notifyDataSetChanged();
                break;
            case LOAD_MORE_CODE:
                if (isSuccessData(data)) {
                    baseLine = data.getBaseLine();
                    socialMainHeadAndBodyList.addAll(data.getDynamicList());
                    notifyDataSetChanged();
                    mListUIListener.afterLoadmoreUI(ListUIListener.SUCCESS_CODE);
                } else {
                    mListUIListener.afterLoadmoreUI(ListUIListener.NULL_LIST_CODE);
                }
                break;
        }
    }

    private boolean isSuccessData(DynamicListResult data) {
//        return data != null && data.getDynamicList() != null && !data.getDynamicList().isEmpty();
        return data != null && data.getDynamicList() != null;
    }

    public void create() {
        ApiManager.listDynamicForRecommend(this, CREATE_CODE, context, null, PAGING_COUNT);
    }

    public void loadmore() {
        ApiManager.listDynamicForRecommend(this, LOAD_MORE_CODE, context, baseLine, PAGING_COUNT);
    }

    public void refresh() {
        ApiManager.listDynamicForRecommend(this, REFRESH_CODE, context, null, PAGING_COUNT);
    }

    public static class SocialMainHeadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R2.id.social_main_gridView)
        public GridView mGridView;

        @BindView(R2.id.recommend_rl)
        public RelativeLayout recommendRl;

        @BindView(R2.id.headerLinear)
        public LinearLayout headerLinear;

        @BindView(R2.id.findMoreSuperStar)
        public TextView findMoreSuperStar;

        @BindView(R2.id.social_main_mycircle_icon)
        public ImageView social_main_mycircle_icon;

        @BindView(R2.id.social_main_mycircle)
        public TextView social_main_mycircle;

        public SocialMainHeadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            findMoreSuperStar.setOnClickListener(this);
            social_main_mycircle_icon.setOnClickListener(this);
            social_main_mycircle.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Activity topActivity = BaseTopActivity.getTopActivity();
            if (topActivity == null) {
                return;
            }
            if (LoginHelper.getInstance().showLoginActivity(topActivity)) return;
            switch (v.getId()) {
                case R2.id.social_main_mycircle:
                case R2.id.social_main_mycircle_icon:
                    if (!ViewUtil.singleClick()) return;
                    Intent intent = new Intent(topActivity, MyEnterCircleActivity.class);
                    intent.putExtra("myBuddy", HotRecommendFragment.myBuddy);
                    topActivity.startActivity(intent);
                    break;
                case R2.id.findMoreSuperStar:
                    topActivity.startActivity(new Intent(topActivity, SugarControlStarActivity.class));
                    break;
            }
        }
    }

    public static class DynamicTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.rootView)
        public View rootView;

        @BindView(R2.id.headerLine)
        public View headerLine;

        @BindView(R2.id.headerPad)
        public View headerPad;

        @BindView(R2.id.dynamicHeader)
        public View dynamicHeader;

        @BindView(R2.id.avatar)
        public CircleImageView avatarIV;

        @BindView(R2.id.vFlag)
        public ImageView vFlag;

        @BindView(R2.id.userName)
        public TextView userNameTV;

        @BindView(R2.id.createdTime)
        public TextView createdTimeTV;

        @BindView(R2.id.title)
        public TextView titleTV;

        @BindView(R2.id.content)
        public TextView contentTV;

        @BindView(R2.id.come)
        public ViewGroup comeVG;

        @BindView(R2.id.comeFrom)
        public TextView comeFromTV;

        @BindView(R2.id.zan)
        public TextView zanTV;

        @BindView(R2.id.comment)
        public TextView commentTV;

        @BindView(R2.id.renqi)
        public TextView renqiTV;

        @BindView(R2.id.sendingBar)
        public ProgressBar sendingBar;

        @BindView(R2.id.sendError)
        public ImageView sendError;

        @BindView(R2.id.sendStatus)
        public TextView sendStatus;

        @BindView(R2.id.topic_tv)
        public TextView topicTV;

        @BindView(R2.id.circleOwnerTag)
        public FancyButton circleOwnerTag;

        @BindView(R2.id.circleRecommendTag)
        public FancyButton circleRecommendTag;

        @BindView(R2.id.avatarArea)
        public PercentRelativeLayout avatartArea;

        public DynamicTextViewHolder.DynamicTextViewHolderClicks mDynamicTextViewHolderClicks;

        DynamicTextViewHolder(View view, DynamicTextViewHolderClicks dynamicTextViewHolderClicks) {
            super(view);
            ButterKnife.bind(this, view);
            mDynamicTextViewHolderClicks = dynamicTextViewHolderClicks;
            rootView.setOnClickListener(this);
            dynamicHeader.setOnClickListener(this);
            avatartArea.setOnClickListener(this);
            userNameTV.setOnClickListener(this);
            comeFromTV.setOnClickListener(this);
            sendError.setOnClickListener(this);
            sendStatus.setOnClickListener(this);
        }

        DynamicTextViewHolder(View view) {
            super(view);
        }

        public interface DynamicTextViewHolderClicks {
            void onItemView(View caller, int position);

            void onUserArea(View caller, int position);

            void onComeFrom(View caller, int position);

            void onSend(View caller, DynamicTextViewHolder viewHolder);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R2.id.dynamicHeader:
//                    mDynamicTextViewHolderClicks.onUserArea(v, getAdapterPosition());
//                    return;
                case R2.id.avatarArea:
                    mDynamicTextViewHolderClicks.onUserArea(v, getAdapterPosition());
                    return;
                case R2.id.userName:
                    mDynamicTextViewHolderClicks.onUserArea(v, getAdapterPosition());
                    return;
                case R2.id.rootView:
                    mDynamicTextViewHolderClicks.onItemView(v, getAdapterPosition());
                    return;
                case R2.id.comeFrom:
                    mDynamicTextViewHolderClicks.onComeFrom(v, getAdapterPosition());
                    return;
                case R2.id.sendError:
                    mDynamicTextViewHolderClicks.onSend(v, this);
                    return;
                case R2.id.sendStatus:
                    mDynamicTextViewHolderClicks.onSend(v, this);
                    return;
            }

        }
    }

    public class DynamicOneImageViewHolder extends DynamicTextViewHolder {

        @BindView(R2.id.firstImage)
        ImageView firstImage;

        DynamicOneImageViewHolder(View view, DynamicTextViewHolderClicks dynamicTextViewHolderClicks) {
            super(view, dynamicTextViewHolderClicks);
        }
    }

    public class DynamicTwoImageViewHolder extends SocialMainHeadAndBodyAdapter.DynamicTextViewHolder {

        @BindView(R2.id.firstImage)
        ImageView firstImage;

        @BindView(R2.id.secondImage)
        ImageView secondImage;

        DynamicTwoImageViewHolder(View view, DynamicTextViewHolderClicks dynamicTextViewHolderClicks) {
            super(view, dynamicTextViewHolderClicks);
        }
    }

    public class DynamicThreeImageViewHolder extends SocialMainHeadAndBodyAdapter.DynamicTextViewHolder {

        @BindView(R2.id.firstImage)
        ImageView firstImage;

        @BindView(R2.id.secondImage)
        ImageView secondImage;

        @BindView(R2.id.thirdImage)
        ImageView thirdImage;

        @BindView(R2.id.moreImageTag)
        TextView moreImageTag;

        DynamicThreeImageViewHolder(View view, DynamicTextViewHolderClicks dynamicTextViewHolderClicks) {
            super(view, dynamicTextViewHolderClicks);
        }
    }

    DisplayImageOptions delayOptons = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .delayBeforeLoading(4000)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
            .build();

    class Reloadlistener implements ImageLoadingListener {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
        }

        @Override
        public void onLoadingFailed(final String imageUri, final View view, FailReason failReason) {
            if (BuildConfig.DEBUG)
                Toast.makeText(context, "failed " + imageUri, Toast.LENGTH_LONG).show();
            ImageManager.loadImage(imageUri, null, (ImageView) view, delayOptons);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    }

    class SendDynamicDialog extends CustomDialog {

        private SocialMainHeadAndBodyAdapter.DynamicTextViewHolder holder;

        public SendDynamicDialog(Activity context, SocialMainHeadAndBodyAdapter.DynamicTextViewHolder holder) {
            super(context);
            this.holder = holder;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.social_dialog_send_dynamic);
            findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialMainHeadAndBodyData socialMainHeadAndBodyData = socialMainHeadAndBodyList.get(holder.getAdapterPosition());
                    if (socialMainHeadAndBodyData instanceof Dynamic) {
                        Dynamic sendingDynamic = (Dynamic) socialMainHeadAndBodyData;
                        if (sendingDynamic.getStatusAndroid() == 2) {
                            sendingDynamic.setCreatedTime(System.currentTimeMillis());
                            sendingDynamic.setStatusAndroid(1);
                            PublishDynamicManager publishDynamicManager = new PublishDynamicManager(sendingDynamic,
                                    context);
                            publishDynamicManager.start();
                            holder.sendingBar.setVisibility(View.VISIBLE);
                            holder.sendError.setVisibility(View.GONE);
                            holder.sendStatus.setVisibility(View.GONE);
                        }
                        dismiss();
                    }
                }
            });
            findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activityWR == null || activityWR.get() == null)
                        return;
                    Activity activity = activityWR.get();
                    ConfirmTipsDialog confirmTipsDialog = new ConfirmTipsDialog(activity, "删除后无法恢复，请确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SocialMainHeadAndBodyData socialMainHeadAndBodyData = socialMainHeadAndBodyList.get(holder.getAdapterPosition());
                            if ( socialMainHeadAndBodyData instanceof Dynamic){
                                Dynamic dynamic = (Dynamic) socialMainHeadAndBodyData;
                                socialMainHeadAndBodyList.remove(holder.getAdapterPosition());
                                PublishDynamicManager.sendingDynamicList.remove(dynamic);
                                notifyDataSetChanged();
                            }
                        }
                    });
                    confirmTipsDialog.show();
                    dismiss();
                }
            });
        }

    }


}
