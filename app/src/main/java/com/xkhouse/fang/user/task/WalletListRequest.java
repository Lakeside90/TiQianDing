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
import com.xkhouse.fang.user.entity.WalletRecord;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 
* @Description: 我的钱包
* @author wujian  
* @date 2015-10-30 上午9:33:14
 */
public class WalletListRequest {

	private String TAG = "WalletListRequest";
	private RequestListener requestListener;
	
	private String uId;  	//用户id
	private String type;  	//账单类型
	private int page;		//分页索引
	private int num;		//每页个数
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<WalletRecord> walletRecordList = new ArrayList<WalletRecord>();
	private String sum;   //钱包余额
	private String totalWithdrawals;   //累计提现
	private String totalRecom;   //累计赚佣
    private int count;

	public WalletListRequest(String uId, String type, int page, int num, RequestListener requestListener) {
		this.uId = uId;
		this.type = type;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String uId, String type, int page, int num) {
		this.uId = uId; 
		this.type = type; 
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		walletRecordList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", uId);
		params.put("type", type);
		params.put("page", String.valueOf(page));
		params.put("num", String.valueOf(num));
		String url = StringUtil.getRequestUrl(Constants.XKB_WALLET_LIST, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putString("sum", sum);
                        	data.putString("totalWithdrawals", totalWithdrawals);
                        	data.putString("totalRecom", totalRecom);
                        	data.putSerializable("walletRecordList", walletRecordList);
                        	message.setData(data);
                            message.arg1 = count;
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
                })
        {
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
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                JSONArray jsonArray = dataObj.optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject recordJson = jsonArray.getJSONObject(i);
	                	
	                	WalletRecord record = new WalletRecord();
	                	record.setDate(recordJson.optString("dataDate"));
	                	record.setMoney(recordJson.optString("money"));
	                	record.setStateName(recordJson.optString("status"));
	                	record.setType(recordJson.optString("type"));
	                	record.setTitle(recordJson.optString("title"));
	                	walletRecordList.add(record);
	                }
	        	}
	        	
	        	sum = dataObj.getString("total");
                totalWithdrawals = dataObj.getString("totalWithdrawals");
                totalRecom = dataObj.getString("totalRecom");

                try{
                    count = Integer.parseInt(dataObj.getString("count"));
                }catch (Exception e) {
                    count = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
