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
import com.xkhouse.fang.user.entity.MyCheckInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 我的买单
* @author wujian  
 */
public class MyCheckListRequest {

	private String TAG = MyCheckListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;
	private int num;		//条数
	private int page;		//页数

	private String code;	//返回状态
	private String msg;		//返回提示语
	private int count;

	private ArrayList<MyCheckInfo> checkList = new ArrayList<>();

	public MyCheckListRequest(String token, int page, int num, RequestListener requestListener) {
		
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
		checkList.clear();

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("pagenum", String.valueOf(num));
		params.put("page", String.valueOf(page));
		
		String url = StringUtil.getRequestUrl(Constants.USER_CHECK_LIST, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putSerializable("checkList", checkList);
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

						MyCheckInfo myCheckInfo = new MyCheckInfo();

                        myCheckInfo.setId(json.optString("id"));
                        myCheckInfo.setPay_number(json.optString("pay_number"));
						myCheckInfo.setOld_money(json.optString("old_money"));
						myCheckInfo.setMoney(json.optString("money"));
						myCheckInfo.setPay_time(json.optString("pay_time"));
						myCheckInfo.setTradeno(json.optString("tradeno"));
						myCheckInfo.setPay_type(json.optString("pay_type"));
						myCheckInfo.setStatus(json.optString("status"));
						myCheckInfo.setBusiness_id(json.optString("business_id"));
						myCheckInfo.setContent(json.optString("content"));
						myCheckInfo.setCreate_time(json.optString("create_time"));
						myCheckInfo.setUpdate_time(json.optString("update_time"));
						myCheckInfo.setComment_id(json.optString("comment_id"));
						myCheckInfo.setBusiness_name(json.optString("business_name"));
						myCheckInfo.setAverage_consump(json.optString("average_consump"));
						myCheckInfo.setConver_banner(json.optString("conver_banner"));
//                        myCheckInfo.setBusiness_label(json.optString(""));

                		checkList.add(myCheckInfo);
                	}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
