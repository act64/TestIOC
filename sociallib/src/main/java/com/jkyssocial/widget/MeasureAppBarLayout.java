package com.jkyssocial.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

/**
 * Created by on
 * Author: xiaoke
 * DATE: 2016/6/4
 * Time: 13:47
 * email: fmqin@91jkys.com
 */
public class MeasureAppBarLayout extends AppBarLayout{
    public MeasureAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2
                , MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
