package com.xkhouse.fang.house.task;

import android.os.Bundle;
import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.entity.OldHouse;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 二手房出售列表 
* @author wujian  
* @date 2015-9-24 上午10:53:17
 */
public class OldHouseListRequest {

	private String TAG = "OldHouseListRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String searchContent; //筛选条件
	private int page;	//分页索引
	private int num;	//每页个数
	private String url;
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<OldHouse> houseList = new ArrayList<OldHouse>();
	private int count;

    public OldHouseListRequest(){
        super();
    }

	public OldHouseListRequest(String siteId, int page, int num, String searchContent, RequestListener requestListener) {
		this.siteId = siteId;
		this.page = page;
		this.num = num;
		this.searchContent = searchContent;
		this.requestListener = requestListener;
		url = Constants.OLD_HOUSE_LIST + "?siteId="+ siteId + "&page=" + page + "&num=" + num + searchContent;
	}
	
	//地图找房专用（不分页）
	public OldHouseListRequest(String siteId, String searchContent, RequestListener requestListener){
		this.siteId = siteId;
		this.searchContent = searchContent;
		this.requestListener = requestListener;
		url = Constants.OLD_HOUSE_LIST + "?siteId="+ siteId + searchContent;
	}
	
	//地图找房专用（不分页）
	public void setData(String siteId, String searchContent) {
		this.siteId = siteId; 
		this.searchContent = searchContent;
		url = Constants.OLD_HOUSE_LIST + "?siteId="+ siteId + searchContent;
	}
	
	public void setData(String siteId, int page, int num, String searchContent) {
		this.siteId = siteId; 
		this.page = page;
		this.num = num;
		this.searchContent = searchContent;
		url = Constants.OLD_HOUSE_LIST + "?siteId="+ siteId + "&page=" + page + "&num=" + num + searchContent;
	}
	
	public void doRequest(){
		houseList.clear();
		
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code) && houseList.size()>0 ){
                        	message.obj = houseList;
                        	message.arg1 = count;
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;

                            if(isNeedCache()){
                                //缓存第一页没有筛选过的数据
                                AppCache.writeOldHouseListJson(siteId, response);
                            }
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
	                	JSONObject houseJson = jsonArray.getJSONObject(i);
	                	
	                	OldHouse house = new OldHouse();
	                	house.setAreaName(houseJson.optString("areaname"));
	                	house.setFloor(houseJson.optString("floor"));
	                	house.setHouseArea(houseJson.optString("housearea"));
	                	house.setHousephoto(houseJson.optString("housephoto"));
	                	house.setLatitude(houseJson.optString("latitude"));
	                	house.setLongitude(houseJson.optString("longitude"));
	                	house.setMembertype(houseJson.optString("membertype"));
	                	house.setPorjectId(houseJson.optString("sid"));
	                	house.setPorjectName(houseJson.optString("projname"));
	                	house.setPrice(houseJson.optString("price"));
	                	house.setRoomtype(houseJson.optString("roomtype"));
	                	house.setTitle(houseJson.optString("saletitle"));
	                	
	                	houseList.add(house);
	                }
	        	}

                if(StringUtil.isEmpty(dataObj.getString("count"))){
                    count = 0;
                }else{
                    count = Integer.parseInt(dataObj.getString("count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}



    public Bundle getCacheModel(){
        Bundle data = new Bundle();
        data.putInt("count", count);
        data.putSerializable("houseList", houseList);
        return data;
    }


    /**
     * 缓存数据的条件：第一页，且不包含筛选条件
     * @return
     */
    public boolean isNeedCache(){
        if (page != 1){
            return false;
        } else{
            if (searchContent.contains("&area=") || searchContent.contains("&type=")
                    || searchContent.contains("&price=") || searchContent.contains("&sort=")
                    || searchContent.contains("&houseType=") || searchContent.contains("&areaZone=")
                    || searchContent.contains("&roomFace=") || searchContent.contains("&buildingera=")
                    || searchContent.contains("&membertype=") || searchContent.contains("&fitment=")
                    || searchContent.contains("&projname=")) {
                return false;
            }else{
                return true;
            }
        }
    }
}
