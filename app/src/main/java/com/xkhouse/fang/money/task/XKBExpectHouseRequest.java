package com.xkhouse.fang.money.task;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description: 星空宝期望楼盘
* @author wujian  
* @date 2015-10-15 上午11:27:58
 */
public class XKBExpectHouseRequest {

	private String TAG = "XKBExpectHouseRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<CommonType> areaHouseList = new ArrayList<CommonType>();
	
	public XKBExpectHouseRequest(String siteId, RequestListener requestListener) {
		this.siteId = siteId;
		this.requestListener = requestListener;
	}
	
	public void doRequest(){
		areaHouseList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
		
		String url = StringUtil.getRequestUrl(Constants.XKB_EXPECT_HOUSE_LIST, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.obj = areaHouseList;
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
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	
	                	JSONObject dataJson = jsonArray.getJSONObject(i);
	                	
	                	//地区
	                	JSONObject areaJson = dataJson.optJSONObject("area");
	                	CommonType area = new CommonType();
	                	area.setId(areaJson.optString("aid"));
	                	area.setName(areaJson.optString("aName"));
	                	
	                	//楼盘
	                	JSONArray houseArray = dataJson.optJSONArray("project");
	                	if (houseArray != null && houseArray.length() > 0) {
	                		List<CommonType> houseList = new ArrayList<CommonType>();
	                		
	    	                for (int j = 0; j <= houseArray.length() - 1; j++) {
	    	                	
	    	                	JSONObject houseJson = houseArray.getJSONObject(j);
	    	                	CommonType house = new CommonType();
	    	                	house.setId(houseJson.optString("pid"));
	    	                	house.setName(houseJson.optString("pName"));
	    	                	houseList.add(house);
    	                	}
	    	                area.setChild(houseList);
    	                }
	                	
	                	areaHouseList.add(area);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
