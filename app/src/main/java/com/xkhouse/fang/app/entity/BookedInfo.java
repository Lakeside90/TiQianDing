package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/**
 * Created by wujian on 17/8/26.
 */

public class BookedInfo implements Serializable {

    private String bookingId;
    private String businessId;
    private String discount;
    private String businessName;
    private String averageConsump;
    private String businessAddress;
    private String[] businessLabel;
    private String orderNum;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String[] getBusinessLabel() {
        return businessLabel;
    }

    public void setBusinessLabel(String[] businessLabel) {
        this.businessLabel = businessLabel;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
}
