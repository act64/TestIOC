package com.jkyssocial.data;

import java.util.List;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/1/25
 * Time: 16:16
 * Email:AndroidZern@163.com
 */

public class SocialMainHeadData extends SocialMainHeadAndBodyData{
    private List<Circle> circleList;
    private List<Buddy> buddyList;

    public SocialMainHeadData(List<Circle> circleList, List<Buddy> buddyList) {
        this.circleList = circleList;
        this.buddyList = buddyList;
    }

    public List<Circle> getCircleList() {
        return circleList;
    }

    public void setCircleList(List<Circle> circleList) {
        this.circleList = circleList;
    }

    public List<Buddy> getBuddyList() {
        return buddyList;
    }

    public void setBuddyList(List<Buddy> buddyList) {
        this.buddyList = buddyList;
    }
}
