package com.jkyssocial.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangxiaolong on 15/9/6.
 */
public class Comment implements Serializable {
//    private String id;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    private int status;
    private String commentId;
    private String content;
    private String dynamicId;
    private Buddy owner;
    private List<Reply> replyList;
    private long createdTime;
    private long modifiedTime;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
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

    public Buddy getOwner() {
        return owner;
    }

    public void setOwner(Buddy owner) {
        this.owner = owner;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;

    }
}
