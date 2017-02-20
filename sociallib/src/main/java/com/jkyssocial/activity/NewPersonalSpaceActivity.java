package com.jkyssocial.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkys.jkysbase.TimeUtil;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.event.ChangSocialMessageEvent;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.jkyssocial.event.FollowUserEvent;
import com.jkyssocial.listener.EndlessRecyclerOnScrollListener;
import com.jkyssocial.pageradapter.MySpacePagerAdapter;
import com.jkyssocial.pageradapter.MySpacePagerOtherBuddyAdapter;
import com.mintcode.area_patient.area_mine.MyInfoPOJO;
import com.mintcode.area_patient.area_mine.MyInfoUtil;
import com.mintcode.area_patient.entity.MyInfo;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.DensityUtils;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;

import org.jsoup.helper.StringUtil;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;
import mehdi.sakout.fancybuttons.FancyButton;

//import com.jkyssocial.adapter.PersonalSpaceDynamicListAdapter;


/**
 * 社区糖友圈 - 个人空间页
 *
 * @author yangxiaolong
 */
public class NewPersonalSpaceActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    public static final int REQUEST_OHTER_INFO = 3;

    @Bind(R.id.back)
    View back;
    @Bind(R.id.toolbarTitle)
    TextView toolbarTitle;
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    //    @Bind(R.id.recyclerView)
//    RecyclerView recyclerView;
//    @Bind(R.id.swipeRefreshLayout)
//    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.avatar)
    ImageView avatar;
    @Bind(R.id.vFlag)
    ImageView vFlag;
    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.first_attr)
    TextView firstAttr;
    @Bind(R.id.signature)
    TextView signature;
    @Bind(R.id.attentionBtn)
    FancyButton attentionBtn;
    @Bind(R.id.personalSpaceHeader)
    RelativeLayout personalSpaceHeader;
    @Bind(R.id.backgroundImage)
    ImageView personalSpaceHeaderBG;
    @Bind(R.id.attention)
    TextView attention;
    @Bind(R.id.beAttention)
    TextView beAttention;
    @Bind(R.id.circle)
    TextView circle;
    @Bind(R.id.message)
    TextView message;
    @Bind(R.id.messageArea)
    View messageArea;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.main_content)
    CoordinatorLayout mainContent;
    @Bind(R.id.publishDynamic)
    ImageView publishDynamic;
    //    @Bind(R.id.scrollToTop)
//    ImageView scrollToTop;
    @Bind(R.id.unreadNum)
    FancyButton unreadNum;
    @Bind(R.id.guidance)
    ViewStub guidance;
    //    @Bind(R.id.tv_bg_message)
//    TextView emptyMessage;
    @Bind(R.id.new_personal_viewPager)
    ViewPager viewPager;
    @Bind(R.id.new_personal_tablayout)
    TabLayout tabLayout;
    @Bind(R.id.tab_frameLayout)
    FrameLayout tabFrameLayout;

