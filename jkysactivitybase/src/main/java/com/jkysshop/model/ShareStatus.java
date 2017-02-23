package com.jkysshop.model;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/7/14
 * Time: 09:48
 * Email:AndroidZern@163.com
 */
public class ShareStatus {
    private int status;
    private String detailMsg;

    // 10000 表示分享成功， 10001 表示分享失败， 10002 表示分享取消
    public static final int ShareSuccess = 10000;
    public static final int ShareFail = 10001;
    public static final int ShareCancel = 10002;

    public ShareStatus(int status, String detailMsg) {
        this.status = status;
        this.detailMsg = detailMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetailMsg() {
        return detailMsg;
    }

    public void setDetailMsg(String detailMsg) {
        this.detailMsg = detailMsg;
    }
}
