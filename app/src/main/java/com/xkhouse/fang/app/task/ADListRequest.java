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
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 首页轮询图广告，专题推荐 接口 
* @author wujian  
* @date 2015-10-15 下午4:51:51
 */
public class ADListRequest {

	private String TAG = "ADListRequest";
	private RequestListener requestListener;
	
	private String siteId;
	private String moduleId;    //188: 专题推荐  187：广告轮询图
	
	private String code;	//返回状态
	private String msg;				//返回提示语
	
	private ArrayList<XKAd> adList = new ArrayList<XKAd>();

    public ADListRequest(){
        super();
    }
	
	public ADListRequest(String siteId, String moduleIdm, RequestListener requestListener) {
		this.siteId = siteId;
		this.moduleId = moduleIdm;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String moduleIdm) {
		this.siteId = siteId;
		this.moduleId = moduleIdm;
	}
	
	public void doRequest(){
		adList.clear();
		
		String url = Constants.INDEX_AD_LIST + "?siteId=" + siteId + "&moduleId=" + moduleId;
		Logger.d(TAG, url);
		
		StringRequest request = new StringRequest(url,  new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
                parseResult(response);
                
                Message message = new Message();
                Bundle data = new Bundle();

                if(Constants.SUCCESS_CODE_OLD.equals(code)){
                    //缓存json到本地
                    if ("187".equals(moduleId)){
                        AppCache.writeHomeAdJson(siteId, response);
                    }else if("188".equals(moduleId)){
                        AppCache.writeHomeTopicJson(siteId, response);
                    }

                	data.putSerializable("adList", adList);
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
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i < jsonArray.length(); i++) {
	                	JSONObject adJson = jsonArray.getJSONObject(i);
	                	
	                	XKAd ad = new XKAd();
	                	ad.setNewsId(adJson.optString("newsId"));
	                	ad.setNewsUrl(adJson.optString("newsUrl"));
	                	ad.setPhotoUrl(adJson.optString("photoUrl"));
	                	ad.setRemark(adJson.optString("remark"));
	                	ad.setTitle(adJson.optString("title"));
	                	
	                	adList.add(ad);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public ArrayList<XKAd> getAdList() {
        return adList;
    }
}
