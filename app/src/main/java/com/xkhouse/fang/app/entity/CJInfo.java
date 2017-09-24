package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/**
 * Created by wujian on 17/9/24.
 */

public class CJInfo implements Serializable {

    private String id;
    private String lottery_number;
    private String img;
    private String title;
    private String join_count;
    private String count;
    private String pub_type;
    private String real_winning_time;
    private String winning_number;
    private String nickname;
    private String mid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLottery_number() {
        return lottery_number;
    }

    public void setLottery_number(String lottery_number) {
        this.lottery_number = lottery_number;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJoin_count() {
        return join_count;
    }

    public void setJoin_count(String join_count) {
        this.join_count = join_count;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPub_type() {
        return pub_type;
    }

    public void setPub_type(String pub_type) {
        this.pub_type = pub_type;
    }

    public String getReal_winning_time() {
        return real_winning_time;
    }

    public void setReal_winning_time(String real_winning_time) {
        this.real_winning_time = real_winning_time;
    }

    public String getWinning_number() {
        return winning_number;
    }

    public void setWinning_number(String winning_number) {
        this.winning_number = winning_number;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
}
