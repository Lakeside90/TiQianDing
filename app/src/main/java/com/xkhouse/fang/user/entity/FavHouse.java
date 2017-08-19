package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 我的收藏（新房、二手房、租房）
 * @author wujian  
 * @date 2015-11-3 上午9:27:06  
 */
public class FavHouse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 97723142145L;
	
	private String projectId;
	private String projectName;
	private String areaName;
	private String propertyType;
	private String projectFeature;
	private String saleState;
	private String effectPhoto;
	private String averagePrice;
	private String groupDiscountInfo;
	private String createTime;
	
	private String saletitle;
	private String roomtype;
	private String houseArea;
	private String price;
	
	private String hiretitle;


	public String getProjectId() {
		return projectId;
	}


	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}


	public String getProjectName() {
		return projectName;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public String getAreaName() {
		return areaName;
	}


	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}


	public String getPropertyType() {
		return propertyType;
	}


	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}


	public String getProjectFeature() {
		return projectFeature;
	}


	public void setProjectFeature(String projectFeature) {
		this.projectFeature = projectFeature;
	}


	public String getSaleState() {
		return saleState;
	}


	public void setSaleState(String saleState) {
		this.saleState = saleState;
	}


	public String getEffectPhoto() {
		return effectPhoto;
	}


	public void setEffectPhoto(String effectPhoto) {
		this.effectPhoto = effectPhoto;
	}


	public String getAveragePrice() {
		return averagePrice;
	}


	public void setAveragePrice(String averagePrice) {
		this.averagePrice = averagePrice;
	}


	public String getGroupDiscountInfo() {
		return groupDiscountInfo;
	}


	public void setGroupDiscountInfo(String groupDiscountInfo) {
		this.groupDiscountInfo = groupDiscountInfo;
	}


	public String getCreateTime() {
		return createTime;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}


	public String getSaletitle() {
		return saletitle;
	}


	public void setSaletitle(String saletitle) {
		this.saletitle = saletitle;
	}


	public String getRoomtype() {
		return roomtype;
	}


	public void setRoomtype(String roomtype) {
		this.roomtype = roomtype;
	}


	public String getHouseArea() {
		return houseArea;
	}


	public void setHouseArea(String houseArea) {
		this.houseArea = houseArea;
	}


	public String getPrice() {
		return price;
	}


	public void setPrice(String price) {
		this.price = price;
	}


	public String getHiretitle() {
		return hiretitle;
	}


	public void setHiretitle(String hiretitle) {
		this.hiretitle = hiretitle;
	}
	
	
	

}
