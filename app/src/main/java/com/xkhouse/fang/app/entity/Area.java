package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/** 
 * @Description: 某站点下的行政区
 * @author wujian  
 * @date 2015-9-21 上午9:05:03  
 */
public class Area implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8353245345123454L;
	
	private String areaId;
	
	private String areaName;
	
	private String longitude;
	
	private String latitude;

	/** 区域内房源数量（地图找房）**/
	private String count;
	
	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
}
