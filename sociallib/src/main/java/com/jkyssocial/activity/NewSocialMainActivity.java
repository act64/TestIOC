package com.jkyssocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jkys.common.widget.AutoScrollViewPager;
import com.jkys.jkysim.database.KeyValueDBService;
import com.jkys.proxy.AppImpl;
import com.jkys.proxy.MyInfoUtilProxy;
import com.jkys.proxy.ProxyClassFactory;
import com.jkys.sociallib.R;
import com.jkys.sociallib.R2;
import com.jkys.tools.ListUtil;
import com.jkyshealth.manager.MedicalVolleyListener;
import com.jkyshealth.result.HomeBannerData;
import com.jkyssocial.Fragment.NewLatestDynamicFragment;
import com.jkyssocial.Fragment.NewRecommendDynamicFragment;
import com.jkyssocial.Fragment.NewTopicDynamicFragment;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.data.Topic;
import com.jkyssocial.data.TopicListResult;
import com.jkyssocial.event.ChangSocialMessageEvent;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.mintcode.area_patient.area_mine.MyInfoPOJO;
import com.mintcode.area_patient.entity.MyInfo;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;
import com.mintcode.util.Keys;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 社区糖友圈 - 个人空间页
 *
 * @author yangxiaolong
 */
public class NewSocialMainActivity extends BaseActivity implements RequestManager.RequestListener<TopicListResult> {

    LayoutInflater mLayoutInflater;

    Buddy myBuddy;

    //    @BindView(R2.id.new_personal_tablayout)
//    TabLayout tabLayout;
    @BindView(R2.id.new_personal_viewPager)
    ViewPager viewPager;
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.toolbarTitle)
    TextView toolbarTitle;
    //    @BindView(R2.id.my_avatar)
