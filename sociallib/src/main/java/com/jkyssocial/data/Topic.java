package com.jkyssocial.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangxiaolong on 15/8/31.
 */
public class Topic implements Serializable {

    /**
     * 新加的字段
     */
    private String id ;

    private String name ;

    // 当前圈子的统计信息
    private CircleStat stat ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CircleStat getStat() {
        return stat;
    }

    public void setStat(CircleStat stat) {
        this.stat = stat;
    }
}
