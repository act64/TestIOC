package com.jkyshealth.result;

import java.io.Serializable;

/**
 * Created by jinzifu on 16/1/6.
 */
public class HomeBannerData implements Serializable {

    /**
     * title :
     * imagePath : /index/a53bbd91-1170-48b8-afb9-4fe72dc7182f.png
     * redirect : {"type":"WEB_PAGE","url":"http://static3.91jkys.com/events/insurance/activityPage/"}
     * imageURL : /index/a53bbd91-1170-48b8-afb9-4fe72dc7182f.png
     */

    private String title;
    private String imagePath;
    /**
     * type : WEB_PAGE
     * url : http://static3.91jkys.com/events/insurance/activityPage/
     */

    private RedirectEntity redirect;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setRedirect(RedirectEntity redirect) {
        this.redirect = redirect;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public RedirectEntity getRedirect() {
        return redirect;
    }

    public static class RedirectEntity {
        private String type;
        private String url;

        public void setType(String type) {
            this.type = type;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }
    }
}