//    PersonalSpaceDynamicListAdapter dynamicListAdapter;

    LinearLayoutManager linearLayoutManager;

    LayoutInflater mLayoutInflater;

    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    Buddy myBuddy;

    Buddy otherBuddy;

    public Buddy getOtherBuddy() {
        return otherBuddy;
    }

    FragmentPagerAdapter pagerAdapter;

    static class FollowUserResquestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<NewPersonalSpaceActivity> activityWR;
        private int follow;

        public FollowUserResquestListener(NewPersonalSpaceActivity activity, int follow) {
            activityWR = new WeakReference<NewPersonalSpaceActivity>(activity);
            this.follow = follow;
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null) {
                return;
            }
            NewPersonalSpaceActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                EventBus.getDefault().post(new ChangeUserInfoEvent());
                EventBus.getDefault().post(new FollowUserEvent(activity.otherBuddy.getBuddyId(), follow));
                activity.otherBuddy.setIdolFlag((byte) follow);
                if (follow == 1)
                    activity.attentionBtn.setVisibility(View.GONE);
            }
        }
    }

    static class GetUserInfoResquestListener implements RequestManager.RequestListener<GetUserInfoResult> {
        private WeakReference<NewPersonalSpaceActivity> activityWR;

        public GetUserInfoResquestListener(NewPersonalSpaceActivity activity) {
            activityWR = new WeakReference<NewPersonalSpaceActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (activityWR == null || activityWR.get() == null) {
                return;
            }
            NewPersonalSpaceActivity activity = activityWR.get();
            if (activity.otherBuddy == null || activity.myBuddy.getBuddyId().equals(activity.otherBuddy.getBuddyId())) {
                activity.myBuddy = data.getBuddy();
                activity.initMyBuddyHeaderView();
                // 我的那边信息是每次resume中获取到本地缓存的图片地址。我在这边存储上传的头像。
                String avatarUrl = activity.myBuddy.getImgUrl();
                MyInfoUtil infoUtil = new MyInfoUtil();
                MyInfoPOJO infoPOJO = infoUtil.getMyInfo();
                MyInfo myInfo;
                if (infoPOJO != null) {
                    myInfo = infoPOJO.getMyinfo();
                    if (myInfo == null) {
                        myInfo = new MyInfo();
                    }
                } else {
                    infoPOJO = new MyInfoPOJO();
                    myInfo = new MyInfo();
                }
                myInfo.setAvatar(avatarUrl);
                infoPOJO.setMyinfo(myInfo);
                infoUtil.saveMyInfo(infoPOJO);
            }
        }
    }

    static class GetOtherUserInfoResquestListener implements RequestManager.RequestListener<GetUserInfoResult> {
        private WeakReference<NewPersonalSpaceActivity> activityWR;

        public GetOtherUserInfoResquestListener(NewPersonalSpaceActivity activity) {
            activityWR = new WeakReference<NewPersonalSpaceActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (activityWR == null || activityWR.get() == null) {
                return;
            }
            NewPersonalSpaceActivity activity = activityWR.get();
            if (data != null && data.getBuddy() != null) {
                activity.otherBuddy = data.getBuddy();
                activity.initOtherBuddyHeaderView();
            }
        }
    }

    @OnClick(R.id.back)
    void back(View view) {
        finish();
    }

    @OnClick(R.id.publishDynamic)
    void publishDynamic(View view) {
        startActivity(new Intent(NewPersonalSpaceActivity.this, PublishDynamicActivity.class));
    }

    @OnClick(R.id.message)
    public void messageOnClick(View view) {
        startActivity(new Intent(NewPersonalSpaceActivity.this, MessageCenterActivity.class));
    }

    @OnClick(R.id.circle)
    public void circleOnClick(View view) {
        Intent intent = new Intent(NewPersonalSpaceActivity.this, MyEnterCircleOldActivity.class);
        if (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId())) {
            intent.putExtra("myBuddy", myBuddy);
        } else {
            intent.putExtra("otherBuddy", otherBuddy);
        }
        startActivity(intent);
    }

    @OnClick(R.id.attentionBtn)
    public void attentionBtnOnClick(View view) {
        if (otherBuddy.getIdolFlag() == 1) {
            LogUtil.addLog(getApplicationContext(), "event-forum-cancel-concern");
            ApiManager.followUser(new FollowUserResquestListener(this, 0), 1, getApplicationContext(), otherBuddy.getBuddyId(), 0);
        } else {
            LogUtil.addLog(getApplicationContext(), "event-forum-concern");
            ApiManager.followUser(new FollowUserResquestListener(this, 1), 1, getApplicationContext(), otherBuddy.getBuddyId(), 1);
        }
    }

    @OnClick(R.id.attention)
    public void attentionOnClick(View view) {
        Intent intent = new Intent(NewPersonalSpaceActivity.this, ListAttentionActivity.class);
        if (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId())) {
            intent.putExtra("myBuddy", myBuddy);
        } else {
            intent.putExtra("otherBuddy", otherBuddy);
        }
        startActivity(intent);
    }

    @OnClick(R.id.beAttention)
    public void beAttentionOnClick(View view) {
        Intent intent = new Intent(NewPersonalSpaceActivity.this, ListBeAttentionActivity.class);
        if (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId())) {
            intent.putExtra("myBuddy", myBuddy);
        } else {
            intent.putExtra("otherBuddy", otherBuddy);
        }
        startActivity(intent);
    }

    @OnClick(R.id.avatar)
    public void avatarOnClick(View view) {
        if (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId())) {
            Intent intent = new Intent(NewPersonalSpaceActivity.this, EditPersonalSpaceActivity.class);
            intent.putExtra("myBuddy", myBuddy);
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_new_personal_space);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        myBuddy = CommonInfoManager.getInstance().getUserInfo(this);
        otherBuddy = (Buddy) getIntent().getSerializableExtra("otherBuddy");
        if (myBuddy == null) {
            finish();
            return;
        } else {
            // 我的空间
            if (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId())) {
                if (CommonInfoManager.showGuidance(getApplicationContext(), getClass().getName())) {
                    final RelativeLayout guidanceLayout = (RelativeLayout) guidance.inflate();
                    final ImageView imageView = (ImageView) guidanceLayout.findViewById(R.id.image_shower);
                    final FancyButton fancyButton = (FancyButton) guidanceLayout.findViewById(R.id.i_know);
                    fancyButton.setVisibility(View.GONE);
                    imageView.setImageResource(R.drawable.social_personal_space_gudiance_avatar);
                    guidanceLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageView.setPadding(DensityUtils.dipTopx(getApplicationContext(), 55), 0, 0, 0);
                            imageView.setImageResource(R.drawable.social_personal_space_gudiance_dynamic);
                            fancyButton.setVisibility(View.VISIBLE);
                        }
                    });
                    fancyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            guidanceLayout.setVisibility(View.GONE);
                        }
                    });
                }
                initMyBuddyHeaderView();
