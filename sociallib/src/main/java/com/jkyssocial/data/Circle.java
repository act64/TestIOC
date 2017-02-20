package com.jkyssocial.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangxiaolong on 15/8/31.
 */
public class Circle implements Serializable {

    /**
     * 新加的字段
     */
    // 是否是圈主的字段
    private String ownerId ;

    private String ownerName ;

    // 我是否加入该圈子
    private int hasMe ;

    // 当前圈子的状态:
    private int status ;

    // 当前圈子的统计信息
    private CircleStat stat ;

    private String id;

    private int type;

    private String title;

    private String classCode;

    private String className;

    private String summary;

    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private List<Buddy> buddyList;

    private Long createdTime;

    private Long modifiedTime;

    private List<Dynamic> dynamicList;

    private String avatar;

    public void setHasMe(int hasMe) {
        this.hasMe = hasMe;
    }

    public int getHasMe() {
        return hasMe;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static String getErrorMessage(Circle circle){
        String error = null;
        if(circle == null)
            return error;
        else if(circle.getStatus() == 1)
            error = "圈子正在审核中";
        else if(circle.getStatus() == 2)
            error = "圈子已经被解散";
        else if(circle.getStatus() == 4)
            error = "圈子已经被解散";
        return error;
    }

    public Circle(){}

    public Circle(String title, String summary, String avatar, List<Dynamic> dynamicList, Long createdTime) {
        this.title = title;
        this.summary = summary;
        this.avatar = avatar;
        this.dynamicList = dynamicList;
        this.createdTime = createdTime;
    }

    public List<Dynamic> getDynamicList() {
        return dynamicList;
    }

    public void setDynamicList(List<Dynamic> dynamicList) {
        this.dynamicList = dynamicList;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Buddy> getBuddyList() {
        return buddyList;
    }

    public void setBuddyList(List<Buddy> buddyList) {
        this.buddyList = buddyList;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public CircleStat getStat() {
        return stat;
    }

    public void setStat(CircleStat stat) {
        this.stat = stat;
    }
}
