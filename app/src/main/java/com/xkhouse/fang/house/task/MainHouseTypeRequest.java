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
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.XKRoom;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
* @Description: 主力户型页面 
* @author wujian  
* @date 2015-9-29 下午4:42:33
 */
public class MainHouseTypeRequest {

	private String TAG = "MainHouseTypeRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String pid;  //楼盘ID
	private String houseType;  //户型类别
	private int page;
	private int num;

	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<CommonType> houseTypeList = new ArrayList<CommonType>();   // 二室，三室，四室
	private ArrayList<XKRoom> roomList = new ArrayList<XKRoom>();
	
	public MainHouseTypeRequest(String siteId, String pid, String houseType,
			int page, int num, RequestListener requestListener) {
		
		this.siteId = siteId;
		this.pid = pid;
		this.houseType = houseType;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String pid, String houseType, int page, int num) {
		this.siteId = siteId; 
		this.pid = pid;
		this.houseType = houseType;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		houseTypeList.clear();
		roomList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
		params.put("pid", pid);
		params.put("houseType", houseType);
		params.put("page", String.valueOf(page));
		params.put("num", String.valueOf(num));
		String url = StringUtil.getRequestUrl(Constants.HOUSE_TYPE_LIST, params);
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putSerializable("houseTypeList", houseTypeList);
                        	data.putSerializable("roomList", roomList);
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
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                JSONArray houseTypeArray = dataObj.optJSONArray("houseType");
	        	if (houseTypeArray != null && houseTypeArray.length() > 0) {
	                for (int i = 0; i <= houseTypeArray.length() - 1; i++) {
	                	JSONObject houseTypeJson = houseTypeArray.getJSONObject(i);
	                	
	                	CommonType houseType = new CommonType();
	                	houseType.setId(houseTypeJson.optString("bedroom"));
	                	houseType.setName(houseTypeJson.optString("housename"));
	                	houseTypeList.add(houseType);
	                }
	        	}
	        	
	        	JSONArray roomArray = dataObj.optJSONArray("list");
	        	if (roomArray != null && roomArray.length() > 0) {
	                for (int i = 0; i <= roomArray.length() - 1; i++) {
	                	JSONObject roomJson = roomArray.getJSONObject(i);
	                	
	                	XKRoom room = new XKRoom();
	                	room.setHousetypeId(roomJson.optString("housetypeId"));
	                	room.setTitle(roomJson.optString("title"));
	                	room.setSaleState(roomJson.optString("saleState"));
	                	room.setTypeArea(roomJson.optString("typeArea"));
	                	room.setPhotoPath(roomJson.optString("photoPath"));
	                	room.setTypename(roomJson.optString("housename"));
	                	room.setTotalPrice(roomJson.optString("totalPrice"));
	                	
	                	roomList.add(room);
	                }
		        }
	        	
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
