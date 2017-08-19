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
import com.xkhouse.fang.house.entity.XKSearchResult;
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
* @Description: 根据关键词匹配新房楼盘名、二手房小区名、租房小区名返回---只匹配楼盘名、小区名
* @author wujian  
* @date 2015-10-16 下午2:58:46
 */
public class SearchProjectNameRequest {

	private String TAG = "SearchProjectNameRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String keyword;  //站点 

	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<XKSearchResult> resultList = new ArrayList<XKSearchResult>();
	
	public SearchProjectNameRequest(String siteId, String keyword, RequestListener requestListener) {
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
			params.put("keyword", URLEncoder.encode(keyword, "utf-8"));
		} catch (Exception e) {
			params.put("keyword", keyword);
		}
		
		String url = StringUtil.getRequestUrl(Constants.SEARCH_PROJECT_NAME, params);
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
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
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject resultJson = jsonArray.getJSONObject(i);
	                	
	                	XKSearchResult xkResult = new XKSearchResult();
	                	xkResult.setProjectName(resultJson.optString("projectName"));
	                	xkResult.setType(resultJson.optString("type"));
	                	
	                	resultList.add(xkResult);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
