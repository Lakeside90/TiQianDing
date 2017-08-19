package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
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
* @Description: 设置/修改支付密码  
* @author wujian  
* @date 2015-10-27 下午2:22:25
 */
public class ChangePayPasswordRequest {

	private String TAG = "ChangePayPasswordRequest";
	private RequestListener requestListener;
	
	private String userId;  		//手机号
	private String oldpassword;  	//旧密码
	private String newpassword; 	//新支付密码
	private String paystatus; 		//支付密码是否设置了，0没有设置1设置
	private String mobile; 			//手机号
	private String mobileCode; 		//手机验证码
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	public ChangePayPasswordRequest(String userId, String oldpassword, String newpassword, 
			String paystatus, String mobile, String mobileCode, RequestListener requestListener) {
		
		this.userId = userId;
		this.oldpassword = oldpassword;
		this.newpassword = newpassword;
		this.paystatus = paystatus;
		this.mobile = mobile;
		this.mobileCode = mobileCode;
		
		this.requestListener = requestListener;
	}
	
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
        if (StringUtil.isEmpty(oldpassword)) oldpassword = "";
		params.put("oldpassword", oldpassword);
		params.put("newpassword", newpassword);
		params.put("paystatus", paystatus);
        if (StringUtil.isEmpty(mobile))   mobile = "";
        if (StringUtil.isEmpty(mobileCode))   mobileCode= "";
		params.put("mobile", mobile);
		params.put("mobileCode", mobileCode);
		
		String url = StringUtil.getRequestUrl(Constants.USER_CHANGE_PAY_PSW, params);
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
                })
        {
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
