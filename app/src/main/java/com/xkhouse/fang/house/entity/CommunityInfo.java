package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/**
 *
 * 学校划片小区
 * Created by wujian on 16/8/8.
 */
public class CommunityInfo implements Serializable{

    private String id;
    private String projectName;
    private String count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


}
