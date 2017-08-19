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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
* @Description: 获取个人信息 
* @author wujian  
* @date 2015-10-27 上午10:59:24
 */
public class UserInfoRequest {

	private String TAG = "UserInfoRequest";
	private RequestListener requestListener;
	
	private String userId;  	
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	private User user;
	
	
	public UserInfoRequest(String userId, RequestListener requestListener) {
		
		this.userId = userId;
		this.requestListener = requestListener;
	}
	
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		
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
            	code = jsonObject.optString("code");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("data");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                user = new User();
                user.setUid(userId);
                user.setPassword(dataObj.optString("member_passport"));
                user.setPayPassword(dataObj.optString("member_paypassport"));
                user.setUserName(dataObj.optString("member_username"));
                user.setRealName(dataObj.optString("member_realname"));
                user.setNickName(dataObj.optString("member_nickname"));
                user.setEmail(dataObj.optString("member_email"));
                user.setPhone(dataObj.optString("member_phone"));
                user.setMobile(dataObj.optString("member_mobile"));
                user.setAge(dataObj.optString("member_age"));
//                user.setLastLogintTime(dataObj.optString("member_lastelogintime"));
//                user.setLoginNum(dataObj.optString("member_loginnum"));
                user.setUsernamestatus(dataObj.optString("member_usernamestatus"));
                user.setSex(dataObj.optString("member_sex"));
                user.setCity(dataObj.optString("member_city"));
                user.setHeadPhoto(dataObj.optString("member_headphoto"));
                user.setPassportstatus(dataObj.optString("member_passportstatus"));
                user.setMemberType(dataObj.optString("member_type"));
                user.setOldhouseSaleExtAuth(dataObj.optString("oldhouse_sale_ext_auth"));
                user.setOldhouseHireExtAuth(dataObj.optString("oldhouse_hire_ext_auth"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
