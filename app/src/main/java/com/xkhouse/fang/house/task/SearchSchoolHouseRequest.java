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
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 根据关键词学区名
* @author wujian  
* @date 2016-6-22
 */
public class SearchSchoolHouseRequest {

	private String TAG = "SearchSchoolHouseRequest";
	private RequestListener requestListener;

	private String siteId;  //站点
	private String keyword;

	private String code;	//返回状态
	private String msg;		//返回提示语

	private ArrayList<CommonType> resultList = new ArrayList<CommonType>();

	public SearchSchoolHouseRequest(String siteId, String keyword, RequestListener requestListener) {
		this.siteId = siteId;
		this.keyword = keyword;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String keyword) {
		this.siteId = siteId; 
		this.keyword = keyword;
	}
	
	public void doRequest(){
		resultList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
		try {
			params.put("key", URLEncoder.encode(keyword, "utf-8"));
		} catch (Exception e) {
			params.put("key", keyword);
		}
		
		String url = StringUtil.getRequestUrl(Constants.SEARCH_SCHOOL_NAME, params);
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.obj = resultList;
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
	                	JSONObject resultJson = jsonArray.getJSONObject(i);

                        CommonType type = new CommonType();
                        type.setId(resultJson.optString("id"));
                        type.setName(resultJson.optString("name"));

                        resultList.add(type);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
