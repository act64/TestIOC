package com.jkyssocial.event;

import com.mintcode.base.RecentActData;

/**
 * Created by on
 * Author: xiaoke
 * DATE: 2016/6/22
 * Time: 18:43
 * email: fmqin@91jkys.com
 */
public class RecentActEvent {
    RecentActData recentActData ;
    public RecentActEvent(RecentActData recentActData){
        this.recentActData = recentActData;
    }

    public RecentActData getRecentData(){
        return recentActData;
    }
}
