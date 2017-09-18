package com.xkhouse.fang.booked.entity;

import java.io.Serializable;

/**
 * 商户相册
 * Created by Administrator on 2017/9/11.
 */

public class StoreAlbum implements Serializable {

    private String id;
    private String img;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
