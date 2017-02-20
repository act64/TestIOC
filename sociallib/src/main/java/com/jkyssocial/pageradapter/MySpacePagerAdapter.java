package com.jkyssocial.pageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jkyssocial.Fragment.MyDynamicFragment;
import com.jkyssocial.Fragment.SugarFriendDynamicFragment;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/6/3
 * Time: 17:35
 * Email:AndroidZern@163.com
 */
public class MySpacePagerAdapter extends FragmentPagerAdapter{

    private String tabTitles[];
    private final int PAGE_COUNT = 2;
    public MySpacePagerAdapter(FragmentManager fm, String[] arg) {
        super(fm);
        tabTitles = arg;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return MyDynamicFragment.newInstance();
        else if(position == 1)
            return SugarFriendDynamicFragment.newInstance();
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
