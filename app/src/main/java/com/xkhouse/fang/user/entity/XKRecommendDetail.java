package com.xkhouse.fang.user.entity;

import java.io.Serializable;
import java.util.ArrayList;

/** 
 * @Description: 星空宝客户推荐详情
 * @author wujian  
 * @date 2015-11-12 下午2:27:16  
 */
public class XKRecommendDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 64323423L;

	private String name;
	
	private String phone;
	
	private String project;
	
	private String propertyType;
	
	private String city;
	
	private String averagePrice;
	
	private String remark;
	
	private String date;
	
	private String status;
	
	private ArrayList<XKRecommendStatus> statusList;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getAveragePrice() {
		return averagePrice;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setAveragePrice(String averagePrice) {
		this.averagePrice = averagePrice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<XKRecommendStatus> getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList<XKRecommendStatus> statusList) {
		this.statusList = statusList;
	}
	
}
