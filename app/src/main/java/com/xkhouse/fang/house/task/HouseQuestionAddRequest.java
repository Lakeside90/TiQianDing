package com.xkhouse.fang.house.task;


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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
* @Description: 楼盘提问发布
* @author wujian  
* @date 2016-6-23
 */
public class HouseQuestionAddRequest {

	private String TAG = "HouseQuestionAddRequest";
	private RequestListener requestListener;

	private String uid;
	private String pid;
	private String siteId;
	private String phone;
	private String question;


	private String code;	//返回状态
	private String msg;		//返回提示语

	public HouseQuestionAddRequest(String uid, String pid, String siteId, String phone,
                                   String question,RequestListener requestListener) {
		this.uid = uid;
		this.pid = pid;
		this.siteId = siteId;
		this.phone = phone;
		this.question = question;
		this.requestListener = requestListener;
	}
	
	public void setData(String uid, String pid, String siteId, String phone,String question) {
        this.uid = uid;
        this.pid = pid;
        this.siteId = siteId;
        this.phone = phone;
        this.question = question;
	}
	
	public void doRequest(){
		
        StringRequest request = new StringRequest(Method.POST,
				Constants.HOUSE_QUESTION_ADD, new Listener<String>() {

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
				params.put("siteId", siteId);
				params.put("uid", uid);
				params.put("pid", pid);
				params.put("phone", phone);
				params.put("question", question);

				Logger.d(TAG, StringUtil.getRequestUrl(Constants.HOUSE_QUESTION_ADD, params));

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
