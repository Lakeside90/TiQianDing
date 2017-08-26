package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.entity.XKRecommendDetail;
import com.xkhouse.fang.user.entity.XKRecommendStatus;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 推荐客户详情
* @author wujian  
* @date 2015-11-12 下午5:09:46
 */

public class XKRecommendDetailRequest {

	private String TAG = "XKRecommendDetailRequest";
	private RequestListener requestListener;
	
	private String id;  	//详情页id
	private String uid;  	//用户id
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	private XKRecommendDetail recommendDetail;
	
	public XKRecommendDetailRequest(String id, String uid, RequestListener requestListener) {
		
		this.id = id;
		this.uid = uid;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String id, String uid) {
		this.id = id;
		this.uid = uid;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("uid", uid);
		String url = StringUtil.getRequestUrl(Constants.XKB_RECOMMEND_DETAIL, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code) && recommendDetail != null){
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        	message.obj = recommendDetail;
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
                 	msg = jsonObject.optString("data");
                 	return;
                 }
            	 
            	 JSONObject dataObj = jsonObject.getJSONObject("data");
            	 
            	 recommendDetail = new XKRecommendDetail();
            	 recommendDetail.setAveragePrice(dataObj.optString("averagePrice"));
            	 recommendDetail.setCity(dataObj.optString("city"));
            	 recommendDetail.setDate(dataObj.optString("dataTime"));
            	 recommendDetail.setName(dataObj.optString("rName"));
            	 recommendDetail.setProject(dataObj.optString("projectStr"));
            	 recommendDetail.setPropertyType(dataObj.optString("propertyType"));
            	 recommendDetail.setRemark(dataObj.optString("remarks"));
            	 recommendDetail.setStatus(dataObj.optString("status"));
            	 recommendDetail.setPhone(dataObj.optString("rPhone"));
            	 
            	 JSONArray dataArray = dataObj.getJSONArray("list");
            	 if(dataArray != null){
            		 ArrayList<XKRecommendStatus> statusList = new ArrayList<XKRecommendStatus>();
            		 for (int i = 0; i < dataArray.length(); i++) {
            			 JSONObject statusObj = dataArray.getJSONObject(i);
            			 XKRecommendStatus recommendStatus = new XKRecommendStatus();
            			 recommendStatus.setContent(statusObj.optString("cont"));
            			 recommendStatus.setDate(statusObj.optString("time"));
            			 recommendStatus.setStatus(statusObj.optString("type"));
            			 statusList.add(recommendStatus);
					}
            		 recommendDetail.setStatusList(statusList);
            	 }
            	 
            	 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
