package com.jkyssocial.data;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/6.
 */
public class Reply implements Serializable {
    private String replyId;
    private String content;
    private String commentId;
    private Buddy owner;
    private Buddy targetBuddy;
    private long createdTime;
    private long modifiedTime;

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Buddy getOwner() {
        return owner;
    }

    public void setOwner(Buddy owner) {
        this.owner = owner;
    }

    public Buddy getTargetBuddy() {
        return targetBuddy;
    }

    public void setTargetBuddy(Buddy targetBuddy) {
        this.targetBuddy = targetBuddy;
    }
}
