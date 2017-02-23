package com.jkyssocial.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jkys.common.widget.CustomToolbar;
import com.jkys.proxy.AppImpl;
import com.jkys.sociallib.R;
import com.jkys.sociallib.R2;
import com.jkyssocial.Fragment.AllCircleTypeFragment;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.util.ZernToast;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.CircleClass;
import com.jkyssocial.data.CircleClassListResult;
import com.mintcode.base.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class AllCircleActivity extends BaseActivity implements View.OnClickListener {
    private TabLayout tab;
    private ViewPager viewPager;
    private List<CircleClass> titles;
    //    private List<AllCircleTypeFragment> fragments;
    private CommonFragmentPagerAdapter adapter;
//    private TextView activity_all_circle_back, activity_all_circle_buildcircle;

    @BindView(R2.id.toolbar)
    CustomToolbar toolbar;

    /**
     * 圈子类别列表监听
     */
    static class CircleClassListRequestListener implements RequestManager.RequestListener<CircleClassListResult> {
        private WeakReference<AllCircleActivity> activityWR;

        public CircleClassListRequestListener(AllCircleActivity activity) {
            activityWR = new WeakReference(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, CircleClassListResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            AllCircleActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    if (data.getCircleClassList() != null && !data.getCircleClassList().isEmpty()) {
                        if (requestCode == activity.requestCode) {
                            // 清空本地tab栏的缓存数据
                            activity.titles.clear();
                            activity.titles.addAll(data.getCircleClassList());
                            // 网络端返回,更新本地的tab栏数据
                            activity.adapter = new CommonFragmentPagerAdapter(activity.getSupportFragmentManager(), activity.titles);
                            // 根据数量来动态的判断 tab栏的模式
                            if (activity.titles.size() >= 5) {
                                activity.tab.setTabMode(TabLayout.MODE_SCROLLABLE);
                            } else if (activity.titles.size() < 5 && activity.titles.size() > 0) {
                                activity.tab.setTabMode(TabLayout.MODE_FIXED);
                            }
                            activity.viewPager.setAdapter(activity.adapter);
                            activity.tab.setupWithViewPager(activity.viewPager);
//                        viewPager.setOffscreenPageLimit(4);
                            activity.viewPager.setCurrentItem(0);
//                        tab.setTabsFromPagerAdapter(adapter);
                        }
                    }
                } else {
                    Toast.makeText(activity.getApplicationContext(), "亲,检查下有没有网络连接啊!", Toast.LENGTH_LONG).show();
                }

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_circle);
         ButterKnife.bind(this);
        if (CommonInfoManager.showGuidance(this, getClass().getName())) {
            ViewStub guidance = (ViewStub) findViewById(R.id.guidance);
            final RelativeLayout guidanceLayout = (RelativeLayout) guidance.inflate();
            final ImageView imageView = (ImageView) guidanceLayout.findViewById(R.id.image_shower);
            final FancyButton fancyButton = (FancyButton) guidanceLayout.findViewById(R.id.i_know);
            fancyButton.setVisibility(View.VISIBLE);
            imageView.setPadding(dp2px(35), dp2px(15), 0, 0);
            imageView.setImageResource(R.drawable.social_all_circle_guidance_create);
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
        initView();
        initData();
        initEvent();
        AppImpl.getAppRroxy().addLog(this, "page-forum-circle-list");
    }

    private void initEvent() {
//        activity_all_circle_back.setOnClickListener(this);
//        activity_all_circle_buildcircle.setOnClickListener(this);
    }

    private int requestCode = 1;

    // 填充数据的步骤
    private void initData() {
        titles = new LinkedList<>();
        ApiManager.listCircleClass(new CircleClassListRequestListener(this), requestCode, this);
    }

    private void initView() {
        // 获取用户建立的圈子个数:
        getUserInfoCircleNum();
        // 返回按钮
//        activity_all_circle_back = (TextView) findViewById(R.id.activity_all_circle_back);
        // 新建按钮
//        activity_all_circle_buildcircle = (TextView) findViewById(R.id.activity_all_circle_buildcircle);
        toolbar.setBackVisble(true, this);
        tab = (TabLayout) findViewById(R.id.activity_all_circle_tabLayout);
        tab.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFFFF"));
        tab.setTabTextColors(Color.parseColor("#333333"), Color.parseColor("#4991FD"));
        viewPager = (ViewPager) findViewById(R.id.avtivity_all_circle_content);
    }

    // 获取用户建立的圈子个数:
    private void getUserInfoCircleNum() {
        Buddy userInfo = CommonInfoManager.getInstance().getUserInfo(this);
        if (userInfo != null) {
            int hasCircles = userInfo.getHasCircles();
            if (hasCircles > 0) {
                circleNum = hasCircles;
            } else {
                circleNum = 0;
            }
        } else {
            circleNum = 0;
        }
    }

    // 用户建立的圈子的个数
    private int circleNum = 0;

    @OnClick(R2.id.left_rl)
    void onBackClick(View view) {
        ZernToast.cancelToast();
        finish();
    }

    @OnClick(R2.id.right_rl)
    void onRightRLClick(View view) {
        Intent intent = new Intent(this, BuildCircleActivity.class);
        if (circleNum < 3) {
            // 用户已经建立小于2个的时候不需要提示就跳转到新建界面
            startActivity(intent);
        } else if (circleNum >= 3) {
            Toast.makeText(this, "最多只能建3个圈子", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
//        int id = v.getId();
//        switch (id) {
//            case R2.id.left_rl:
//                ZernToast.cancelToast();
//                finish();
//                break;
//            case R2.id.activity_all_circle_buildcircle:
//                Intent intent = new Intent(this, BuildCircleActivity.class);
//                if (circleNum < 3) {
//                    // 用户已经建立小于2个的时候不需要提示就跳转到新建界面
//                    startActivity(intent);
//                } else if (circleNum >= 3) {
//                    Toast.makeText(this, "最多只能建3个圈子", Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
    }

    public static class CommonFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<CircleClass> datas;

        public CommonFragmentPagerAdapter(FragmentManager fm, List<CircleClass> datas) {
            super(fm);
            this.datas = datas;
        }

        @Override
        public Fragment getItem(int position) {
            return AllCircleTypeFragment.newInstance(datas.get(position).getCode(), datas.get(position).getName());
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return datas.get(position).getName();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ZernToast.cancelToast();
    }
}
