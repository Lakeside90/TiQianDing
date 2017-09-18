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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
* @Description:   手机快捷登录接口
* @author wujian  
* @date 2016-04-05 上午10:18:41
 */
public class LoginPhoneRequest {

	private String TAG = LoginPhoneRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String phone;  	//手机号
	private String verify; 	//验证码

	private String code;	//返回状态
	private String msg;		//返回提示语
	private User user;


	public LoginPhoneRequest(String phone, String verify, RequestListener requestListener) {
		
		this.phone = phone;
		this.verify = verify;

		this.requestListener = requestListener;
	}
	
	
	public void setData(String userName, String passWord) {
		this.phone = userName;
		this.verify = passWord;
	}
	
	public void doRequest(){
		
		StringRequest request = new StringRequest(Method.POST, Constants.USER_LOGIN_PHONE,
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
                params.put("phone", phone);
                params.put("verify", verify);

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
            	code = jsonObject.optString("status");
                msg = jsonObject.optString("msg");

                if (!Constants.SUCCESS_CODE.equals(code)) return;
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                user = new User();
                user.setId(dataObj.optString("uid"));
                user.setToken(dataObj.optString("token"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
