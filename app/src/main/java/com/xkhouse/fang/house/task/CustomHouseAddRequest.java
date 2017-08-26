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
* @Description: 定制购房需求发布 
* @author wujian  
* @date 2015-9-24 下午5:52:15
 */
public class CustomHouseAddRequest {

	private String TAG = "CustomHouseAddRequest";
	private RequestListener requestListener;
	
	private String uId;
	private String siteId;      //站点
	private String phone;       //手机号
	private String verifyCode;  //验证码 
	private String areaId;    //表单内容
	private String priceRange;    
	private String areaRange;    
	private String feature;    
	private String remark;    
	

	private String code;	//返回状态
	private String msg;		//返回提示语
	
	public CustomHouseAddRequest(String uId, String siteId, String phone, String verifyCode,
			String areaId, String priceRange, String areaRange,
			String feature, String remark, RequestListener requestListener) {
		
		this.uId = uId;
		this.siteId = siteId;
		this.phone = phone;
		this.verifyCode = verifyCode;
		this.areaId = areaId;
		this.priceRange = priceRange;
		this.areaRange = areaRange;
		this.feature = feature;
		this.remark = remark;
		this.requestListener = requestListener;
	}
	
	public void setData(String uId, String siteId, String phone, String verifyCode,
			String areaId, String priceRange, String areaRange,
			String feature, String remark) {
		
		this.uId = uId;
		this.siteId = siteId;
		this.phone = phone;
		this.verifyCode = verifyCode;
		this.areaId = areaId;
		this.priceRange = priceRange;
		this.areaRange = areaRange;
		this.feature = feature;
		this.remark = remark;
	}
	
	public void doRequest(){
		
        StringRequest request = new StringRequest(Method.POST,
				Constants.CUSTOM_HOUSE_ADD, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                if(Constants.SUCCESS_CODE_OLD.equals(code)){
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
				params.put("uid", uId);
				params.put("phone", phone);
				params.put("verifyCode", verifyCode);
				if (!StringUtil.isEmpty(areaId)) {
					params.put("areaId", areaId);
				}
				if (!StringUtil.isEmpty(priceRange)) {
					params.put("priceRange", priceRange);
				}
				if (!StringUtil.isEmpty(areaRange)) {
					params.put("areaRange", areaRange);
				}
				if (!StringUtil.isEmpty(feature)) {
					params.put("feature", feature);
				}
				if (!StringUtil.isEmpty(remark)) {
					params.put("remark", remark);
				}

				Logger.d(TAG, StringUtil.getRequestUrl(Constants.CUSTOM_HOUSE_ADD, params));

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
