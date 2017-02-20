package com.jkyssocial.event;

import com.jkyssocial.data.Dynamic;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/10/09.
 */
public class PrePublishDynamicEvent implements Serializable{

    private Dynamic dynamic;

    public PrePublishDynamicEvent(Dynamic dynamic) {
        this.dynamic = dynamic;
    }

    public Dynamic getDynamic(){
        return dynamic;
    }

}
