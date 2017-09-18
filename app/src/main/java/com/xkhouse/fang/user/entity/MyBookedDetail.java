package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/**
 *
 * 我的预定
 * Created by Administrator on 2017/9/14.
 */

public class MyBookedDetail implements Serializable {

    private String id;
    private String order_number;
    private String member_name;
    private String member_phone;
    private String money;
    private String pay_time;
    private String tradeno;
    /**
     * 1 微信  2支付宝
     */
    private String pay_type;
    /**
     * 1 待付款 2 已付款 3 已完成
     */
    private String status;
    private String people_num;
    private String member_remarks;
    private String business_id;
    private String booking_id;
    private String business_remarks;
    private String create_time;
    private String update_time;
    private String comment_id;
    private String member_id;
    private String use_time;
    private String business_name;
    private String average_consump;
    private String cover_banner;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getTradeno() {
        return tradeno;
    }

    public void setTradeno(String tradeno) {
        this.tradeno = tradeno;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getBusiness_remarks() {
        return business_remarks;
    }

    public void setBusiness_remarks(String business_remarks) {
        this.business_remarks = business_remarks;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getUse_time() {
        return use_time;
    }

    public void setUse_time(String use_time) {
        this.use_time = use_time;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getAverage_consump() {
        return average_consump;
    }

    public void setAverage_consump(String average_consump) {
        this.average_consump = average_consump;
    }

    public String getCover_banner() {
        return cover_banner;
    }

    public void setCover_banner(String cover_banner) {
        this.cover_banner = cover_banner;
    }
}
