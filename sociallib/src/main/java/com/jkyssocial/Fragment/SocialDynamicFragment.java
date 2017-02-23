package com.jkyssocial.Fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.jkys.proxy.AppImpl;
import com.jkyssocial.adapter.SugarFriendCirclesAdapter;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.CircleListResult;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.data.ListBuddyResult;
import com.jkyssocial.event.ChangSocialLatestDynamicEvent;
import com.jkyssocial.pageradapter.SocialDynamicPagerAdapter;
import com.mintcode.database.CasheDBService;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import butterknife.Unbinder;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

/**
 * 社区糖友圈首页-动态
 *
 * @author yangxiaolong
 */
public class SocialDynamicFragment extends SocialBaseFragment implements RequestManager.RequestListener<GetUserInfoResult>, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    private static final int MESSAGE_READ = 1;
    private static final int LATEST_DYNAMIC_READ = 2;

    @BindView(R2.id.dynamicViewPager)
    ViewPager dynamicViewPager;

//    @BindView(R2.id.appBarLayout)
//    AppBarLayout appBarLayout;

//    @BindView(R2.id.collapsingToolbar)
//    CollapsingToolbarLayout collapsingToolbar;

//    @BindView(R2.id.tabLayout)
//    TabLayout tabLayout;

//    @BindView(R2.id.social_main_mycircle)
//    TextView myCircle;

//    @BindView(R2.id.social_main_mycircle_icon)
//    ImageView myCircleIcon;

//    @BindView(R2.id.social_main_gridView)
//    UnScrollGridView mGridView;

//    @BindView(R2.id.headerLinear)
//    LinearLayout headerLinear;

