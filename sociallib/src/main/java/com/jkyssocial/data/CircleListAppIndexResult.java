package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ylei on 16/6/30.
 */
public class CircleListAppIndexResult extends NetWorkResult {
    private static final long serialVersionUID = -7242495429883691962L;

    private ArrayList<CircleItemData> list;

    public ArrayList<CircleItemData> getList() {
        return list;
    }

    public void setList(ArrayList<CircleItemData> list) {
        this.list = list;
    }

    public  static class  CircleItemData implements Serializable{
        /**
         * status : 0
         * createdTime : 1467008409467
         * modifiedTime : 1467200126944
         * id : 5770c599d5e8b64c61d009b7
         * title : 1型
         * showname : 1型
         * classCode : 健康
         * className : null
         * avatar : /avatar/1467008409068_6B2A22BE14F44A46B4FE0F3CE9BB9764_960x960.jpg
         * ownerId : 564d7ced2e38d78326e54530
         */

        private int status;
        private long createdTime;
        private long modifiedTime;
        private String id;
        private String title;
        private String showname;
        private String classCode;
        private Object className;
        private String avatar;
        private String ownerId;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(long createdTime) {
            this.createdTime = createdTime;
        }

        public long getModifiedTime() {
            return modifiedTime;
        }

        public void setModifiedTime(long modifiedTime) {
            this.modifiedTime = modifiedTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getShowname() {
            return showname;
        }

        public void setShowname(String showname) {
            this.showname = showname;
        }

        public String getClassCode() {
            return classCode;
        }

        public void setClassCode(String classCode) {
            this.classCode = classCode;
        }

        public Object getClassName() {
            return className;
        }

        public void setClassName(Object className) {
            this.className = className;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }
    }

}
