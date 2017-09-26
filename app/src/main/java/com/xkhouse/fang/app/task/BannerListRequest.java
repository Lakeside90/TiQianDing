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
import com.xkhouse.fang.app.entity.Banner;
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 首页轮询图
 */
public class BannerListRequest {

	private String TAG = BannerListRequest.class.getSimpleName();
	private RequestListener requestListener;
	
	private String siteId;

	private String code;	//返回状态
	private String msg;				//返回提示语
	
	private ArrayList<Banner> bannerList = new ArrayList<>();

    public BannerListRequest(){
        super();
    }
	
	public BannerListRequest(String siteId,  RequestListener requestListener) {
		this.siteId = siteId;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId) {
		this.siteId = siteId;
	}
	
	public void doRequest(){
		bannerList.clear();
		
		String url = Constants.INDEX_BANNER_LIST + "?cityId=" + siteId;
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
                    AppCache.writeHomeBannerJson(siteId, response);

                	data.putSerializable("bannerList", bannerList);
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
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i < jsonArray.length(); i++) {
	                	JSONObject bannerJson = jsonArray.getJSONObject(i);

                        Banner banner = new Banner();
                        banner.setId(bannerJson.optString("id"));
                        banner.setType(bannerJson.optString("type"));
                        banner.setTitle(bannerJson.optString("title"));
                        banner.setImgurl(bannerJson.optString("imgurl"));
                        banner.setBusiness_id(bannerJson.optString("business_id"));
                        banner.setLottery_id(bannerJson.optString("lottery_id"));
                        banner.setLink(bannerJson.optString("link"));
                        banner.setCreate_time(bannerJson.optString("create_time"));
                        banner.setUpdate_time(bannerJson.optString("update_time"));
                        banner.setStatus(bannerJson.optString("status"));
                        banner.setCity_id(bannerJson.optString("city_id"));
	                	
	                	bannerList.add(banner);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public ArrayList<Banner> getBannerList() {
        return bannerList;
    }
}
