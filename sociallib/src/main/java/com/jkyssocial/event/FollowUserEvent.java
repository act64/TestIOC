package com.jkyssocial.event;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/21.
 * 关注或取消关注用户成功事件
 */
public class FollowUserEvent implements Serializable{

    public String buddyId;

    public int follow;

    public FollowUserEvent(String buddyId, int follow) {
        this.buddyId = buddyId;
        this.follow = follow;
    }
}
