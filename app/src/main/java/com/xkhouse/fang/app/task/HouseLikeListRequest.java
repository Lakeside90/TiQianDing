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
import com.xkhouse.fang.app.entity.House;
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
 * @Description: 猜你喜欢（楼盘推荐） 
 * @author wujian  
 * @date 2015-9-6 下午2:35:54  
 */
public class HouseLikeListRequest {

	private String TAG = "HouseLikeListRequest";
	private RequestListener requestListener;
	
	private int num;	//返回数量，默认为6
	private int page;	//分页，默认为1
	private String siteId;	//站点ID
	
	private String code;	//返回状态
	private String msg;				//返回提示语
	
	private ArrayList<House> houseList = new ArrayList<House>();


    public HouseLikeListRequest(){
        super();
    }


	public HouseLikeListRequest(String siteId, int num, int page, RequestListener requestListener) {
		this.siteId = siteId; 
		this.num = num;
		this.page = page;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, int num, int page) {
		this.siteId = siteId; 
		this.num = num;
		this.page = page;
	}
	
	public void doRequest(){
		houseList.clear();
        
		Map<String, String> params = new HashMap<String, String>();
		params.put("num", String.valueOf(num));
		params.put("page", String.valueOf(page));
		params.put("siteId", siteId);
		String url = StringUtil.getRequestUrl(Constants.HOUSE_LIKE, params);
		Logger.d(TAG, url);
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Logger.d(TAG, response);

				parseResult(response);

				Message message = new Message();
                Bundle data = new Bundle();

				if (Constants.SUCCESS_CODE.equals(code)) {
                    AppCache.writeHouseLikeJson(siteId, response);
                    data.putSerializable("houseList", houseList);
                    data.putString("siteId", siteId);
                    message.setData(data);
					message.what = Constants.SUCCESS_DATA_FROM_NET;
				} else {
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
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject houseJson = jsonArray.getJSONObject(i);
	                	
	                	House house = new House();
	                	house.setAveragePrice(houseJson.optString("averagePrice"));
	                	house.setEffectPhoto(houseJson.optString("effectPhoto"));
	                	house.setLatitude(houseJson.optString("latitude"));
	                	house.setLongitude(houseJson.optString("longitude"));
	                	house.setProjectId(houseJson.optString("projectId"));
	                	house.setProjectName(houseJson.optString("projectName"));
	                	house.setHaveCommission(houseJson.optString("haveCommission"));
	                	house.setAreaName(houseJson.optString("areaName"));
	                	house.setPropertyType(houseJson.optString("propertyType"));
	                	house.setSaleState(houseJson.optString("saleState"));
	                	house.setProjectFeature(houseJson.optString("projectFeature"));
	                	house.setHouseType(houseJson.optString("houseType"));
	                	houseList.add(house);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public ArrayList<House> getHouseList() {
        return houseList;
    }
}
