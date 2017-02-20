package com.jkyssocial.data;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/17.
 */
public class UnreadMsg implements Serializable{
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
