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
* @Description: 修改密码 
* @author wujian  
* @date 2015-10-26 上午11:37:27
 */
public class ChangePasswordRequest {

	private String TAG = "ChangePasswordRequest";
	private RequestListener requestListener;
	
	private String userId;  		//手机号
	private String oldpassword;  	//旧密码
	private String passWord; 		//新密码
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	public ChangePasswordRequest(String userId, String oldpassword, String passWord, RequestListener requestListener) {
		
		this.userId = userId;
		this.oldpassword = oldpassword;
		this.passWord = passWord;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String userId, String oldpassword, String passWord) {
		this.userId = userId;
		this.oldpassword = oldpassword;
		this.passWord = passWord;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("oldpassword", oldpassword);
		params.put("passWord", passWord);
		params.put("chkpassword", passWord);
		
		String url = StringUtil.getRequestUrl(Constants.USER_CHANGE_PSW, params);
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
