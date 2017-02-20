package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.util.List;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/11/28
 * Time: 10:07
 * Email:AndroidZern@163.com
 */

public class TrumpHomeSocialData extends NetWorkResult {
    private String baseLine;
    private List<Dynamic> dynamicList;
    private String tip;

    public String getBaseLine() {
        return baseLine;
    }

    public void setBaseLine(String baseLine) {
        this.baseLine = baseLine;
    }

    public List<Dynamic> getDynamicList() {
        return dynamicList;
    }

    public void setDynamicList(List<Dynamic> dynamicList) {
        this.dynamicList = dynamicList;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
