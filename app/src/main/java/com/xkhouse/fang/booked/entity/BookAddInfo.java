package com.xkhouse.fang.booked.entity;

import java.io.Serializable;

/**
 * 在线预定
 * Created by Administrator on 2017/9/14.
 */

public class BookAddInfo implements Serializable {

    private String member_name;
    private String cityId;
    private String member_phone;
    private String people_num;
    private String member_remarks;
    private String business_id;
    private String booking_id;
    private String use_time;
    private String gender;


    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public String getPeople_num() {
        return people_num;
    }

    public void setPeople_num(String people_num) {
        this.people_num = people_num;
    }

    public String getMember_remarks() {
        return member_remarks;
    }

    public void setMember_remarks(String member_remarks) {
        this.member_remarks = member_remarks;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getUse_time() {
        return use_time;
    }

    public void setUse_time(String use_time) {
        this.use_time = use_time;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
