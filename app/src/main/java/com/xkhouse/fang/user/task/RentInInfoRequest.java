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
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.user.entity.RentInEditBean;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 获取  发布的求租房源
* @author wujian  
* @date 2016-04-25
 */
public class RentInInfoRequest {

	private String TAG = "RentInInfoRequest";
	private RequestListener requestListener;

    private String uid;
    private String siteId;
    private String houseId;

    private RentInEditBean resultBean;
	private String code;	//返回状态
	private String msg;		//返回提示语


	public RentInInfoRequest(String uid, String siteId, String houseId, RequestListener requestListener) {
		
        this.uid = uid;
        this.siteId = siteId;
        this.houseId = houseId;
		this.requestListener = requestListener;
	}

	
	public void doRequest(){

		
		StringRequest request = new StringRequest(Request.Method.POST, Constants.USER_RENT_IN_INFO, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code) && resultBean != null){
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;
                        	message.obj = resultBean;
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
                params.put("houseId", houseId);

                Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_RENT_IN_INFO, params));

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

                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                    msg = jsonObject.optString("msg");
                    return;
                }

                JSONObject dataObj = jsonObject.optJSONObject("data");
                if(dataObj != null){
                    resultBean = new RentInEditBean();
                    resultBean.setId(dataObj.optString("id"));
                    resultBean.setSiteId(dataObj.optString("siteId"));
                    resultBean.setUid(dataObj.optString("uid"));
                    resultBean.setContacter(dataObj.optString("contacter"));
                    resultBean.setContactPhone(dataObj.optString("contactPhone"));
                    resultBean.setTitle(dataObj.optString("title"));
                    resultBean.setDetail(dataObj.optString("detail"));

                    resultBean.setPropertyType(dataObj.optString("propertyType"));
                    resultBean.setPropertyTypeName(dataObj.optString("wuyeleixing"));

                    resultBean.setHouseType(dataObj.optString("houseType"));
                    resultBean.setHouseTypeName(dataObj.optString("huxing"));

                    resultBean.setRentType(dataObj.optString("hireType"));
                    resultBean.setRentTypeName(dataObj.optString("chuzufangshi"));
                    resultBean.setSharedType(dataObj.optString("sharedType"));
                    resultBean.setSharedTypeName(dataObj.optString("hezuleixing"));

                    JSONObject mianjiObj = dataObj.optJSONObject("mianji");
                    resultBean.setArea_start(mianjiObj.optString("1"));
                    resultBean.setArea_end(mianjiObj.optString("2"));

                    JSONObject shoujiaObj = dataObj.optJSONObject("shoujia");
                    resultBean.setPrice_start(shoujiaObj.optString("1"));
                    resultBean.setPrice_end(shoujiaObj.optString("2"));

                    JSONObject loucengObj = dataObj.optJSONObject("louceng");
                    resultBean.setFloor_start(loucengObj.optString("1"));
                    resultBean.setFloor_end(loucengObj.optString("2"));


//                    JSONObject areaObj = dataObj.optJSONObject("qiwangquyu");
                    ArrayList<CommonType> areaList = new ArrayList<>();
                    JSONArray  areaArray = dataObj.optJSONArray("qiwangquyu");
                    for (int i = 0; i < areaArray.length(); i++){
                        CommonType type = new CommonType();
                        type.setId(areaArray.getJSONObject(i).optString("id"));
                        type.setName(areaArray.getJSONObject(i).optString("areaName"));
                        areaList.add(type);
                    }
                    resultBean.setAreaList(areaList);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
