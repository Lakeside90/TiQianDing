package com.xkhouse.fang.user.entity;

import java.io.Serializable;

/** 
 * @Description: 
 * @author wujian  
 * @date 2015-10-10 下午3:19:26  
 */
public class XKMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9823232L;

	private String title;
	
	private String content;
	
	private String date;
	
	private String url;

    private boolean isRead = false;
	
	/** 图标资源文件的ID   **/
	private int iconRes;

	
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

	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
