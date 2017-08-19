package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/** 
 * @Description: 房源（楼盘中的某个具体房源）
 * @author wujian  
 * @date 2015-9-29 下午4:24:24  
 */
public class XKRoom implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 134598734234545209L;
	
	/** 选房ID **/
	private String choiceId;
	
	/** 楼盘ID **/
	private String pid;
	
	/** 楼栋号  **/
	private String buildNo;
	
	/** 房源楼层号 **/
	private String floor;
	
	/** 房源房号  **/
	private String roomNo;
	
	/** 房源建筑面积  **/
	private String buildArea;
	
	/** 房源参考总价  **/
	private String totalPrice;
	
	/** 户型缩略图  **/
	private String photoPath;
	
	/** 楼盘名 **/
	private String projectName;
	
	/** 完整户型名称  **/
	private String typename;
	
	/** 户型ID **/
	private String housetypeId;
	
	/** 户型标题 **/
	private String title;
	
	/** 销售状态 **/
	private String saleState;
	
	/** 户型面积 **/
	private String typeArea;
	
	/** 户型名称 **/
	private String houseTitle;

    /** 区域 **/
    private String areaName;

    /** 单价 **/
    private String averagePrice;

    /** 经度 **/
    private String longitude;

    /** 纬度 **/
    private String latitude;

	
	public String getChoiceId() {
		return choiceId;
	}

	public void setChoiceId(String choiceId) {
		this.choiceId = choiceId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getBuildNo() {
		return buildNo;
	}

	public void setBuildNo(String buildNo) {
		this.buildNo = buildNo;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public String getBuildArea() {
		return buildArea;
	}

	public void setBuildArea(String buildArea) {
		this.buildArea = buildArea;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public String getHousetypeId() {
		return housetypeId;
	}

	public void setHousetypeId(String housetypeId) {
		this.housetypeId = housetypeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSaleState() {
		return saleState;
	}

	public void setSaleState(String saleState) {
		this.saleState = saleState;
	}

	public String getTypeArea() {
		return typeArea;
	}

	public void setTypeArea(String typeArea) {
		this.typeArea = typeArea;
	}

	public String getHouseTitle() {
		return houseTitle;
	}

	public void setHouseTitle(String houseTitle) {
		this.houseTitle = houseTitle;
	}

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
}
