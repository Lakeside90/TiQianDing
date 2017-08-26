package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;

/**
* @Description: 预约看房
* @author wujian
 */
public class KanFangRequest {

	private String TAG = "KanFangRequest";
	private RequestListener requestListener;

	private String siteId;  	//站点
	private String projectName;  	//楼盘名
	private String phone;  	//手机号
	private String verifyCode; 	//手机验证码

	private String code;	//返回状态
	private String msg;		//返回提示语


	public KanFangRequest(String siteId, String projectName, String phone, String verifyCode, RequestListener requestListener) {
		
		this.siteId = siteId;
		this.projectName = projectName;
		this.phone = phone;
		this.verifyCode = verifyCode;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String siteId, String projectName, String phone, String verifyCode) {
		this.siteId = siteId;
        this.projectName = projectName;
        this.phone = phone;
        this.verifyCode = verifyCode;
	}
	
	public void doRequest(){
		
		StringRequest request = new StringRequest(Request.Method.POST, Constants.HOUSE_KAN_FANG, new Listener<String>() {

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
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("siteId", siteId);
                params.put("projectName", projectName);
                params.put("phone", phone);
                params.put("verifyCode", verifyCode);

                String url = StringUtil.getRequestUrl(Constants.HOUSE_KAN_FANG, params);
                Logger.d(TAG, url);

                return params;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(5*1000, 2, 2);
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
