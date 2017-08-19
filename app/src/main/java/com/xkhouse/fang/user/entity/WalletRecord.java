package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 账单记录 
 * @author wujian  
 * @date 2015-10-10 上午11:50:26  
 */
public class WalletRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9213445466L;
	
	/**   状态 1客户成交 2申请提现   **/
	private String type;
	
	/**  客户成交   **/
	private String typeName;
	
	/**  已到账  **/
	private String stateName;
	
	/**  金额   **/
	private String money;

	/**  时间   **/
	private String date;

    private String title;


	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
