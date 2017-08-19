package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description:  我的消息--系统消息
 * @author wujian  
 * @date 2015-11-4 下午7:36:46  
 */
public class MSGSystem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 78234123412L;
	
	private String content;
	
	private String date;

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
