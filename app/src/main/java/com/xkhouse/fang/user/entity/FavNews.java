package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description:  我的收藏--资讯
 * @author wujian  
 * @date 2015-11-3 上午9:43:10  
 */
public class FavNews implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7892342134L;
	
	
	private String newsId;
	
	private String title;
	
	private String photoUrl;
	
	private String createTime;
	
	//半截子地址
	private String url;
	
	
	public String getNewsId() {
		return newsId;
	}

	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
