package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

/**
 * Created by yangxiaolong on 15/8/31.
 */
public class GetUserInfoResult extends NetWorkResult {
    private Buddy buddy;

    public Buddy getBuddy() {
        return buddy;
    }

    public void setBuddy(Buddy buddy) {
        this.buddy = buddy;
    }
}
