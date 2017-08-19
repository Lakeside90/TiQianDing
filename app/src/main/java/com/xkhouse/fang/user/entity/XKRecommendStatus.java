package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 星空宝客户推荐状态跟踪 
 * @author wujian  
 * @date 2015-11-12 下午2:16:18  
 */
public class XKRecommendStatus implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3467234523L;

	private String status;
	
	private String content;

	private String date;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	
}
