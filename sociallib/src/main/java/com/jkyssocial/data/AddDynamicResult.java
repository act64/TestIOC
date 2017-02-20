package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

/**
 * Created by yangxiaolong on 15/8/31.
 */
public class AddDynamicResult extends NetWorkResult {
    private Dynamic dynamic;

    public Dynamic getDynamic() {
        return dynamic;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }
}
