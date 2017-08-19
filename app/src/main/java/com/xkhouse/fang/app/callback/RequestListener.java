package com.xkhouse.fang.app.callback;

import android.os.Message;

/**
 * 网络请求结果的回调，用于向Activity传递网络请求结果
 * 
 * @author wujian
 *
 * create at 2014-9-17.上午10:48:52
 */
public interface RequestListener {
	
	public void sendMessage(Message message);
}
