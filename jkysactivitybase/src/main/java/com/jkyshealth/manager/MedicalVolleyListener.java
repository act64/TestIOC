package com.jkyshealth.manager;


/**
 * 网络请求回调接口
 * Created by tom on 15/10/15.
 */
public interface MedicalVolleyListener {

    public void successResult(String result, String url);

    public void errorResult(String result, String url);

}
