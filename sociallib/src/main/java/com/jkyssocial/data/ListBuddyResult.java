package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.util.List;

/**
 * Created by yangxiaolong on 15/8/31.
 */
public class ListBuddyResult extends NetWorkResult {

    private String baseLine;

    private List<Buddy> buddyList;

    public String getBaseLine() {
        return baseLine;
    }

    public void setBaseLine(String baseLine) {
        this.baseLine = baseLine;
    }

    public List<Buddy> getBuddyList() {
        return buddyList;
    }

    public void setBuddyList(List<Buddy> buddyList) {
        this.buddyList = buddyList;
    }
}
