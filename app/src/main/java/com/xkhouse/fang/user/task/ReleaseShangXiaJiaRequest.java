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
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
* @Description: 和经纪人上架下架房源
 * @author wujian
* @date 2016-04-21
 */
public class ReleaseShangXiaJiaRequest {

	private String TAG = "ReleaseDeleteRequest";
	private RequestListener requestListener;

    public static final String SELL_RELEASE = "1";
    public static final String RENT_RELEASE = "2";

    public static final String SHANG_JIA = "1";
    public static final String XIA_JIA = "2";

	private String houseId;  	//房源id 多条数据用逗号隔开
	private String uid;  	//用户uid
	private String siteId; 	//站点id
	private String type; 	//房源类型：1出售 2出租 3求购 4求租
	private String status; 	//上架 1 下架 2


	private String code;	//返回状态
	private String msg;		//返回提示语


	public ReleaseShangXiaJiaRequest(String houseId, String uid, String siteId, String type, String status,
                                     RequestListener requestListener) {
		
		this.houseId = houseId;
		this.uid = uid;
		this.siteId = siteId;
		this.type = type;
		this.status = status;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String houseId, String uid, String siteId, String type, String status) {
		this.houseId = houseId;
		this.uid = uid;
		this.siteId = siteId;
        this.status = status;
		this.type = type;
	}
	
	public void doRequest(){

		StringRequest request = new StringRequest(Request.Method.POST, Constants.USER_RELEASE_SHANG_XIA_JIA, new Listener<String>() {

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

                Map<String, String> params = new HashMap<String, String>();
                params.put("houseId", houseId);
                params.put("uid", uid);
                params.put("siteId", siteId);
                params.put("type", type);
                params.put("status", status);

                String url = StringUtil.getRequestUrl(Constants.USER_RELEASE_SHANG_XIA_JIA, params);
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
                msg = jsonObject.optString("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
