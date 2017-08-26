package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
* @Description: 老用户未绑定手机号的，需要绑定手机号
* @author wujian  
* @date 2015-10-29 上午9:02:56
 */
public class ChangeMobileRequest {

	private String TAG = "ChangeMobileRequest";
	private RequestListener requestListener;
	
	private String userName;  	//用户名
	private String mobile;  	//手机号
	private String mobileCode; 	//手机验证码
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	public ChangeMobileRequest(String userName, String mobile, String mobileCode, RequestListener requestListener) {
		
		this.userName = userName;
		this.mobile = mobile;
		this.mobileCode = mobileCode;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String userName, String mobile, String mobileCode) {
		this.userName = userName;
		this.mobile = mobile;
		this.mobileCode = mobileCode;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("mobile", mobile);
		params.put("mobileCode", mobileCode);
		
		String url = StringUtil.getRequestUrl(Constants.USER_CHANGE_MOBILE, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        	message.obj = msg;
                        }else{
                           message.obj = msg;
                           message.what = Constants.NO_DATA_FROM_NET;
                        }
                        requestListener.sendMessage(message);
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    	Logger.e(TAG, error.toString());
                        Message message = new Message();
                        message.what = Constants.ERROR_DATA_FROM_NET;
                        requestListener.sendMessage(message);
                    }
                });

       
        BaseApplication.getInstance().getRequestQueue().add(request);
	}
	
	
	public void parseResult(String result) {
		if (StringUtil.isEmpty(result)) {
            return;
        }

        try {
        	if(result != null && result.startsWith("\ufeff")){
        		result =  result.substring(1);
        	}
        	
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject != null) {
            	code = jsonObject.optString("code");
            	
            	 if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                 	msg = jsonObject.optString("data");
                     
                 }else{
                	msg = "手机号绑定成功，您可以使用手机号或者用户名登录啦！";
                 }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
