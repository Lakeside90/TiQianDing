package com.xkhouse.fang.house.task;

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
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 购房能力评估结果
* @author wujian  
 */
public class BuyAbilityResultRequest {

	private String TAG = "BuyAbilityResultRequest";
	private RequestListener requestListener;

    private String areaId;
    private String currentMoney;
    private String expense;
    private String houseSize;
    private String loanTime;

	private String siteId;  //站点
	private int page;	//分页索引
	private int num;	//每页个数


	private String code;	//返回状态
	private String msg;		//返回提示语

	private ArrayList<House> houseList = new ArrayList<House>();
	private String count;
    private String allPrice;
    private String billPrice;

	public BuyAbilityResultRequest(String siteId, int page, int num,
                                   String areaId,
                                   String currentMoney,
                                   String expense,
                                   String houseSize,
                                   String loanTime,
                                   RequestListener requestListener) {
		this.siteId = siteId;
		this.page = page;
		this.num = num;
		this.areaId = areaId;
		this.currentMoney = currentMoney;
		this.expense = expense;
		this.houseSize = houseSize;
		this.loanTime = loanTime;
		this.requestListener = requestListener;
	}


	
	public void setData(int page, int num) {
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		houseList.clear();

        Map<String, String> params = new HashMap<String, String>();
        params.put("siteId", siteId);
        params.put("page", String.valueOf(page));
        params.put("num", String.valueOf(num));
        params.put("areaId", areaId);
        params.put("currentMoney", currentMoney);
        params.put("expense", expense);
        params.put("houseSize", houseSize);
        params.put("loanTime", loanTime);
        String url = StringUtil.getRequestUrl(Constants.HOUSE_PING_GU, params);
        Logger.d(TAG, url);

		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                            Bundle data = new Bundle();
                            data.putSerializable("houseList", houseList);
                            data.putString("count", count);
                            data.putString("allPrice", allPrice);
                            data.putString("billPrice", billPrice);
                            message.setData(data);
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
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");

                JSONArray jsonArray = dataObj.optJSONArray("list");
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject houseJson = jsonArray.getJSONObject(i);
	                	
	                	House house = new House();
	                	house.setProjectId(houseJson.optString("projectId"));
	                	house.setProjectName(houseJson.optString("projectName"));
	                	house.setEffectPhoto(houseJson.optString("effectPhoto"));
	                	house.setAveragePrice(houseJson.optString("averagePrice"));
	                	house.setSaleState(houseJson.optString("saleState"));
	                	house.setAreaName(houseJson.optString("areaName"));
	                	house.setGroupDiscountInfo(houseJson.optString("groupDiscountInfo"));
	                	house.setHouseType(houseJson.optString("houseType"));
	                	houseList.add(house);
	                }
	        	}


                count = dataObj.getString("count");
                allPrice = dataObj.getString("allPrice");
                billPrice = dataObj.getString("billPrice");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
