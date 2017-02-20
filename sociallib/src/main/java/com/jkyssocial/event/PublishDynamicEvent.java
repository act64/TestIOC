package com.jkyssocial.event;

import com.jkyssocial.data.Dynamic;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/21.
 */
public class PublishDynamicEvent implements Serializable{

    private Dynamic dynamic;

    private Dynamic sendingDynamic;

    public PublishDynamicEvent(Dynamic dynamic, Dynamic sendingDynamic) {
        this.dynamic = dynamic;
        this.sendingDynamic = sendingDynamic;
    }
    public Dynamic getDynamic(){
        return dynamic;
    }

    public Dynamic getSendingDynamic() {
        return sendingDynamic;
    }
}
