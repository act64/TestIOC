package com.jkys.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by jinzifu on 15/11/25.
 */
public class ListUtil {

    /**
     * 去除list中重复数据——乱序返回
     *
     * @param list
     */
    public static List<String> removeDuplicate(List<String> list) {
        HashSet<String> h = new HashSet<String>(list);
        List<String> mList = new ArrayList<String>();
        mList.addAll(h);
        return mList;
    }

    /**
     * list倒序
     * @param list
     * @return
     */
    public static List<String> replaceSort(List<String> list){
        List<String> list2 = new ArrayList<String>();
        if (isListEmpty(list))return list2;
        if (list.size() == 1)return list;
        for (int i = list.size()-1;i >= 0;i--){
            list2.add(list.get(i));
        }
        return list2;
    }

    /**
     * list是否为空
     */
    public static boolean isListEmpty(List<?> list) {
        if (list == null || list.size() == 0)
            return true;
        return false;
    }

}
