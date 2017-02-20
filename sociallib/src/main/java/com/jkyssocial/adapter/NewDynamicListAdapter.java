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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.jkysbase.TimeUtil;
import com.jkys.jkyswidget.ConfirmTipsDialog;
import com.jkys.jkyswidget.CustomDialog;
import com.jkys.tools.DeviceUtil;
import com.jkys.tools.ImageUtil;
import com.jkys.tools.MainSelector;
import com.jkyslogin.LoginHelper;
import com.jkyssocial.activity.CircleMainActivity;
import com.jkyssocial.activity.DynamicDetailActivity;
import com.jkyssocial.activity.NewPersonalSpaceActivity;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.DynamicListResult;
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
 * Created by yangxiaolong on 15/9/2.
 */
public class NewDynamicListAdapter extends RecyclerView.Adapter<NewDynamicListAdapter.DynamicTextViewHolder> implements RequestManager.RequestListener<DynamicListResult> {

    public static final int CREATE_CODE = 1;

    public static final int REFRESH_CODE = 2;

    public static final int LOAD_MORE_CODE = 3;

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_ONE_IMAGE = 1;
    public static final int TYPE_TWO_IMAGE = 2;
    public static final int TYPE_THREE_IMAGE = 3;
    public static final int PAGING_COUNT = 20;

    protected final LayoutInflater mLayoutInflater;
    protected final ImageLoader imageLoader;
    //    protected final Activity mContext;
    protected WeakReference<Activity> activityWR;
    protected Context context;
    protected final ListUIListener mListUIListener;
    protected List<Dynamic> dynamicList;

    String baseLine;

    String source;

    DisplayImageOptions localOptions = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .cacheInMemory(false)//设置下载的图片是否缓存在内存中
            .cacheOnDisk(false)//设置下载的图片是否缓存在SD卡中
            .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
            .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
            .build();//构建完成

    public NewDynamicListAdapter(Activity activity, ListUIListener listUIListener, String source) {
        this.context = activity.getApplicationContext();
        this.source = source;
        activityWR = new WeakReference<Activity>(activity);
        mLayoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        dynamicList = new ArrayList<>();
        mListUIListener = listUIListener;
    }

    public NewDynamicListAdapter(Activity activity, ListUIListener listUIListener) {
        this.context = activity.getApplicationContext();
        activityWR = new WeakReference<Activity>(activity);
        mLayoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        dynamicList = new ArrayList<>();
        mListUIListener = listUIListener;
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
            Dynamic dynamic = dynamicList.get(position);
            if (dynamic.getStatusAndroid() != 0) {
                return;
            }
            if (activity instanceof NewPersonalSpaceActivity) return;
            if (MainSelector.isNeedNewMain())
                return;
            Intent intent = new Intent(activity, NewPersonalSpaceActivity.class);
            intent.putExtra("otherBuddy", dynamic.getOwner());
            activity.startActivity(intent);
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
            Dynamic dynamic = dynamicList.get(position);
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

        @Override
        public void onSend(View caller, DynamicTextViewHolder viewHolder) {
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
            Dynamic dynamic = dynamicList.get(viewHolder.getAdapterPosition());
            if (dynamic.getStatusAndroid() != 2) {
                return;
            }
            SendDynamicDialog dialog = new SendDynamicDialog(activity, viewHolder);
            dialog.show();
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
            Dynamic dynamic = dynamicList.get(position);
            if (dynamic.getStatusAndroid() != 0) {
                return;
            }
            if (!TextUtils.isEmpty(source))
                LogUtil.addLog(context, "event-topic-detail-trump-" + source + dynamic.getDynamicId());
            Intent intent = new Intent(activity, DynamicDetailActivity.class);
            intent.putExtra("dynamic", dynamic);
            activity.startActivity(intent);
        }
    };

    @Override
    public DynamicTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXT)
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

    @Override
    public void onBindViewHolder(DynamicTextViewHolder holder, int position) {
        Dynamic dynamic = getItem(position);
        if (dynamic.getOwner() != null) {
            if (!TextUtils.isEmpty(dynamic.getOwner().getImgUrl())) {
                ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + dynamic.getOwner().getImgUrl(),
                        null, holder.avatarIV, ImageManager.avatarOptions);
            }

            ImageManager.setVFlag(holder.vFlag, dynamic.getOwner());
            holder.userNameTV.setText(dynamic.getOwner().getUserName());
        }
        holder.createdTimeTV.setText(TimeUtil.getInterval(dynamic.getCreatedTime()));
        holder.titleTV.setVisibility(TextUtils.isEmpty(dynamic.getTitle()) ? View.GONE : View.VISIBLE);
        holder.titleTV.setText(dynamic.getTitle());
        if (TextUtils.isEmpty(dynamic.getTitle()))
            holder.contentTV.setTextColor(Color.parseColor("#333333"));
        else holder.contentTV.setTextColor(Color.parseColor("#999999"));
        holder.contentTV.setVisibility(TextUtils.isEmpty(dynamic.getContent()) ? View.GONE : View.VISIBLE);
        holder.contentTV.setText(dynamic.getContent());
        if (dynamic.getCircle() != null) {
            holder.comeVG.setVisibility(View.VISIBLE);
            holder.comeFromTV.setText(dynamic.getCircle().getTitle());
        } else {
            holder.comeVG.setVisibility(View.GONE);
        }
        holder.zanTV.setText("" + dynamic.getLikeCount());
