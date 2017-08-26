package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.entity.XKMessage;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 推荐客户列表 
* @author wujian  
* @date 2015-10-22 下午2:54:43
 */
public class MessageListRequest {

	private String TAG = "XKRecommendListRequest";
	private RequestListener requestListener;
	
	private String uId;  	//用户id
	private String deviceId;  	//设备ID
	private String siteId; 	//站点id
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<XKMessage> messageList = new ArrayList<XKMessage>();
	
	public MessageListRequest(String uId, String deviceId, String siteId, RequestListener requestListener) {
		this.uId = uId;
		this.deviceId = deviceId;
		this.siteId = siteId;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String uId, String deviceId, String siteId) {
		this.uId = uId;
        this.deviceId = deviceId;
		this.siteId = siteId; 
	}
	
	public void doRequest(){
		messageList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", uId);
		params.put("siteId", siteId);
        if (!StringUtil.isEmpty(deviceId)){
            params.put("deviceId", deviceId);
        }
		String url = StringUtil.getRequestUrl(Constants.USER_NEWS, params);
//        String url = StringUtil.getRequestUrl("http://xkapi.com/v1.1/Passport/MessageRead.api", params);  test
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.obj = messageList;
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
                
                JSONObject dayObj = dataObj.optJSONObject("yaowen");
                XKMessage msg_day = new XKMessage();
        		msg_day.setContent(dayObj.optString("title"));
        		msg_day.setTitle("每日要闻");
        		msg_day.setDate(dayObj.optString("createTime"));
        		msg_day.setIconRes(R.drawable.tidings_icon_news);
                if (1 == dayObj.optInt("read")){
                    msg_day.setIsRead(true);
                } else{
                    msg_day.setIsRead(false);
                }
        		messageList.add(msg_day);
        		
        		JSONObject activityObj = dataObj.optJSONObject("huodong");
        		XKMessage msg_activity = new XKMessage();
        		msg_activity.setContent(activityObj.optString("title"));
        		msg_activity.setTitle("最新活动");
        		msg_activity.setDate(activityObj.optString("createTime"));
        		msg_activity.setIconRes(R.drawable.tidings_icon_activity);
                if (1 == activityObj.optInt("read")){
                    msg_activity.setIsRead(true);
                } else{
                    msg_activity.setIsRead(false);
                }
        		messageList.add(msg_activity);
        		
        		JSONObject houseObj = dataObj.optJSONObject("xiaoxi");
        		XKMessage msg_house = new XKMessage();
        		msg_house.setContent(houseObj.optString("title"));
        		msg_house.setTitle("系统消息");
        		msg_house.setDate(houseObj.optString("createTime"));
        		msg_house.setIconRes(R.drawable.tidings_icon_sys);
                if (1 == houseObj.optInt("read")){
                    msg_house.setIsRead(true);
                } else{
                    msg_house.setIsRead(false);
                }
        		messageList.add(msg_house);
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
