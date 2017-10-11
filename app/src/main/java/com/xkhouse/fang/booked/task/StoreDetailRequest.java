package com.xkhouse.fang.booked.task;

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
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.booked.entity.StoreDetail;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wujian
 * @Description: 商户详情
 */
public class StoreDetailRequest {

    private String TAG = StoreDetailRequest.class.getSimpleName();

    private RequestListener requestListener;

    private String token;
    private String business_id;
    private String siteId;


    private String code;    //返回状态
    private String msg;        //返回提示语

    private StoreDetail storeDetail;

    public StoreDetailRequest(String token, String business_id, String siteId,
                              RequestListener requestListener) {
        this.token = token;
        this.business_id = business_id;
        this.siteId = siteId;
        this.requestListener = requestListener;
    }


    public void setData(String token, String business_id, String siteId) {
        this.token = token;
        this.business_id = business_id;
        this.siteId = siteId;
    }

    public void doRequest() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("business_id", business_id);
        params.put("cityId", siteId);
        if (!StringUtil.isEmpty(token)) {
            params.put("token", token);
        }

        String url = StringUtil.getRequestUrl(Constants.STORE_DETAIL, params);
        Logger.d(TAG, url);

        StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);

                parseResult(response);

                Message message = new Message();
                if (Constants.SUCCESS_CODE.equals(code)) {
                    Bundle data = new Bundle();
                    data.putSerializable("storeDetail", storeDetail);
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

                JSONObject dataObj = jsonObject.optJSONObject("data");
                if (dataObj != null) {
                    storeDetail = new StoreDetail();
                    storeDetail.setBusinessId(dataObj.optString("business_id"));
                    storeDetail.setBusinessName(dataObj.optString("business_name"));
                    storeDetail.setAverageConsump(dataObj.optString("average_consump"));
                    storeDetail.setAddress(dataObj.optString("business_address"));
                    storeDetail.setPhone(dataObj.optString("business_phone"));
                    storeDetail.setCheck_discount_id(dataObj.optString("check_discount_id"));
                    storeDetail.setCheck_discount(dataObj.optString("check_discount"));
                    storeDetail.setCollection(dataObj.optString("collection"));

                    String business_label = dataObj.optString("business_label");
                    if (!StringUtil.isEmpty(business_label)) {
                        String[] labels = business_label.split(",");
                        storeDetail.setBusinessLabel(labels);
                    }

                    String banner = dataObj.optString("banner");
                    if (!StringUtil.isEmpty(banner)) {
                        String[] banners = banner.split(",");
                        storeDetail.setBanner(banners);
                    }

                    ArrayList<BookedInfo> bookings = new ArrayList<>();
                    JSONArray bookingArray = dataObj.optJSONArray("booking");
                    if (bookingArray != null && bookingArray.length() > 0) {
                        for(int i = 0; i < bookingArray.length(); i++) {
                            JSONObject bookJson = bookingArray.getJSONObject(i);
                            BookedInfo booking = new BookedInfo();
                            booking.setBookingId(bookJson.optString("booking_id"));
                            booking.setDiscount(bookJson.optString("discount"));
                            booking.setPayment(bookJson.optString("payment"));
                            booking.setMortgage(bookJson.optString("mortgage"));
                            booking.setOrderNum(bookJson.optString("order_num"));
                            booking.setToday_order_num(bookJson.optString("today_order_num"));
                            bookings.add(booking);
                        }
                    }
                    storeDetail.setBookings(bookings);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
