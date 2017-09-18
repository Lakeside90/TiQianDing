package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
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
 * 修改账号的手机号
 */
public class MobileResetRequest {

	private String TAG = MobileResetRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;
	private String oldphone;
	private String oldverif;
	private String newphone;
	private String newverif;

	private String code;	//返回状态
	private String msg;		//返回提示语


	public MobileResetRequest(String token, String oldphone, String oldverif, String newphone, String newverif,
							  RequestListener requestListener) {
		
		this.token = token;
		this.oldphone = oldphone;
		this.oldverif = oldverif;
		this.newphone = newphone;
		this.newverif = newverif;

		this.requestListener = requestListener;
	}
	
	
	public void setData(String oldphone, String oldverif, String newphone, String newverif) {
		this.oldphone = oldphone;
		this.oldverif = oldverif;
		this.newphone = newphone;
		this.newverif = newverif;
	}
	
	public void doRequest(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("oldphone", oldphone);
		params.put("oldverif", oldverif);
		params.put("newphone", newphone);
		params.put("newverif", newverif);

		String url = StringUtil.getRequestUrl(Constants.USER_MOBILE_RESET, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
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
                });

       
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
                msg = jsonObject.optString("msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
