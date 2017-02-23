package com.jkyssocial.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.proxy.AppImpl;
import com.jkys.sociallib.R2;
import com.jkyssocial.activity.AllCircleActivity;
import com.jkyssocial.activity.CircleMainActivity;
import com.jkyssocial.activity.SortMyCircleActivity;
import com.jkyssocial.adapter.CircleWithDynamicListAdapter;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.CircleListResult;
import com.jkyssocial.event.ChangeMyCircleOrderEvent;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.jkyssocial.event.FollowCircleEvent;
import com.jkyssocial.listener.ListUIListener;
import com.jkyssocial.listener.ScrollToTopListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.util.ImageManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;
import mehdi.sakout.fancybuttons.FancyButton;

import static butterknife.ButterKnife.bind;

/**
 * 社区糖友圈首页-圈子
 *
 * @author yangxiaolong
 */
public class SocialCircleFragment extends SocialBaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    @BindView(R2.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R2.id.headerLinear)
    LinearLayout headerLinear;

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    CircleWithDynamicListAdapter dynamicListAdapter;

    LinearLayoutManager linearLayoutManager;

    LayoutInflater mLayoutInflater;
    @BindView(R2.id.scrollToTop)
    ImageView scrollToTop;
    @BindView(R2.id.main_content)
    CoordinatorLayout mainContent;
    private Unbinder unbinder;

    static class ListUIListenerImpl implements ListUIListener {
        private WeakReference<SocialCircleFragment> fragmentWR;

        public ListUIListenerImpl(SocialCircleFragment fragment) {
            fragmentWR = new WeakReference<SocialCircleFragment>(fragment);
        }

        @Override
        public void afterCreateUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            SocialCircleFragment fragment = fragmentWR.get();
            if (fragment.swipeRefreshLayout != null)
                fragment.swipeRefreshLayout.setEnabled(true);
        }

        @Override
        public void afterRefreshUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            SocialCircleFragment fragment = fragmentWR.get();
            if (fragment.swipeRefreshLayout != null)
                fragment.swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void afterLoadmoreUI(int resultCode) {

        }

    }


    static class FollowCircleRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<SocialCircleFragment> fragmentWR;
        private Circle circle;
        private FancyButton fancyButton;

        public FollowCircleRequestListener(SocialCircleFragment fragment, Circle circle, FancyButton fancyButton) {
            fragmentWR = new WeakReference<SocialCircleFragment>(fragment);
            this.circle = circle;
            this.fancyButton = fancyButton;
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            SocialCircleFragment fragment = fragmentWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                circle.setHasMe(1);
                fancyButton.setVisibility(View.GONE);
                EventBus.getDefault().post(new FollowCircleEvent(circle.getId(), 1));
                EventBus.getDefault().post(new ChangeUserInfoEvent());
            }
        }
    }

    static class ListBuddyForRecommendListener implements RequestManager.RequestListener<CircleListResult> {
        private WeakReference<SocialCircleFragment> fragmentWR;

        public ListBuddyForRecommendListener(SocialCircleFragment fragment) {
            fragmentWR = new WeakReference<SocialCircleFragment>(fragment);
        }

        @Override
        public void processResult(int requestCode, int resultCode, CircleListResult data) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            final SocialCircleFragment fragment = fragmentWR.get();
            if (data != null && data.getCircleList() != null && !data.getCircleList().isEmpty()) {
                int size = data.getCircleList().size() < 4 ? data.getCircleList().size() : 4;
                for (int i = 0; i < size; ++i) {
                    final Circle circle = data.getCircleList().get(i);
                    View view = fragment.mLayoutInflater.inflate(R.layout.social_include_listitem_social_circle_header,
                            fragment.headerLinear, false);
                    view.setTag(circle);
                    fragment.recommendCircleViewList.add(view);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fragment.showLoginDialog())
                                return;
                            AppImpl.getAppRroxy().addLog(fragment.getContext(), "event-forum-recommend-circle-" + circle.getId());
                            Intent intent = new Intent(fragment.getActivity(), CircleMainActivity.class);
                            intent.putExtra("circle", circle);
                            fragment.getActivity().startActivity(intent);
                        }
                    });
                    RoundedImageView avatar = (RoundedImageView) view.findViewById(R.id.avatar);
                    TextView circleName = (TextView) view.findViewById(R.id.circleName);
                    TextView circleUserCount = (TextView) view.findViewById(R.id.circleUserCount);
                    TextView circleDesc = (TextView) view.findViewById(R.id.circleDesc);
                    final FancyButton fancyButton = (FancyButton) view.findViewById(R.id.joinCircle);
                    if (circle.getHasMe() == 0) {
                        fancyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (fragment.showLoginDialog())
                                    return;
                                if (circle.getHasMe() == 0) {
                                    ApiManager.followCircle(new FollowCircleRequestListener(fragment, circle, fancyButton), 0, fragment.getActivity(), circle.getId(), 1);
                                }
                            }
                        });
                    } else {
                        fancyButton.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(circle.getAvatar())) {
                        ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + circle.getAvatar(), null,
                                avatar, ImageManager.circleAvatarOptions);
                    }

                    circleName.setText(circle.getTitle());
                    int memberCount = circle.getStat() == null ? 0 : circle.getStat().getMemberCount();
                    circleUserCount.setText("成员: " + memberCount);
                    circleDesc.setText(circle.getSummary());
                    fragment.headerLinear.addView(view);
                }
            }
        }
    }

    public static SocialCircleFragment newInstance() {
        SocialCircleFragment socialCircleDynamicFragment = new SocialCircleFragment();
        return socialCircleDynamicFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutInflater = LayoutInflater.from(getContext());
    }

    private List<View> recommendCircleViewList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_fragment_social_circle, container, false);
        unbinder= ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
