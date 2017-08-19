package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 星空宝推荐客户 
 * @author wujian  
 * @date 2015-10-22 下午2:44:45  
 */
public class XKRecommend implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9235245563L;
	
	private String recommendId;

    /**
     * 被推荐人姓名
     */
	private String customerName;

    /**
     * 被推荐人手机号
     */
    private String phone;

    /**
     * 城市
     */
    private String siteName;

    /**
     * 物业类型
     */
    private String propertyName;

    /**
     * 楼盘
     */
    private String projectStr;

	private String status;
	
	private String date;

    /**
     * 推荐状态
     */
	private String statusName;

	
	public String getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getProjectStr() {
        return projectStr;
    }

    public void setProjectStr(String projectStr) {
        this.projectStr = projectStr;
    }
}
