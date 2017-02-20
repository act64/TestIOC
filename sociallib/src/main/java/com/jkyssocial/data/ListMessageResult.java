package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.util.List;

/**
 * Created by yangxiaolong on 15/8/27.
 */
public class ListMessageResult extends NetWorkResult{
    private List<Message> msgList;

    public List<Message> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<Message> msgList) {
        this.msgList = msgList;
    }
}
