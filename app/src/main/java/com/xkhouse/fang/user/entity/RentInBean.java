package com.xkhouse.fang.user.entity;

/**
 * Created by wujian on 2016/4/19.
 * 发布求租请求参数封装
 */
public class RentInBean {

    private String area;
    private String propertyType;
    private String houseType;
    private String area_start;
    private String area_end;
    private String price_start;
    private String price_end;
    private String floor_start;
    private String floor_end;
    private String rentType;
    private String sharedType;
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

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }
}
