package com.jkyssocial.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jkys.jkysim.database.KeyValueDBService;
import com.jkys.tools.DeviceUtil;
import com.jkyshealth.tool.ViewUtil;
import com.jkyslogin.LoginHelper;
import com.jkyssocial.activity.MyEnterCircleActivity;
import com.jkyssocial.activity.SugarControlStarActivity;
import com.jkyssocial.adapter.SocialMainHeadAndBodyAdapter;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.CircleListResult;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.data.ListBuddyResult;
import com.jkyssocial.data.SocialMainHeadAndBodyData;
import com.jkyssocial.data.SocialMainHeadData;
import com.jkyssocial.listener.EndlessRecyclerOnScrollListener;
import com.jkyssocial.listener.ListUIListener;
import com.mintcode.util.Keys;
import com.mintcode.util.Utils;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

/**
 * 热门推荐
 */
public class HotRecommendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.scrollToTop)
    ImageView scrollToTop;

    @Bind(R.id.recommend_rl_show)
    RelativeLayout recommend_rl_show;

    public static Buddy myBuddy;

    LayoutInflater mLayoutInflater;

    SocialMainHeadAndBodyAdapter dynamicListAdapter;

    LinearLayoutManager linearLayoutManager;

    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    static class ListUIListenerImpl implements ListUIListener {
        private WeakReference<HotRecommendFragment> fragmentWR;

        public ListUIListenerImpl(HotRecommendFragment fragment) {
            fragmentWR = new WeakReference<HotRecommendFragment>(fragment);
        }

        @Override
        public void afterCreateUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            HotRecommendFragment fragment = fragmentWR.get();
            fragment.swipeRefreshLayout.setEnabled(true);
            isNeedLoadMore = true; // 第一次加载的时候不需要loadmore。
        }

        @Override
        public void afterRefreshUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            HotRecommendFragment fragment = fragmentWR.get();
            if (fragment.swipeRefreshLayout != null)
                fragment.swipeRefreshLayout.setRefreshing(false);
            // 通知SocialMainFragment更新完成
            fragment.endlessRecyclerOnScrollListener.reset();
            if (fragment.isLayoutFrozen) {
                fragment.recyclerView.setLayoutFrozen(false);
                fragment.isLayoutFrozen = false;
            }
        }

        @Override
        public void afterLoadmoreUI(int resultCode) {
        }

    }

    public static HotRecommendFragment newInstance() {
        HotRecommendFragment pageFragment = new HotRecommendFragment();
        return pageFragment;
    }

    private boolean isLayoutFrozen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutInflater = LayoutInflater.from(getContext());
        myBuddy = CommonInfoManager.getInstance().getUserInfo(getContext());
        updateTime = System.currentTimeMillis();
        if (BuildConfig.DEBUG)
            intervalTime = 3 * 60 * 1000;
        else
            intervalTime = 20 * 60 * 1000;
    }

    public static boolean isNeedLoadMore = false; // 是否需要再加更多。 目前用endless回调会有问题。首次填充数据的时候就会有一次回调将这次去掉。

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_fragment_hot_recommend, container, false);
        ButterKnife.bind(this, view);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("ZernOnScrolled-loadMore", "endlessRecyclerScrLis-onLoadMore---" + isNeedLoadMore);
                if (isNeedLoadMore) {
                    dynamicListAdapter.loadmore();
                }
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);

        swipeRefreshLayout.setColorSchemeResources(R.color.social_primary, R.color.social_primary, R.color.social_primary, R.color.social_primary);
        swipeRefreshLayout.setProgressViewOffset(true, 0, 50 * DeviceUtil.getDensity());
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(true);

        swipeRefreshLayout.setRefreshing(true);
        recyclerView.addOnScrollListener(scrollListener);
        dynamicListAdapter = new SocialMainHeadAndBodyAdapter(getActivity(), new ListUIListenerImpl(HotRecommendFragment.this));
        recyclerView.setAdapter(dynamicListAdapter);
        ApiManager.listCircleForRecommend(listCircleForRecommendListener, 0, getContext());
        dynamicListAdapter.create();
        return view;
    }

    List<Circle> circleList = new LinkedList<>();
    List<Buddy> buddyList = new LinkedList<>();

    // 糖友圈子的数据加载
    RequestManager.RequestListener<CircleListResult> listCircleForRecommendListener = new RequestManager.RequestListener<CircleListResult>() {
        @Override
        public void processResult(int requestCode, int resultCode, CircleListResult data) {
            swipeRefreshLayout.setRefreshing(false);
            if (data != null && data.getCircleList() != null && !data.getCircleList().isEmpty()) {
                int size = data.getCircleList().size() <= 5 ? data.getCircleList().size() : 5;
                circleList.clear();
                for (int i = 0; i < size; i++) {
                    circleList.add(data.getCircleList().get(i));
                }
                ApiManager.listStar(listBuddyForRecommendListener, 0, getContext());
            }
        }
    };

    RequestManager.RequestListener<ListBuddyResult> listBuddyForRecommendListener = new RequestManager.RequestListener<ListBuddyResult>() {
        @Override
        public void processResult(int requestCode, int resultCode, ListBuddyResult data) {
            buddyList.clear();
            if (data != null && data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
                int size = data.getBuddyList().size() < 4 ? data.getBuddyList().size() : 4;
                for (int i = 0; i < size; ++i) {
                    buddyList.add(data.getBuddyList().get(i));
                }
            }
            List<SocialMainHeadAndBodyData> socialMainHeadAndBodyList = dynamicListAdapter.getSocialMainHeadAndBodyList();
            SocialMainHeadData socialMainHeadData = new SocialMainHeadData(circleList, buddyList);
            socialMainHeadAndBodyList.add(0, socialMainHeadData);
            dynamicListAdapter.notifyDataSetChanged();
        }
    };

    int zernLog = 0;
    private int totalDy = 0;

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            totalDy += dy;
            Log.d("ZernOnScrolled-", "dy--" + dy);
