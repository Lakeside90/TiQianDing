package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/25.
 */
public class KanFang implements Serializable{

    private String id;

    private String title;

    private String applyNum;


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

    public String getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(String applyNum) {
        this.applyNum = applyNum;
    }
}
