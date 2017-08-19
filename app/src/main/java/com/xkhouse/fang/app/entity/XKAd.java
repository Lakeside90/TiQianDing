package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/** 
 * @Description: 首页轮训图 , 专题推荐
 * @author wujian  
 * @date 2015-10-15 下午5:03:16  
 */
public class XKAd implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 89123421354545356L;
	
	/**  资讯ID  **/
	private String newsId;
	
	/**  资讯标题  **/
	private String title;
	
	/**  资讯缩略图  **/
	private String photoUrl;
	
	/**  描述  **/
	private String remark;
	
	/**  资讯URL链接  **/
	private String newsUrl;

	
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

}
