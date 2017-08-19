package com.xkhouse.fang.house.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.entity.XKRoom;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 地图找房，获取某小区的房源列表 
* @author wujian  
* @date 2015-10-14 下午1:52:50
 */
public class MapOldHouseListRequest {

	private String TAG = "MapOldHouseListRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String projname;  //小区名称
	private String searchContent; //筛选条件
	private int page;	//分页索引
	private int num;	//每页个数
	private String url;
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<XKRoom> roomList = new ArrayList<XKRoom>();
	private int count;
	
	public MapOldHouseListRequest(String siteId, String projname, int page, int num, String searchContent, RequestListener requestListener) {
		this.siteId = siteId;
		this.page = page;
		this.projname = projname;
		this.num = num;
		this.searchContent = searchContent;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String projname, int page, int num, String searchContent) {
		this.siteId = siteId; 
		this.page = page;
		this.projname = projname;
		this.num = num;
		this.searchContent = searchContent;
	}
	
	public void doRequest(){
		roomList.clear();
		
		if(!StringUtil.isEmpty(searchContent)){
			url = Constants.MAP_OLD_HOUSE_LIST + "?siteId="+ siteId + "&projname=" + projname +
					"&page=" + page + "&num=" + num + searchContent;
		}else {
			url = Constants.MAP_OLD_HOUSE_LIST + "?siteId="+ siteId + "&projname=" + projname +
					"&page=" + page + "&num=" + num;
		}
		
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = roomList;
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
                JSONArray jsonArray = dataObj.optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject roomJson = jsonArray.getJSONObject(i);
	                	
	                	XKRoom room = new XKRoom();
	                	room.setChoiceId(roomJson.optString("aid"));
	                	room.setTitle(roomJson.optString("saletitle"));
	                	room.setFloor(roomJson.optString("floors") + "/" +roomJson.optString("totalfloors"));
	                	room.setTypeArea(roomJson.optString("housearea"));
	                	room.setTotalPrice(roomJson.optString("price"));
	                	room.setHouseTitle(roomJson.optString("roomtype"));
	                	room.setPhotoPath(roomJson.optString("housephoto"));
	                	room.setProjectName(projname);
	                	
	                	roomList.add(room);
	                }
	        	}
	        	try{
	        		count = Integer.parseInt(dataObj.getString("count"));
	        	}catch (Exception e) {
	        		count = 0;
				}
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
