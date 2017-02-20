package com.jkyssocial.pageradapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jkyssocial.Fragment.HotRecommendFragment;

/**
 * Created by Administrator on 2015/7/30.
 */
public class SocialDynamicPagerAdapter extends FragmentPagerAdapter {

//    final int PAGE_COUNT = 2;
    final int PAGE_COUNT = 1;
//    private String tabTitles[] = new String[]{"热门推荐","糖友动态"};
//    private String tabTitles[] = new String[]{"推荐动态"};
    private Context context;

    public SocialDynamicPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return HotRecommendFragment.newInstance();
//        else if(position == 1)
//            return SugarFriendDynamicFragment.newInstance();
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabTitles[position];
//    }
}