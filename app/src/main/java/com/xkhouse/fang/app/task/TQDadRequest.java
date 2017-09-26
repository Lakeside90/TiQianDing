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
import com.xkhouse.fang.app.entity.TQDad;
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 首页广告
 */
public class TQDadRequest {

	private String TAG = TQDadRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String siteId;
	private String position;    //广告位置，（1首页广告，其他待定）

	private String code;	//返回状态
	private String msg;				//返回提示语

	private TQDad ad = new TQDad();

    public TQDadRequest() {
        super();
    }

	public TQDadRequest(String siteId, String position, RequestListener requestListener) {
		this.siteId = siteId;
		this.position = position;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String position) {
		this.siteId = siteId;
		this.position = position;
	}
	
	public void doRequest(){
		
		String url = Constants.TQD_AD + "?cityId=" + siteId + "&position=" + position;
		Logger.d(TAG, url);
		
		StringRequest request = new StringRequest(url,  new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
                parseResult(response);
                
                Message message = new Message();
                Bundle data = new Bundle();

                if(Constants.SUCCESS_CODE.equals(code)){
                    //缓存json到本地
                    if ("1".equals(position)){
                        AppCache.writeIndexAdJson(siteId, response);
                    }

                	data.putSerializable("ad", ad);
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
            	code = jsonObject.optString("status");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }

                JSONObject adJson = jsonObject.getJSONObject("data");

                ad.setId(adJson.optString("id"));
                ad.setType(adJson.optString("type"));
                ad.setCity_id(adJson.optString("city_id"));
                ad.setTitle(adJson.optString("title"));
                ad.setImgurl(adJson.optString("imgurl"));
                ad.setLink(adJson.optString("link"));
                ad.setBusiness_id(adJson.optString("business_id"));
                ad.setLottery_id(adJson.optString("lottery_id"));
                ad.setPosition(adJson.optString("position"));
                ad.setCreate_time(adJson.optString("create_time"));
                ad.setUpdate_time(adJson.optString("update_time"));
                ad.setStatus(adJson.optString("status"));
                ad.setSort(adJson.optString("sort"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public TQDad getAd() {
        return ad;
    }
}
