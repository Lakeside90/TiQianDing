package com.xkhouse.fang.house.entity;

import java.io.Serializable;
import java.util.List;

/** 
 * @Description: key-value   通用类型
 * @author wujian  
 * @date 2015-6-12 下午5:11:54  
 */
public class CommonType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 125233522222L;
	
	private String id;

	private String name;
	
	private List<CommonType> child;
	
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
	
	

	public List<CommonType> getChild() {
		return child;
	}

	public void setChild(List<CommonType> child) {
		this.child = child;
	}

	
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public String toString() {
		return "CommonType [id=" + id + ", name=" + name + "]";
	}

}
