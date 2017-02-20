package com.jkyssocial.listener;

/**
 * Created by yangxiaolong on 15/11/9.
 */
public interface ListUIListener {

    public static final int NETWORK_NULL_LIST_CODE = -1;
    public static final int NULL_LIST_CODE = -2;
    public static final int SUCCESS_CODE = 0;

    void afterCreateUI(int resultCode);
    void afterRefreshUI(int resultCode);
    void afterLoadmoreUI(int resultCode);

}
