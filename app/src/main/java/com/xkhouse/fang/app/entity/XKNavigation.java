package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/** 
 * @Description: 首页导航测试类
 * @author wujian  
 * @date 2015-9-25 下午5:55:28  
 */
public class XKNavigation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4213423235231234L;
	
	/**   导航ID  **/
	private String navId;
	
	/**   标题  **/
	private String title;
	
	/**   导航图标地址  **/
	private String photoUrl;
	
	/**   3G端链接  **/
	private String link;
	
	/**   移动端方法名， 确定要跳转的activity，当做key **/
	private String method;
	
	/**
	 * 默认图标
	 */
	private int defaultIcon;
	
	public String getNavId() {
		return navId;
	}

	public void setNavId(String navId) {
		this.navId = navId;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getDefaultIcon() {
		return defaultIcon;
	}

	public void setDefaultIcon(int defaultIcon) {
		this.defaultIcon = defaultIcon;
	}
	
}
