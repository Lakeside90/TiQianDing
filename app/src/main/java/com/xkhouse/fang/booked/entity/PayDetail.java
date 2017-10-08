package com.xkhouse.fang.booked.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wujian on 17/10/7.
 */

public class PayDetail implements Serializable {



    private Booking booking;
    private List<Discount> discountArray;
    private String paid;
    private List<Rule> rules;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<Discount> getDiscountArray() {
        return discountArray;
    }

    public void setDiscountArray(List<Discount> discountArray) {
        this.discountArray = discountArray;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public class Discount {
        String check_discount_id;
        String check_discount_name;
        String check_discount_money;
        String use_time;

        public String getCheck_discount_id() {
            return check_discount_id;
        }

        public void setCheck_discount_id(String check_discount_id) {
            this.check_discount_id = check_discount_id;
        }

        public String getCheck_discount_name() {
            return check_discount_name;
        }

        public void setCheck_discount_name(String check_discount_name) {
            this.check_discount_name = check_discount_name;
        }

        public String getCheck_discount_money() {
            return check_discount_money;
        }

        public void setCheck_discount_money(String check_discount_money) {
            this.check_discount_money = check_discount_money;
        }

        public String getUse_time() {
            return use_time;
        }

        public void setUse_time(String use_time) {
            this.use_time = use_time;
        }
    }

    public class Rule {
        String check_discount_name;
        String use_time;

        public String getCheck_discount_name() {
            return check_discount_name;
        }

        public void setCheck_discount_name(String check_discount_name) {
            this.check_discount_name = check_discount_name;
        }

        public String getUse_time() {
            return use_time;
        }

        public void setUse_time(String use_time) {
            this.use_time = use_time;
        }
    }

    public class Booking {
        String discount_name;
        String discount;
        String payment;
        String mortgage;

        public String getDiscount_name() {
            return discount_name;
        }

        public void setDiscount_name(String discount_name) {
            this.discount_name = discount_name;
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
    }
}