//                dynamicListAdapter = new PersonalSpaceDynamicListAdapter(this, this, myBuddy);
            } else {
                // 别人的空间
                ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_WITHOUT_CACHE,
                        new GetOtherUserInfoResquestListener(this), 1, this, otherBuddy.getBuddyId());

                initOtherBuddyHeaderView();
//                dynamicListAdapter = new PersonalSpaceDynamicListAdapter(this, this, otherBuddy);
            }
        }
        mLayoutInflater = LayoutInflater.from(this);

//        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(dynamicListAdapter);

//        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int current_page) {
//                dynamicListAdapter.loadmore();
//            }
//        };
//        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);

//        swipeRefreshLayout.setColorSchemeResources(R.color.social_primary, R.color.social_primary, R.color.social_primary, R.color.social_primary);
//        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setEnabled(false);

        appBarLayout.addOnOffsetChangedListener(this);

//        ScrollToTopListener scrollToTopListener = new ScrollToTopListener(linearLayoutManager) {
//            @Override
//            public void onHide() {
//                scrollToTop.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onShow() {
//                scrollToTop.setVisibility(View.VISIBLE);
//            }
//        };
//        recyclerView.addOnScrollListener(scrollToTopListener);

//        dynamicListAdapter.create();
        if (otherBuddy == null)
            LogUtil.addLog(this, "page-forum-myspace-" + myBuddy.getBuddyId());
        else
            LogUtil.addLog(this, "page-forum-myspace-" + otherBuddy.getBuddyId());

        setViewPager();
    }

    private void setViewPager() {
        if (otherBuddy == null) {
//            pagerAdapter = new MySpacePagerAdapter(getSupportFragmentManager(), otherBuddy==null ? new String[]{"糖友动态","我的动态"} : new String[]{"全部动态","他的动态"});
            tabFrameLayout.setVisibility(View.VISIBLE);
            pagerAdapter = new MySpacePagerAdapter(getSupportFragmentManager(), new String[]{"我的动态", "糖友动态"});
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFFFF"));
            tabLayout.setTabTextColors(Color.parseColor("#333333"), Color.parseColor("#4991FD"));
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabFrameLayout.setVisibility(View.GONE);
            pagerAdapter = new MySpacePagerOtherBuddyAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
        }

    }

