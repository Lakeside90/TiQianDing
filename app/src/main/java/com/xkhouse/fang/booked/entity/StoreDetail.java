package com.xkhouse.fang.booked.entity;

import java.io.Serializable;

/**
 *
 * 商户详情
 * Created by wujian on 17/9/10.
 */

public class StoreDetail implements Serializable {

    private String businessId;
    private String businessName;
    private String averageConsump;
    private String address;
    private String phone;
    private String[] businessLabel;
    private String[] banner;


    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAverageConsump() {
        return averageConsump;
    }

    public void setAverageConsump(String averageConsump) {
        this.averageConsump = averageConsump;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String[] getBusinessLabel() {
        return businessLabel;
    }

    public void setBusinessLabel(String[] businessLabel) {
        this.businessLabel = businessLabel;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String[] getBanner() {
        return banner;
    }

    public void setBanner(String[] banner) {
        this.banner = banner;
    }
}
