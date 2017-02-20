package com.jkyssocial.event;

import com.jkyssocial.data.Dynamic;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/21.
 */
public class ChangSocialMessageEvent implements Serializable{

    private int num;

    public ChangSocialMessageEvent(int num) {
        this.num = num;
    }
    public int getNum() {
        return num;
    }
}
