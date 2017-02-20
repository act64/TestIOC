package com.jkyssocial.data;

/**
 * Created by on
 * Author: Zern
 * DATE:2015/12/4
 * Time: 11:17
 * email:AndroidZern@163.com
 */
public class ZSTY {
    private String name ;
    private String type ;
    private boolean care = true ;
    private String illage ;

    public String getIllage() {
        return illage;
    }

    public void setIllage(String illage) {
        this.illage = illage;
    }

    public ZSTY(String name, String type, String illage, boolean care ) {

        this.name = name;
        this.type = type;
        this.care = care;
        this.illage = illage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public boolean isCare() {
        return care;
    }

    public void setCare(boolean care) {
        this.care = care;
    }


}
