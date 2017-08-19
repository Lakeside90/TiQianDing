package com.xkhouse.fang.house.entity;

import java.io.Serializable;

/** 
 * @Description: 搜索结果通用实体类 
 * @author wujian  
 * @date 2015-10-16 下午2:42:20  
 */
public class XKSearchResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 12346562345324L;
	
	/*** 搜索结果类型  （newhouse/oldhouse/zufang/news） **/
	private String type;
	
	/**  楼盘名或小区名   **/
	private String projectName;
	
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
