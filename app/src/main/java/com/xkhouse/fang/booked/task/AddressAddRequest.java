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
* @Description: 添加收货地址
* @author wujian  
 */
public class AddressAddRequest {

	private String TAG = AddressAddRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;
	private AddressInfo addressInfo;

	private String code;	//返回状态
	private String msg;		//返回提示语


	public AddressAddRequest(String token, AddressInfo addressInfo, RequestListener requestListener) {
		
		this.token = token;
		this.addressInfo = addressInfo;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String token, AddressInfo addressInfo) {
		this.token = token;
		this.addressInfo = addressInfo;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();

		params.put("token", token);
		params.put("name", addressInfo.getName());
		params.put("phone", addressInfo.getPhone());
		params.put("province_id", addressInfo.getProvince_id());
		params.put("city_id", addressInfo.getCity_id());
		params.put("area_id", addressInfo.getArea_id());
		params.put("content", addressInfo.getContent());
		params.put("is_selected", addressInfo.getIs_selected());

		String url = StringUtil.getRequestUrl(Constants.ADDRESS_ADD, params);
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
