package com.jkyssocial.data;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/12/25.
 */
public class CircleStat implements Serializable {
    private int viewCount ;
    private int memberCount ;
    private int dynamicCount ;

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getDynamicCount() {
        return dynamicCount;
    }

    public void setDynamicCount(int dynamicCount) {
        this.dynamicCount = dynamicCount;
    }
}
