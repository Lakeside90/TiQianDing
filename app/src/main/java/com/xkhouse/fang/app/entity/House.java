package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/** 
 * @Description: 楼盘实体类 
 * @author wujian  
 * @date 2015-9-6 下午2:25:57  
 */
public class House implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 16532153213531L;
	
	/** 项目ID **/
	private String projectId;
	
	/** 项目名称 **/
	private String projectName;
	
	/** 项目效果图 **/
	private String effectPhoto;
	
	/** 项目均价**/
	private String averagePrice;
	
	/** 经度**/
	private String longitude;
	
	/** 纬度**/
	private String latitude;
	
	/** 销售状态   eg.在售 **/
	private String saleState;
	
	/** 楼盘类型  eg.住宅#商铺#写字楼# **/
	private String propertyType;
	
	/** 小学 **/
	private String juniorSchool;
	
	/** 初中 **/
	private String middleSchool;
	
	private String haveVideo;

	/** 楼盘特色，优惠#综合体#地铁盘# **/
	private String projectFeature;
	
	/** 区域  eg.包河区 **/
	private String areaName;
	
	/** 有值的话，表示星团 **/
	private String groupDiscountInfo;
	
	/** 折扣信息, 星空宝专用**/
	private String discount;
	
	/** 可赚佣金, 星空宝专用**/
	private String commission;
	
	/** 意向人数, 星空宝专用**/
	private String intent;
	
	/**  是否可以赚佣，1为是  **/
	private String haveCommission;

    /**  主力户型  **/
    private String houseType;

    /**  1：看房团  **/
    private String isgroup;

    /**  看房团信息  **/
    private String groupinfo;

    /** 星空团活动剩余多少天  */
    private String endTimeStr;

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

	public String getSaleState() {
		return saleState;
	}

	public void setSaleState(String saleState) {
		this.saleState = saleState;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getJuniorSchool() {
		return juniorSchool;
	}

	public void setJuniorSchool(String juniorSchool) {
		this.juniorSchool = juniorSchool;
	}

	public String getMiddleSchool() {
		return middleSchool;
	}

	public void setMiddleSchool(String middleSchool) {
		this.middleSchool = middleSchool;
	}

	public String getHaveVideo() {
		return haveVideo;
	}

	public void setHaveVideo(String haveVideo) {
		this.haveVideo = haveVideo;
	}

	public String getProjectFeature() {
		return projectFeature;
	}

	public void setProjectFeature(String projectFeature) {
		this.projectFeature = projectFeature;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getGroupDiscountInfo() {
		return groupDiscountInfo;
	}

	public void setGroupDiscountInfo(String groupDiscountInfo) {
		this.groupDiscountInfo = groupDiscountInfo;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getHaveCommission() {
		return haveCommission;
	}

	public void setHaveCommission(String haveCommission) {
		this.haveCommission = haveCommission;
	}

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getIsgroup() {
        return isgroup;
    }

    public void setIsgroup(String isgroup) {
        this.isgroup = isgroup;
    }

    public String getGroupinfo() {
        return groupinfo;
    }

    public void setGroupinfo(String groupinfo) {
        this.groupinfo = groupinfo;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }
}
