package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 我的消息--最新活动 
 * @author wujian  
 * @date 2015-11-4 下午7:31:27  
 */
public class MSGActivity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3453145234L;
	
	
	private String id;
	private String title;
	private String content;
	private String date;
	private String photoUrl;
	private double startTime;
	private double endTime;
	private double nowTime;
	
	
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
	public double getStartTime() {
		return startTime;
	}
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	public double getEndTime() {
		return endTime;
	}
	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public double getNowTime() {
		return nowTime;
	}
	public void setNowTime(double nowTime) {
		this.nowTime = nowTime;
	}
	
	
	
}
