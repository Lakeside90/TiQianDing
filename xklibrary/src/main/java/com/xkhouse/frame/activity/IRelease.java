/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title  ICancel.java   
 * @Package com.cdel.frame.Interface 
 * @Author nieshuting     
 * @Date   2014-3-24 下午2:11:24   
 * @Version V1.0     
 */
package com.xkhouse.frame.activity;

/**
 * 释放资源的接口，子类统一实现此接口，销毁前调用
 * 
 * @Author nieshuting
 * @ClassName ICancel
 * @Date 2014-3-24下午2:11:24
 */
public interface IRelease {

	void release();
}
