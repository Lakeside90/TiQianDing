package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/** 
 * @Description: 站点实体类
 * @author wujian  
 * @date 2015-9-6 下午4:39:13  
 */
public class Site implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 435234314534L;
	
	/** 站点ID **/
	private String siteId;
	
	/** 站点简称  **/
	private String title;
	
	/** 站点域名 **/
	private String domain;
	
	/** 站点城市 **/
	private String area;
	
	/** 站点经度 **/
	private String longitude;

	/** 站点纬度 **/
	private String latitude;
	
	/** 热门城市   1：热门城市  0：非热门 **/
	private String isHot;
	
	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
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

	public String getIsHot() {
		return isHot;
	}

	public void setIsHot(String isHot) {
		this.isHot = isHot;
	}
	

}
