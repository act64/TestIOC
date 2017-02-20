package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.util.List;

/**
 * Created by yangxiaolong on 15/8/27.
 */
public class TopicListResult extends NetWorkResult{

    private  String baseLine;
    private List<Topic> topicList;

    public String getBaseLine() {
        return baseLine;
    }

    public void setBaseLine(String baseLine) {
        this.baseLine = baseLine;
    }

    public List<Topic> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<Topic> topicList) {
        this.topicList = topicList;
    }
}
