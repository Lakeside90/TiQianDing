package com.xkhouse.fang.booked.entity;

import com.xkhouse.fang.app.entity.BookedInfo;

import java.io.Serializable;
import java.util.ArrayList;

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
    private ArrayList<BookedInfo> bookings;
    private String check_discount;
    private String check_discount_id;
    private String collection;



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

    public ArrayList<BookedInfo> getBookings() {
        return bookings;
    }

    public void setBookings(ArrayList<BookedInfo> bookings) {
        this.bookings = bookings;
    }

    public String getCheck_discount() {
        return check_discount;
    }

    public void setCheck_discount(String check_discount) {
        this.check_discount = check_discount;
    }

    public String getCheck_discount_id() {
        return check_discount_id;
    }

    public void setCheck_discount_id(String check_discount_id) {
        this.check_discount_id = check_discount_id;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
