/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title:  IRequestListener.java   
 * @Package com.cdel.frame.network 
 * @Author: nieshuting     
 * @Date:   2013-12-17 下午7:44:40   
 * @Version V1.0     
 */
package com.xkhouse.frame.download;

/**
 * @Author nieshuting
 * @ClassName IRequestListener
 * @Description TODO(用一句话描述该文件做什么)
 * @Date 2013-12-17下午7:44:40
 */
public interface ICallbackListener {

	void setOnSucess(ISuccessListener listener);

	void setOnError(IErrorListener listener);

}
