package com.xkhouse.fang.money.entity;

import java.io.Serializable;

/** 
 * @Description: 推荐客户中的期望楼盘的区域实体类
 * @author wujian  
 * @date 2015-9-9 下午5:06:44  
 */
public class XKBArea implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 754532334L;
	
	private String id;
	
	private String name;

	private boolean isSelected = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	

}
