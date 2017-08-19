package com.xkhouse.fang.app.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 通过配置标识获取相应配置子选项列表 
* @author wujian  
* @date 2015-9-18 下午1:48:46
 */
public class CommonConfigRequest {

	private String TAG = "CommonConfigRequest";
	private RequestListener requestListener;
	
	private String siteId;	//站点ID
	private String configName;	//配置项Key
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<CommonType> typeList = new ArrayList<CommonType>();
	
	public CommonConfigRequest(String siteId, String configName, RequestListener requestListener) {
		this.siteId = siteId; 
		this.configName = configName;
		this.requestListener = requestListener;
	}
	
	public void doRequest(){
		typeList.clear();
		  
		Map<String, String> params = new HashMap<String, String>();
        params.put("siteId", siteId);
        params.put("configName", configName);
        String url = StringUtil.getRequestUrl(Constants.CONFIG_LIST, params);
        Logger.d(TAG, url);
	        
		StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                if(Constants.SUCCESS_CODE.equals(code)){
                	message.obj = typeList;
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
            	code = jsonObject.optString("code");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray(configName);
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i < jsonArray.length(); i++) {
	                	JSONObject typeJson = jsonArray.getJSONObject(i);
	                	
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	typeList.add(type);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
