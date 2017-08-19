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
* @Description: 登录接口
* @author wujian  
* @date 2015-10-23 上午10:18:41
 */
public class LoginRequest {

	private String TAG = "LoginRequest";
	private RequestListener requestListener;
	
	private String userName;  	//用户名或者手机号
	private String passWord; 	//用户密码
	private String userId;			//百度userId
	private String channelId;		//百度channelId
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	private User user;
	
	
	public LoginRequest(String userName, String passWord,
			String userId, String channelId, RequestListener requestListener) {
		
		this.userName = userName;
		this.passWord = passWord;
		if(userId == null){
			userId = "";
		}
		if(channelId == null){
			channelId = "";
		}
		this.userId = userId;
		this.channelId = channelId;
		
		this.requestListener = requestListener;
	}
	
	
	public void setData(String userName, String passWord,
			String userId, String channelId) {
		this.userName = userName;
		this.passWord = passWord;
		if(userId == null){
			userId = "";
		}
		if(channelId == null){
			channelId = "";
		}
		this.userId = userId;
		this.channelId = channelId;
	}
	
	public void doRequest(){
		
		StringRequest request = new StringRequest(Method.POST, Constants.USER_LOGIN,
                new Listener<String>() {

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
                        }else if("106".equals(code)){
                        	message.obj = code;
                        	message.what = Constants.NO_DATA_FROM_NET;
                        } else{
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
                params.put("userName", userName);
                params.put("passWord", passWord);
                params.put("userId", userId);
                params.put("channelId", channelId);
                params.put("device_type", "3");

                Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_LOGIN, params));

                return params;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(5*1000, 2, 2);
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
                
                if("106".equals(code)){
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                user = new User();
                user.setPassword(passWord);
                user.setUid(dataObj.optString("member_id"));
                user.setUserName(dataObj.optString("member_username"));
                user.setRealName(dataObj.optString("member_realname"));
                user.setNickName(dataObj.optString("member_nickname"));
                user.setEmail(dataObj.optString("member_email"));
                user.setPhone(dataObj.optString("member_phone"));
                user.setMobile(dataObj.optString("member_mobile"));
                user.setAge(dataObj.optString("member_age"));
                user.setLastLogintTime(dataObj.optString("member_lastelogintime"));
                user.setLoginNum(dataObj.optString("member_loginnum"));
                user.setSex(dataObj.optString("member_sex"));
                user.setCity(dataObj.optString("member_city"));
                user.setHeadPhoto(dataObj.optString("member_headphoto"));
                user.setNuid(dataObj.optString("member_salt"));
                user.setMemberType(dataObj.optString("member_type"));
                user.setOldhouseSaleExtAuth(dataObj.optString("oldhouse_sale_ext_auth"));
                user.setOldhouseHireExtAuth(dataObj.optString("oldhouse_hire_ext_auth"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
