package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/** 
 * @Description: 新闻实体类 
 * @author wujian  
 * @date 2015-9-6 下午3:38:37  
 */
public class News implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 561223513213L;

	/**  文章ID **/
	private String NewsId;
	
	/**  文章标题  **/
	private String title;
	
	/**  文章缩略图  **/
	private String photoUrl;
	
	/**  创建时间  **/
	private String createTime;

    /**  首页资讯图集 **/
	private Atlas atlas;
	
	public String getNewsId() {
		return NewsId;
	}

	public void setNewsId(String newsId) {
		NewsId = newsId;
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

    public Atlas getAtlas() {
        return atlas;
    }

    public void setAtlas(Atlas atlas) {
        this.atlas = atlas;
    }


}
