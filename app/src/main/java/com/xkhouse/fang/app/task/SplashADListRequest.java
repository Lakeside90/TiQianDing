package com.xkhouse.fang.app.task;

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

/**
* @Description: 启动页接口 
* @author wujian  
* @date 2015-10-15 下午4:51:51
 */
public class SplashADListRequest {

	private String TAG = "ADListRequest";
	private RequestListener requestListener;
	
	private String siteId;
	private String typeId;    //2414
	
	private String code;	//返回状态
	private String msg;				//返回提示语
	
	private String adUrl;
	
	
	public SplashADListRequest(String siteId, String typeId, RequestListener requestListener) {
		this.siteId = siteId;
		this.typeId = typeId;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String typeId) {
		this.siteId = siteId;
		this.typeId = typeId;
	}
	
	public void doRequest(){
		
		String url = Constants.SPLASH_AD_LIST + "?siteId=" + siteId + "&typeId=" + typeId + "&size=720x1036";
		Logger.d(TAG, url);
		
		StringRequest request = new StringRequest(url,  new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                if(Constants.SUCCESS_CODE_OLD.equals(code)){
                	message.obj = adUrl;
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
            	code = jsonObject.optString("code");
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                adUrl = jsonObject.optString("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
}
