package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 星空宝提现--选择银行实体类 
 * @author wujian  
 * @date 2015-10-22 上午10:28:34  
 */
public class XKBank implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9867437966L;

	private String bankId;
	
	private String bankName;
	
	private String bankIcon;

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankIcon() {
		return bankIcon;
	}

	public void setBankIcon(String bankIcon) {
		this.bankIcon = bankIcon;
	}
	
	
	
}
