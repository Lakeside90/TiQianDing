package com.xkhouse.fang.user.task;

import android.os.Bundle;
import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.StoreDetail;
import com.xkhouse.fang.user.entity.MyBookedDetail;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wujian
 * @Description: 我的预定详情
 */
public class MyBookedDetailRequest {

    private String TAG = MyBookedDetailRequest.class.getSimpleName();

    private RequestListener requestListener;

    private String token;
    private String order_number;


    private String code;    //返回状态
    private String msg;        //返回提示语

    private MyBookedDetail myBookedDetail;

    public MyBookedDetailRequest(String token, String order_number,
                                 RequestListener requestListener) {
        this.token = token;
        this.order_number = order_number;
        this.requestListener = requestListener;
    }


    public void setData(String token, String order_number) {
        this.token = token;
        this.order_number = order_number;
    }

    public void doRequest() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("order_number", order_number);

        String url = StringUtil.getRequestUrl(Constants.USER_BOOKED_DETAIL, params);
        Logger.d(TAG, url);

        StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);

                parseResult(response);

                Message message = new Message();
                if (Constants.SUCCESS_CODE.equals(code)) {
                    Bundle data = new Bundle();
                    data.putSerializable("myBookedDetail", myBookedDetail);
                    message.setData(data);
                    message.what = Constants.SUCCESS_DATA_FROM_NET;
                } else {
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
        }) {
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
            if (result != null && result.startsWith("\ufeff")) {
                result = result.substring(1);
            }

            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject != null) {
                code = jsonObject.optString("status");

                if (!Constants.SUCCESS_CODE.equals(code)) {
                    msg = jsonObject.optString("msg");
                    return;
                }

                JSONObject json = jsonObject.optJSONObject("data");
                if (json != null) {
                    myBookedDetail = new MyBookedDetail();

                    myBookedDetail.setId(json.optString("id"));
                    myBookedDetail.setOrder_number(json.optString("order_number"));
                    myBookedDetail.setMember_name(json.optString("member_name"));
                    myBookedDetail.setMember_phone(json.optString("member_phone"));
                    myBookedDetail.setMoney(json.optString("money"));
                    myBookedDetail.setPay_time(json.optString("pay_time"));
                    myBookedDetail.setTradeno(json.optString("tradeno"));
                    myBookedDetail.setPay_type(json.optString("pay_type"));
                    myBookedDetail.setStatus(json.optString("status"));
                    myBookedDetail.setPeople_num(json.optString("people_num"));
                    myBookedDetail.setMember_remarks(json.optString("member_remarks"));
                    myBookedDetail.setBusiness_id(json.optString("business_id"));
                    myBookedDetail.setBooking_id(json.optString("booking_id"));
                    myBookedDetail.setBusiness_remarks(json.optString("business_remarks"));
                    myBookedDetail.setCreate_time(json.optString("create_time"));
                    myBookedDetail.setUpdate_time(json.optString("update_time"));
                    myBookedDetail.setComment_id(json.optString("comment_id"));
                    myBookedDetail.setUse_time(json.optString("use_time"));
                    myBookedDetail.setBusiness_name(json.optString("business_name"));
                    myBookedDetail.setAverage_consump(json.optString("average_consump"));
                    myBookedDetail.setCover_banner(json.optString("cover_banner"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
