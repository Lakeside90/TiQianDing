package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 用户信息
 * @author wujian
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 234563213L;

    /**  登陆保存的token**/
    private String token;

	private String id;

	private String nickname;

	private String realname;

	private String head_img;

	/**
	 * 钱包总金额
	 */
	private String account_balance;

	/**
	 * 活动机会
	 */
	private String activity_num;

	/**
	 * 商家id（用户为员工时有值）
	 */
	private String business_id;

	/**
	 * 性别
	 */
	private String gender;

	private String phone;

	/**
	 * 兴趣
	 */
	private String interest;

	/**
	 * 是否为员工
	 */
	private String is_staff;


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	public String getAccount_balance() {
		return account_balance;
	}

	public void setAccount_balance(String account_balance) {
		this.account_balance = account_balance;
	}

	public String getActivity_num() {
		return activity_num;
	}

	public void setActivity_num(String activity_num) {
		this.activity_num = activity_num;
	}

	public String getBusiness_id() {
		return business_id;
	}

	public void setBusiness_id(String business_id) {
		this.business_id = business_id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getIs_staff() {
		return is_staff;
	}

	public void setIs_staff(String is_staff) {
		this.is_staff = is_staff;
	}
}
