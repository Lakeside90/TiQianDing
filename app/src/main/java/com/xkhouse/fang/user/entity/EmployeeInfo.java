package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/9.
 */

public class EmployeeInfo implements Serializable {

    private String name;
    private String reward_balance;
    private String withdrawals_num;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReward_balance() {
        return reward_balance;
    }

    public void setReward_balance(String reward_balance) {
        this.reward_balance = reward_balance;
    }

    public String getWithdrawals_num() {
        return withdrawals_num;
    }

    public void setWithdrawals_num(String withdrawals_num) {
        this.withdrawals_num = withdrawals_num;
    }
}
