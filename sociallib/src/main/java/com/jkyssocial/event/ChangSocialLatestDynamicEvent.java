package com.jkyssocial.event;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/21.
 */
public class ChangSocialLatestDynamicEvent implements Serializable{

    private int num;

    public ChangSocialLatestDynamicEvent(int num) {
        this.num = num;
    }
    public int getNum() {
        return num;
    }
}
