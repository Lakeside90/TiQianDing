package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/**
 * Created by wujian on 2016/2/26.
 */
public class BaikeTab implements Serializable{

    private int id;
    private int icon;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
