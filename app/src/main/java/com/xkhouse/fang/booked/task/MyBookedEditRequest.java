package com.xkhouse.fang.booked.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.AddressInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 
* @Description: 修改预定信息
* @author wujian  
 */
public class MyBookedEditRequest {

	private String TAG = MyBookedEditRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;
	private String order_number;
	private String type;
	private String use_time;

	private String code;	//返回状态
	private String msg;		//返回提示语


	public MyBookedEditRequest(String token, String order_number, String type, String use_time, RequestListener requestListener) {
		
		this.token = token;
		this.order_number = order_number;
		this.type = type;
		this.use_time = use_time;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String token, String order_number, String type, String use_time) {
		this.token = token;
		this.order_number = order_number;
		this.type = type;
		this.use_time = use_time;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();

		params.put("token", token);
		params.put("type", type);
		params.put("order_number", order_number);
        if (!StringUtil.isEmpty(use_time)) {
            params.put("use_time", use_time);
        }

		String url = StringUtil.getRequestUrl(Constants.BOOKED_EDIT, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = msg;
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        }else {
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
                msg = jsonObject.optString("msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