//    CircleImageView myAvatar;
//    @BindView(R2.id.avatarArea)
//    LinearLayout avatarArea;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R2.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R2.id.main_content)
    CoordinatorLayout mainContent;

    FragmentPagerAdapter pagerAdapter;
    @BindView(R2.id.new_banner)
    AutoScrollViewPager banner;
    @BindView(R2.id.new_dot)
    LinearLayout newDotLayout;
    @BindView(R2.id.rel_pager)
    RelativeLayout bannerGroup;
    @BindView(R2.id.recommend_tv)
    TextView recommendTv;
    @BindView(R2.id.latest_tv)
    TextView latestTv;
    @BindView(R2.id.topic_tv)
    TextView topicTv;
    @BindView(R2.id.topic_rl)
    View topicRL;
    @BindView(R2.id.fab)
    FloatingActionButton fab;

    ListPopupWindow popupMenu = null;

    List<Topic> topicList = new ArrayList<>();
    List<String> topicStrList = new ArrayList<>();

    @Override
    public void processResult(int requestCode, int resultCode, TopicListResult data) {
        if (data == null || data.getTopicList() == null) return;
        topicList = data.getTopicList();
        for (Topic topic : topicList) {
            topicStrList.add("#" + topic.getName());
        }
    }

    static class GetUserInfoResquestListener implements RequestManager.RequestListener<GetUserInfoResult> {
        private WeakReference<NewSocialMainActivity> activityWR;

        public GetUserInfoResquestListener(NewSocialMainActivity activity) {
            activityWR = new WeakReference<NewSocialMainActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (activityWR == null || activityWR.get() == null || data == null || data.getBuddy() == null) {
                return;
            }
            NewSocialMainActivity activity = activityWR.get();
            activity.myBuddy = data.getBuddy();

            // 我的那边信息是每次resume中获取到本地缓存的图片地址。我在这边存储上传的头像。
            String avatarUrl = activity.myBuddy.getImgUrl();
//            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + avatarUrl, activity.getContext(), activity.myAvatar, ImageManager.avatarOptions);
            MyInfoUtilProxy infoUtil = ProxyClassFactory.getProxyClass(AppImpl.getAppRroxy().getMyInfoProxyClazz());
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

    @OnClick(R2.id.back)
    void back(View view) {
        finish();
    }

    @OnClick(R2.id.fab)
    void fabOnClick(View view) {
        startActivity(new Intent(this, NewPublishDynamicActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_new_social_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        myBuddy = CommonInfoManager.getInstance().getUserInfo(this);
        if (myBuddy == null) {
            finish();
            return;
        } else {
            initMyBuddyHeaderView();
        }
        mLayoutInflater = LayoutInflater.from(this);

        setViewPager();
        MedicalVolleyListenerImpl impl = new MedicalVolleyListenerImpl();
//        MedicalApiManager.getInstance().getSocialBanner(impl);
        AppImpl.getAppRroxy().getSocialBanner(impl);
        KeyValueDBService mValueDBService = KeyValueDBService.getInstance();
        token = mValueDBService.findValue(Keys.TOKEN);
        uid = mValueDBService.findValue(Keys.UID);
        if (uid != null && !"-1000".equals(uid)) {
            ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE,
                    new GetUserInfoResquestListener(this), 1, getApplicationContext(), null);
        }
        ApiManager.listTopic(this, context, null, 100, 2);

        AppImpl.getAppRroxy().addLog(this, "page-forum-home-trump");


    }

    private void setViewPager() {
        pagerAdapter = new MySpacePagerAdapter(getSupportFragmentManager(), new String[]{"我的动态", "糖友动态", "糖友动态"});
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        recommendTv.setSelected(true);
        recommendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendTv.setSelected(true);
                latestTv.setSelected(false);
                topicTv.setSelected(false);
                viewPager.setCurrentItem(0);
                AppImpl.getAppRroxy().addLog(NewSocialMainActivity.this, "event-topic-short-trump");
            }
        });
        latestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendTv.setSelected(false);
                latestTv.setSelected(true);
                topicTv.setSelected(false);
                viewPager.setCurrentItem(1);
                AppImpl.getAppRroxy().addLog(NewSocialMainActivity.this, "event-topic-new-trump");
            }
        });
        topicRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topicList == null) {
                    ApiManager.listTopic(NewSocialMainActivity.this, context, null, 100, 2);
                    return;
                }
                popupMenu = new ListPopupWindow(NewSocialMainActivity.this);
                popupMenu.setAdapter(new ArrayAdapter<String>(NewSocialMainActivity.this, R.layout.item_topic, R.id.topic_tv, topicStrList));
                popupMenu.setAnchorView(topicRL);
                popupMenu.setModal(true);
                popupMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        String s = topicStrList.get(position);
                        if (newTopicDynamicFragment != null) {
                            AppImpl.getAppRroxy().addLog(NewSocialMainActivity.this, "event-topic-theme-trump-" + topicList.get(position).getId());
                            newTopicDynamicFragment.setTopic(topicList.get(position));
                            newTopicDynamicFragment.onRefresh();
                        }
                        recommendTv.setSelected(false);
                        latestTv.setSelected(false);
                        topicTv.setSelected(true);
                        viewPager.setCurrentItem(2);
                        popupMenu.dismiss();
                    }
                });
                popupMenu.show();
            }
        });
    }

    private List<View> bannerViewList = new ArrayList<View>();
    private List<HomeBannerData> bannerList = new ArrayList<HomeBannerData>();
    private int preDotPosition = 0;  //上一个被选中的小圆点的索引，默认值为0
    private BannerViewPagerAdapter bannerViewPagerAdapter;

    class BannerViewPagerAdapter extends PagerAdapter {

        private List<View> list = new ArrayList<View>();

        public void addList(List<View> list) {
            if (null != list && !list.isEmpty()) {
                this.list.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void cleanList() {
            if (null != list) {
                this.list.clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    NewTopicDynamicFragment newTopicDynamicFragment;

    public class MySpacePagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[];
        private final int PAGE_COUNT = 3;

        public MySpacePagerAdapter(FragmentManager fm, String[] arg) {
            super(fm);
            tabTitles = arg;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return NewRecommendDynamicFragment.newInstance();
            else if (position == 1)
                return NewLatestDynamicFragment.newInstance();
            else if (position == 2) {
                if (!topicList.isEmpty())
                    newTopicDynamicFragment = NewTopicDynamicFragment.newInstance(topicList.get(0));
                else
                    newTopicDynamicFragment = NewTopicDynamicFragment.newInstance(null);
                return newTopicDynamicFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }


    private void setBannerFromNet(List<HomeBannerData> bannerList) {
        if (ListUtil.isListEmpty(bannerList)) return;
        banner.removeAllViews();
        bannerViewList.clear();
        preDotPosition = 0;
        LayoutInflater inflater = LayoutInflater.from(context);
        int size = bannerList.size();
        for (int i = 0; i < size; i++) {
            ImageView bannerImg = (ImageView) inflater.inflate(R.layout.common_banner_image, null);
            bannerImg.setTag(i);
            final HomeBannerData bean = bannerList.get(i);
            if (bean == null) continue;
            if (!AppImpl.getAppRroxy().getIsHideMall()) {
                if (!TextUtils.isEmpty(bean.getImagePath())) {
                    ImageManager.loadImageByDefaultImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + bean.getImagePath(),
                            context, bannerImg, R.drawable.default_banner_img);
                } else {
                    bannerImg.setImageResource(R.drawable.default_banner_img);
                }
            } else {
                try {
                    int resId = Integer.valueOf(bean.getImagePath());
                    bannerImg.setImageResource(resId);
                } catch (Exception ex) {
                }

            }
            final int finalI = i;
            bannerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeBannerData.RedirectEntity redirect = bean.getRedirect();
                    if (redirect == null) return;
                    String type = redirect.getType();
                    String url = redirect.getUrl();
                    if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(url)) {
                        AppImpl.getAppRroxy().addLog(NewSocialMainActivity.this, "event-banner-" + url, "event-forum-banner-trump-" + url + "-" + finalI);
                        if ("WEB_PAGE".equals(type)) {//跳转到webview页面
                            try {
                                Intent intent = new Intent(NewSocialMainActivity.this, Class.forName("com.mintcode.area_patient.area_home.BannerActivity") );
                                intent.putExtra("pageToUrl", url);
                                startActivity(intent);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else if ("NATIVE".equals(type)) {//跳转到原生界面
                           AppImpl.getAppRroxy().startIntent(url, NewSocialMainActivity.this, null);
                        }
                    }
                }
            });
            bannerViewList.add(bannerImg);
        }
        initDotView(newDotLayout, bannerViewList);
        banner.setOffscreenPageLimit(3);
        banner.setInterval(4000);
        banner.startAutoScroll();
        bannerGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return banner.dispatchTouchEvent(event);
            }
        });

        bannerViewPagerAdapter = new BannerViewPagerAdapter();

        bannerViewPagerAdapter.addList(bannerViewList);
        banner.setAdapter(bannerViewPagerAdapter);
        newDotLayout.getChildAt(0).setEnabled(true);
        banner.setOnPageChangeListener(new BannerViewPagerChangeListener());
    }

    class MedicalVolleyListenerImpl implements MedicalVolleyListener {

        public MedicalVolleyListenerImpl() {
        }

        @Override
        public void successResult(String result, String url) {
            hideLoadDialog();
            if (AppImpl.getAppRroxy().getSOCIAL_BANNER_PATH().equals(url)) {//头部banner
                bannerList = GSON.fromJson(result, new TypeToken<ArrayList<HomeBannerData>>() {
                }.getType());
                setBannerFromNet(bannerList);
            }
        }


        @Override
        public void errorResult(String result, String url) {
            hideLoadDialog();
        }
    }

    /**
     * 头部banner的viewpager监听
     */
    class BannerViewPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int page, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int page) {
            int newPositon = page % bannerViewList.size();
            newDotLayout.getChildAt(preDotPosition).setEnabled(false);
            newDotLayout.getChildAt(newPositon).setEnabled(true);
            preDotPosition = newPositon;
        }
    }

    /**
     * 初始化Dot
     *
     * @param middleDot
     * @param list
     */
    private void initDotView(LinearLayout middleDot, List<View> list) {
        View midPoint = null;
        LinearLayout.LayoutParams params = null;
        middleDot.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            midPoint = new View(this);
            midPoint.setBackgroundResource(R.drawable.dot_bg_selector);
            params = new LinearLayout.LayoutParams(15, 15);
            params.leftMargin = 15;
            midPoint.setEnabled(false);
            midPoint.setLayoutParams(params);
            middleDot.addView(midPoint); // 向线性布局中添加"点"
        }
    }

    private void initMyBuddyHeaderView() {
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(ChangeUserInfoEvent event) {
        ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, new GetUserInfoResquestListener(this), 1, this, null);
    }

    public void onEventMainThread(ChangSocialMessageEvent event) {
    }

    // LoginHelper在登出的时候发出一个通知用来更新社区用户头像
//    public void onEventMainThread(ShopLoginStatus event) {
//        if(event.getStatus() != ShopLoginStatus.LoginFail)
//            myAvatar.setImageResource(R.drawable.social_new_avatar);
//    }
//
//    public void onEventMainThread(Buddy buddy) {
//        if (buddy != null) {
//            myBuddy = buddy;
//            if (myAvatar != null)
//                ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + myBuddy.getImgUrl(), getContext(), myAvatar, ImageManager.avatarOptions);
//        }
//    }
}
