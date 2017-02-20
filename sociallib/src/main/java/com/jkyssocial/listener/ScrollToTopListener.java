package com.jkyssocial.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/*
* Created by Rutul on 02-03-2015.
* This class is a ScrollListener for RecyclerView that allows to show/hide
* views when list is scrolled.
* It assumes that you have added a header to your list.
* */
public abstract class ScrollToTopListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 50;

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;
    int lastVisiblePosition, type;

    private RecyclerView.LayoutManager mLayoutManager;

    public ScrollToTopListener(RecyclerView.LayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        if(layoutManager instanceof LinearLayoutManager)
            type = 0;
        else if(layoutManager instanceof StaggeredGridLayoutManager)
            type = 1;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
//        if(type == 0) {
//            lastVisiblePosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
//        }else if(type == 1){
//            int[] lastVisiblePositions = null;
//            lastVisiblePositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(lastVisiblePositions);
//            if(lastVisiblePositions != null && lastVisiblePositions.length > 0) {
//                lastVisiblePosition = lastVisiblePositions[0];
//            }
//        }
//
//        if(lastVisiblePosition > 5){
//            onShow();
//        }else{
//            onHide();
//        }
        mScrolledDistance += dy;
        if(mScrolledDistance > 1500){
            onShow();
        }else{
            onHide();
        }
    }

    public abstract void onHide();
    public abstract void onShow();
}