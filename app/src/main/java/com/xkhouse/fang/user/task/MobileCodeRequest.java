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
* @Description: 找回密码获取手机验证码接口（找回密码第一步）
* @author wujian  
* @date 2015-10-26 上午10:26:30
 */
public class MobileCodeRequest {

	private String TAG = "MobileCodeRequest";
	private RequestListener requestListener;
	
	private String mobile;  	//手机号
	private String mobileCode; 	//手机验证码
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	public MobileCodeRequest(String mobile, String mobileCode, RequestListener requestListener) {
		
		this.mobile = mobile;
		this.mobileCode = mobileCode;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String mobile, String mobileCode) {
		this.mobile = mobile;
		this.mobileCode = mobileCode;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("mobileCode", mobileCode);
		
		String url = StringUtil.getRequestUrl(Constants.USER_MOBILE_CODE, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
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
                msg = jsonObject.optString("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
