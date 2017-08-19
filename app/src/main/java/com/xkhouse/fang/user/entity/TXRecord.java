package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/**
 * Created by wujian on 16/7/19.
 *
 * 最近提现 -- 联系人
 */
public class TXRecord implements Serializable {

    /**
     * 开户名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 银行卡号 或者 支付宝账号
     */
    private String cardNo;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 提现类型
     */
    private String type;

    /**
     * 银行，支付宝图标
     */
    private String icon;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