//    @BindView(R2.id.latestDynamicUnreadView)
//    View latestDynamicUnreadView;

    SocialDynamicPagerAdapter pagerAdapter;

    Buddy myBuddy;

    LayoutInflater mLayoutInflater;

    private CasheDBService casheDBService;
    private SugarFriendCirclesAdapter mGridadapter;
    private Unbinder unbinder;

    public static SocialDynamicFragment newInstance() {
        SocialDynamicFragment socialCircleDynamicFragment = new SocialDynamicFragment();
        return socialCircleDynamicFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutInflater = LayoutInflater.from(getContext());
        myBuddy = CommonInfoManager.getInstance().getUserInfo(getContext());
        pagerAdapter = new SocialDynamicPagerAdapter(getChildFragmentManager(), getContext());

    }

    // 糖友圈子的数据加载
    RequestManager.RequestListener<CircleListResult> listCircleForRecommendListener = new RequestManager.RequestListener<CircleListResult>() {
        @Override
        public void processResult(int requestCode, int resultCode, CircleListResult data) {
            if (data != null && data.getCircleList() != null && !data.getCircleList().isEmpty()) {
                int size = data.getCircleList().size() <= 5 ? data.getCircleList().size() : 5;
                List<Circle> circleList = new LinkedList<>();
                for (int i = 0; i < size; i++) {
                    circleList.add(data.getCircleList().get(i));
                }
//                mGridadapter = new SugarFriendCirclesAdapter(getContext(), circleList);
//                mGridView.setAdapter(mGridadapter);
//                mGridadapter.notifyDataSetChanged();
//                mGridView.setOnScrollListener(SocialDynamicFragment.this);
            }
        }
    };

    RequestManager.RequestListener<ListBuddyResult> listBuddyForRecommendListener = new RequestManager.RequestListener<ListBuddyResult>() {
        @Override
        public void processResult(int requestCode, int resultCode, ListBuddyResult data) {
//            if (data != null && data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
//                int size = data.getBuddyList().size() < 4 ? data.getBuddyList().size() : 4;
//                for (int i = 0; i < size; ++i) {
//                    final Buddy buddy1 = data.getBuddyList().get(i);
//                    View view = mLayoutInflater.inflate(R.layout.social_include_listitem_social_dynamic_header, headerLinear, false);
//                    ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
//                    ImageView vFlag = (ImageView) view.findViewById(R.id.vFlag);
//                    TextView userTitle = (TextView) view.findViewById(R.id.userTitle);
//                    TextView userName = (TextView) view.findViewById(R.id.userName);
//                    ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + buddy1.getImgUrl(), null, avatar, ImageManager.avatarOptions);
//                    ImageManager.setVFlag(vFlag, buddy1);
//                    userName.setText(buddy1.getUserName());
//                    userTitle.setText(buddy1.getUserType() == 1 ? "专家医生" : "资深糖友");
//                    view.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (showLoginDialog())
//                                return;
//                            AppImpl.getAppRroxy().addLog(getContext(), "event-forum-recommend-expert-" + buddy1.getBuddyId());
//                            Intent intent = new Intent(getContext(), NewPersonalSpaceActivity.class);
//                            intent.putExtra("otherBuddy", buddy1);
//                            startActivity(intent);
//                        }
//                    });
//                    headerLinear.addView(view);
//                }
//            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_fragment_social_dynamic, container, false);
        unbinder= ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
//        String latestStr = CasheDBService.getInstance(getContext()).findValue(Keys.SOCIAL_LATEST_DYNAMIC);
//        if (!TextUtils.isEmpty(latestStr)) {
//            latestDynamicUnreadView.setVisibility(View.VISIBLE);
//        }
//        ApiManager.listCircleForRecommend(listCircleForRecommendListener, 0, getContext());
//        ApiManager.listStar(listBuddyForRecommendListener, 0, getContext());
        dynamicViewPager.setAdapter(pagerAdapter);
//        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFFFF"));
//        tabLayout.setTabTextColors(Color.parseColor("#333333"), Color.parseColor("#4991FD"));
//        tabLayout.setupWithViewPager(dynamicViewPager);
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        if (myBuddy != null && myBuddy.getCircleCount() > 0) {
//            myCircle.setVisibility(View.VISIBLE);
//            myCircleIcon.setVisibility(View.VISIBLE);
//        } else {
//            myCircle.setVisibility(View.GONE);
//            myCircleIcon.setVisibility(View.GONE);
//        }
//        collapsingToolbar.setOnScrollChangeListener(this);
        return view;
    }

    @OnPageChange(value = R.id.dynamicViewPager, callback = OnPageChange.Callback.PAGE_SELECTED)
    void onPageSelected(int position) {
        if (position == 1) {
            AppImpl.getAppRroxy().addLog(getContext(), "event-forum-recenttopic-tab");
            EventBus.getDefault().post(new ChangSocialLatestDynamicEvent(-1));
//            if (latestDynamicUnreadView.getVisibility() == View.VISIBLE) {
//                latestDynamicUnreadView.setVisibility(View.GONE);
//            }
        }
    }

//   @OnClick(R2.id.social_main_mycircle)
//    void intentToMyEnterCircle(View view) {
//        if (!ViewUtil.singleClick()) return;
////        if (myBuddy != null && myBuddy.getCircleCount() > 0) { // 因为点击我的圈子 如果我没有加入任何圈子是进不去的。
//            Intent intent = new Intent(getContext(), MyEnterCircleActivity.class);
//            intent.putExtra("myBuddy", myBuddy);
//            startActivity(intent);
////        }
//    }

//   @OnClick(R2.id.findMoreSuperStar)
//    void onfindMoreSuperStarClick(View view) {
//        if (showLoginDialog())
//            return;
//        startActivity(new Intent(getContext(), SugarControlStarActivity.class));
//    }

    @Override
    public void onResume() {
        super.onResume();
//        ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, this, 100, getActivity(), null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 刷新我加入的圈子
    @Override
    public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
        if (requestCode == 100) {
            if (requestCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data != null && data.getBuddy() != null) {
                    myBuddy = data.getBuddy();
                }
            }else if(requestCode == 9960){
                ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, this, 100, getActivity(), null);
            }
        }
    }

    public void onEventMainThread(ChangSocialLatestDynamicEvent event) {
//        if (latestDynamicUnreadView != null)
//            latestDynamicUnreadView.setVisibility(event.getNum() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        ApiManager.listCircleForRecommend(listCircleForRecommendListener, 0, getContext());
    }

    // 刷新我加入圈子的数据
//    public void onEventMainThread(RefreshMyCircleCount event) {
//        ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, this, 100, getActivity(), null);
//    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.i("DebugZernNice---", view + ": firstVisibleItem---" + firstVisibleItem + "visibleItemCount---" + visibleItemCount + "totalItemCount---" + totalItemCount);
    }

//    @Override
//    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//        Log.i( v + ": scrollX---" , scrollX + "scrollY---" + scrollY + "oldScrollX---" + oldScrollX + "oldScrollY---" + oldScrollY );
//    }
}
