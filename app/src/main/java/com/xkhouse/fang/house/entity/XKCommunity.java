package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/** 
 * @Description: 小区 (地图找房)
 * @author wujian  
 * @date 2015-10-14 上午9:55:35  
 */
public class XKCommunity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 76921345454321L;
	
	/** 小区名称  **/
	private String buildName;
	
	/** 小区房源总数  **/
	private String countNum;
	
	/**  坐标：维度  **/
	private String latitude;
	
	/**  坐标：经度  **/
	private String longitude;

	
	
	public String getBuildName() {
		return buildName;
	}

	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

	public String getCountNum() {
		return countNum;
	}

	public void setCountNum(String countNum) {
		this.countNum = countNum;
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
	
	
}
