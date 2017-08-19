package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/**
 * Created by wujian on 2016/6/22.
 */
public class SchoolHouse implements Serializable{

    private String id;
    private String name;
    private String isImportant;
    private String address;
    private String photourl;
    private String areaName;
    private String level;
    private String projectName;
    private String averagePrice;
    /**
     * 新房个数
     */
    private String num;
    /**
     * 二手房个数
     */
    private String oldNum;
    /**
     * 划片小区个数
     */
    private String communityNum;

    /** 经度**/
    private String longitude;

    /** 纬度**/
    private String latitude;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(String isImportant) {
        this.isImportant = isImportant;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getOldNum() {
        return oldNum;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public String getCommunityNum() {
        return communityNum;
    }

    public void setCommunityNum(String communityNum) {
        this.communityNum = communityNum;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
