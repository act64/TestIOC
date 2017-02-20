package com.jkyssocial.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.listener.ListUIListener;

import it.sephiroth.android.library.easing.Circ;

/**
 * Sugar
 * Created by yangxiaolong on 15/9/2.
 */
public class CircleMainDynamicListAdapter extends NewDynamicListAdapter{

    Circle circle;

    public CircleMainDynamicListAdapter(Activity context, ListUIListener listUIListener, Circle circle) {
        super(context, listUIListener);
        this.circle = circle;
    }

    @Override
    public void onBindViewHolder(DynamicTextViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.comeVG.setVisibility(View.GONE);
        Dynamic dynamic = dynamicList.get(position);
        if(dynamic.getRecForCircleLevel() > 0){
            holder.circleRecommendTag.setVisibility(View.VISIBLE);
        }else {
            holder.circleRecommendTag.setVisibility(View.GONE);
        }
        if(dynamic.getOwner() != null && dynamic.getOwner().getBuddyId() != null && dynamic.getOwner().getBuddyId().equals(circle.getOwnerId())){
            holder.circleOwnerTag.setVisibility(View.VISIBLE);
        }else {
            holder.circleOwnerTag.setVisibility(View.GONE);
        }
        if(position == 0){
            holder.headerLine.setVisibility(View.GONE);
            holder.headerPad.setVisibility(View.GONE);
        }else{
            holder.headerLine.setVisibility(View.VISIBLE);
            holder.headerPad.setVisibility(View.VISIBLE);
        }
    }

    public void create(){
        ApiManager.listDynamicForCircle(this, CREATE_CODE, context, circle.getId(), null, PAGING_COUNT);
    }

    public void loadmore(){
        ApiManager.listDynamicForCircle(this, LOAD_MORE_CODE, context, circle.getId(), baseLine, PAGING_COUNT);
    }

    public void refresh(){
        ApiManager.listDynamicForCircle(this, REFRESH_CODE, context, circle.getId(), null, PAGING_COUNT);
    }

}