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
import com.xkhouse.fang.user.entity.XKBank;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 获取支付宝订单
* @author wujian  
* @date 2015-10-30 下午4:35:08
 */
public class GetAlipayOrderRequest {

	private String TAG = GetAlipayOrderRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;
	private String money;
	private String order_id;

	private String orderInfo;


	public GetAlipayOrderRequest(String token, String money, String order_id, RequestListener requestListener) {
        this.token = token;
        this.money = money;
        this.order_id = order_id;
		this.requestListener = requestListener;
	}
	
	public void doRequest(){

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("money", money);
        params.put("order_id", order_id);

        String url = StringUtil.getRequestUrl(Constants.PAY_ORDER_ALI, params);
        Logger.d(TAG, url);

		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);

                        Message message = new Message();
                        if (StringUtil.isEmpty(response)) {
                            message.obj = "获取订单失败";
                            message.what = Constants.NO_DATA_FROM_NET;
                        }else{
                            message.obj = response;
                            message.what = Constants.SUCCESS_DATA_FROM_NET;
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
}
