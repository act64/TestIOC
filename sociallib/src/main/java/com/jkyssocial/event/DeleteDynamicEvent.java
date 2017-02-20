package com.jkyssocial.event;

import com.jkyssocial.data.Dynamic;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/10/09.
 */
public class DeleteDynamicEvent implements Serializable{

    private Dynamic dynamic;

    public DeleteDynamicEvent(Dynamic dynamic) {
        this.dynamic = dynamic;
    }

    public Dynamic getDynamic(){
        return dynamic;
    }

}