//        ApiManager.listExpPatient(listBuddyForRecommendListener, 0, getContext(), null, 10);
        ApiManager.listCircleForRecommend(new ListBuddyForRecommendListener(this), 0, getContext());
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        dynamicListAdapter = new CircleWithDynamicListAdapter(getActivity(), new ListUIListenerImpl(this));
        recyclerView.setAdapter(dynamicListAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.social_primary, R.color.social_primary, R.color.social_primary, R.color.social_primary);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        appBarLayout.addOnOffsetChangedListener(this);
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
        return view;
    }

   @OnClick(R2.id.banner)
    void backdropOnClick() {
        if (showLoginDialog())
            return;
        startActivity(new Intent(getActivity(), AllCircleActivity.class));
    }

   @OnClick(R2.id.sortMyCircle)
    void sortMyCircleOnClick() {
        if (showLoginDialog())
            return;
        startActivity(new Intent(getActivity(), SortMyCircleActivity.class));
    }

   @OnClick(R2.id.scrollToTop)
    void onScrollToTop(View view) {
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipeRefreshLayout.setEnabled(verticalOffset == 0);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        dynamicListAdapter.refresh();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        dynamicListAdapter = null;
        super.onDestroyView();
        unbinder.unbind();
    }

    public void onEventMainThread(FollowCircleEvent event) {
        for (View view : recommendCircleViewList) {
            final Circle circle = (Circle) view.getTag();
            if (circle != null && circle.getId() != null && circle.getId().equals(event.circleId)) {
                circle.setHasMe(event.follow);
                final FancyButton fancyButton = (FancyButton) view.findViewById(R.id.joinCircle);
                if (circle.getHasMe() == 0) {
                    fancyButton.setVisibility(View.VISIBLE);
                    fancyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (showLoginDialog())
                                return;
                            if (circle.getHasMe() == 0) {
                                ApiManager.followCircle(new FollowCircleRequestListener(
                                                SocialCircleFragment.this, circle, fancyButton), 0,
                                        getContext(), circle.getId(), 1);
                            }
                        }
                    });
                } else {
                    fancyButton.setVisibility(View.GONE);
                }
            }
        }
        if (dynamicListAdapter != null) {
            if (event.follow == 0) {
                dynamicListAdapter.deleteCircle(event.circleId);
            } else {
                dynamicListAdapter.refresh();
            }
        }
    }

    public void onEventMainThread(ChangeMyCircleOrderEvent event) {
        if (dynamicListAdapter != null) {
            dynamicListAdapter.order(event.circleIdList);
        }
    }
}
