/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title  ThumbImageThread.java   
 * @Package com.cdel.chinaacc.phone.app.thread 
 * @Author nieshuting     
 * @Date   2014-1-9 下午5:18:37   
 * @Version V1.0     
 */
package com.xkhouse.frame.db;


/**
 * 升级数据库
 * 
 * @Author nieshuting
 * @ClassName UpdateDBThread
 * @Date 2014-1-9下午5:18:37
 */
public class UpdateDBThread implements Runnable {

	private BaseUpdateDBService service;

	/**
	 * 构造器
	 */
	public UpdateDBThread(BaseUpdateDBService service) {
		this.service = service;
	}

	/**
	 * 
	 * 
	 * @see Runnable#run()
	 */
	@Override
	public void run() {
		service.updateTable();
	}
}
