package com.xkhouse.fang.money.task;

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
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 星空宝 （赚佣楼盘列表） 
* @author wujian  
* @date 2015-10-14 下午4:10:44
 */
public class XKBHouseListRequest {

	private String TAG = "XKBHouseListRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 
	private String searchContent; //筛选条件
	private int page;	//分页索引
	private int num;	//每页个数
	private String url;
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<House> houseList = new ArrayList<House>();
	private int count;


    public XKBHouseListRequest(){
        super();
    }

	public XKBHouseListRequest(String siteId, int page, int num, String searchContent, RequestListener requestListener) {
		this.siteId = siteId;
		this.page = page;
		this.num = num;
		this.searchContent = searchContent;
		this.requestListener = requestListener;
		
	}
	
	public void setData(String siteId, int page, int num, String searchContent) {
		this.siteId = siteId; 
		this.page = page;
		this.num = num;
		this.searchContent = searchContent;
	}
	
	public void doRequest(){
		houseList.clear();
		
		url = Constants.XKB_HOUSE_LIST + "?siteId="+ siteId + "&page=" + page + "&num=" + num + searchContent;
		
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code) || ("106".equals(code) && page > 1)){
                        	message.obj = houseList;
                        	message.arg1 = count;
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;

                            //缓存第一页没有筛选过的数据
                            if(isNeedCache()){
                                AppCache.writeMoneyHouseListJson(siteId, response);
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
	                	
	                	House house = new House();
	                	house.setProjectId(houseJson.optString("projectId"));
	                	house.setPropertyType(houseJson.optString("propertyType"));
	                	house.setProjectName(houseJson.optString("projectName"));
	                	house.setEffectPhoto(houseJson.optString("img"));
	                	house.setAreaName(houseJson.optString("areaName"));
	                	house.setLatitude(houseJson.optString("latitude"));
	                	house.setLongitude(houseJson.optString("longitude"));
	                	house.setCommission(houseJson.optString("commission"));
	                	house.setIntent(houseJson.optString("intent"));
	                	house.setDiscount(houseJson.optString("discount"));
	                	house.setEndTimeStr(houseJson.optString("endTimeStr"));
	                	houseList.add(house);
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


    /**
     * 缓存数据的条件：第一页，且不包含筛选条件
     * @return
     */
    public boolean isNeedCache(){
        if (page != 1){
            return false;
        } else{
            if (searchContent.contains("&areaId=") || searchContent.contains("&type=")
                    || searchContent.contains("&order=") || searchContent.contains("&k=")) {
                return false;
            }else{
                return true;
            }
        }
    }

    public ArrayList<House> getHouseList() {
        return houseList;
    }
}
