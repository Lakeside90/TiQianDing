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
import com.xkhouse.fang.house.entity.CommunityInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 获取学校划片小区列表
* @author wujian  
* @date 2016-8-8
 */
public class CommunityListRequest {

	private String TAG = "CommunityListRequest";
	private RequestListener requestListener;

	private String siteId;  //站点
	private String id;  //学校id
	private int page;	//分页索引
	private int num;	//每页个数

	private String code;	//返回状态
	private String msg;		//返回提示语
    private int count;
    private String schoolName;  //小区名称

	private ArrayList<CommunityInfo> communityList = new ArrayList<CommunityInfo>();

	public CommunityListRequest(String siteId, String id, int page, int num, RequestListener requestListener) {
		this.siteId = siteId;
		this.id = id;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String id, int page, int num) {
		this.siteId = siteId;
        this.id = id;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		communityList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
		params.put("id", id);

        String url = StringUtil.getRequestUrl(Constants.SCHOOL_COMMUNITY_LIST, params);
        Logger.d(TAG, url);
        
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                            Bundle data = new Bundle();
                            data.putSerializable("communityList", communityList);
                            data.putString("schoolName", schoolName);
                            message.setData(data);
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

                schoolName = jsonObject.optJSONObject("data").optJSONObject("schoolinfo").optString("name");
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("projectlistoldsale");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject communityJson = jsonArray.getJSONObject(i);

                        CommunityInfo communityInfo = new CommunityInfo();

                        communityInfo.setId(communityJson.optString("id"));
                        communityInfo.setProjectName(communityJson.optString("projectName"));
                        communityInfo.setCount(communityJson.optString("oldcount"));

	                	communityList.add(communityInfo);
	                }
	        	}

                count = Integer.parseInt(jsonObject.optJSONObject("data").optString("projectcount"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
