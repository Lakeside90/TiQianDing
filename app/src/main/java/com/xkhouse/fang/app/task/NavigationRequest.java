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
import com.xkhouse.fang.app.entity.XKNavigation;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 获取导航栏目列表
* @author wujian  
* @date 2015-9-25 下午4:31:36
 */
public class NavigationRequest {

	private String TAG = "NavigationRequest";
	private RequestListener requestListener;
	
	private String siteId;	//站点ID
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<XKNavigation> homeList = new ArrayList<XKNavigation>();		//首页
	private ArrayList<XKNavigation> moreList = new ArrayList<XKNavigation>();		//更多
	private ArrayList<XKNavigation> appList = new ArrayList<XKNavigation>();		//app下载


    public NavigationRequest(){
        super();
    }


	public NavigationRequest(String siteId, RequestListener requestListener) {
		this.siteId = siteId; 
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId) {
		this.siteId = siteId; 
	}
	
	public void doRequest(){
		homeList.clear();
		moreList.clear();
		appList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
        params.put("siteId", siteId);
        String url = StringUtil.getRequestUrl(Constants.NAVIGATION_LIST, params);
        Logger.d(TAG, url);
	            
        StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                Bundle data = new Bundle();
                if(Constants.SUCCESS_CODE.equals(code)){
                    AppCache.writeNavigationJson(siteId, response);

                	data.putSerializable("home", homeList);
                	data.putSerializable("more", moreList);
                	data.putSerializable("app", appList);
                    data.putString("siteId", siteId);
                	message.setData(data);
                	message.what = Constants.SUCCESS_DATA_FROM_NET;
                }else{
                   data.putString("msg", msg);
                    data.putString("siteId", siteId);
                    message.setData(data);
                   message.what = Constants.NO_DATA_FROM_NET;
                }
                requestListener.sendMessage(message);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            	Logger.e(TAG, error.toString());

                Message message = new Message();
                Bundle data = new Bundle();
                data.putString("siteId", siteId);
                message.setData(data);
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
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                JSONArray homeArray = dataObj.optJSONArray("home");
	        	if (homeArray != null && homeArray.length() > 0) {
	                for (int i = 0; i < homeArray.length(); i++) {
	                	JSONObject homeJson = homeArray.getJSONObject(i);
	                	
	                	XKNavigation home = new XKNavigation();
	                	home.setLink(homeJson.optString("link"));
	                	home.setMethod(homeJson.optString("method"));
	                	home.setNavId(homeJson.optString("navId"));
	                	home.setPhotoUrl(homeJson.optString("photoUrl"));
	                	home.setTitle(homeJson.optString("title"));
	                	homeList.add(home);
	                }
	        	}
	        	
	        	JSONArray moreArray = dataObj.optJSONArray("more");
	        	if (moreArray != null && moreArray.length() > 0) {
	                for (int i = 0; i < moreArray.length(); i++) {
	                	JSONObject moreJson = moreArray.getJSONObject(i);
	                	
	                	XKNavigation more = new XKNavigation();
	                	more.setLink(moreJson.optString("link"));
	                	more.setMethod(moreJson.optString("method"));
	                	more.setNavId(moreJson.optString("navId"));
	                	more.setPhotoUrl(moreJson.optString("photoUrl"));
	                	more.setTitle(moreJson.optString("title"));
	                	moreList.add(more);
	                }
	        	}
	        	
	        	JSONArray appArray = dataObj.optJSONArray("app");
	        	if (appArray != null && appArray.length() > 0) {
	                for (int i = 0; i < appArray.length(); i++) {
	                	JSONObject appJson = appArray.getJSONObject(i);
	                	
	                	XKNavigation app = new XKNavigation();
	                	app.setLink(appJson.optString("link"));
	                	app.setMethod(appJson.optString("method"));
	                	app.setNavId(appJson.optString("navId"));
	                	app.setPhotoUrl(appJson.optString("photoUrl"));
	                	app.setTitle(appJson.optString("title"));
	                	appList.add(app);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public ArrayList<XKNavigation> getHomeList() {
        return homeList;
    }

    public ArrayList<XKNavigation> getMoreList() {
        return moreList;
    }

    public ArrayList<XKNavigation> getAppList() {
        return appList;
    }
}
