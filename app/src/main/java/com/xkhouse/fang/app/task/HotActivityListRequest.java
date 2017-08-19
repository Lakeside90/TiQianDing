package com.xkhouse.fang.app.task;

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
import com.xkhouse.fang.app.entity.SaleHouse;
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
* @Description: 首页热门活动 
* @author wujian  
* @date 2015-10-14 下午4:32:14
 */
public class HotActivityListRequest {

	private String TAG = "HotActivityListRequest";
	private RequestListener requestListener;
	
	
	private String siteId;	//站点ID
	
	private String code;	//返回状态
	private String msg;				//返回提示语
	
	private ArrayList<SaleHouse> houseList = new ArrayList<SaleHouse>();
	private double startTime;	//活动开始时间
	private double endTime;		//活动开始时间
	private double nowTime;		//服务器当前时间
	
	
	
	public HotActivityListRequest(String siteId, RequestListener requestListener) {
		this.siteId = siteId; 
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId) {
		this.siteId = siteId; 
	}
	
	public void doRequest(){
		houseList.clear();
        
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
		String url = StringUtil.getRequestUrl(Constants.HOT_ACTIVITY_LIST, params);
		Logger.d(TAG, url);
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Logger.d(TAG, response);

				parseResult(response);

				Message message = new Message();
                Bundle data = new Bundle();
				if (Constants.SUCCESS_CODE.equals(code)) {
					data.putSerializable("houseList", houseList);
					data.putDouble("startTime", startTime);
					data.putDouble("endTime", endTime);
					data.putDouble("nowTime", nowTime);
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
                JSONObject dataObj = jsonObject.optJSONObject("data");
                String activityId = dataObj.optString("activityId");
                startTime = dataObj.optDouble("startTime");
                endTime = dataObj.optDouble("endTime");
                nowTime = dataObj.optDouble("nowTime");
                JSONArray jsonArray = dataObj.optJSONArray("project");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject houseJson = jsonArray.getJSONObject(i);
	                	
	                	SaleHouse house = new SaleHouse();
	                	house.setName(houseJson.optString("projectName"));
	                	house.setDiscountPrice(houseJson.optString("discountPrice"));
	                	house.setPrice(houseJson.optString("totalPrice"));
	                	house.setNum(houseJson.optString("buildNo") + "#" +houseJson.optString("roomNo"));
	                	house.setArea(houseJson.optString("buildArea"));
	                	house.setPhotoUrl(houseJson.optString("photoUrl"));
	                	house.setId(activityId);
	                	houseList.add(house);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	
}