//    @OnClick(R.id.scrollToTop)
//    void onScrollToTop(View view){
//        recyclerView.smoothScrollToPosition(0);
//    }

    private void initMyBuddyHeaderView() {
        if (!TextUtils.isEmpty(myBuddy.getImgUrl())) {
            ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + myBuddy.getImgUrl(), this, avatar,
                    ImageManager.avatarOptions);
        }
        if (!TextUtils.isEmpty(myBuddy.getBgImgUrl())) {
            ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH + myBuddy.getBgImgUrl(),
                    this, personalSpaceHeaderBG, R.drawable.social_personal_space_bg);
        } else {
            personalSpaceHeaderBG.setImageResource(R.drawable.social_personal_space_bg);
        }
        if (myBuddy.getUserType() == 1) {
            String suffix = myBuddy.getCertStatus() != 2 ? " (未认证)" : "";
            String hosptital = TextUtils.isEmpty(myBuddy.getHospital()) ? "" : myBuddy.getHospital() + suffix;
            String title = TextUtils.isEmpty(myBuddy.getTitle()) ? "" : myBuddy.getTitle();
            firstAttr.setText(hosptital + "  " + title);
        } else {
            String diabetesType = CommonInfoManager.getDiabetesType(myBuddy.getDiabetesType(), myBuddy.getDiabetesTypeName());
            if (myBuddy.getDiabetesType() != 3 && myBuddy.getDiabetesTime() != null) {
                String time = TimeUtil.getDiabetesYear(myBuddy.getDiabetesType(), myBuddy.getDiabetesTime());
                firstAttr.setText(diabetesType + "  " + time);
            } else {
                firstAttr.setText(diabetesType);
            }
        }
        userName.setText(myBuddy.getUserName());
        signature.setText(myBuddy.getSignature());
        ImageManager.setVFlag(vFlag, myBuddy);
        attentionBtn.setVisibility(View.GONE);
        attention.setText(myBuddy.getIdolCount() + "\n关注");
        beAttention.setText(myBuddy.getFansCount() + "\n被关注");
        circle.setText(myBuddy.getCircleCount() + "\n圈子");
        message.setText(myBuddy.getMsgCount() + "\n消息");
        toolbarTitle.setText("我的空间");
    }

    private void initOtherBuddyHeaderView() {
        if (otherBuddy.getIdolFlag() == 0)
            attentionBtn.setVisibility(View.VISIBLE);
        else
            attentionBtn.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(otherBuddy.getImgUrl())) {
            ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + otherBuddy.getImgUrl(), this,
                    avatar, ImageManager.avatarOptions);
        }
        if (!TextUtils.isEmpty(otherBuddy.getBgImgUrl())) {
            ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH + otherBuddy.getBgImgUrl(),
                    this, personalSpaceHeaderBG, R.drawable.social_personal_space_bg);
        } else {
            personalSpaceHeaderBG.setImageResource(R.drawable.social_personal_space_bg);
        }


        if (otherBuddy.getUserType() == 1) {
            String suffix = otherBuddy.getCertStatus() != 2 ? " (未认证)" : "";
            String hosptital = TextUtils.isEmpty(otherBuddy.getHospital()) ? "" : otherBuddy.getHospital() + suffix;
            String title = TextUtils.isEmpty(otherBuddy.getTitle()) ? "" : otherBuddy.getTitle();
            firstAttr.setText(hosptital + "  " + title);
        } else {
            String diabetesType = CommonInfoManager.getDiabetesType(otherBuddy.getDiabetesType(), otherBuddy.getDiabetesTypeName());
            if (otherBuddy.getDiabetesType() != 3 && otherBuddy.getDiabetesTime() != null) {
                String time = TimeUtil.getDiabetesYear(otherBuddy.getDiabetesType(), otherBuddy.getDiabetesTime());
                firstAttr.setText(diabetesType + "  " + time);
            } else {
                firstAttr.setText(diabetesType);
            }
        }
        userName.setText(otherBuddy.getUserName());
        signature.setText(otherBuddy.getSignature());
        ImageManager.setVFlag(vFlag, otherBuddy);
        messageArea.setVisibility(View.GONE);
        publishDynamic.setVisibility(View.GONE);
        attention.setText(otherBuddy.getIdolCount() + "\n关注");
        beAttention.setText(otherBuddy.getFansCount() + "\n被关注");
        circle.setText(otherBuddy.getCircleCount() + "\n圈子");
        toolbarTitle.setText(otherBuddy.getUserName() + " 的空间");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
