package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rajk on 28/11/17.
 */

public class ImageContentData implements Serializable {

    @SerializedName("img_url")
    private String img_url;

    public ImageContentData(String img_url) {
        this.img_url = img_url;
    }

    public static ImageContentData addImages(String img_url) {
        return new ImageContentData(img_url);
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    @Override
    public String toString() {
        return "ClassPojo [img_url = " + img_url + "]";
    }
}
