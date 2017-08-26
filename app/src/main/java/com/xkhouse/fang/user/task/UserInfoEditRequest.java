package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
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
* @Description: 修改个人信息
* @author wujian  
* @date 2015-10-27 下午5:44:54
 */
public class UserInfoEditRequest {

	private String TAG = "UserInfoEditRequest";
	private RequestListener requestListener;
	
	private String userId;  	
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	public UserInfoEditRequest(String userId, RequestListener requestListener) {
		this.userId = userId;
		this.requestListener = requestListener;
	}
	
	
	//修改头像
	public void doHeadPhotoRequest(String headphoto){

        final Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("member_headphoto", headphoto);
        Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_INFO_EDIT, params));

		StringRequest request = new StringRequest(Method.POST,
				Constants.USER_INFO_EDIT, new Listener<String>() {

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
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        BaseApplication.getInstance().getRequestQueue().add(request);
	}
	
	//修改用户名
	public void doUserNameRequest(String username){

        final Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("member_username", username);
        Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_INFO_EDIT, params));

		StringRequest request = new StringRequest(Method.POST,
				Constants.USER_INFO_EDIT, new Listener<String>() {

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
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        BaseApplication.getInstance().getRequestQueue().add(request);
	}
	
	//修改姓名
	public void doRealNameRequest(String realname){

       final Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("member_realname", realname);
        Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_INFO_EDIT, params));

		StringRequest request = new StringRequest(Method.POST,
				Constants.USER_INFO_EDIT, new Listener<String>() {

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
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        BaseApplication.getInstance().getRequestQueue().add(request);
        
	}
	
	//修改性别
	public void doSexRequest(String sex){

        final Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("member_sex", sex);
        Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_INFO_EDIT, params));

		StringRequest request = new StringRequest(Method.POST,
				Constants.USER_INFO_EDIT, new Listener<String>() {

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
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        BaseApplication.getInstance().getRequestQueue().add(request);
	}
	
	//修改城市
	public void doCityRequest(String city){

        final Map<String, String> params = new HashMap<>();
        params.put("member_city", city);
        params.put("userId", userId);
        Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_INFO_EDIT, params));

		StringRequest request = new StringRequest(Method.POST,
				Constants.USER_INFO_EDIT, new Listener<String>() {

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
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
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
