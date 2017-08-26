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
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 新房列表
* @author wujian  
* @date 2015-9-23 上午11:45:31
 */
public class NewHouseListRequest {

	private String TAG = "NewHouseListRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String searchContent; //筛选条件
	private int page;	//分页索引
	private int num;	//每页个数
	private String url;
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<House> houseList = new ArrayList<House>();
    private ArrayList<XKAd> adList = new ArrayList<XKAd>();         //这是个四不像
	private int count;
	private int adIndex;    //在第几条数据后面插广告


    public NewHouseListRequest(){
       super();
    }

	public NewHouseListRequest(String siteId, int page, int num, String searchContent, RequestListener requestListener) {
		this.siteId = siteId;
		this.page = page;
		this.num = num;
		this.searchContent = searchContent;
		this.requestListener = requestListener;
		url = Constants.NEW_HOUSE_LIST + "?siteId="+ siteId + "&page=" + page + "&num=" + num + searchContent;
	}
	
	//地图找房专用（因为不分页）
	public NewHouseListRequest(String siteId, String searchContent,RequestListener requestListener) {
		this.siteId = siteId;
		this.searchContent = searchContent;
		this.requestListener = requestListener;
		url = Constants.NEW_HOUSE_LIST + "?siteId=" + siteId + searchContent;
	}
	//地图找房专用（因为不分页）
	public void setData(String siteId, String searchContent) {
		this.siteId = siteId; 
		this.searchContent = searchContent;
		url = Constants.NEW_HOUSE_LIST + "?siteId=" + siteId + searchContent;
	}
	
	public void setData(String siteId, int page, int num, String searchContent) {
		this.siteId = siteId; 
		this.page = page;
		this.num = num;
		this.searchContent = searchContent;
		url = Constants.NEW_HOUSE_LIST + "?siteId="+ siteId + "&page=" + page + "&num=" + num + searchContent;
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
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.obj = houseList;
                        	message.arg1 = count;
                            if (page == 1){
                                Bundle data = new Bundle();
                                data.putSerializable("adList", adList);
                                data.putInt("adIndex", adIndex);
                                message.setData(data);
                            }
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;

                            if(isNeedCache()){
                                //缓存第一页没有筛选过的数据
                                if(url.contains("new=1")){
                                    AppCache.writeNewHouseNListJson(siteId, response);
                                }else if(url.contains("y=1")){
                                    AppCache.writeNewHouseDListJson(siteId, response);
                                }else{
                                    AppCache.writeNewHouseListJson(siteId, response);
                                }

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
                return new DefaultRetryPolicy(4*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
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
	                	
	                	House house = new House();
	                	house.setProjectId(houseJson.optString("projectId"));
	                	house.setProjectName(houseJson.optString("projectName"));
	                	house.setEffectPhoto(houseJson.optString("effectPhoto"));
	                	house.setAveragePrice(houseJson.optString("averagePrice"));
	                	house.setSaleState(houseJson.optString("saleState"));
	                	house.setPropertyType(houseJson.optString("propertyType"));
	                	house.setLongitude(houseJson.optString("longitude"));
	                	house.setLatitude(houseJson.optString("latitude"));
	                	house.setJuniorSchool(houseJson.optString("juniorSchool"));
	                	house.setMiddleSchool(houseJson.optString("middleSchool"));
	                	house.setHaveVideo(houseJson.optString("haveVideo"));
	                	house.setProjectFeature(houseJson.optString("projectFeature"));
	                	house.setAreaName(houseJson.optString("areaName"));
	                	house.setGroupDiscountInfo(houseJson.optString("groupDiscountInfo"));
	                	house.setHaveCommission(houseJson.optString("haveCommission"));
	                	house.setHouseType(houseJson.optString("houseType"));
                        house.setIsgroup(houseJson.optString("isgroup"));
                        house.setGroupinfo(houseJson.optString("groupinfo"));
	                	houseList.add(house);
	                }
	        	}
	        	try{
	        		count = Integer.parseInt(dataObj.getString("count"));
	        	}catch (Exception e) {
	        		count = 0;
				}

                if(page == 1){
                    adList.clear();
                    JSONArray adArray = dataObj.optJSONArray("diy");

                    if (adArray != null && adArray.length() > 0) {
                        for (int i = 0; i < adArray.length(); i++) {
                            JSONObject adJson = adArray.getJSONObject(i);

                            XKAd ad = new XKAd();
                            ad.setNewsId(adJson.optString("newsId"));
                            ad.setNewsUrl(adJson.optString("newsUrl"));
                            ad.setPhotoUrl(adJson.optString("photoUrl"));
                            ad.setRemark(adJson.optString("remark"));
                            ad.setTitle(adJson.optString("title"));

                            adList.add(ad);
                        }
                    }
                    try{
                        adIndex = Integer.parseInt(dataObj.getString("num"));
                    }catch (Exception e) {
                        adIndex = 0;
                    }
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
        data.putInt("adIndex", adIndex);
        data.putSerializable("adList", adList);
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
            if (searchContent.contains("areaId") || searchContent.contains("schoolId")
                    || searchContent.contains("propertyType") || searchContent.contains("price")
                    || searchContent.contains("houseType") || searchContent.contains("areaSize")
                    || searchContent.contains("developers") || searchContent.contains("feature")
                    || searchContent.contains("saleState") || searchContent.contains("opentime")
                    || searchContent.contains("keyword") || searchContent.contains("order")) {
                return false;
            }else{
                return true;
            }
        }
    }

}
