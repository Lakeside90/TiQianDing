package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/** 
 * @Description: 抢购楼盘信息 
 * @author wujian  
 * @date 2015-8-27 下午4:46:01  
 */
public class SaleHouse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 143534534534L;
	
	/** 楼盘编号  **/
	private String id;
	
	/** 楼盘名称  **/
	private String name;

	/** 房间号  **/
	private String num; 
	
	/** 面积  **/
	private String area;

	/** 价格  **/
	private String price;
	
	/** 折扣价  **/
	private String discountPrice;

	/** 图片地址  **/
	private String photoUrl;
	
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

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}

	
	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	@Override
	public String toString() {
		return "SaleHouse [id=" + id + ", name=" + name + ", num=" + num
				+ ", area=" + area + ", price=" + price + ", discountPrice="
				+ discountPrice + "]";
	}
}
