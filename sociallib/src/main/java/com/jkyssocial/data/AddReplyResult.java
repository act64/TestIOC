package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

/**
 * Created by yangxiaolong on 15/8/31.
 */
public class AddReplyResult extends NetWorkResult {

    private Reply reply;

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }
}
