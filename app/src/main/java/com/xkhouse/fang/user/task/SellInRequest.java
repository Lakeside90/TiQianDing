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
import com.xkhouse.fang.user.entity.SellInBean;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
* @Description: 发布求购房源
* @author wujian  
* @date 2016-04-18
 */
public class SellInRequest {

	private String TAG = "SellInRequest";
	private RequestListener requestListener;

	private String uid;
	private String siteId;
    private SellInBean requestBean;
	private String code;	//返回状态
	private String msg;		//返回提示语


	public SellInRequest(String uid, String siteId, SellInBean requestBean, RequestListener requestListener) {
		
		this.uid = uid;
		this.siteId = siteId;
        this.requestBean = requestBean;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String userName, String siteId, SellInBean requestBean) {
		this.uid = userName;
		this.siteId = siteId;
        this.requestBean = requestBean;
	}
	
	public void doRequest(){

		
		StringRequest request = new StringRequest(Request.Method.POST, Constants.USER_SELL_IN, new Listener<String>() {

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
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("siteId", siteId);
                params.put("area", requestBean.getArea());
                params.put("propertyType", requestBean.getPropertyType());
                params.put("houseType", requestBean.getHouseType());
                params.put("area_start", requestBean.getArea_start());
                params.put("area_end", requestBean.getArea_end());
                params.put("price_start", requestBean.getPrice_start());
                params.put("price_end", requestBean.getPrice_end());
                params.put("floor_start", requestBean.getFloor_start());
                params.put("floor_end", requestBean.getFloor_end());
                params.put("title", requestBean.getTitle());
                params.put("detail", requestBean.getDetail());
                params.put("contacter", requestBean.getContacter());
                params.put("contactPhone", requestBean.getContactPhone());

                Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_SELL_IN, params));

                return params;
            }


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
