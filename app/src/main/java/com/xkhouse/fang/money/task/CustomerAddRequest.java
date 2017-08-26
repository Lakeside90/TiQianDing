package com.xkhouse.fang.money.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
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
 * @Description: 星空宝推荐客户 
 * @author wujian  
 * @date 2015-10-14 下午7:36:40  
 */
public class CustomerAddRequest {
	
	private String TAG = "CustomerAddRequest";
	private RequestListener requestListener;
	
	private String uId;  	//用户ID
	private String rName;  
	private String rPhone;  
	private String projectStr;  
	private String propertyType;  
	private String averagePrice;  
	private String remarks;  
	private String siteId;  

	
	private String url;
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	public CustomerAddRequest(String uId, String rName, String rPhone, String projectStr, 
			String propertyType, String averagePrice, String remarks, String siteId,
			RequestListener requestListener) {
		
		if(StringUtil.isEmpty(projectStr)){
			projectStr = "";
		}
		if(StringUtil.isEmpty(propertyType)){
			propertyType = "";
		}
		if(StringUtil.isEmpty(averagePrice)){
			averagePrice = "";
		}
		if(StringUtil.isEmpty(remarks)){
			remarks = "";
		}
		if(StringUtil.isEmpty(siteId)){
			siteId = "";
		}
		
		this.uId = uId;
		this.rName = rName;
		this.rPhone = rPhone;
		this.projectStr = projectStr;
		this.propertyType = propertyType;
		this.averagePrice = averagePrice;
		this.remarks = remarks;
		this.siteId = siteId;
		
		this.requestListener = requestListener;
	}
	
	
	public void doRequest(){
		StringRequest request = new StringRequest(Request.Method.POST, Constants.XKB_CUSTOMER_ADD,
				new Response.Listener<String>() {
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
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Logger.e(TAG, error.toString());
				Message message = new Message();
				message.what = Constants.ERROR_DATA_FROM_NET;
				requestListener.sendMessage(message);
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				//在这里设置需要post的参数
				Map<String, String> params = new HashMap<>();
				params.put("uId", uId);
				params.put("rName", rName);
				params.put("rPhone", rPhone);
				params.put("projectStr", projectStr);
				params.put("propertyType", propertyType);
				params.put("averagePrice", averagePrice);
				params.put("remarks", remarks);
				params.put("siteId", siteId);

				Logger.d(TAG, StringUtil.getRequestUrl(Constants.XKB_CUSTOMER_ADD, params));
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
