package com.xkhouse.fang.user.task;

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
import com.xkhouse.fang.user.entity.FavHouse;
import com.xkhouse.fang.user.entity.FavNews;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 我的收藏 
* @author wujian  
* @date 2015-11-3 上午9:49:08
 */
public class FavoriteListRequest {

	private String TAG = "FavoriteListRequest";
	private RequestListener requestListener;
	
	private String uId;  	//用户id
	private String siteId; 	//站点id
	private int type; 	//类别（1新房、2二手房、3租房、4资讯）
	private int num;		//条数
	private int page;		//页数
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	private int count;

	private ArrayList<FavHouse> newHouseList = new ArrayList<FavHouse>();
	private ArrayList<FavHouse> oldHouseList = new ArrayList<FavHouse>();
	private ArrayList<FavHouse> rentHouseList = new ArrayList<FavHouse>();
	private ArrayList<FavNews> newsList = new ArrayList<FavNews>();
	
	public FavoriteListRequest(String uId, String siteId, int type, 
			int page, int num, RequestListener requestListener) {
		
		this.uId = uId;
		this.siteId = siteId;
		this.type = type;
		this.num = num;
		this.page = page;
		
		this.requestListener = requestListener;
	}
	
	
	public void setData(String uId, String siteId, int type, int page, int num) {
		this.uId = uId; 
		this.siteId = siteId; 
		this.type = type;
		this.num = num;
		this.page = page;
	}
	
	
	public void doRequest(){
		newHouseList.clear();
		oldHouseList.clear();
		rentHouseList.clear();
		newsList.clear();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", uId);
		params.put("siteId", siteId);
		params.put("type", String.valueOf(type));
		params.put("num", String.valueOf(num));
		params.put("page", String.valueOf(page));
		
		String url = StringUtil.getRequestUrl(Constants.USER_FAVORITE_LIST, params);
		Logger.d(TAG, url); 
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putSerializable("newHouseList", newHouseList);
                        	data.putSerializable("oldHouseList", oldHouseList);
                        	data.putSerializable("rentHouseList", rentHouseList);
                        	data.putSerializable("newsList", newsList);
                        	message.setData(data);
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
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");

                count = Integer.parseInt(dataObj.optString("count"));

                //新房
                JSONArray newHouseArray = dataObj.optJSONArray("xinfang");
                if(newHouseArray != null && newHouseArray.length() > 0){
                	for(int i = 0; i < newHouseArray.length(); i++){
                		JSONObject houseJson = newHouseArray.getJSONObject(i);
                		
                		FavHouse house = new FavHouse();
                		house.setProjectId(houseJson.optString("projectId"));
                		house.setProjectName(houseJson.optString("projectName"));
                		house.setAreaName(houseJson.optString("areaName"));
                		house.setPropertyType(houseJson.optString("propertyType"));
                		house.setProjectFeature(houseJson.optString("projectFeature"));
                		house.setSaleState(houseJson.optString("saleState"));
                		house.setEffectPhoto(houseJson.optString("effectPhoto"));
                		house.setAveragePrice(houseJson.optString("averagePrice"));
                		house.setCreateTime(houseJson.optString("createTime"));
                		
                		newHouseList.add(house);
                	}
                }
                
               //二手房
                JSONArray oldHouseArray = dataObj.optJSONArray("oldhouse");
                if(oldHouseArray != null && oldHouseArray.length() > 0){
                	for(int i = 0; i < oldHouseArray.length(); i++){
                		JSONObject houseJson = oldHouseArray.getJSONObject(i);
                		
                		FavHouse house = new FavHouse();
                		house.setProjectId(houseJson.optString("aid"));
                		house.setProjectName(houseJson.optString("projname"));
                		house.setAreaName(houseJson.optString("areaName"));
                		house.setSaletitle(houseJson.optString("saletitle"));
                		house.setRoomtype(houseJson.optString("roomtype"));
                		house.setHouseArea(houseJson.optString("housearea"));
                		house.setPrice(houseJson.optString("price"));
                		house.setEffectPhoto(houseJson.optString("housephoto"));
                		house.setCreateTime(houseJson.optString("createTime"));
                		
                		oldHouseList.add(house);
                	}
                }
                
                
              //租房房
                JSONArray rentHouseArray = dataObj.optJSONArray("zufang");
                if(rentHouseArray != null && rentHouseArray.length() > 0){
                	for(int i = 0; i < rentHouseArray.length(); i++){
                		JSONObject houseJson = rentHouseArray.getJSONObject(i);
                		
                		FavHouse house = new FavHouse();
                		house.setProjectId(houseJson.optString("aid"));
                		house.setProjectName(houseJson.optString("projname"));
                		house.setAreaName(houseJson.optString("areaName"));
                		house.setHiretitle(houseJson.optString("hiretitle"));
                		house.setRoomtype(houseJson.optString("roomtype"));
                		house.setHouseArea(houseJson.optString("housearea"));
                		house.setPrice(houseJson.optString("price"));
                		house.setEffectPhoto(houseJson.optString("housephoto"));
                		house.setCreateTime(houseJson.optString("createTime"));
                		
                		rentHouseList.add(house);
                	}
                }
                
              //资讯
                JSONArray newsArray = dataObj.optJSONArray("zixun");
                if(newsArray != null && newsArray.length() > 0){
                	for(int i = 0; i < newsArray.length(); i++){
                		JSONObject newsJson = newsArray.getJSONObject(i);
                		
                		FavNews news = new FavNews();
                		news.setNewsId(newsJson.optString("newsId"));
                		news.setTitle(newsJson.optString("title"));
                		news.setPhotoUrl(newsJson.optString("photoUrl"));
                		news.setCreateTime(newsJson.optString("createTime"));
                		news.setUrl(newsJson.optString("url"));
                		
                		newsList.add(news);
                	}
                }
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