//        if (dynamic.getViewCount() >= 10000) {
//            holder.renqiTV.setText("人气 " + String.format("%.1f", ((float) dynamic.getViewCount()) / 10000) + "万");
//        } else {
//            holder.renqiTV.setText("人气 " + dynamic.getViewCount());
//        }
        holder.commentTV.setText("" + dynamic.getCommentCount());


        holder.sendingBar.setVisibility((dynamic.getStatusAndroid() == 1) ? View.VISIBLE : View.GONE);
        holder.sendError.setVisibility((dynamic.getStatusAndroid() == 2) ? View.VISIBLE : View.GONE);
        holder.sendStatus.setVisibility((dynamic.getStatusAndroid() == 2) ? View.VISIBLE : View.GONE);

        if (dynamic.getStatusAndroid() == 1) {
            long interval = System.currentTimeMillis() - dynamic.getCreatedTime();
            if (interval > 300000)
                dynamic.setStatusAndroid(2);
        }

        if (holder instanceof DynamicOneImageViewHolder) {
            DynamicOneImageViewHolder dynamicOneImageViewHolder = (DynamicOneImageViewHolder) holder;
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
        } else if (holder instanceof DynamicTwoImageViewHolder) {
            DynamicTwoImageViewHolder dynamicTwoImageViewHolder = (DynamicTwoImageViewHolder) holder;
            loadDynamicContentImage(dynamic, dynamicTwoImageViewHolder.firstImage, 0);
            loadDynamicContentImage(dynamic, dynamicTwoImageViewHolder.secondImage, 1);
        } else if (holder instanceof DynamicThreeImageViewHolder) {
            DynamicThreeImageViewHolder dynamicThreeImageViewHolder = (DynamicThreeImageViewHolder) holder;
            loadDynamicContentImage(dynamic, dynamicThreeImageViewHolder.firstImage, 0);
            loadDynamicContentImage(dynamic, dynamicThreeImageViewHolder.secondImage, 1);
            loadDynamicContentImage(dynamic, dynamicThreeImageViewHolder.thirdImage, 2);
            if (dynamic.getImages().size() > 3) {
                dynamicThreeImageViewHolder.moreImageTag.setVisibility(View.VISIBLE);
            } else {
                dynamicThreeImageViewHolder.moreImageTag.setVisibility(View.GONE);
            }
        }
    }

    private void loadDynamicContentImage(Dynamic dynamic, ImageView imageView, int position) {
        if (dynamic.getStatusAndroid() == 0) {
            String url = dynamic.getImages().get(position);
            if (!TextUtils.isEmpty(url)) {
                ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH
                                + ImageUtil.getSmallImageUrl(url), null,
                        imageView, new Reloadlistener());
            }

        } else {
            imageLoader.displayImage("file://" + dynamic.getImages().get(position), imageView, localOptions);
        }
    }

    protected Dynamic getItem(int position) {
        return dynamicList.get(position);
    }

    public void addItem(Dynamic dynamic) {
        dynamicList.add(0, dynamic);
        notifyItemInserted(0);
    }

    public void replaceItem(Dynamic targtDynamic, Dynamic srcDynamic) {
        int i = dynamicList.indexOf(srcDynamic);
        if (i >= 0 && i < dynamicList.size()) {
            dynamicList.set(i, targtDynamic);
            notifyItemChanged(i);
        }
    }

    public void removeItem(String dynamicId) {
        if (TextUtils.isEmpty(dynamicId))
            return;
        for (int i = 0; i < dynamicList.size(); ++i) {
            if (dynamicId.equals(dynamicList.get(i).getDynamicId())) {
                dynamicList.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dynamicList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Dynamic dynamic = getItem(position);
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
    }

    @Override
    public void processResult(int requestCode, int resultCode, DynamicListResult data) {
        switch (requestCode) {
            case CREATE_CODE:
                if (isSuccessData(data)) {
                    baseLine = data.getBaseLine();
                    dynamicList = new ArrayList<Dynamic>();
                    dynamicList.addAll(PublishDynamicManager.sendingDynamicList);
                    dynamicList.addAll(data.getDynamicList());
                    mListUIListener.afterCreateUI(ListUIListener.SUCCESS_CODE);

                } else {
                    dynamicList = new ArrayList<Dynamic>();
                    dynamicList.addAll(PublishDynamicManager.sendingDynamicList);
                    if (dynamicList.isEmpty()) {
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
                    dynamicList = new ArrayList<Dynamic>();
                    dynamicList.addAll(PublishDynamicManager.sendingDynamicList);
                    dynamicList.addAll(data.getDynamicList());
                    mListUIListener.afterRefreshUI(ListUIListener.SUCCESS_CODE);
                    notifyDataSetChanged();
                } else {
//                    dynamicList = new ArrayList<Dynamic>();
//                    dynamicList.addAll(PublishDynamicManager.sendingDynamicList);
                    if (dynamicList.isEmpty()) {
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
                    dynamicList.addAll(data.getDynamicList());
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
    }

    public void loadmore() {
    }

    public void refresh() {
    }

    public static class DynamicTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.rootView)
        public View rootView;

        @Bind(R.id.headerLine)
        public View headerLine;

        @Bind(R.id.headerPad)
        public View headerPad;

        @Bind(R.id.dynamicHeader)
        public View dynamicHeader;

        @Bind(R.id.avatar)
        public CircleImageView avatarIV;

        @Bind(R.id.vFlag)
        public ImageView vFlag;

        @Bind(R.id.userName)
        public TextView userNameTV;

        @Bind(R.id.createdTime)
        public TextView createdTimeTV;

        @Bind(R.id.title)
        public TextView titleTV;

        @Bind(R.id.content)
        public TextView contentTV;

        @Bind(R.id.come)
        public ViewGroup comeVG;

        @Bind(R.id.comeFrom)
        public TextView comeFromTV;

        @Bind(R.id.zan)
        public TextView zanTV;

        @Bind(R.id.comment)
        public TextView commentTV;

        @Bind(R.id.renqi)
        public TextView renqiTV;

        @Bind(R.id.sendingBar)
        public ProgressBar sendingBar;

        @Bind(R.id.sendError)
        public ImageView sendError;

        @Bind(R.id.sendStatus)
        public TextView sendStatus;

        @Bind(R.id.topic_tv)
        public TextView topicTV;

        @Bind(R.id.circleOwnerTag)
        public FancyButton circleOwnerTag;

        @Bind(R.id.circleRecommendTag)
        public FancyButton circleRecommendTag;

        @Bind(R.id.avatarArea)
        public PercentRelativeLayout avatartArea;

        public DynamicTextViewHolderClicks mDynamicTextViewHolderClicks;

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

        DynamicTextViewHolder(View view){
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
//                case R.id.dynamicHeader:
//                    mDynamicTextViewHolderClicks.onUserArea(v, getAdapterPosition());
//                    return;
                case R.id.avatarArea:
                    mDynamicTextViewHolderClicks.onUserArea(v, getAdapterPosition());
                    return;
                case R.id.userName:
                    mDynamicTextViewHolderClicks.onUserArea(v, getAdapterPosition());
                    return;
                case R.id.rootView:
                    mDynamicTextViewHolderClicks.onItemView(v, getAdapterPosition());
                    return;
                case R.id.comeFrom:
                    mDynamicTextViewHolderClicks.onComeFrom(v, getAdapterPosition());
                    return;
                case R.id.sendError:
                    mDynamicTextViewHolderClicks.onSend(v, this);
                    return;
                case R.id.sendStatus:
                    mDynamicTextViewHolderClicks.onSend(v, this);
                    return;
            }

        }
    }

    public class DynamicOneImageViewHolder extends DynamicTextViewHolder {

        @Bind(R.id.firstImage)
        ImageView firstImage;

        DynamicOneImageViewHolder(View view, DynamicTextViewHolderClicks dynamicTextViewHolderClicks) {
            super(view, dynamicTextViewHolderClicks);
        }
    }

    public class DynamicTwoImageViewHolder extends DynamicTextViewHolder {

        @Bind(R.id.firstImage)
        ImageView firstImage;

        @Bind(R.id.secondImage)
        ImageView secondImage;

        DynamicTwoImageViewHolder(View view, DynamicTextViewHolderClicks dynamicTextViewHolderClicks) {
            super(view, dynamicTextViewHolderClicks);
        }
    }

    public class DynamicThreeImageViewHolder extends DynamicTextViewHolder {

        @Bind(R.id.firstImage)
        ImageView firstImage;

        @Bind(R.id.secondImage)
        ImageView secondImage;

        @Bind(R.id.thirdImage)
        ImageView thirdImage;

        @Bind(R.id.moreImageTag)
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

        private DynamicTextViewHolder holder;

        public SendDynamicDialog(Activity context, DynamicTextViewHolder holder) {
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
                    Dynamic sendingDynamic = dynamicList.get(holder.getAdapterPosition());
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
                            Dynamic sendingDynamic = dynamicList.get(holder.getAdapterPosition());
                            dynamicList.remove(holder.getAdapterPosition());
                            PublishDynamicManager.sendingDynamicList.remove(sendingDynamic);
                            notifyDataSetChanged();
                        }
                    });
                    confirmTipsDialog.show();
                    dismiss();
                }
            });
        }

    }


}