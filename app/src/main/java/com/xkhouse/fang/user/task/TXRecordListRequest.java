package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.entity.TXRecord;
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
* @Description: 最近提现记录
* @author wujian  
* @date 2016-7-19
 */
public class TXRecordListRequest {

	private String TAG = "TXRecordListRequest";
	private RequestListener requestListener;

    private String uid;


	private String code;	//返回状态
	private String msg;		//返回提示语

	private ArrayList<TXRecord> recordList = new ArrayList<TXRecord>();


	public TXRecordListRequest(String uid, RequestListener requestListener) {
        this.uid = uid;
		this.requestListener = requestListener;
	}
	
	public void doRequest(){
		recordList.clear();

        String url = Constants.XKB_TX_RECORD__LIST + "?uid=" +uid + "&page=1&num=10";
        Logger.d(TAG, url);

		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = recordList;
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
            	code = jsonObject.optString("code");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                JSONArray jsonArray = dataObj.optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject recordJson = jsonArray.getJSONObject(i);
	                	
	                    TXRecord record = new TXRecord();
                        record.setCardNo(recordJson.optString("cardNo"));
                        record.setIcon(recordJson.optString("bankImgUrl"));
                        record.setName(recordJson.optString("userName"));
                        record.setPhone(recordJson.optString("phone"));
                        record.setType(recordJson.optString("bankType"));
                        record.setBankName(recordJson.optString("cn"));

	                	recordList.add(record);
	                }
	        	}
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
