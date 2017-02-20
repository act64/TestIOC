package com.jkyssocial.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jkys.jkysbase.TimeUtil;
import com.jkyssocial.activity.CircleMainActivity;
import com.jkyssocial.activity.DynamicDetailActivity;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.CircleListResult;
import com.jkyssocial.listener.ListUIListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.util.ImageManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;

/**
 * Created by yangxiaolong on 15/9/2.
 */
public class CircleWithDynamicListAdapter extends RecyclerView.Adapter<CircleWithDynamicListAdapter.CircleViewHolder> implements RequestManager.RequestListener<CircleListResult> {

    public static final int CREATE_CODE = 1;

    public static final int REFRESH_CODE = 2;

    public static final int LOAD_MORE_CODE = 3;

    public static final int PAGING_COUNT = 20;

    protected final LayoutInflater mLayoutInflater;
    protected final ImageLoader imageLoader;
    //    protected final Activity activity;
    protected WeakReference<Activity> activityWR;
    private Context context;
    protected final ListUIListener mListUIListener;
    protected List<Circle> circleList;

    String baseLine;

    public void deleteCircle(String circleId) {
        int index = 0;
        for (Circle circle : circleList) {
            if (circle.getId() != null && circle.getId().equals(circleId)) {
                circleList.remove(index);
                notifyItemRemoved(index);
                break;
            }
            index++;
        }
    }

    public CircleWithDynamicListAdapter(Activity activity, ListUIListener listUIListener) {
        activityWR = new WeakReference<Activity>(activity);
        context = activity.getApplicationContext();
        mLayoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        circleList = new ArrayList<>();
        mListUIListener = listUIListener;
    }

    CircleViewHolder.CircleTextViewHolderClicks circleTextViewHolderClicks = new CircleViewHolder.CircleTextViewHolderClicks() {
        @Override
        public void onItemView(View caller, int position) {
            if (activityWR == null || activityWR.get() == null)
                return;
            Activity activity = activityWR.get();
            Circle circle = circleList.get(position);
            Intent intent = new Intent(activity, CircleMainActivity.class);
            intent.putExtra("circle", circle);
            activity.startActivity(intent);
        }
    };