//            float eventLength = statusBarHeight + Utils.sp2px(getContext(), 448.5f);
            float eventLength = Utils.sp2px(getContext(), 421.5f);
            if (totalDy >= eventLength) { // recycleView滑动的距离长度超过当前推荐圈子处于卡顶状态的值时 卡顶效果展现
                recommend_rl_show.setVisibility(View.VISIBLE);
                Log.d("ZernOnScrolled-", "visible-" + totalDy + "-" + eventLength);
            } else {
                recommend_rl_show.setVisibility(View.GONE);
                Log.d("ZernOnScrolled-", "gone-" + totalDy + "-" + eventLength);
            }
            if (totalDy > 1500) {
                scrollToTop.setVisibility(View.VISIBLE);
                Log.d("ZernOnScrolled-", "scrollToTop-onShow");
            } else {
                scrollToTop.setVisibility(View.GONE);
                Log.d("ZernOnScrolled-", "scrollToTop-onHide");
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    @OnClick(R.id.scrollToTop)
    void onScrollToTop(View view) {
        recyclerView.stopScroll();
        recyclerView.getLayoutManager().scrollToPosition(0);
        totalDy = 0;
        recommend_rl_show.setVisibility(View.GONE);
        scrollToTop.setVisibility(View.GONE);
    }

    long updateTime;

    long intervalTime;

    @Override
    public void onResume() {
        super.onResume();
        String uid = KeyValueDBService.getInstance().findValue(Keys.UID);
        if (uid != null && !"-1000".equals(uid)) {
            ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, getUserInfoListener, 1, getActivity(), null);
        }
        if (System.currentTimeMillis() - updateTime > intervalTime) {
            long lastTime = System.currentTimeMillis() - updateTime;
            Log.d("SocialMain-", lastTime + "---" + intervalTime);
            updateTime = System.currentTimeMillis();
            onRefresh();
        }
    }

    RequestManager.RequestListener<GetUserInfoResult> getUserInfoListener = new RequestManager.RequestListener<GetUserInfoResult>() {
        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data != null && data.getBuddy() != null) {
                    myBuddy = data.getBuddy();
                    EventBus.getDefault().post(myBuddy);
                }
            }
        }
    };

    @Override
    public void onRefresh() {
        isLayoutFrozen = true;
        recyclerView.setLayoutFrozen(true);
        myBuddy = CommonInfoManager.getInstance().getUserInfo(getContext());
        dynamicListAdapter.getSocialMainHeadAndBodyList().clear();
        ApiManager.listCircleForRecommend(listCircleForRecommendListener, 0, getContext());
        dynamicListAdapter.refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dynamicListAdapter = null;
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        if (LoginHelper.getInstance().showLoginActivity(getActivity())) return;
        switch (v.getId()) {
            case R.id.social_main_mycircle:
            case R.id.social_main_mycircle_icon:
                if (!ViewUtil.singleClick()) return;
                Intent intent = new Intent(getActivity(), MyEnterCircleActivity.class);
                intent.putExtra("myBuddy", myBuddy);
                startActivity(intent);
                break;
            case R.id.findMoreSuperStar:
                startActivity(new Intent(getActivity(), SugarControlStarActivity.class));
                break;
        }
    }
}