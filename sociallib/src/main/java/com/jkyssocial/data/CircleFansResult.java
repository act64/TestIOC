package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.util.List;

/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/16
 * Time: 18:18
 * email: AndroidZern@163.com
 */
public class CircleFansResult extends NetWorkResult{
    private String baseLine ;
    private List<Buddy> buddyList ;

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
