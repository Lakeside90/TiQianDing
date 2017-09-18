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
import com.xkhouse.fang.booked.entity.StoreAlbum;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 商户相册
* @author wujian  
* @date 2015-9-21 下午2:20:00
 */
public class StoreAlbumListRequest {

	private String TAG = StoreAlbumListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String business_id;
	private String category_id;
	private int page;	//分页索引
	private int num;	//每页个数

	private String code;	//返回状态
	private String msg;		//返回提示语
    private int count;

	private ArrayList<StoreAlbum> storeAlbumList = new ArrayList<>();

	public StoreAlbumListRequest(String business_id, String category_id, int page, int num, RequestListener requestListener) {
		this.business_id = business_id;
		this.category_id = category_id;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	public void setData(String business_id, String category_id, int page, int num) {
		this.business_id = business_id;
		this.category_id = category_id;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		storeAlbumList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("business_id", business_id);
		params.put("category_id", category_id);
        params.put("page", String.valueOf(page));
        params.put("num", String.valueOf(num));

        String url = StringUtil.getRequestUrl(Constants.STORE_ALBUM, params);
        Logger.d(TAG, url);
        
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = storeAlbumList;
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
	                	JSONObject albumJson = jsonArray.getJSONObject(i);
	                	
	                	StoreAlbum storeAlbum = new StoreAlbum();
                        storeAlbum.setId(albumJson.optString("id"));
                        storeAlbum.setImg(albumJson.optString("img"));
                        storeAlbum.setContent(albumJson.optString("content"));

	                	storeAlbumList.add(storeAlbum);
	                }
	        	}

//                count = Integer.parseInt(jsonObject.optJSONObject("data").optString("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
