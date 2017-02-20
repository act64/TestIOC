package com.jkyssocial.data;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/8/31.
 */
public class Buddy implements Serializable{
    protected int userId;
    protected String buddyId;
    protected byte userType;
    protected String imgUrl;
    protected String userName;
    protected String signature;
    protected byte vFlag;

    private byte diabetesType;
    private Long diabetesTime;
    private String diabetesTypeName;
    private long enrollTime;

    private int hospitalId;
    private String hospital;
    private String title;
    private String bgImgUrl;
    private int idolCount;
    private int fansCount;
    private int msgCount;
    private byte idolFlag;
    private long followTime;
    private long likeTime;
    private int certStatus;
    private int officialFlag;

    // 新加的字段用来判断用户创建的圈子数
    private int hasCircles ;

    private int circleCount ;

    //是否关注
    private boolean isCare = false ;

    public boolean isCare() {
        return isCare;
    }

    public void setIsCare(boolean isCare) {
        this.isCare = isCare;
    }

    public int getCircleCount() {
        return circleCount;
    }

    public void setCircleCount(int circleCount) {
        this.circleCount = circleCount;
    }

    public int getCertStatus() {
        return certStatus;
    }

    public void setCertStatus(int certStatus) {
        this.certStatus = certStatus;
    }

    public int getOfficialFlag() {
        return officialFlag;
    }

    public void setOfficialFlag(int officialFlag) {
        this.officialFlag = officialFlag;
    }

    public long getLikeTime() {
        return likeTime;
    }

    public void setLikeTime(long likeTime) {
        this.likeTime = likeTime;
    }

    public String getDiabetesTypeName() {
        return diabetesTypeName;
    }

    public void setDiabetesTypeName(String diabetesTypeName) {
        this.diabetesTypeName = diabetesTypeName;
    }

    public int getIdolCount() {
        return idolCount;
    }

    public void setIdolCount(int idolCount) {
        this.idolCount = idolCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public byte getIdolFlag() {
        return idolFlag;
    }

    public void setIdolFlag(byte idolFlag) {
        this.idolFlag = idolFlag;
    }

    public long getFollowTime() {
        return followTime;
    }

    public void setFollowTime(long followTime) {
        this.followTime = followTime;
    }

    public String getBgImgUrl() {
        return bgImgUrl;
    }

    public void setBgImgUrl(String bgImgUrl) {
        this.bgImgUrl = bgImgUrl;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte getDiabetesType() {
        return diabetesType;
    }

    public void setDiabetesType(byte diabetesType) {
        this.diabetesType = diabetesType;
    }

    public Long getDiabetesTime() {
        return diabetesTime;
    }

    public void setDiabetesTime(Long diabetesTime) {
        this.diabetesTime = diabetesTime;
    }

    public long getEnrollTime() {
        return enrollTime;
    }

    public void setEnrollTime(long enrollTime) {
        this.enrollTime = enrollTime;
    }

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public byte getvFlag() {
        return vFlag;
    }

    public void setvFlag(byte vFlag) {
        this.vFlag = vFlag;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte getUserType() {
        return userType;
    }

    public void setUserType(byte userType) {
        this.userType = userType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getHasCircles() {
        return hasCircles;
    }

    public void setHasCircles(int hasCircles) {
        this.hasCircles = hasCircles;
    }
}
