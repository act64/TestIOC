package com.jkyssocial.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.DynamicListResult;
import com.jkyssocial.listener.ListUIListener;

/**
 * Sugar
 * Created by yangxiaolong on 15/9/2.
 */
public class SugarFriendDynamicListAdapter extends NewDynamicListAdapter{

    Buddy buddy;

    Long baseTime;

    public SugarFriendDynamicListAdapter(Activity context, ListUIListener listUIListener) {
        super(context, listUIListener);
    }

    @Override
    public void onBindViewHolder(DynamicTextViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(position == 0){
            holder.headerLine.setVisibility(View.GONE);
            holder.headerPad.setVisibility(View.GONE);
        }else{
            holder.headerLine.setVisibility(View.VISIBLE);
            holder.headerPad.setVisibility(View.VISIBLE);
        }
    }

    public void create(){
        ApiManager.listDynamicForNew(this, CREATE_CODE, context, null, PAGING_COUNT);
    }

    public void loadmore(){
        if(dynamicList.size() > 0) {
            baseTime = dynamicList.get(dynamicList.size()-1).getCreatedTime();
        }
        ApiManager.listDynamicForNew(this, LOAD_MORE_CODE, context, baseTime, PAGING_COUNT);
    }

    public void refresh(){
        ApiManager.listDynamicForNew(this, REFRESH_CODE, context, null, PAGING_COUNT);
    }

}