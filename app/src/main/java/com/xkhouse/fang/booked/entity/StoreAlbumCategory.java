package com.xkhouse.fang.booked.entity;

import java.io.Serializable;

/**
 *
 * 商户相册分类
 * Created by Administrator on 2017/9/11.
 */

public class StoreAlbumCategory implements Serializable {

    private String category_id;
    private String title;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
