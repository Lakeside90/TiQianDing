package com.xkhouse.fang.user.task;

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
import com.xkhouse.fang.user.entity.RentReleaseBean;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
* @Description: 发布出租房源
* @author wujian  
* @date 2016-04-19
 */
public class RentReleaseRequest {

	private String TAG = "RentReleaseRequest";
	private RequestListener requestListener;

	private String uid;
	private String siteId;
    private RentReleaseBean requestBean;
	private String code;	//返回状态
	private String msg;		//返回提示语


	public RentReleaseRequest(String uid, String siteId, RentReleaseBean requestBean, RequestListener requestListener) {
		
		this.uid = uid;
		this.siteId = siteId;
        this.requestBean = requestBean;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String userName, String siteId, RentReleaseBean requestBean) {
		this.uid = userName;
		this.siteId = siteId;
        this.requestBean = requestBean;
	}
	
	public void doRequest(){

		
		StringRequest request = new StringRequest(Request.Method.POST, Constants.USER_RENT_RELEASE, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        	message.obj = msg;
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("siteId", siteId);
                params.put("pId", requestBean.getpId());
                params.put("propertyType", requestBean.getPropertyType());
                params.put("hireType", requestBean.getHireType());
                params.put("houseType", requestBean.getHouseType());
                params.put("houseLivingRoom", requestBean.getHouseLivingRoom());
                params.put("toilet", requestBean.getToilet());
                params.put("floor", requestBean.getFloor());
                params.put("totalFloor", requestBean.getTotalFloor());
                params.put("houseArea", requestBean.getHouseArea());
                params.put("hirePrice", requestBean.getHirePrice());
                params.put("payType", requestBean.getPayType());
                if (!StringUtil.isEmpty(requestBean.getFacility())){
                    params.put("facility", requestBean.getFacility());
                }
                params.put("fitment", requestBean.getFitment());
                params.put("orientation", requestBean.getOrientation());
                params.put("title", requestBean.getTitle());
                params.put("detail", requestBean.getDetail());
                params.put("contacter", requestBean.getContacter());
                params.put("contactPhone", requestBean.getContactPhone());
                if (!StringUtil.isEmpty(requestBean.getPhotoUrl())){
                    params.put("photoUrl", requestBean.getPhotoUrl());
                }

                Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_RENT_RELEASE, params));

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
            	
                msg = jsonObject.optString("data");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
