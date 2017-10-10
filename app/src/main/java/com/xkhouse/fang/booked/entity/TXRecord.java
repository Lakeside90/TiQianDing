package com.xkhouse.fang.booked.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/9.
 */

public class TXRecord implements Serializable {

    private String id;
    private String money;
    private String create_time;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
