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
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/** 
 * @Description: 获取城市站点列表 
 * @author wujian  
 * @date 2015-9-6 下午4:53:03  
 */
public class SiteListRequest {

	private String TAG = "SiteListRequest";
	private RequestListener requestListener;
	
	private String code;	//返回状态
	private String msg;				//返回提示语
	
	private ArrayList<Site> siteList = new ArrayList<Site>();
	
	
	public SiteListRequest(RequestListener requestListener) {
		this.requestListener = requestListener;
	}
	
	public void doRequest(){
		siteList.clear();
		Logger.d(TAG, Constants.SITE_LIST);
		StringRequest request = new StringRequest(Constants.SITE_LIST,  new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                if(Constants.SUCCESS_CODE.equals(code)){
                	message.obj = siteList;
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
            	code = jsonObject.optString("status");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i < jsonArray.length(); i++) {
	                	JSONObject siteJson = jsonArray.getJSONObject(i);
	                	
	                	Site site = new Site();
	                	site.setArea(siteJson.optString("name"));
	                	site.setDomain(Constants.HOST);
	                	site.setSiteId(siteJson.optString("id"));
	                	site.setTitle(siteJson.optString("name"));
	                	site.setLongitude("");
	                	site.setLatitude("");
	                	site.setIsHot(siteJson.optString("is_hot"));
	                	
	                	siteList.add(site);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private String getDomain(String str){
		String domain = "http://m.hfhouse.com";
		if(str.contains("www")){
			domain = str.replace("www", "m");
		}else{
			domain = str.replace("http://", "http://m.");
		}
		return domain;
	}
}
