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
* @Description: 系统消息，我的收藏 数目
* @author wujian  
* @date 2015-11-2 上午9:40:21
 */
public class MsgFavoriteNumRequest {

	private String TAG = "MsgFavoriteNumRequest";
	private RequestListener requestListener;
	
	private String uId;		//用户id
	private String type;	//类型（1系统消息、2我的收藏）
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private String count;	//条数
	
	
	public MsgFavoriteNumRequest(String uId, String type, RequestListener requestListener) {
		this.uId = uId;
		this.type = type;
		this.requestListener = requestListener;
	}
	
	public void setData(String uId, String type){
		this.uId = uId;
		this.type = type;
	}
	
	public void doRequest(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", uId);
		params.put("type", type);
		String url = StringUtil.getRequestUrl(Constants.USER_NEWS_FAVORITE_COUNT, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = count;
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
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                count = dataObj.optString("count");
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
