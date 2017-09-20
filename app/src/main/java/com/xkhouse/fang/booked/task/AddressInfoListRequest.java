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
import com.xkhouse.fang.booked.entity.AddressInfo;
import com.xkhouse.fang.booked.entity.CommentInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 获取收货地址
* @author wujian  
 */
public class AddressInfoListRequest {

	private String TAG = AddressInfoListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;  //站点
	private int page;	//分页索引
	private int num;	//每页个数

	private String code;	//返回状态
	private String msg;		//返回提示语
    private int count;

	private ArrayList<AddressInfo> addressInfoList = new ArrayList<>();

	public AddressInfoListRequest(String token, int page, int num, RequestListener requestListener) {
		this.token = token;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	public void setData(String token, int page, int num) {
		this.token = token;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		addressInfoList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
        params.put("page", String.valueOf(page));
        params.put("pagenum", String.valueOf(num));

        String url = StringUtil.getRequestUrl(Constants.ADDRESS_LIST, params);
        Logger.d(TAG, url);
        
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = addressInfoList;
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
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject json = jsonArray.getJSONObject(i);
	                	
	                	AddressInfo addressInfo = new AddressInfo();

                        addressInfo.setId(json.optString("id"));
                        addressInfo.setProvince_id(json.optString("province_id"));
                        addressInfo.setCity_id(json.optString("city_id"));
                        addressInfo.setArea_id(json.optString("area_id"));
                        addressInfo.setContent(json.optString("content"));
                        addressInfo.setName(json.optString("name"));
                        addressInfo.setPhone(json.optString("phone"));
                        addressInfo.setIs_selected(json.optString("is_selected"));
                        addressInfo.setMember_id(json.optString("member_id"));
                        addressInfo.setCreate_time(json.optString("create_time"));
                        addressInfo.setUpdate_time(json.optString("update_time"));
                        addressInfo.setStatus(json.optString("status"));

	                	addressInfoList.add(addressInfo);
	                }
	        	}

                count = Integer.parseInt(jsonObject.optJSONObject("data").optString("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
