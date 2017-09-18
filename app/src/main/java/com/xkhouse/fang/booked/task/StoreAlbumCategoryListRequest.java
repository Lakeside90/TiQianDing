package com.xkhouse.fang.booked.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.CommentInfo;
import com.xkhouse.fang.booked.entity.StoreAlbumCategory;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 商户相册分类
* @author wujian  
* @date 2015-9-21 下午2:20:00
 */
public class StoreAlbumCategoryListRequest {

	private String TAG = StoreAlbumCategoryListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String business_id;

	private String code;	//返回状态
	private String msg;		//返回提示语
    private int count;

	private ArrayList<StoreAlbumCategory> categoryList = new ArrayList<>();

	public StoreAlbumCategoryListRequest(String business_id, RequestListener requestListener) {
		this.business_id = business_id;
		this.requestListener = requestListener;
	}
	
	public void setData(String business_id) {
		this.business_id = business_id;
	}
	
	public void doRequest(){
		categoryList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("business_id", business_id);

        String url = StringUtil.getRequestUrl(Constants.STORE_ALBUM_CATEGORY, params);
        Logger.d(TAG, url);
        
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = categoryList;
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
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject categoryJson = jsonArray.getJSONObject(i);
	                	
	                	StoreAlbumCategory storeAlbumCategory = new StoreAlbumCategory();
                        storeAlbumCategory.setCategory_id(categoryJson.optString("category_id"));
                        storeAlbumCategory.setTitle(categoryJson.optString("title"));
	                	categoryList.add(storeAlbumCategory);
	                }
	        	}
//                count = Integer.parseInt(jsonObject.optJSONObject("data").optString("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
