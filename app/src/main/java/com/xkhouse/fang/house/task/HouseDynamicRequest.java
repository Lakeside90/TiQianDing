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
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.XKDynamic;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 楼盘销售动态
* @author wujian  
* @date 2015-10-8 下午2:04:37
 */
public class HouseDynamicRequest {

	private String TAG = "HouseDynamicRequest";
	private RequestListener requestListener;
	
	private String pid;  //楼盘ID
	private String year;  //户型类别
	private int page;
	private int num;

	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<CommonType> yearList = new ArrayList<CommonType>();  
	private ArrayList<XKDynamic> dynamicList = new ArrayList<XKDynamic>();
	
	public HouseDynamicRequest(String pid, String year,
			int page, int num, RequestListener requestListener) {
		this.pid = pid;
		this.year = year;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	public void setData(String pid, String year, int page, int num) {
		this.pid = pid;
		this.year = year;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		yearList.clear();
		dynamicList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("pid", pid);
		params.put("year", year);
		params.put("page", String.valueOf(page));
		params.put("num", String.valueOf(num));
		String url = StringUtil.getRequestUrl(Constants.HOUSE_DYNAMIC_LIST, params);
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putSerializable("yearList", yearList);
                        	data.putSerializable("dynamicList", dynamicList);
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
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                JSONArray yearArray = dataObj.optJSONArray("years");
	        	if (yearArray != null && yearArray.length() > 0) {
	                for (int i = 0; i <= yearArray.length() - 1; i++) {
	                	JSONObject yearJson = yearArray.getJSONObject(i);
	                	
	                	CommonType year = new CommonType();
	                	year.setId(yearJson.optString("year"));
	                	year.setName(yearJson.optString("year"));
	                	yearList.add(year);
	                }
	        	}
	        	
	        	JSONArray dynamicArray = dataObj.optJSONArray("list");
	        	if (dynamicArray != null && dynamicArray.length() > 0) {
	                for (int i = 0; i <= dynamicArray.length() - 1; i++) {
	                	JSONObject dynamicJson = dynamicArray.getJSONObject(i);
	                	
	                	XKDynamic dynamic = new XKDynamic();
	                	dynamic.setContent(dynamicJson.optString("content"));
	                	dynamic.setDataDate(dynamicJson.optString("dataDate"));
	                	dynamic.setDynamicId(dynamicJson.optString("dynamicId"));
	                	
	                	dynamicList.add(dynamic);
	                }
		        }
	        	
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
