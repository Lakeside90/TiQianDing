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
import com.xkhouse.fang.user.activity.MessageCenterActivity;
import com.xkhouse.fang.user.entity.MSGActivity;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.entity.MSGSystem;
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
* @Description: 我的消息二级列表
* @author wujian  
* @date 2015-11-4 下午7:11:31
 */
public class MessageDetailListRequest {

	private String TAG = "MessageDetailListRequest";
	private RequestListener requestListener;
	
	private String uId;  	//用户id
	private String deviceId;  	//设备id
	private String siteId; 	//站点id
	private int type;
	private int num;
	private int page;
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	private int count;

	private ArrayList<MSGNews> newsList = new ArrayList<MSGNews>();		//每日要闻
	private ArrayList<MSGActivity> activityList = new ArrayList<MSGActivity>();		//最新活动
	private ArrayList<MSGSystem> systemList = new ArrayList<MSGSystem>();		//系统消息
	
	
	public MessageDetailListRequest(String uId, String deviceId, String siteId, int type,
			int page, int num, RequestListener requestListener) {
		
		this.uId = uId;
		this.deviceId = deviceId;
		this.siteId = siteId;
		this.type = type;
		this.num = num;
		this.page = page;
		this.requestListener = requestListener;
	}
	
	
	public void setData(String uId, String deviceId, String siteId, int type, int page, int num) {
		this.uId = uId;
        this.deviceId = deviceId;
		this.siteId = siteId; 
		this.type = type;
		this.num = num;
		this.page = page;
	}
	
	public void doRequest(){
		newsList.clear();
		activityList.clear();
		systemList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", uId);
        if (!StringUtil.isEmpty(deviceId)){
            params.put("deviceId", deviceId);
        }
		params.put("siteId", siteId);
		params.put("type", String.valueOf(type));
		params.put("num", String.valueOf(num));
		params.put("page", String.valueOf(page));

		String url = StringUtil.getRequestUrl(Constants.USER_MESSAGE_LIST, params);
//		String url = StringUtil.getRequestUrl("http://xkapi.com/v1.2/Passport/MessageRead.api", params);  test
		Logger.d(TAG, url);
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	if(type == MessageCenterActivity.MSG_NEWS){
                        		message.obj = newsList;
                        	}else if(type == MessageCenterActivity.MSG_ACTIVITY){
                        		message.obj = activityList;
                        	}else if(type == MessageCenterActivity.MSG_SYSTEM){
                        		message.obj = systemList;
                        	}
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
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");

                count = Integer.parseInt(dataObj.optString("count"));

                JSONArray dataArray = dataObj.optJSONArray("list");
	        	if (dataArray != null && dataArray.length() > 0 ) {
					switch (type) {
					case MessageCenterActivity.MSG_NEWS:
						for(int i = 0; i < dataArray.length(); i++){
							JSONObject newsObj = dataArray.getJSONObject(i);
							MSGNews news = new MSGNews();
							
							news.setContent(newsObj.optString("content"));
							news.setDate(newsObj.optString("createTime"));
							news.setId(newsObj.optString("tId"));
							news.setPhotoUrl(newsObj.optString("photoUrl"));
							news.setTitle(newsObj.optString("title"));
							
							newsList.add(news);
						}
						break;

					case MessageCenterActivity.MSG_ACTIVITY:
						for(int i = 0; i < dataArray.length(); i++){
							JSONObject activityObj = dataArray.getJSONObject(i);	
							MSGActivity activity = new MSGActivity();
							
							activity.setId(activityObj.optString("tId"));
							activity.setContent(activityObj.optString("content"));
							activity.setPhotoUrl(activityObj.optString("photoUrl"));
							activity.setDate(activityObj.optString("createTime"));
							activity.setEndTime(activityObj.optDouble("endTime"));
							activity.setStartTime(activityObj.optDouble("startTime"));
							activity.setNowTime(activityObj.optDouble("nowTime"));
							activity.setTitle(activityObj.optString("title"));
							
							activityList.add(activity);
						}			
						break;
						
					case MessageCenterActivity.MSG_SYSTEM:
						for(int i = 0; i < dataArray.length(); i++){
							JSONObject activityObj = dataArray.getJSONObject(i);	
							MSGSystem system = new MSGSystem();
							
							system.setContent(activityObj.optString("content"));
							system.setDate(activityObj.optString("createTime"));
							systemList.add(system);
						}
						break;
					}
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
