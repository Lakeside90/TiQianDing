package com.xkhouse.fang.user.task;

import android.os.Bundle;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.entity.XKRelease;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description:  房源管理--出租列表
* @author wujian  
* @date 2016-04-20
 */
public class RentReleaseListRequest {

	private String TAG = "RentReleaseListRequest";
	private RequestListener requestListener;

	private String uId;  	//用户id
	private String siteId; 	//站点id
	private String type; 	//1已发布 2待发布 3已删除 其他参数查询全部
	private int num;		//条数
	private int page;		//页数

	private String code;	//返回状态
	private String msg;		//返回提示语

    private String yifabu;
    private String daifabu;
    private String yishanchu;
    private String todat_refresh_num;

	private ArrayList<XKRelease> rentList = new ArrayList<XKRelease>();


	public RentReleaseListRequest(String uId, String siteId,String type,
                                  int page, int num, RequestListener requestListener) {
		
		this.uId = uId;
		this.siteId = siteId;
        this.type = type;
		this.num = num;
		this.page = page;
		
		this.requestListener = requestListener;
	}
	
	
	public void setData(String uId, String siteId,String type, int page, int num) {
		this.uId = uId; 
		this.siteId = siteId;
        this.type = type;
		this.num = num;
		this.page = page;
	}
	
	
	public void doRequest(){
		rentList.clear();

		StringRequest request = new StringRequest(Request.Method.POST, Constants.USER_RENT_RELEASE_LIST, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putSerializable("rentList", rentList);
                            data.putString("yifabu", yifabu);
                            data.putString("daifabu", daifabu);
                            data.putString("yishanchu", yishanchu);
                            data.putString("todat_refresh_num", todat_refresh_num);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uId);
                params.put("siteId", siteId);
                params.put("num", String.valueOf(num));
                params.put("page", String.valueOf(page));
                if(!StringUtil.isEmpty(type)){
                    params.put("type", type);
                }
                String url = StringUtil.getRequestUrl(Constants.USER_RENT_RELEASE_LIST, params);
                Logger.d(TAG, url);

                return params;
            }


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
                JSONArray newHouseArray = dataObj.optJSONArray("list");
                if(newHouseArray != null && newHouseArray.length() > 0){
                	for(int i = 0; i < newHouseArray.length(); i++){
                		JSONObject rentJson = newHouseArray.getJSONObject(i);

                        XKRelease rent = new XKRelease();
                        rent.setId(rentJson.optString("hId"));
                        rent.setTitle(rentJson.optString("title"));
                        rent.setDetail(rentJson.optString("detail"));
                        rent.setRefreshTime(rentJson.optString("refreshTime"));
                        rent.setStatus(rentJson.optString("status"));
                		rentList.add(rent);
                	}
                }

                yifabu = dataObj.optString("yifabu");
                daifabu = dataObj.optString("daifabu");
                yishanchu = dataObj.optString("yishanchu");
                todat_refresh_num = dataObj.optString("todat_refresh_num");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
