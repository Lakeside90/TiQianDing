package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
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
* @Description: 重置密码（修改密码）
* @author wujian  
 */
public class ResetPasswordRequest {

	private String TAG = ResetPasswordRequest.class.getSimpleName();
	private RequestListener requestListener;


	private String token;
	private String phone;  	//手机号
	private String verif;  	//验证码
	private String passWord; 	//新密码
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	public ResetPasswordRequest(String token, String phone,
								String passWord, String verif,
								RequestListener requestListener) {
		
		this.token = token;
		this.phone = phone;
		this.passWord = passWord;
		this.verif = verif;
		this.requestListener = requestListener;
	}
	
	
	public void setData( String phone, String passWord, String verif) {
		this.phone = phone;
		this.passWord = passWord;
		this.verif = verif;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("phone", phone);
		params.put("passWord", passWord);
		params.put("verif", verif);
		params.put("verifpassword", passWord);
		
		String url = StringUtil.getRequestUrl(Constants.USER_RESET_PSW, params);
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
                });

       
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
