package com.jkyssocial.data;

import java.util.List;

/**
 * Created by yangxiaolong on 15/8/27.
 */
public class Dynamic extends SocialMainHeadAndBodyData{
    private String dynamicId;

    private Buddy owner;

    private String content;

    private String title;

    private List<String> images;

    private int viewCount;

    private int likeCount;

    private List<Buddy> likerList;

    private int commentCount;

    private Circle circle;

    private Topic topic;

    private long createdTime;

    private long modifiedTime;

    private int likeFlag;

    private long recTime;

    private int recForCircleLevel;

    private List<Comment> commentList;

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public int getRecForCircleLevel() {
        return recForCircleLevel;
    }

    public void setRecForCircleLevel(int recForCircleLevel) {
        this.recForCircleLevel = recForCircleLevel;
    }

    /*
        客户端自定义属性(0:普通；1:发送中 2:发送失败)
         */
    private int statusAndroid = 0;

    public int getStatusAndroid() {
        return statusAndroid;
    }

    public void setStatusAndroid(int statusAndroid) {
        this.statusAndroid = statusAndroid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public long getRecTime() {
        return recTime;
    }

    public void setRecTime(long recTime) {
        this.recTime = recTime;
    }

    public int getLikeFlag() {
        return likeFlag;
    }

    public void setLikeFlag(int likeFlag) {
        this.likeFlag = likeFlag;
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
    }

    public Buddy getOwner() {
        return owner;
    }

    public void setOwner(Buddy owner) {
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int incrLikeCount() { return ++likeCount;}

    public int decrLikeCount() { return --likeCount;}

    public int incrCommentCount() { return ++commentCount;}

    public int decrCommentCount() { return --commentCount;}

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<Buddy> getLikerList() {
        return likerList;
    }

    public void setLikerList(List<Buddy> likerList) {
        this.likerList = likerList;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
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
}
