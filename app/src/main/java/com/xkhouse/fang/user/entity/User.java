package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 用户信息
 * @author wujian  
 * @date 2015-10-23 上午9:40:32  
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 234563213L;
	
	/** id **/
	private String uid;
	
	/** 用户名 **/
	private String userName;
	
	/** 密码 **/
	private String password;
	
	/** 支付密码 **/
	private String payPassword;
	
	/** 真实姓名 **/
	private String realName;
	
	/** 昵称 **/
	private String nickName;
	
	/** 邮箱 **/
	private String email;
	
	/** 固定电话 **/
	private String phone;
	
	/** 手机号码 **/
	private String mobile;
	
	/** 年龄 **/
	private String age;
	
	/** 时间戳 **/
	private String lastLogintTime;
	
	/** 登录次数 **/
	private String loginNum;
	
	/** 1,男，2，女 **/
	private String sex;
	
	/** 所在城市 **/
	private String city;
	
	/** 用户头像 **/
	private String headPhoto;

    /** 邀请码 **/
	private String nuid;

    /** 密码是否设置了，0没有设置 1 设置 **/
	private String passportstatus;

    /** 1 个人 2 经纪人 3 中介 4 律师 **/
    private String memberType;

    /** 发布出售房源是否可以修改联系人 有值：可以  没值：不可以 **/
    private String oldhouseSaleExtAuth;

    /** 发布出租房源是否可以修改联系人 有值：可以   没值：不可以 **/
    private String oldhouseHireExtAuth;

    /** 1: 用户名不能修改，*/
    private String usernamestatus;
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getLastLogintTime() {
		return lastLogintTime;
	}

	public void setLastLogintTime(String lastLogintTime) {
		this.lastLogintTime = lastLogintTime;
	}

	public String getLoginNum() {
		return loginNum;
	}

	public void setLoginNum(String loginNum) {
		this.loginNum = loginNum;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHeadPhoto() {
		return headPhoto;
	}

	public void setHeadPhoto(String headPhoto) {
		this.headPhoto = headPhoto;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

    public String getNuid() {
        return nuid;
    }

    public void setNuid(String nuid) {
        this.nuid = nuid;
    }

    public String getPassportstatus() {
        return passportstatus;
    }

    public void setPassportstatus(String passportstatus) {
        this.passportstatus = passportstatus;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getOldhouseSaleExtAuth() {
        return oldhouseSaleExtAuth;
    }

    public void setOldhouseSaleExtAuth(String oldhouseSaleExtAuth) {
        this.oldhouseSaleExtAuth = oldhouseSaleExtAuth;
    }

    public String getOldhouseHireExtAuth() {
        return oldhouseHireExtAuth;
    }

    public void setOldhouseHireExtAuth(String oldhouseHireExtAuth) {
        this.oldhouseHireExtAuth = oldhouseHireExtAuth;
    }

    public String getUsernamestatus() {
        return usernamestatus;
    }

    public void setUsernamestatus(String usernamestatus) {
        this.usernamestatus = usernamestatus;
    }
}
