package com.example.myretrofittest.network.bean;

import java.io.Serializable;

/**
 * Created by gcy on 2017/5/25 0025.
 */
public class News implements Serializable {
    private String picUrl;
    private String title;
    private String content;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
