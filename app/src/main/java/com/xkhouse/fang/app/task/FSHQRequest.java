package com.xkhouse.fang.app.task;

import android.os.Bundle;
import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.FSHQ;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

/**
 * Created by wujian on 2016/6/24.
 * 方式
 */
public class FSHQRequest {

    private String TAG = "FSHQRequest";

    private String siteId;
    private RequestListener requestListener;

    private String code;	//返回状态
    private String msg;				//返回提示语

    private String hq_month;
    private String hq_price;
    private String hq_count;

    private FSHQ fshq;



    public FSHQRequest(String siteId, RequestListener requestListener){
        this.siteId = siteId;
        this.requestListener = requestListener;
    }

    public void setData(String siteId){
        this.siteId = siteId;
    }


    public void doRequest(){

        String url = Constants.FSHQ + "?siteId=" + siteId;
        Logger.d(TAG, url);

        StringRequest request = new StringRequest(url,  new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
                parseResult(response);

                Message message = new Message();
                Bundle data = new Bundle();
                if(Constants.SUCCESS_CODE.equals(code)){
                    data.putSerializable("fshq", fshq);
                    data.putString("siteId", siteId);
                    message.setData(data);
                    message.what = Constants.SUCCESS_DATA_FROM_NET;
                }else{
                    data.putString("msg", msg);
                    data.putString("siteId", siteId);
                    message.setData(data);
                    message.what = Constants.NO_DATA_FROM_NET;
                }
                requestListener.sendMessage(message);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e(TAG, error.toString());
                Message message = new Message();
                Bundle data = new Bundle();
                data.putString("siteId", siteId);
                message.setData(data);
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

                fshq = new FSHQ();
                fshq.setMonth(dataObj.optString("date"));
                fshq.setPrice(dataObj.optString("avePrice"));
                fshq.setCount(dataObj.optString("dealNum"));
                fshq.setUrl(dataObj.optString("url"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
