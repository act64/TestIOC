package com.jkyssocial.data;

import java.io.Serializable;

/**
 * Created by yangxiaolong on 15/9/15.
 */
public class Message implements Serializable{

    private String msgId;

    private int type;

    private Buddy creator;

    private String content;

    private Dynamic dynamic;

    private Arg arg;

    private Long createdTime;

    private Long modifiedTime;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Buddy getCreator() {
        return creator;
    }

    public void setCreator(Buddy creator) {
        this.creator = creator;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Dynamic getDynamic() {
        return dynamic;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }

    public Arg getArg() {
        return arg;
    }

    public void setArg(Arg arg) {
        this.arg = arg;
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
}
