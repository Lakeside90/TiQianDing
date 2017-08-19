package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/** 
 * @Description: 专题资讯
 * @author wujian  
 * @date 2015-8-27 下午6:02:50  
 */
public class NewsTheme implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2341564564L;
	
	/** id **/
	private String id;
	
	/** 专题图片 **/
	private String iconUrl;
	
	/** 专题标语 **/
	private String title;
	
	/** 专题导语 **/
	private String content;
	
	/** 时间 **/
	private String date;
	
	/** 详情地址 **/
	private String detailUrl;

	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
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

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	@Override
	public String toString() {
		return "NewsTheme [id=" + id + ", iconUrl=" + iconUrl + ", title="
				+ title + ", content=" + content + ", detailUrl=" + detailUrl
				+ "]";
	}
	
}
