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
import com.xkhouse.fang.booked.entity.AccountInfo;
import com.xkhouse.fang.booked.entity.PayDetail;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description: 在线买单信息
* @author wujian  
* @date 2015-9-21 下午2:20:00
 */
public class PayDetailRequest {

	private String TAG = PayDetailRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;  //站点
    private String business_id;
    private String total_money;
    private String no_discount_money;


	private String code;	//返回状态
	private String msg;		//返回提示语

    private PayDetail payDetail;


	public PayDetailRequest(String token, String business_id, String total_money,
                            String no_discount_money, RequestListener requestListener) {
		this.token = token;
		this.business_id = business_id;
		this.total_money = total_money;
		this.no_discount_money = no_discount_money;
		this.requestListener = requestListener;
	}
	
	public void setData(String token, String business_id, String total_money,
                        String no_discount_money) {
        this.token = token;
        this.business_id = business_id;
        this.total_money = total_money;
        this.no_discount_money = no_discount_money;
	}
	
	public void doRequest(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
        params.put("business_id", business_id);
        params.put("total_money", no_discount_money);
        params.put("no_discount_money", total_money);

        String url = StringUtil.getRequestUrl(Constants.PAY_DETAIL, params);
        Logger.d(TAG, url);
        
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                            message.obj = payDetail;
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

                JSONObject dataObj = jsonObject.optJSONObject("data");

                payDetail = new PayDetail();

                JSONObject bookingObj = dataObj.optJSONObject("booking");
                PayDetail.Booking booking =  payDetail.new Booking();
                booking.setDiscount(bookingObj.optString("discount"));
                booking.setDiscount_name(bookingObj.optString("discount_name"));
                booking.setMortgage(bookingObj.optString("mortgage"));
                booking.setPayment(bookingObj.optString("payment"));

                JSONArray discountArray = dataObj.optJSONArray("discountarray");
	        	if (discountArray != null && discountArray.length() > 0) {

                    List<PayDetail.Discount> discounts = new ArrayList<>();

	                for (int i = 0; i <= discountArray.length() - 1; i++) {
	                	JSONObject json = discountArray.getJSONObject(i);

                        PayDetail.Discount discount = payDetail.new Discount();

                        discount.setCheck_discount_id(json.optString("check_discount_id"));
                        discount.setCheck_discount_name(json.optString("check_discount_name"));
                        discount.setCheck_discount_money(json.optString("check_discount_money"));
                        discount.setUse_time(json.optString("use_time("));

                        discounts.add(discount);
	                }
	                payDetail.setDiscountArray(discounts);
	        	}

	        	payDetail.setPaid(dataObj.optString("paid"));


                JSONArray ruleArray = dataObj.optJSONArray("rule");
                if (ruleArray != null && ruleArray.length() > 0) {

                    List<PayDetail.Rule> rules = new ArrayList<>();

                    for (int i = 0; i < ruleArray.length(); i++) {
                        JSONObject json = ruleArray.getJSONObject(i);

                        PayDetail.Rule rule = payDetail.new Rule();

                        rule.setCheck_discount_name(json.optString("check_discount_name"));
                        rule.setUse_time(json.optString("use_time"));

                        rules.add(rule);
                    }
                    payDetail.setRules(rules);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
