package com.xkhouse.fang.app.task;

import android.os.Bundle;
import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.AppUpgrade;
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @Description: 版本更新升级接口
* @author wujian  
* @date 2016-7-26
 */
public class VersionCheckRequest {

	private String TAG = "VersionCheckRequest";
	private RequestListener requestListener;

    private String id = "1";    //应用id 星房惠:1
    private String device = "a";    //设备名称 安卓a 苹果 i 默认a
	private String version; //版本号


	private String code;	//返回状态
	private String msg;				//返回提示语

	private AppUpgrade appUpgrade;

    public VersionCheckRequest(){
        super();
    }

	public VersionCheckRequest(String version, RequestListener requestListener) {
		this.version = version;
		this.requestListener = requestListener;
	}

	
	public void doRequest(){

		String url = Constants.APP_VERSION_CHECK + "?id=" + id + "&device=" + device + "&version=" + version;
		Logger.d(TAG, url);
		
		StringRequest request = new StringRequest(url,  new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
                parseResult(response);
                
                Message message = new Message();

                if(Constants.SUCCESS_CODE.equals(code)){
                    message.obj = appUpgrade;
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

                JSONObject dataObject = jsonObject.optJSONObject("data");
               
	        	if (dataObject != null) {
	                appUpgrade = new AppUpgrade();
                    appUpgrade.setVersion(dataObject.optString("version"));
                    appUpgrade.setVersionName(dataObject.optString("versionName"));
                    appUpgrade.setContent(dataObject.optString("contents"));
                    appUpgrade.setDownLoadUrl(dataObject.optString("url"));
	        	}
            }

            HashMap<String, String> param = new HashMap<>();
            param.put("siteid","340100");

            param.get("siteid");

        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
