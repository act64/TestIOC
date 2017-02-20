package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.util.List;

/**
 * Created by yangxiaolong on 15/8/27.
 */
public class CircleListResult extends NetWorkResult{

    private  String baseLine;
    private List<Circle> circleList;

    public String getBaseLine() {
        return baseLine;
    }

    public void setBaseLine(String baseLine) {
        this.baseLine = baseLine;
    }

    public List<Circle> getCircleList() {
        return circleList;
    }

    public void setCircleList(List<Circle> circleList) {
        this.circleList = circleList;
    }
}
