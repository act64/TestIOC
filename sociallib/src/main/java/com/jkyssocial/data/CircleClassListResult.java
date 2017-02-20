package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.util.List;

/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/15
 * Time: 13:29
 * email: AndroidZern@163.com
 */
public class CircleClassListResult extends NetWorkResult {
    private List<CircleClass> circleClassList ;

    public List<CircleClass> getCircleClassList() {
        return circleClassList;
    }

    public void setCircleClassList(List<CircleClass> circleClassList) {
        this.circleClassList = circleClassList;
    }
}
