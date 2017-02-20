package com.jkyssocial.event;

/**
 * Created by xiaoke on 16/12/8.
 */

public class PublishFinishEvent {

    boolean isSuccess;
    public PublishFinishEvent(boolean isSuccess){
        this.isSuccess = isSuccess;
    }

    public boolean getIsSuccess(){
       return isSuccess;
    }

}
