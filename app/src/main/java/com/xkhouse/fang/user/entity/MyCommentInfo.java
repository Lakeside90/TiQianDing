package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/**
 * 我的评价
 * Created by Administrator on 2017/9/14.
 */

public class MyCommentInfo implements Serializable {

    private String business_id;
    private String comment_id;
    private String business_name;
    private String average_consump;
    private String[] business_label;
    private String cover_banner;
    private String member_content;
    private String[] member_img;
    private String create_time;
    private String business_content;
    private String[] business_img;


    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
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

    public String[] getBusiness_label() {
        return business_label;
    }

    public void setBusiness_label(String[] business_label) {
        this.business_label = business_label;
    }

    public String getCover_banner() {
        return cover_banner;
    }

    public void setCover_banner(String cover_banner) {
        this.cover_banner = cover_banner;
    }

    public String getMember_content() {
        return member_content;
    }

    public void setMember_content(String member_content) {
        this.member_content = member_content;
    }

    public String[] getMember_img() {
        return member_img;
    }

    public void setMember_img(String[] member_img) {
        this.member_img = member_img;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getBusiness_content() {
        return business_content;
    }

    public void setBusiness_content(String business_content) {
        this.business_content = business_content;
    }

    public String[] getBusiness_img() {
        return business_img;
    }

    public void setBusiness_img(String[] business_img) {
        this.business_img = business_img;
    }
}

