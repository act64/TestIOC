package com.jkyssocial.data;

import java.util.List;

/**咨询医生提交资料
 * Created by xiaoke on 16/11/23.
 */

public class SumbitQuestionBean {
    private long user_id;
    private String upload_files;//分号分隔
    private String title;
    private String description;
    private List<String> images;


    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUpload_files() {
        return upload_files;
    }

    public void setUpload_files(String upload_files) {
        this.upload_files = upload_files;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
