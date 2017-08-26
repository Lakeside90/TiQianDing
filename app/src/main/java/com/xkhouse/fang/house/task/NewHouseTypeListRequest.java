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
import com.xkhouse.fang.house.entity.XKRoom;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* @Description: 新房户型列表
* @author wujian  
* @date 2015-9-23 上午11:45:31
 */
public class NewHouseTypeListRequest {

	private String TAG = "NewHouseTypeListRequest";
	private RequestListener requestListener;

	private String siteId;  //站点
	private String searchContent; //筛选条件
	private int page;	//分页索引
	private int num;	//每页个数
	private String url;

	private String code;	//返回状态
	private String msg;		//返回提示语

	private ArrayList<XKRoom> houseTypeList = new ArrayList<XKRoom>();
	private int count;

    public NewHouseTypeListRequest() {
        super();
    }

	public NewHouseTypeListRequest(String siteId, int page, int num, String searchContent, RequestListener requestListener) {
		this.siteId = siteId;
		this.page = page;
		this.num = num;
		this.searchContent = searchContent;
		this.requestListener = requestListener;
		url = Constants.NEW_HOUSE_TYPE_LIST + "?siteId="+ siteId + "&page=" + page + "&num=" + num + searchContent;
	}

	public void setData(String siteId, int page, int num, String searchContent) {
		this.siteId = siteId; 
		this.page = page;
		this.num = num;
		this.searchContent = searchContent;
		url = Constants.NEW_HOUSE_TYPE_LIST + "?siteId="+ siteId + "&page=" + page + "&num=" + num + searchContent;
	}
	
	public void doRequest(){
        houseTypeList.clear();
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	message.obj = houseTypeList;
                        	message.arg1 = count;
                        	message.what = Constants.SUCCESS_DATA_FROM_NET;

                            if(isNeedCache()){
                                //缓存第一页没有筛选过的数据
                                AppCache.writeNewHouseTypeListJson(siteId, response);
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
	                	
                        XKRoom houseType = new XKRoom();
                        houseType.setPid(houseJson.optString("projectId"));
                        houseType.setHousetypeId(houseJson.optString("housetypeId"));
                        houseType.setAreaName(houseJson.optString("areaName"));
                        houseType.setTypeArea(houseJson.optString("typeArea"));
                        houseType.setPhotoPath(houseJson.optString("photoPath"));
                        houseType.setProjectName(houseJson.optString("projectName"));
                        houseType.setLongitude(houseJson.optString("longitude"));
                        houseType.setLatitude(houseJson.optString("latitude"));
                        houseType.setTotalPrice(houseJson.optString("totalPrice"));
                        houseType.setAveragePrice(houseJson.optString("averagePrice"));
                        houseType.setHouseTitle(houseJson.optString("housetype"));
                        houseType.setSaleState(houseJson.optString("saleState"));

                        houseTypeList.add(houseType);
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


    public Bundle getCacheModel(){
        Bundle data = new Bundle();
        data.putInt("count", count);
        data.putSerializable("houseTypeList", houseTypeList);
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
                    || searchContent.contains("saleState") || searchContent.contains("keyword")
                    || searchContent.contains("order")){
                return false;
            }else{
                return true;
            }
        }
    }
}
