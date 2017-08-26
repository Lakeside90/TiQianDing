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
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
* @Description:  删除，清空收藏
* @author wujian  
* @date 2015-11-3 下午3:39:53
 */
public class FavoriteEditRequest {

	private String TAG = "FavoriteAddRequest";
	private RequestListener requestListener;
	
	private String uId;		//用户id
	private String mId;  	//收藏对象id
	private int type; 	//类别（1新房、2二手房、3租房、4资讯）
	private int all;
	private String siteId; 	//城市id
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	
	public FavoriteEditRequest(String uId, String mId, int type, int all, String siteId, RequestListener requestListener) {
		
		this.uId = uId;
		this.mId = mId;
		this.type = type;
		this.all = all;
		this.siteId = siteId;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String uId, String mId, int type, int all, String siteId) {
		this.uId = uId;
		this.mId = mId;
		this.type = type;
		this.all = all;
		this.siteId = siteId;
	}
	
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", uId);
		params.put("mId", mId);
		params.put("type", String.valueOf(type));
		params.put("all", String.valueOf(all));
		params.put("siteId", siteId);
		
		String url = StringUtil.getRequestUrl(Constants.USER_FAVORITE_EDIT, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.obj = msg;
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        }else {
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
                msg = jsonObject.optString("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
