package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/** 
 * @Description: 网友定制内容实体类
 * @author wujian  
 * @date 2015-9-21 下午1:55:41  
 */
public class CustomHouse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8654423432423L;
	
	private String customId;
	
	/** 价格 **/
	private String priceRange;
	
	/** 面积 **/
	private String space;
	
	/** 区域 **/
	private String areaName;
	
	/** 特色 **/
	private String feature;
	
	/** 其他需求 **/
	private String requriement;

    /** 定制人手机号 **/
	private String phone;

    /** 定制时间 **/
	private String time;

    /** 定制状态 **/
	private String status;




	/** 合房推荐 **/
	private String reply;

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(String priceRange) {
		this.priceRange = priceRange;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getRequriement() {
		return requriement;
	}

	public void setRequriement(String requriement) {
		this.requriement = requriement;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
