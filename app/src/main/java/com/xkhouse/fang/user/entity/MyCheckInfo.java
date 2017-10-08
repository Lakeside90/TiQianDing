package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/**
 *
 * 我的买单
 * Created by Administrator on 2017/9/14.
 */

public class MyCheckInfo implements Serializable {

    private String id;
    private String pay_number;
    private String old_money;
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
    private String content;
    private String create_time;
    private String update_time;
    private String comment_id;
    private String member_id;
    private String business_name;
    private String average_consump;
    private String conver_banner;
    private String business_id;

    private String[] business_label;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPay_number() {
        return pay_number;
    }

    public void setPay_number(String pay_number) {
        this.pay_number = pay_number;
    }

    public String getOld_money() {
        return old_money;
    }

    public void setOld_money(String old_money) {
        this.old_money = old_money;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getConver_banner() {
        return conver_banner;
    }

    public void setConver_banner(String conver_banner) {
        this.conver_banner = conver_banner;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String[] getBusiness_label() {
        return business_label;
    }

    public void setBusiness_label(String[] business_label) {
        this.business_label = business_label;
    }

    public String getAverage_consump() {
        return average_consump;
    }

    public void setAverage_consump(String average_consump) {
        this.average_consump = average_consump;
    }
}
