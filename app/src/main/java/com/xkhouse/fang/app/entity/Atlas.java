package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/**
 * 首页资讯图集,视频
 * Created by wujian on 2016/6/6.
 */
public class Atlas implements Serializable{

    private String aid;
    private String title;
    private String aPhotoUrl;
    private String bPhotoUrl;
    private String cPhotoUrl;
    private String type;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getaPhotoUrl() {
        return aPhotoUrl;
    }

    public void setaPhotoUrl(String aPhotoUrl) {
        this.aPhotoUrl = aPhotoUrl;
    }

    public String getbPhotoUrl() {
        return bPhotoUrl;
    }

    public void setbPhotoUrl(String bPhotoUrl) {
        this.bPhotoUrl = bPhotoUrl;
    }

    public String getcPhotoUrl() {
        return cPhotoUrl;
    }

    public void setcPhotoUrl(String cPhotoUrl) {
        this.cPhotoUrl = cPhotoUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
