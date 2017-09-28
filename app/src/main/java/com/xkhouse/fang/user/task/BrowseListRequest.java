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
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 我的浏览
* @author wujian  
* @date 2015-11-3 上午9:49:08
 */
public class BrowseListRequest {

	private String TAG = BrowseListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;  	//用户id
	private int num;		//条数
	private int page;		//页数

	private String code;	//返回状态
	private String msg;		//返回提示语
	private int count;

	private ArrayList<BookedInfo> bookedList = new ArrayList<>();

	public BrowseListRequest(String token, int page, int num, RequestListener requestListener) {
		
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
		
		String url = StringUtil.getRequestUrl(Constants.USER_BROWSE_LIST, params);
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
                JSONArray favList = dataObj.optJSONArray("list");
                if(favList != null && favList.length() > 0) {
                    for (int i = 0; i < favList.length(); i++) {
                        JSONObject favJson = favList.getJSONObject(i);

                        BookedInfo favoriteInfo = new BookedInfo();

                        favoriteInfo.setBusinessId(favJson.optString("business_id"));
                        favoriteInfo.setBusinessName(favJson.optString("business_name"));
                        favoriteInfo.setAverageConsump(favJson.optString("average_consump"));
                        favoriteInfo.setCover_banner(favJson.optString("cover_banner"));
                        favoriteInfo.setBusinessAddress(favJson.optString("business_address"));

                        JSONArray labelArray = favJson.optJSONArray("business_label");
                        if (labelArray != null && labelArray.length() > 0) {
                            String[] labels = new String[labelArray.length()];
                            for (int j = 0; j < labelArray.length(); j++) {
                                labels[j] = labelArray.optString(j);
                            }
                            favoriteInfo.setBusinessLabel(labels);
                        }
                        bookedList.add(favoriteInfo);
                    }
                }
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
