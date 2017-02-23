package com.jkyssocial.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jkyssocial.Fragment.AllCircleBaseFragment;
import com.jkyssocial.Fragment.ExpertDoctorFragment;
import com.jkyssocial.Fragment.SeniorSugarFriendFragment;
import com.mintcode.base.BaseActivity;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.R;

public class SugarControlStarActivity extends BaseActivity implements View.OnClickListener {
    private FragmentManager manager ;
    // 资深糖友Fragment
    private SeniorSugarFriendFragment seniorSugarFriendFragment ;
    // 专家医生Fragment
    private ExpertDoctorFragment expertDoctorFragment ;
    private Fragment currentFragment ;
    private String arr[] = new String[]{"资深糖友","专家医生"} ;
    private List<AllCircleBaseFragment> datas ;
    private ViewPager viewPager ;
    private SugarControlStarAdapter adapter ;
    private TabLayout tab ;
    public static final String PageIndex = "pageIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugar_control_star);
        ButterKnife.bind(this);
        // 数据的加载
        initData() ;
        // 初始化所有的视图g
        initView();
        // 所有事件的监听注册
        initEvent();

    }

   @OnClick(R2.id.left_rl)
    void back(View v){
        finish();
    }

    private void initData() {
        datas = new LinkedList<>() ;
        seniorSugarFriendFragment = SeniorSugarFriendFragment.newInstance() ;
        datas.add(seniorSugarFriendFragment) ;
        expertDoctorFragment = ExpertDoctorFragment.newInstance() ;
        datas.add(expertDoctorFragment) ;
        for (int i = 0; i < arr.length; i++) {
            datas.get(i).setTitle(arr[i]);
        }
        manager = getSupportFragmentManager() ;
        adapter = new SugarControlStarAdapter(manager,datas) ;
    }

    private void initEvent() {
    }

    private void initView() {
        //控件的初始化
        viewPager = (ViewPager) findViewById(R.id.activity_control_sugar_star_viewPager);
        tab = (TabLayout) findViewById(R.id.activity_control_sugar_star_tab);
        tab.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFFFF"));
        tab.setTabTextColors(Color.parseColor("#333333"), Color.parseColor("#4991FD"));
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager) ;
        int pageIndex = getIntent().getIntExtra(PageIndex, 0);
        viewPager.setCurrentItem(pageIndex);
    }

    @Override
    public void onClick(View v) {
    }

    public class SugarControlStarAdapter extends FragmentStatePagerAdapter {

        private List<AllCircleBaseFragment> fragments ;

        /**
         *
         * @param fm
         * @param fragments
         */
        public SugarControlStarAdapter(FragmentManager fm, List<AllCircleBaseFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            int ret = 0 ;
            if(fragments!=null) {
                ret = fragments.size();
            }
            return ret ;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            String ret = fragments.get(position).getFragmentTitle();
            return ret ;
        }
    }

}
