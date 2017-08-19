package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/** 
 * @Description: 二手房(租房)房源实体类 
 * @author wujian  
 * @date 2015-9-8 上午10:50:13  
 */
public class OldHouse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 654325324L;
	
	/** 房源ID**/
	private String porjectId;
	
	/** 项目名称**/
	private String porjectName;
	
	/** 标题  水岸云锦公寓 豪华装修 市政供暖 中央空调满五年无税 **/
	private String title;
	
	/** 小区名称 **/
	private String areaName;
	
	/** 纬度**/
	private String latitude;
	
	/** 经度**/
	private String longitude;
	
	/** 面积 90平方米**/
	private String houseArea;
	
	/** 房价 90万**/
	private String price;
	
	/** 户型 三室两厅 **/
	private String roomtype;
	
	/** 楼层 **/
	private String floor;
	
	/** 来源 个人/经纪人**/
	private String membertype;
	
	/** 海报图片地址 **/
	private String housephoto;

	
	public String getPorjectId() {
		return porjectId;
	}

	public void setPorjectId(String porjectId) {
		this.porjectId = porjectId;
	}

	public String getPorjectName() {
		return porjectName;
	}

	public void setPorjectName(String porjectName) {
		this.porjectName = porjectName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
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

	public String getRoomtype() {
		return roomtype;
	}

	public void setRoomtype(String roomtype) {
		this.roomtype = roomtype;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getMembertype() {
		return membertype;
	}

	public void setMembertype(String membertype) {
		this.membertype = membertype;
	}

	public String getHousephoto() {
		return housephoto;
	}

	public void setHousephoto(String housephoto) {
		this.housephoto = housephoto;
	}
	
}
