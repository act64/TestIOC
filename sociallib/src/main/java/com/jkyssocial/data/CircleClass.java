package com.jkyssocial.data;

/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/15
 * Time: 13:23
 * email: AndroidZern@163.com
 */
public class CircleClass {
    private String code ;
    private String name ;

    public CircleClass(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CircleClass{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
