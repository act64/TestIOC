package com.jkyssocial.event;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/21.
 * 加入或退出圈子成功事件
 */
public class FollowCircleEvent implements Serializable{

    public String circleId;

    public int follow;

    public FollowCircleEvent(String circleId, int follow) {
        this.circleId = circleId;
        this.follow = follow;
    }
}
