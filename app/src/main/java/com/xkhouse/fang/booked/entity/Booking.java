package com.xkhouse.fang.booked.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/26.
 */

public class Booking implements Serializable{

    private String booking_id;
    private String discount;
    private String payment;
    private String mortgage;
    private String order_num;
    private String today_order_num;

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getMortgage() {
        return mortgage;
    }

    public void setMortgage(String mortgage) {
        this.mortgage = mortgage;
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public String getToday_order_num() {
        return today_order_num;
    }

    public void setToday_order_num(String today_order_num) {
        this.today_order_num = today_order_num;
    }
}
