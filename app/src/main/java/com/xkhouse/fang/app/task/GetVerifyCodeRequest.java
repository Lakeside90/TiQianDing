package com.xkhouse.fang.app.task;




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
* @Description: 短信验证码接口 
* @author wujian  
* @date 2015-9-24 下午6:10:03
 */
public class GetVerifyCodeRequest {

	private String TAG = "GetVerifyCodeRequest";

    private RequestListener requestListener;
	private String phone;       //手机号

    private String code;	//返回状态
    private String msg;				//返回提示语
	
	public GetVerifyCodeRequest(String phone, RequestListener requestListener) {
		this.phone = phone;
        this.requestListener = requestListener;
	}
	
	public void setData(String phone){
		this.phone = phone;
	}
	
	
	public void doRequest(){

        Map<String, String> params = new HashMap<String, String>();

        params.put("phone", phone);


        String url = StringUtil.getRequestUrl(Constants.GET_VERIFY_CODE, params);
        Logger.d(TAG, url);

        StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);

                parseResult(response);

                Message message = new Message();
                if(Constants.SUCCESS_CODE.equals(code)){
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
                code = jsonObject.optString("status");
                msg = jsonObject.optString("info");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
