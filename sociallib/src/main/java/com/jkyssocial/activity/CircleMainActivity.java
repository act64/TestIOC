package com.jkyssocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jkys.common.widget.CustomToolbar;
import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkyswidget.ConfirmTipsDialog;
import com.jkys.proxy.AppImpl;
import com.jkys.sociallib.R;
import com.jkys.sociallib.R2;
import com.jkyssocial.adapter.CircleMainDynamicListAdapter;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.util.ScrollUtils;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.CircleResult;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.DynamicListResult;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.jkyssocial.event.CircleChangeEvent;
import com.jkyssocial.event.DeleteDynamicEvent;
import com.jkyssocial.event.FollowCircleEvent;
import com.jkyssocial.event.PrePublishDynamicEvent;
import com.jkyssocial.event.PublishDynamicEvent;
import com.jkyssocial.listener.EndlessRecyclerOnScrollListener;
import com.jkyssocial.listener.ListUIListener;
import com.jkyssocial.listener.ScrollToTopListener;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * 社区糖友圈 - 圈子主页
 *
 * @author yangxiaolong
 */
public class CircleMainActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.toolbar)
    CustomToolbar toolbar;

    @BindView(R2.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R2.id.headerLinear)
    LinearLayout headerLinear;

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R2.id.circleSet)
    View circleSet;

    @BindView(R2.id.circleAvatar)
    ImageView circleAvatar;

    @BindView(R2.id.circleDesc)
    TextView circleDesc;

    @BindView(R2.id.title_toolbar)
    TextView circleName;

    @BindView(R2.id.circleOwnerName)
    TextView circleOwnerName;

    @BindView(R2.id.circleUserCount)
    TextView circleUserCount;

    @BindView(R2.id.scrollToTop)
    ImageView scrollToTop;

    @BindView(R2.id.empty_view)
    TextView emptyView;

    Circle circle;

    CircleMainDynamicListAdapter dynamicListAdapter;

    LinearLayoutManager linearLayoutManager;

    LayoutInflater mLayoutInflater;

    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    //为了防止内存泄漏，改成静态内部类
    static class ListBuddyForRecommendListener implements RequestManager.RequestListener<DynamicListResult> {
        private WeakReference<CircleMainActivity> activityWR;

        public ListBuddyForRecommendListener(CircleMainActivity activity) {
            activityWR = new WeakReference<CircleMainActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, DynamicListResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final CircleMainActivity activity = activityWR.get();
            if (data != null && data.getDynamicList() != null && !data.getDynamicList().isEmpty()) {
                int size = data.getDynamicList().size();
                for (int i = 0; i < size; ++i) {
                    final Dynamic dynamic = data.getDynamicList().get(i);
                    View view = activity.mLayoutInflater.inflate(R.layout.social_include_listitem_circle_main_header,
                            activity.headerLinear, false);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, DynamicDetailActivity.class);
                            intent.putExtra("dynamic", dynamic);
                            activity.startActivity(intent);
                        }
                    });
                    TextView dynamicTitle = (TextView) view.findViewById(R.id.dynamicTitle);
                    dynamicTitle.setText(dynamic.getTitle());
                    activity.headerLinear.addView(view);
                }
            }

        }
    }

    //为了防止内存泄漏，改成静态内部类

    /**
     * 加入圈子
     */
    static class AddFollowCircleRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<CircleMainActivity> activityWR;

        public AddFollowCircleRequestListener(CircleMainActivity activity) {
            activityWR = new WeakReference<CircleMainActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final CircleMainActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                Toast.makeText(activity.getApplicationContext(), "成功加入[" + activity.circle.getTitle() + "]", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new FollowCircleEvent(activity.circle.getId(), 1));
                EventBus.getDefault().post(new ChangeUserInfoEvent());
                // 没时间维护前面的事件处理。目前已增量的形式去完成新的需求。
                EventBus.getDefault().post(new CircleChangeEvent());
            }
        }
    }

    /**
     * 退出圈子
     */
    static class ExitFollowCircleRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<CircleMainActivity> activityWR;

        public ExitFollowCircleRequestListener(CircleMainActivity activity) {
            activityWR = new WeakReference<CircleMainActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final CircleMainActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                EventBus.getDefault().post(new FollowCircleEvent(activity.circle.getId(), 0));
                EventBus.getDefault().post(new ChangeUserInfoEvent());
                // 没时间维护前面的事件处理。目前已增量的形式去完成新的需求。
                EventBus.getDefault().post(new CircleChangeEvent());
            }
        }
    }

    /**
     * 获取圈子信息
     */
    static class GetCircleInfoRequestListener implements RequestManager.RequestListener<CircleResult> {
        private WeakReference<CircleMainActivity> activityWR;

        public GetCircleInfoRequestListener(CircleMainActivity activity) {
            activityWR = new WeakReference<CircleMainActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, CircleResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final CircleMainActivity activity = activityWR.get();
            if (data != null && data.getCircle() != null) {
                activity.circle = data.getCircle();
                activity.initCircleDetailView();
            }
        }
    }

    static class ListUIListenerImpl implements ListUIListener {
        private WeakReference<CircleMainActivity> activityWR;

        public ListUIListenerImpl(CircleMainActivity activity) {
            activityWR = new WeakReference<CircleMainActivity>(activity);
        }

        @Override
        public void afterCreateUI(int resultCode) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final CircleMainActivity activity = activityWR.get();
            if (resultCode == ListUIListener.NULL_LIST_CODE)
                activity.emptyView.setVisibility(View.VISIBLE);
            else
                activity.emptyView.setVisibility(View.GONE);
            activity.swipeRefreshLayout.setEnabled(true);
        }

        @Override
        public void afterRefreshUI(int resultCode) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final CircleMainActivity activity = activityWR.get();
            if (resultCode == ListUIListener.NULL_LIST_CODE)
                activity.emptyView.setVisibility(View.VISIBLE);
            else
                activity.emptyView.setVisibility(View.GONE);
            if (activity.swipeRefreshLayout != null)
                activity.swipeRefreshLayout.setRefreshing(false);
            activity.endlessRecyclerOnScrollListener.reset();
        }

        @Override
        public void afterLoadmoreUI(int resultCode) {

        }
    }


    @BindView(R2.id.joinCircle)
    FancyButton joinCircle;
    @BindView(R2.id.circleHeader)
    LinearLayout circleHeader;

    @OnClick(R2.id.left_rl)
    void back(View view) {
        setResult(10000);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(10000);
        finish();
    }

    @OnClick(R2.id.circleSet)
    void circleSetOnClick(View view) {
        Intent intent = new Intent(this, SetUpCircleActivity.class);
        intent.putExtra("circle", circle);
        startActivity(intent);
    }

    @OnClick(R2.id.right_rl)
    void publishDynamic(View view) {
        if (circle.getHasMe() == 0) {
            ConfirmTipsDialog dialog = new ConfirmTipsDialog(this, "未加入圈子不能发新动态，是否加入", new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    RequestManager.RequestListener lintener = new RequestManager.RequestListener<NetWorkResult>() {
                        @Override
                        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
                            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                                circle.setHasMe(1);
                                EventBus.getDefault().post(new FollowCircleEvent(circle.getId(), 1));
                                EventBus.getDefault().post(new ChangeUserInfoEvent());
                                Intent intent = new Intent(CircleMainActivity.this, PublishDynamicActivity.class);
                                intent.putExtra("circle", circle);
                                startActivity(intent);
                                if (circle != null) {
                                    Toast.makeText(context, "成功加入[" + circle.getTitle() + "]", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    };
                    ApiManager.followCircle(lintener, 0, CircleMainActivity.this, circle.getId(), 1);
                }
            });
            dialog.show();
        } else {
            Intent intent = new Intent(CircleMainActivity.this, PublishDynamicActivity.class);
            intent.putExtra("circle", circle);
            startActivity(intent);
        }
    }


    @OnClick(R2.id.joinCircle)
    void joinCircleOnClick(View view) {
        if (circle.getHasMe() == 0) {
            ApiManager.followCircle(new AddFollowCircleRequestListener(this), 0, CircleMainActivity.this, circle.getId(), 1);
        } else {
            ApiManager.followCircle(new ExitFollowCircleRequestListener(this), 0, context, circle.getId(), 0);
        }
    }

    private void setJoinCircle() {
        if (circle.getHasMe() == 0) {
            joinCircle.setBackgroundColor(ContextCompat.getColor(CircleMainActivity.this, R.color.social_primary));
            joinCircle.setTextColor(ContextCompat.getColor(CircleMainActivity.this, R.color.white));
            joinCircle.setText("加入");
            joinCircle.setVisibility(View.VISIBLE);
        } else {
            joinCircle.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_circle_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        if (CommonInfoManager.showGuidance(this, getClass().getName())) {
            ViewStub guidance = (ViewStub) findViewById(R.id.guidance);
            final RelativeLayout guidanceLayout = (RelativeLayout) guidance.inflate();
            final ImageView imageView = (ImageView) guidanceLayout.findViewById(R.id.image_shower);
            final FancyButton fancyButton = (FancyButton) guidanceLayout.findViewById(R.id.i_know);
            fancyButton.setVisibility(View.VISIBLE);
            imageView.setPadding(dp2px( 52), dp2px(8), 0, 0);
            imageView.setImageResource(R.drawable.social_circle_main_guidance_dynamic);
            guidanceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            fancyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guidanceLayout.setVisibility(View.GONE);
                }
            });
        }

        mLayoutInflater = LayoutInflater.from(this);
        circle = (Circle) getIntent().getSerializableExtra("circle");
        String jsonStr = new Gson().toJson(circle);
        initCircleDetailView();
        ApiManager.listAnnouncement(new ListBuddyForRecommendListener(this), 0, context);
        ApiManager.getCircleInfo(new GetCircleInfoRequestListener(this), 0, getApplicationContext(), circle.getId());

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        dynamicListAdapter = new CircleMainDynamicListAdapter(this, new ListUIListenerImpl(this), circle);
        recyclerView.setAdapter(dynamicListAdapter);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                dynamicListAdapter.loadmore();
            }
        };
        final int vibrant = ContextCompat.getColor(getContext(), R.color.social_primary);
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alpha1 = Math.min(1, (float) -verticalOffset / (circleSet.getMeasuredHeight() + toolbar.getMeasuredHeight()));
                toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha1, vibrant));
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.social_primary, R.color.social_primary, R.color.social_primary, R.color.social_primary);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);

        ScrollToTopListener scrollToTopListener = new ScrollToTopListener(linearLayoutManager) {
            @Override
            public void onHide() {
                scrollToTop.setVisibility(View.GONE);
            }

            @Override
            public void onShow() {
                scrollToTop.setVisibility(View.VISIBLE);
            }
        };
        recyclerView.addOnScrollListener(scrollToTopListener);

        dynamicListAdapter.create();
        AppImpl.getAppRroxy().addLog(this, "page-forum-circle-" + circle.getId());

    }

    private void initCircleDetailView() {
        circleName.setText(circle.getTitle());
        circleDesc.setText("简介: " + (circle.getSummary() == null ? "" : circle.getSummary()));
        circleOwnerName.setText("圈主: " + (circle.getOwnerName() == null ? "" : circle.getOwnerName()));
        int userCount = circle.getStat() == null ? 0 : circle.getStat().getMemberCount();
        circleUserCount.setText("" + userCount);
        if (!TextUtils.isEmpty(circle.getAvatar())) {
            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + circle.getAvatar(), null,
                    circleAvatar, ImageManager.circleAvatarOptions);
        }

        setJoinCircle();
    }

    @OnClick(R2.id.scrollToTop)
    void onScrollToTop(View view) {
        recyclerView.smoothScrollToPosition(0);
    }

    public void onEventMainThread(PublishDynamicEvent event) {
        if (dynamicListAdapter != null) {
            Dynamic sendingDynamic = event.getSendingDynamic();
            if (sendingDynamic.getCircle() != null && circle.getId().equals(sendingDynamic.getCircle().getId()))
                dynamicListAdapter.replaceItem(event.getDynamic(), sendingDynamic);
        }
    }

    public void onEventMainThread(PrePublishDynamicEvent event) {
        if (emptyView != null)
            emptyView.setVisibility(View.GONE);
        Dynamic sendingDynamic = event.getDynamic();
        if (sendingDynamic.getCircle() != null && circle.getId().equals(sendingDynamic.getCircle().getId())) {
            if (dynamicListAdapter != null && recyclerView != null) {
                dynamicListAdapter.addItem(sendingDynamic);
                recyclerView.scrollToPosition(0);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        dynamicListAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onRefresh() {
        dynamicListAdapter.refresh();
    }

    public void onEventMainThread(FollowCircleEvent event) {
        if (circle != null && circle.getId() != null && circle.getId().equals(event.circleId)) {
            if (event.follow == 0) {
                finish();
            } else {
                circle.setHasMe(event.follow);
                setJoinCircle();
            }
        }
    }

    public void onEventMainThread(DeleteDynamicEvent event) {
        if (dynamicListAdapter != null)
            dynamicListAdapter.removeItem(event.getDynamic().getDynamicId());
    }
}