    @Override
    public CircleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup rootView = (ViewGroup) mLayoutInflater.inflate(R.layout.social_listitem_circle_with_dynamic, parent, false);
        return new CircleViewHolder(rootView, circleTextViewHolderClicks);
    }

    @Override
    public void onBindViewHolder(CircleViewHolder holder, int position) {
        Circle circle = getItem(position);
        if (!TextUtils.isEmpty(circle.getAvatar())) {
            ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + circle.getAvatar(), null,
                    holder.avatarIV, ImageManager.circleAvatarOptions);
        }

        holder.circleNameTV.setText(circle.getTitle());
        int memberCount = circle.getStat() == null ? 0 : circle.getStat().getMemberCount();
        holder.todayCountTV.setText("成员: " + memberCount);
        int size = circle.getDynamicList() == null ? 0 : circle.getDynamicList().size();
        size = Math.min(size, 2);
        for (int i = 0; i < size; ++i) {
            Dynamic dynamic = circle.getDynamicList().get(i);
            DynamicView dynamicView = holder.dynamicViewList.get(i);
            dynamicView.rootLayout.setVisibility(View.VISIBLE);
            dynamicView.rootLayout.setTag(dynamic);
            dynamicView.rootLayout.setOnClickListener(onDynamicClickListener);
            dynamicView.titleTV.setText(dynamic.getTitle());
            if (dynamic.getOwner() != null) {
                dynamicView.nameTV.setText(dynamic.getOwner().getUserName());
                dynamicView.createdTimeTV.setText(TimeUtil.getInterval(dynamic.getCreatedTime()));
                String imgUrl = dynamic.getOwner().getImgUrl();
                if (!TextUtils.isEmpty(imgUrl)) {
                    ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + imgUrl, null,
                            dynamicView.avatarIV, ImageManager.avatarOptions);
                }

            }
        }
        for (int i = size; i < 2; ++i) {
            DynamicView dynamicView = holder.dynamicViewList.get(i);
            dynamicView.rootLayout.setVisibility(View.GONE);
        }
    }

    protected Circle getItem(int position) {
        return circleList.get(position);
    }


    @Override
    public int getItemCount() {
        return circleList.size();
    }

    @Override
    public void processResult(int requestCode, int resultCode, CircleListResult data) {
        switch (requestCode) {
            case CREATE_CODE:
//                circleList = mock();
//                mListUIListener.afterCreateUI(ListUIListener.SUCCESS_CODE);
                if (isSuccessData(data)) {
                    baseLine = data.getBaseLine();
                    circleList = new ArrayList<Circle>();
                    circleList.addAll(data.getCircleList());
                    mListUIListener.afterCreateUI(ListUIListener.SUCCESS_CODE);

                } else {
                    circleList = new ArrayList<Circle>();
                    mListUIListener.afterCreateUI(ListUIListener.NULL_LIST_CODE);
                }
                notifyDataSetChanged();
                break;
            case REFRESH_CODE:
//                circleList = mock();
//                mListUIListener.afterRefreshUI(ListUIListener.SUCCESS_CODE);
                if (isSuccessData(data)) {
                    baseLine = data.getBaseLine();
                    circleList = new ArrayList<Circle>();
                    circleList.addAll(data.getCircleList());
                    mListUIListener.afterRefreshUI(ListUIListener.SUCCESS_CODE);
                    notifyDataSetChanged();
                } else
                    mListUIListener.afterRefreshUI(ListUIListener.NULL_LIST_CODE);
                break;
        }
    }

    private boolean isSuccessData(CircleListResult data) {
        return data != null && data.getCircleList() != null && !data.getCircleList().isEmpty();
    }

    public void create() {
        ApiManager.listCircleForUser(this, CREATE_CODE, context);
    }

    public void refresh() {
        ApiManager.listCircleForUser(this, REFRESH_CODE, context);
    }

    public void order(List<String> idList) {
        List<Circle> orderList = new ArrayList<>();
        for (String id : idList) {
            for (Circle circle : circleList) {
                if (id.equals(circle.getId())) {
                    orderList.add(circle);
                    break;
                }
            }
        }
        circleList = orderList;
        notifyDataSetChanged();
    }

    View.OnClickListener onDynamicClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (activityWR == null || activityWR.get() == null)
                return;
            Activity activity = activityWR.get();
            Dynamic dynamic = (Dynamic) v.getTag();
            Intent intent = new Intent(activity, DynamicDetailActivity.class);
            intent.putExtra("dynamic", dynamic);
            activity.startActivity(intent);
        }
    };

    public static class CircleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.rootView)
        public View rootView;

        @Bind(R.id.avatar)
        public RoundedImageView avatarIV;

        @Bind(R.id.circleName)
        public TextView circleNameTV;

        @Bind(R.id.todayCount)
        public TextView todayCountTV;

        @Bind(R.id.circleDynamicLinear)
        public LinearLayout circleDynamicLinear;

        public List<DynamicView> dynamicViewList;

        public CircleTextViewHolderClicks mCircleTextViewHolderClicks;

        CircleViewHolder(View view, CircleTextViewHolderClicks circleTextViewHolderClicks) {
            super(view);
            ButterKnife.bind(this, view);
            LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
            dynamicViewList = new ArrayList<>();
            for (int i = 0; i < 2; ++i) {
                DynamicView dynamicView = new DynamicView();
                LinearLayout rootLayout = (LinearLayout) layoutInflater.inflate(R.layout.social_include_circle_dynamic, circleDynamicLinear, false);
                dynamicView.rootLayout = rootLayout;
                dynamicView.avatarIV = (ImageView) rootLayout.findViewById(R.id.ownerAvatar);
                dynamicView.nameTV = (TextView) rootLayout.findViewById(R.id.ownerName);
                dynamicView.titleTV = (TextView) rootLayout.findViewById(R.id.dynamicTitle);
                dynamicView.createdTimeTV = (TextView) rootLayout.findViewById(R.id.dynamicCreatedTime);
                circleDynamicLinear.addView(rootLayout);
                dynamicViewList.add(dynamicView);
            }
            mCircleTextViewHolderClicks = circleTextViewHolderClicks;
            rootView.setOnClickListener(this);
        }

        public interface CircleTextViewHolderClicks {
            void onItemView(View caller, int position);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rootView:
                    mCircleTextViewHolderClicks.onItemView(v, getAdapterPosition());
                    return;
            }

        }
    }

    public static class DynamicView {

        public LinearLayout rootLayout;

        public TextView titleTV;

        public ImageView avatarIV;

        public TextView nameTV;

        public TextView createdTimeTV;

    }

}