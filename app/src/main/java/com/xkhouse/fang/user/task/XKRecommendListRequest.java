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
import com.xkhouse.fang.user.entity.XKRecommend;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 推荐客户列表 
* @author wujian  
* @date 2015-10-22 下午2:54:43
 */
public class XKRecommendListRequest {

	private String TAG = "XKRecommendListRequest";
	private RequestListener requestListener;
	
	private String uId;  	//用户id
	private String status; 	//状态
	private int page;		//分页索引
	private int num;		//每页个数
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<XKRecommend> recommendList = new ArrayList<XKRecommend>();
	private int count;
	
	public XKRecommendListRequest(String uId, String status, int page, int num, RequestListener requestListener) {
		this.uId = uId;
		this.status = status;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String uId, String status, int page, int num) {
		this.uId = uId; 
		this.status = status;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		recommendList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", uId);
		params.put("status", status);
		params.put("page", String.valueOf(page));
		params.put("num", String.valueOf(num));
		String url = StringUtil.getRequestUrl(Constants.XKB_RECOMMEND_LIST, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.obj = recommendList;
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
            	code = jsonObject.optString("code");
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                JSONArray jsonArray = dataObj.optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject recommendJson = jsonArray.getJSONObject(i);
	                	
	                	XKRecommend recommend = new XKRecommend();
	                	recommend.setCustomerName(recommendJson.optString("rName"));
                        recommend.setPhone(recommendJson.optString("rPhone"));
                        recommend.setSiteName(recommendJson.optString("siteName"));
                        recommend.setPropertyName(recommendJson.optString("propertyTypeName"));
                        recommend.setProjectStr(recommendJson.optString("projectStr"));
	                	recommend.setDate(recommendJson.optString("dataTime"));
	                	recommend.setRecommendId(recommendJson.optString("id"));
	                	recommend.setStatus(recommendJson.optString("status"));
	                	recommend.setStatusName(recommendJson.optString("sName"));
	                	
	                	recommendList.add(recommend);
	                }
	        	}
	        	try{
	        		count = Integer.parseInt(dataObj.getString("total"));
	        	}catch (Exception e) {
	        		count = 0;
				}
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
