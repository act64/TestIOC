package com.jkys.tools;

import android.graphics.Bitmap;

import com.jkys.proxy.AppImpl;

import java.io.ByteArrayOutputStream;

/**
 * Created by yangxiaolong on 15/9/9.
 */
public class ImageUtil {

    public static class ImageParam {
        public int width;
        public int height;

        ImageParam(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static ImageParam getRatioImageParam(int mWidth, int mHeight, int maxWidth, int maxHeight) {
        float x = ((float) mWidth) / maxWidth;
        float y = ((float) mHeight) / maxHeight;
        if (x > y) {
            mWidth = maxWidth;
            mHeight = (int) (mHeight / x);
        } else {
            mHeight = maxHeight;
            mWidth = (int) (mWidth / y);
        }
        return new ImageParam(mWidth, mHeight);
    }

    public static String getBigImageUrl(String url) {
        try {
            String s = url.substring(0, url.lastIndexOf('_')) + url.substring(url.lastIndexOf('.'), url.length());
            return s;
        } catch (Exception e) {
            return null;
        }
    }

//    public static String getSmallImageUrl(String url){
//        try {
//            String s = url.substring(0, url.lastIndexOf('.')) + "_small" + url.substring(url.lastIndexOf('.'), url.length());
//            return s;
//        }catch (Exception e){
//            return null;
//        }
//    }

    public static String getImageUrl(String url, int width, int height) {
        try {
            if (AppImpl.getAppRroxy().geBuildType().equals("preRelease") || AppImpl.getAppRroxy().geBuildType().equals("release")) {
                url = url + "@" + width + "w_" + height + "h";
            }
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getImageUrl(String url, int width) {
        try {
            if (AppImpl.getAppRroxy().geBuildType().equals("preRelease") || AppImpl.getAppRroxy().geBuildType().equals("release")) {
                url = url + "@" + width + "w";
            }
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getImageUrl(String url, int width, String otherOssParam) {
        try {
            if (AppImpl.getAppRroxy().geBuildType().equals("preRelease") || AppImpl.getAppRroxy().geBuildType().equals("release")) {
                url = url + "@" + width + "w" + otherOssParam;
            }
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSmallImageUrl(String url) {
        try {
            if (AppImpl.getAppRroxy().geBuildType().equals("preRelease") || AppImpl.getAppRroxy().geBuildType().equals("release")) {
                url = url + "@300w_300h";
            }
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSmallImageUrl(String url, int width, int height) {
        try {
            if (AppImpl.getAppRroxy().geBuildType().equals("preRelease") || AppImpl.getAppRroxy().geBuildType().equals("release")) {
                url = url + "@" + width + "w_" + height + "h";
            }
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
