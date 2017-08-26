package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
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

/**
* @Description: 推荐客户--再次推荐
* @author wujian  
* @date 2016-7-21
 */
public class RecommendAgainRequest {

	private String TAG = "RecommendAgainRequest";
	private RequestListener requestListener;

    private String uid;
    private String id;
	private String code;	//返回状态
	private String msg;		//返回提示语


	public RecommendAgainRequest(String uid, String id, RequestListener requestListener) {
		
        this.uid = uid;
        this.id = id;
		this.requestListener = requestListener;
	}

	
	public void doRequest(){

		String url = Constants.XKB_RECOMMEND_AGIAN + "?uid="+uid+"&id="+id;

		StringRequest request = new StringRequest(Request.Method.GET, url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        	message.obj = msg;
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
                })
            {
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
