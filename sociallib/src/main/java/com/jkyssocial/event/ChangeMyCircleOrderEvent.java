package com.jkyssocial.event;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yangxiaolong on 15/9/21.
 * 修改用户信息成功事件
 */
public class ChangeMyCircleOrderEvent implements Serializable{

    public List<String> circleIdList;

    public ChangeMyCircleOrderEvent(LinkedList<String> list) {
        circleIdList = list;
    }
}
