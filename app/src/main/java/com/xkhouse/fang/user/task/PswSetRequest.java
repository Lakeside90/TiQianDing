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
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.service.UserService;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 设置密码
 */
public class PswSetRequest {

	private String TAG = PswSetRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String passWord; 	//用户密码


	private String code;	//返回状态
	private String msg;		//返回提示语


	public PswSetRequest(String passWord, RequestListener requestListener) {
		this.passWord = passWord;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String passWord) {
		this.passWord = passWord;
	}
	
	public void doRequest(){
		
		StringRequest request = new StringRequest(Method.POST, Constants.USER_PSW_SET,
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        	message.obj = msg;
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
                params.put("password", passWord);
                params.put("token", Preference.getInstance().readToken());
                Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_PSW_SET, params));

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

                JSONObject dataObj = jsonObject.optJSONObject("data");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
