package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/** 
 * @Description: 销售动态
 * @author wujian  
 * @date 2015-10-8 下午1:55:00  
 */
public class XKDynamic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 162425656L;

	private String dynamicId;
	
	private String content;

	private String dataDate;
	
	

	public String getDynamicId() {
		return dynamicId;
	}

	public void setDynamicId(String dynamicId) {
		this.dynamicId = dynamicId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}
	
}
