package com.xkhouse.fang.house.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.entity.XKCommunity;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 根据经纬度参数获取该经纬度范围内的出售小区房源统计数据 (二手房地图找房) 
* @author wujian  
* @date 2015-9-28 下午3:21:10
 */
public class MapOldHouseCommunityCountRequest {

	private String TAG = "MapOldHouseCommunityCountRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String searchContent; //筛选条件
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<XKCommunity> communityList = new ArrayList<XKCommunity>();
	
	public MapOldHouseCommunityCountRequest(String siteId, String searchContent, RequestListener requestListener) {
		this.siteId = siteId;
		this.searchContent = searchContent;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String searchContent) {
		this.siteId = siteId; 
		this.searchContent = searchContent;
	}
	
	public void doRequest(){
		communityList.clear();
		
		String url = Constants.OLD_HOUSE_COMMUNITY_COUNT + "?siteId=" + siteId;
		
		if(!StringUtil.isEmpty(searchContent)){
			url = url + searchContent;
		}
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = communityList;
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
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject communityJson = jsonArray.getJSONObject(i);
	                	
	                	XKCommunity community = new XKCommunity();
	                	community.setBuildName(communityJson.optString("buildname"));
	                	community.setCountNum(communityJson.optString("countNum"));
	                	community.setLatitude(communityJson.optString("latitude"));
	                	community.setLongitude(communityJson.optString("longitude"));
	                	
	                	communityList.add(community);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
