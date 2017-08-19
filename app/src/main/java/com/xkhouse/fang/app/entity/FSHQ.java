package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/**
 * Created by wujian on 2016/6/24.
 *
 * 房市行情
 */
public class FSHQ implements Serializable{

    private String month;
    private String price;
    private String count;
    private String url;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
