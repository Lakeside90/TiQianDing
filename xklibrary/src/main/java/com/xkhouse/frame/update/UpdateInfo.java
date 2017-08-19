/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title:  UpdateInfo.java   
 * @Package com.cdel.frame.tool.update 
 * @Author: nieshuting     
 * @Date:   2013-12-17 下午8:01:58   
 * @Version V1.0     
 */
package com.xkhouse.frame.update;

/**
 * 更新对象实体，用于封装从服务端得到的版本信息；
 * 
 * @Author nieshuting
 * @ClassName UpdateInfo
 * @Date 2013-12-17下午8:01:58
 */
public class UpdateInfo {

	// 升级包的路径
	private String path;

	// 强制升级标识，0为选择安装，1为强制安装；
	private String isForce;

	// 版本名称，如7.1
	private String verName;

	// 版本号，如8
	private String verCode = "0";

	// 新功能描述
	private String newInfo;

	/**
	 * 强制升级；
	 */
	public static final String FORCE_UPDATE = "1";

	/**
	 * 非强制升级；
	 */
	public static final String UNFORCE_UPDATE = "0";

	/**
	 * 安装包url
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 获取安装包url
	 * 
	 * @return　url
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 设置是否强制升级；
	 * 
	 * @param isForce
	 *            0为选择安装，1为强制安装
	 */
	public void setIsForce(String isForce) {
		this.isForce = isForce;
	}

	/**
	 * 获取是否强制升级
	 * 
	 * @return 强制升级
	 */
	public String getIsForce() {
		return isForce;
	}

	/**
	 * 设置应用版本名称；
	 * 
	 * @param name
	 *            版本名称，如1.0
	 */
	public void setVerName(String name) {
		this.verName = name;
	}

	/**
	 * 获取版本名称
	 * 
	 * @return　名称
	 */
	public String getVerName() {
		return verName;
	}

	/**
	 * 设置版本号；
	 * 
	 * @param code
	 *            版本号
	 */
	public void setVerCode(String code) {
		this.verCode = code;
	}

	/**
	 * 获取版本号
	 * 
	 * @return 版本号
	 */
	public String getVerCode() {
		return verCode;
	}

	/**
	 * 获取版本历史
	 * 
	 * @return 内容
	 */
	public String getNewInfo() {
		return newInfo;
	}

	/**
	 * 设置版本历史
	 * 
	 * @return 内容
	 */
	public void setNewInfo(String info) {
		this.newInfo = info;
	}

}
