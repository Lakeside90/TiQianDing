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
import com.xkhouse.fang.user.entity.SellReleaseEditBean;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 获取  发布的出售房源
* @author wujian  
* @date 2016-04-25
 */
public class SellReleaseInfoRequest {

	private String TAG = "SellReleaseInfoRequest";
	private RequestListener requestListener;

    private String uid;
    private String siteId;
    private String houseId;

    private SellReleaseEditBean resultBean;
	private String code;	//返回状态
	private String msg;		//返回提示语


	public SellReleaseInfoRequest(String uid, String siteId, String houseId, RequestListener requestListener) {
		
        this.uid = uid;
        this.siteId = siteId;
        this.houseId = houseId;
		this.requestListener = requestListener;
	}

	
	public void doRequest(){

		
		StringRequest request = new StringRequest(Request.Method.POST, Constants.USER_SELL_RELEASE_INFO, new Listener<String>() {

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

                Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_SELL_RELEASE_INFO, params));

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
                    resultBean = new SellReleaseEditBean();
                    resultBean.setId(dataObj.optString("sId"));

                    resultBean.setpId(dataObj.optString("pId"));
                    resultBean.setpName(dataObj.optString("xiaoquming"));

                    resultBean.setPropertyType(dataObj.optString("propertyType"));
                    resultBean.setPropertyTypeName(dataObj.optString("wuyeleixing"));



                    resultBean.setBeedroom(dataObj.optString("jishi"));
                    resultBean.setOffice(dataObj.optString("jiting"));
                    resultBean.setToilet(dataObj.optString("jiwei"));

                    resultBean.setFloor(dataObj.optString("floor"));
                    resultBean.setTotalFloor(dataObj.optString("totalFloor"));

                    resultBean.setHouseArea(dataObj.optString("houseArea"));
                    resultBean.setPrice(dataObj.optString("price"));


                    resultBean.setFitment(dataObj.optString("fitment"));
                    resultBean.setFitmentName(dataObj.optString("zhuangxiu"));

                    resultBean.setOrientation(dataObj.optString("orientation"));
                    resultBean.setOrientationName(dataObj.optString("chaoxiang"));


                    resultBean.setContacter(dataObj.optString("contacter"));
                    resultBean.setContactPhone(dataObj.optString("contactPhone"));
                    resultBean.setTitle(dataObj.optString("title"));
                    resultBean.setDetail(dataObj.optString("detail"));

                    JSONArray photoArray = dataObj.optJSONArray("photos");
                    if(photoArray != null &&  photoArray.length()>0){
                        ArrayList<String> photos = new ArrayList<>();
                        for (int i=0; i<photoArray.length(); i++){
                           if (!StringUtil.isEmpty(photoArray.optString(i))){
                               photos.add(photoArray.optString(i));
                           }
                        }
                        resultBean.setPhotos(photos);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
