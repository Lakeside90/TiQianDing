package com.xkhouse.fang.user.entity;

import com.xkhouse.fang.house.entity.CommonType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wujian on 2016/4/19.
 * 发布求租请求参数封装
 */
public class RentInEditBean implements Serializable {

    private String id;
    private String siteId;
    private String uid;
    private String area;
    private String areaName;
    private ArrayList<CommonType> areaList;
    private String propertyType;
    private String propertyTypeName;
    private String houseType;
    private String houseTypeName;
    private String area_start;
    private String area_end;
    private String price_start;
    private String price_end;
    private String floor_start;
    private String floor_end;
    private String rentType;
    private String rentTypeName;
    private String sharedType;
    private String sharedTypeName;
    private String title;
    private String detail;
    private String contacter;
    private String contactPhone;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getArea_start() {
        return area_start;
    }

    public void setArea_start(String area_start) {
        this.area_start = area_start;
    }

    public String getArea_end() {
        return area_end;
    }

    public void setArea_end(String area_end) {
        this.area_end = area_end;
    }

    public String getPrice_start() {
        return price_start;
    }

    public void setPrice_start(String price_start) {
        this.price_start = price_start;
    }

    public String getPrice_end() {
        return price_end;
    }

    public void setPrice_end(String price_end) {
        this.price_end = price_end;
    }

    public String getFloor_start() {
        return floor_start;
    }

    public void setFloor_start(String floor_start) {
        this.floor_start = floor_start;
    }

    public String getFloor_end() {
        return floor_end;
    }

    public void setFloor_end(String floor_end) {
        this.floor_end = floor_end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getContacter() {
        return contacter;
    }

    public void setContacter(String contacter) {
        this.contacter = contacter;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRentType() {
        return rentType;
    }

    public void setRentType(String rentType) {
        this.rentType = rentType;
    }

    public String getSharedType() {
        return sharedType;
    }

    public void setSharedType(String sharedType) {
        this.sharedType = sharedType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public ArrayList<CommonType> getAreaList() {
        return areaList;
    }

    public void setAreaList(ArrayList<CommonType> areaList) {
        this.areaList = areaList;
    }

    public String getHouseTypeName() {
        return houseTypeName;
    }

    public void setHouseTypeName(String houseTypeName) {
        this.houseTypeName = houseTypeName;
    }

    public String getRentTypeName() {
        return rentTypeName;
    }

    public void setRentTypeName(String rentTypeName) {
        this.rentTypeName = rentTypeName;
    }

    public String getSharedTypeName() {
        return sharedTypeName;
    }

    public void setSharedTypeName(String sharedTypeName) {
        this.sharedTypeName = sharedTypeName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyTypeName() {
        return propertyTypeName;
    }

    public void setPropertyTypeName(String propertyTypeName) {
        this.propertyTypeName = propertyTypeName;
    }
}
