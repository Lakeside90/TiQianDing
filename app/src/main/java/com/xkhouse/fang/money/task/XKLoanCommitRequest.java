package com.xkhouse.fang.money.task;


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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
* @Description: 星空贷提交 
* @author wujian  
* @date 2015-10-21 上午10:33:49
 */
public class XKLoanCommitRequest {

	private String TAG = "XKLoanCommitRequest";
	private RequestListener requestListener;
	
	private String userName;
	private String phone;    
	private String verifyCode;    
	private String siteName;    
	private String money;    
	private String deadline;    
	private String projectName;    
	private String content;    
	private int type;    
	
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	
	public XKLoanCommitRequest(String userName, String phone, String verifyCode,
			String siteName, String money, String deadline,
			String projectName, String content, int type,
			RequestListener requestListener) {
		
		this.userName = userName;
		this.phone = phone;
		this.verifyCode = verifyCode;
		this.money = money;
		this.siteName = siteName;
		this.deadline = deadline;
		this.projectName = projectName;
		this.content = content;
		this.type = type;
		this.requestListener = requestListener;
	}
	
	public void setData(String userName, String phone, String verifyCode,
			String siteName, String money, String deadline,
			String projectName, String content, int type) {
		
		this.userName = userName;
		this.phone = phone;
		this.verifyCode = verifyCode;
		this.money = money;
		this.siteName = siteName;
		this.deadline = deadline;
		this.projectName = projectName;
		this.content = content;
		this.type = type;
	}
	
	public void doRequest(){

		StringRequest request = new StringRequest(Method.POST,
				Constants.XK_LOAN_COMMIT, new Listener<String>() {

		            @Override
		            public void onResponse(String response) {
		                Logger.d(TAG, response);
		               
		                parseResult(response);
		                
		                Message message = new Message();
		                if(Constants.SUCCESS_CODE_OLD.equals(code)){
		                	message.obj = msg;
		                	message.what = Constants.SUCCESS_DATA_FROM_NET;
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
		        }) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> params = new HashMap<>();
				params.put("userName", userName);
				params.put("phone", phone);
				params.put("verifyCode", verifyCode);
				params.put("money", money);
				params.put("siteName", siteName);
				params.put("deadline", deadline);
				params.put("projectName", projectName);
				params.put("content", content);
				params.put("type", String.valueOf(type));

				Logger.d(TAG, StringUtil.getRequestUrl(Constants.XK_LOAN_COMMIT, params));

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
