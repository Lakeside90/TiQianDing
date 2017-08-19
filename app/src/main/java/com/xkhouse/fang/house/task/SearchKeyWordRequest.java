package com.xkhouse.fang.house.task;

import android.os.Bundle;
import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 根据关键词匹配新房、二手房、租房、资讯，返回相应数据数量---全文匹配
* @author wujian  
* @date 2015-10-16 下午2:58:46
 */
public class SearchKeyWordRequest {

	private String TAG = "SearchKeyWordRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String keyword;  //站点 

	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private String newhouseCount;
	private String oldhouseCount;
	private String zufangCount;
	private String newsCount;
	
	
	public SearchKeyWordRequest(String siteId, String keyword, RequestListener requestListener) {
		this.siteId = siteId;
		this.keyword = keyword;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String keyword) {
		this.siteId = siteId; 
		this.keyword = keyword;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
		try {
			params.put("keyword", URLEncoder.encode(keyword, "utf-8"));
		} catch (Exception e) {
			params.put("keyword", keyword);
		}
		
		String url = StringUtil.getRequestUrl(Constants.SEARCH_KEY_WORD, params);
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putString("newhouseCount", newhouseCount);
                        	data.putString("oldhouseCount", oldhouseCount);
                        	data.putString("zufangCount", zufangCount);
                        	data.putString("newsCount", newsCount);
                        	
                        	message.setData(data);
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
                newhouseCount = dataObject.optString("newhouse");
                oldhouseCount = dataObject.optString("oldhouse");
                zufangCount = dataObject.optString("zufang");
                newsCount = dataObject.optString("news");
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
