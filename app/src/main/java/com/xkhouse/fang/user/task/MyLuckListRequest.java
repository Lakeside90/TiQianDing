package com.xkhouse.fang.user.task;

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
import com.xkhouse.fang.user.entity.MyBookedInfo;
import com.xkhouse.fang.user.entity.MyLuckInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 我的抽奖活动
* @author wujian  
 */
public class MyLuckListRequest {

	private String TAG = MyLuckListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;
	private int num;		//条数
	private int page;		//页数

	private String code;	//返回状态
	private String msg;		//返回提示语
	private int count;

	private ArrayList<MyLuckInfo> luckList = new ArrayList<>();

	public MyLuckListRequest(String token, int page, int num, RequestListener requestListener) {
		
		this.token = token;
		this.num = num;
		this.page = page;
		
		this.requestListener = requestListener;
	}
	
	
	public void setData(String token, int page, int num) {
		this.token = token;
		this.num = num;
		this.page = page;
	}
	
	
	public void doRequest(){
		luckList.clear();

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("pagesize", String.valueOf(num));
		params.put("page", String.valueOf(page));
		
		String url = StringUtil.getRequestUrl(Constants.USER_LUCK_LIST, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                            message.obj = luckList;
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
            	code = jsonObject.optString("status");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");


                //新房
                if(jsonArray != null && jsonArray.length() > 0){
                	for(int i = 0; i < jsonArray.length(); i++){
                		JSONObject json = jsonArray.getJSONObject(i);

						MyLuckInfo myCheckInfo = new MyLuckInfo();

                        myCheckInfo.setId(json.optString("id"));
                        myCheckInfo.setNumber(json.optString("number"));
                        myCheckInfo.setCreate_time(json.optString("create_time"));
                        myCheckInfo.setIs_winning(json.optString("is_winning"));
                        myCheckInfo.setStatus(json.optString("status"));
                        myCheckInfo.setImg(json.optString("img"));
                        myCheckInfo.setTitle(json.optString("title"));
                        myCheckInfo.setMid(json.optString("mid"));
                        myCheckInfo.setPrice(json.optString("price"));
                        myCheckInfo.setCount(json.optString("count"));
                        myCheckInfo.setJoin_count(json.optString("join_count"));
                        myCheckInfo.setStart_time(json.optString("start_time"));
                        myCheckInfo.setEnd_time(json.optString("end_time"));
                        myCheckInfo.setExpress_id(json.optString("express_id"));
                        myCheckInfo.setType(json.optString("type"));

                		luckList.add(myCheckInfo);
                	}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
