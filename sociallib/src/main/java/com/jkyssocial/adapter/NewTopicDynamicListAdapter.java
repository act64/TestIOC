package com.jkyssocial.adapter;

import android.app.Activity;
import android.view.View;

import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.Topic;
import com.jkyssocial.listener.ListUIListener;

/**
 * Sugar
 * Created by yangxiaolong on 15/9/2.
 */
public class NewTopicDynamicListAdapter extends NewDynamicListAdapter{

    Topic topic;

    public NewTopicDynamicListAdapter(Activity context, ListUIListener listUIListener, Topic topic) {
        super(context, listUIListener, topic == null ? null : "theme-" + topic.getId()+ "-");
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public void onBindViewHolder(DynamicTextViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.comeVG.setVisibility(View.GONE);
        holder.renqiTV.setVisibility(View.GONE);
        Dynamic dynamic = dynamicList.get(position);
        holder.topicTV.setText(dynamic.getTopic() == null ? "" : "# " + dynamic.getTopic().getName());
    }

    public void create(){
        if(topic == null)
            return;
        ApiManager.listDynamicForTopic(this, CREATE_CODE, context, topic.getId(), null, PAGING_COUNT);
    }

    public void loadmore(){
        if(topic == null)
            return;
        ApiManager.listDynamicForTopic(this, LOAD_MORE_CODE, context, topic.getId(), baseLine, PAGING_COUNT);
    }

    public void refresh(){
        if(topic == null)
            return;
        ApiManager.listDynamicForTopic(this, REFRESH_CODE, context, topic.getId(), null, PAGING_COUNT);
    }

}