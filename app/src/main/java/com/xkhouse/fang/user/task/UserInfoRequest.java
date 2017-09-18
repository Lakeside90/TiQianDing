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
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.service.UserService;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
* @Description: 获取个人信息 
* @author wujian  
 */
public class UserInfoRequest {

	private String TAG = UserInfoRequest.class.getSimpleName();
	private RequestListener requestListener;
	
	private String token;
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	private User user;
	
	
	public UserInfoRequest(String token, RequestListener requestListener) {
		
		this.token = token;
		this.requestListener = requestListener;
	}
	
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		String url = StringUtil.getRequestUrl(Constants.USER_INFO, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	
                        	UserService userService = new UserService(); 
                        	userService.insertUser(user);
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        	message.obj = user;
                        	
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
            	code = jsonObject.optString("status");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("data");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                user = new User();

                user.setToken(token);
                user.setId(dataObj.optString("id"));
                user.setNickname(dataObj.optString("nickname"));
                user.setRealname(dataObj.optString("realname"));
                user.setHead_img(dataObj.optString("head_img"));
                user.setAccount_balance(dataObj.optString("account_balance"));
                user.setActivity_num(dataObj.optString("activity_num"));
                user.setBusiness_id(dataObj.optString("business_id"));
                user.setGender(dataObj.optString("gender"));
                user.setPhone(dataObj.optString("phone"));
                user.setInterest(dataObj.optString("interest"));
                user.setIs_staff(dataObj.optString("is_staff"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
