package com.jkyssocial.data;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/17.
 */
public class Arg implements Serializable{
    private String dynamicId;
    private String commentId;

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    private String replyId;
}
