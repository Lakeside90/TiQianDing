package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description:  我的消息--每日要闻列表
 * @author wujian  
 * @date 2015-11-4 下午7:25:47  
 */
public class MSGNews implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 324531236454L;
	
	private String id;
	private String title;
	private String content;
	private String date;
	private String photoUrl;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	
	
}
