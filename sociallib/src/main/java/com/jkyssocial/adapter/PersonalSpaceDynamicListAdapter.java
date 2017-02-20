package com.jkyssocial.adapter;

import android.app.Activity;
import android.view.View;

import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.listener.ListUIListener;

/**
 * Created by yangxiaolong on 15/9/2.
 */
public class PersonalSpaceDynamicListAdapter extends NewDynamicListAdapter {

    Buddy buddy;

    public PersonalSpaceDynamicListAdapter(Activity context, ListUIListener listUIListener, Buddy buddy) {
        super(context, listUIListener);
        this.buddy = buddy;
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

    public void create() {
//        Log.e("内存", "监听=" + this);
        ApiManager.listDynamicForUser(this, CREATE_CODE, context, buddy.getBuddyId(), null, PAGING_COUNT);
    }

    public void loadmore() {
        ApiManager.listDynamicForUser(this, LOAD_MORE_CODE, context, buddy.getBuddyId(), dynamicList.get(dynamicList.size() - 1).getCreatedTime(), PAGING_COUNT);
    }

    public void refresh() {
        ApiManager.listDynamicForUser(this, REFRESH_CODE, context, buddy.getBuddyId(), null, PAGING_COUNT);
    }

}