//        Log.e("内存","NewPersonalSpaceActivity onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

//    @Override
//    public void afterCreateUI(int resultCode) {
//        swipeRefreshLayout.setEnabled(true);
//        switch (resultCode) {
//            case ListUIListener.SUCCESS_CODE:
//                emptyMessage.setVisibility(View.GONE);
//                break;
//            case ListUIListener.NETWORK_NULL_LIST_CODE:
//                break;
//            case ListUIListener.NULL_LIST_CODE:
//                emptyMessage.setVisibility(View.VISIBLE);
//                break;
//        }
//    }

//    @Override
//    public void afterRefreshUI(int resultCode) {
//        if(swipeRefreshLayout != null)
//            swipeRefreshLayout.setRefreshing(false);
//        endlessRecyclerOnScrollListener.reset();
//        switch (resultCode) {
//            case ListUIListener.SUCCESS_CODE:
//                emptyMessage.setVisibility(View.GONE);
//                break;
//            case ListUIListener.NETWORK_NULL_LIST_CODE:
//                break;
//            case ListUIListener.NULL_LIST_CODE:
//                emptyMessage.setVisibility(View.VISIBLE);
//                break;
//        }
//    }

//    @Override
//    public void afterLoadmoreUI(int resultCode) {
//
//    }
//
//    @Override
//    public void onRefresh() {
//        dynamicListAdapter.refresh();
//    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        swipeRefreshLayout.setEnabled(verticalOffset == 0);
        toolbarTitle.setVisibility(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() ? View.VISIBLE : View.GONE);
    }

//    public void onEventMainThread(PublishDynamicEvent event) {
//        if(dynamicListAdapter != null)
//            dynamicListAdapter.replaceItem(event.getDynamic(), event.getSendingDynamic());
//    }

//    public void onEventMainThread(PrePublishDynamicEvent event) {
//        Dynamic sendingDynamic = event.getDynamic();
//        dynamicListAdapter.addItem(sendingDynamic);
//        recyclerView.scrollToPosition(0);
//        emptyMessage.setVisibility(View.GONE);
//    }

    public void onEventMainThread(ChangeUserInfoEvent event) {
        if (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId())) {
            ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, new GetUserInfoResquestListener(this), 1, this, null);
        }
    }

    public void onEventMainThread(ChangSocialMessageEvent event) {
        if (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId()) && event.getNum() > 0) {
            ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, new GetUserInfoResquestListener(this), 1, this, null);
            if (unreadNum != null) {
                unreadNum.setVisibility(event.getNum() > 0 ? View.VISIBLE : View.GONE);
                int num = event.getNum() < 99 ? event.getNum() : 99;
                unreadNum.setText("" + num);
            }
        }
    }

//    public void onEventMainThread(DeleteDynamicEvent event) {
//        if(dynamicListAdapter != null && (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId()))) {
//            dynamicListAdapter.removeItem(event.getDynamic().getDynamicId());
//        }
//        if (dynamicListAdapter.getItemCount() == 0) {
////            emptyMessage.setVisibility(View.VISIBLE);
//        }
//    }

}
