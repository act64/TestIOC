package com.jkyssocial.pageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jkyssocial.Fragment.MyDynamicFragment;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/6/8
 * Time: 15:19
 * Email:AndroidZern@163.com
 */
public class MySpacePagerOtherBuddyAdapter extends FragmentPagerAdapter {

    public MySpacePagerOtherBuddyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MyDynamicFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
