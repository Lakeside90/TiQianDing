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
import com.xkhouse.fang.house.entity.CustomHouse;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 获取定制购房列表
* @author wujian  
* @date 2015-9-21 下午2:20:00
 */
public class CustomHouseListRequest {

	private String TAG = "CustomHouseListRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String uid;  //用户id
	private int page;	//分页索引
	private int num;	//每页个数

	private String code;	//返回状态
	private String msg;		//返回提示语
    private int count;

	private ArrayList<CustomHouse> customHouseList = new ArrayList<CustomHouse>();
	
	public CustomHouseListRequest(String siteId, String uid, int page, int num, RequestListener requestListener) {
		this.siteId = siteId;
		this.uid = uid;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String uid, int page, int num) {
		this.siteId = siteId;
        this.uid = uid;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		customHouseList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
        params.put("page", String.valueOf(page));
        params.put("num", String.valueOf(num));
        if (!StringUtil.isEmpty(uid)){
            params.put("uid", String.valueOf(uid));
        }
        String url = StringUtil.getRequestUrl(Constants.CUSTOM_HOUSE_LIST, params);
        Logger.d(TAG, url);
        
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.obj = customHouseList;
                            message.arg1 = count;
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
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject customJson = jsonArray.getJSONObject(i);
	                	
	                	CustomHouse customHouse = new CustomHouse();
	                	customHouse.setAreaName(customJson.optString("areaName"));
	                	customHouse.setCustomId(customJson.optString("customId"));
	                	customHouse.setFeature(customJson.optString("feature"));
	                	customHouse.setPriceRange(customJson.optString("priceRange"));
	                	customHouse.setReply(customJson.optString("reply"));
	                	customHouse.setRequriement(customJson.optString("remark"));
	                	customHouse.setSpace(customJson.optString("areaRange"));
                        customHouse.setPhone(customJson.optString("phone"));
                        customHouse.setTime(customJson.optString("time"));
                        customHouse.setStatus(customJson.optString("status"));

	                	customHouseList.add(customHouse);
	                }
	        	}

                count = Integer.parseInt(jsonObject.optJSONObject("data").optString("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
