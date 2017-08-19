package com.xkhouse.fang.user.task;


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
* @Description: 提现
* @author wujian  
* @date 2015-10-21 下午4:40:55
 */
public class CashWithdrawCommitRequest {

	private String TAG = "CashWithdrawCommitRequest";
	private RequestListener requestListener;
	
	private String uId;
	
	private String phone;    
	private String userName;    
	private String verifyCode;    
	private String type;    
	private String cardNo;    
	private String money;    
	private String zfpwd;    
	
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	public CashWithdrawCommitRequest(String uId, String phone, String userName,
			String verifyCode, String type, String cardNo, 
			String money, String zfpwd, RequestListener requestListener) {
		
		this.uId = uId;
		this.phone = phone;
		this.userName = userName;
		this.verifyCode = verifyCode;
		this.type = type;
		this.cardNo = cardNo;
		this.money = money;
		this.zfpwd = zfpwd;
		this.requestListener = requestListener;
	}
	
	public void setData(String uId, String phone, String userName,
			String verifyCode, String type, String cardNo, 
			String money, String zfpwd) {
		
		this.uId = uId;
		this.phone = phone;
		this.userName = userName;
		this.verifyCode = verifyCode;
		this.type = type;
		this.cardNo = cardNo;
		this.money = money;
		this.zfpwd = zfpwd;
	}
	
	public void doRequest(){
		
		StringRequest request = new StringRequest(Method.POST,
				Constants.XKB_CASH_WITHDRAW, new Listener<String>() {

	            @Override
	            public void onResponse(String response) {
	                Logger.d(TAG, response);
	               
	                parseResult(response);
	                
	                Message message = new Message();
	                if(Constants.SUCCESS_CODE.equals(code)){
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
	        })
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<>();
				params.put("uId", uId);
				params.put("phone", phone);
				params.put("userName", userName);
				params.put("verifyCode", verifyCode);
				params.put("type", type);
				params.put("cardNo", cardNo);
				params.put("money", money);
				params.put("zfpwd", zfpwd);

				Logger.d(TAG, StringUtil.getRequestUrl(Constants.XKB_CASH_WITHDRAW, params));

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
