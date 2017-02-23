package com.jkys.proxy;

import com.mintcode.area_patient.area_mine.MyInfoPOJO;

/**
 * Created by ylei on 2017/2/23.
 */

public abstract class MyInfoUtilProxy {
    public abstract MyInfoPOJO getMyInfo();
   public abstract void saveMyInfo(MyInfoPOJO infoPOJO);
}
