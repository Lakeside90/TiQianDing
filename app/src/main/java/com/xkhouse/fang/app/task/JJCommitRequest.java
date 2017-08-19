package com.xkhouse.fang.app.task;


import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
* @Description: 家居报名 
* @author wujian  
* @date 2015-10-20 上午8:59:21
 */
public class JJCommitRequest {

	private String TAG = "JJCommitRequest";
	private RequestListener requestListener;
	
	private String userName;
	private String phone;    
	private String verifyCode;    
	private String siteName;    
	private String village;    
	private String intention;    
	private String houseType;    
	private String content;    
	private String type;    
	private String pick;    
	
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	
	public JJCommitRequest(String userName, String phone, String verifyCode,
			String siteName, String village, String intention,
			String houseType, String content, String type, String pick,
			RequestListener requestListener) {
		
		this.userName = userName;
		this.phone = phone;
		this.verifyCode = verifyCode;
		this.siteName = siteName;
		this.village = village;
		this.intention = intention;
		this.houseType = houseType;
		this.content = content;
		this.type = type;
		this.pick = pick;
		this.requestListener = requestListener;
	}
	
	public void setData(String userName, String phone, String verifyCode,
			String siteName, String village, String intention,
			String houseType, String content, String type, String pick) {
		
		this.userName = userName;
		this.phone = phone;
		this.verifyCode = verifyCode;
		this.siteName = siteName;
		this.village = village;
		this.intention = intention;
		this.houseType = houseType;
		this.content = content;
		this.type = type;
		this.pick = pick;
	}
	
	public void doRequest(){

		StringRequest request = new StringRequest(Method.POST,
				Constants.JJ_COMMIT, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Logger.d(TAG, response);

				parseResult(response);

				Message message = new Message();
				if (Constants.SUCCESS_CODE.equals(code)) {
					message.obj = msg;
					message.what = Constants.SUCCESS_DATA_FROM_NET;
				} else {
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
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<>();

				params.put("userName", userName);
				params.put("phone", phone);
				params.put("verifyCode", verifyCode);
				params.put("siteName", siteName);
				params.put("village", village);
				params.put("intention", intention);
				params.put("houseType", houseType);
				params.put("content", content);
				params.put("type", type);
				params.put("pick", pick);

				Logger.d(TAG, StringUtil.getRequestUrl(Constants.JJ_COMMIT, params));

				return params;
			}

            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                        Constants.VOLLEY_MAX_NUM_RETRIES, Constants.VOLLEY_BACKOFF_MULTIPLIER);
            }
        };

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
