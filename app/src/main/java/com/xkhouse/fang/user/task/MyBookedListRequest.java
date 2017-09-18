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
import com.xkhouse.fang.user.entity.FavHouse;
import com.xkhouse.fang.user.entity.FavNews;
import com.xkhouse.fang.user.entity.MyBookedInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 我的预定
* @author wujian  
 */
public class MyBookedListRequest {

	private String TAG = MyBookedListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;
	private int num;		//条数
	private int page;		//页数

	private String code;	//返回状态
	private String msg;		//返回提示语
	private int count;

	private ArrayList<MyBookedInfo> bookedList = new ArrayList<>();

	public MyBookedListRequest(String token, int page, int num, RequestListener requestListener) {
		
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
		bookedList.clear();

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("pagenum", String.valueOf(num));
		params.put("page", String.valueOf(page));
		
		String url = StringUtil.getRequestUrl(Constants.USER_BOOKED_LIST, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putSerializable("bookedList", bookedList);
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
            	code = jsonObject.optString("status");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");

                count = Integer.parseInt(dataObj.optString("count"));

                //新房
                JSONArray jsonArray = dataObj.optJSONArray("list");
                if(jsonArray != null && jsonArray.length() > 0){
                	for(int i = 0; i < jsonArray.length(); i++){
                		JSONObject json = jsonArray.getJSONObject(i);

						MyBookedInfo myBookedInfo = new MyBookedInfo();

						myBookedInfo.setId(json.optString("id"));
						myBookedInfo.setOrder_number(json.optString("order_number"));
						myBookedInfo.setMember_name(json.optString("member_name"));
						myBookedInfo.setMember_phone(json.optString("member_phone"));
						myBookedInfo.setMoney(json.optString("money"));
						myBookedInfo.setPay_time(json.optString("pay_time"));
						myBookedInfo.setTradeno(json.optString("tradeno"));
						myBookedInfo.setPay_type(json.optString("pay_type"));
						myBookedInfo.setStatus(json.optString("status"));
						myBookedInfo.setPeople_num(json.optString("people_num"));
						myBookedInfo.setMember_remarks(json.optString("member_remarks"));
						myBookedInfo.setBusiness_id(json.optString("business_id"));
						myBookedInfo.setBooking_id(json.optString("booking_id"));
						myBookedInfo.setBusiness_remarks(json.optString("business_remarks"));
						myBookedInfo.setCreate_time(json.optString("create_time"));
						myBookedInfo.setUpdate_time(json.optString("update_time"));
						myBookedInfo.setComment_id(json.optString("comment_id"));
						myBookedInfo.setUse_time(json.optString("use_time"));
						myBookedInfo.setBusiness_name(json.optString("business_name"));

                		bookedList.add(myBookedInfo);
                	}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
