package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.util.List;

/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/18
 * Time: 15:51
 * email: AndroidZern@163.com
 */
public class ListCircleForUserResult extends NetWorkResult{
    private List<Circle> circleList ;

    public List<Circle> getCircleList() {
        return circleList;
    }

    public void setCircleList(List<Circle> circleList) {
        this.circleList = circleList;
    }
}
