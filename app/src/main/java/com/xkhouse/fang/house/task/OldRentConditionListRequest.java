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
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 二手房，租房筛序条件列表（这两个放在一起，与新房分开） 
* @author wujian  
* @date 2015-9-24 上午9:35:27
 */
public class OldRentConditionListRequest {

	private String TAG = "OldRentConditionListRequest";
	private RequestListener requestListener;
	
	private String siteId;  //站点 

	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<Area> areaList = new ArrayList<Area>();  					//区域（二手房，租房）
	private ArrayList<CommonType> houseTypeList = new ArrayList<CommonType>();  	//物业类型（二手房）
	private ArrayList<CommonType> priceZoneList = new ArrayList<CommonType>();  	//单价（二手房）
	private ArrayList<CommonType> sortList = new ArrayList<CommonType>();  			//排序（二手房）
	private ArrayList<CommonType> roomTypeList = new ArrayList<CommonType>();  		//户型（二手房，租房）
	private ArrayList<CommonType> areaZoneList = new ArrayList<CommonType>();  		//面积（二手房，租房）
	private ArrayList<CommonType> buildingAgeList = new ArrayList<CommonType>(); 	//房龄（二手房）
	private ArrayList<CommonType> roomFaceList = new ArrayList<CommonType>();  		//朝向（二手房）
	private ArrayList<CommonType> memberTypeList = new ArrayList<CommonType>();  	//来源（二手房，租房）
	private ArrayList<CommonType> priceZoneHireList = new ArrayList<CommonType>();	//租金（租房）
	private ArrayList<CommonType> sortHireList = new ArrayList<CommonType>();  		//排序（租房）
	private ArrayList<CommonType> isShareList = new ArrayList<CommonType>();  		//  整租/合租（租房）
	private ArrayList<CommonType> fitmentList = new ArrayList<CommonType>();  		//  装修（租房）
	
	
	public OldRentConditionListRequest(String siteId, RequestListener requestListener) {
		this.siteId = siteId;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId) {
		this.siteId = siteId; 
	}
	
	public void doRequest(){
		areaList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
		String url = StringUtil.getRequestUrl(Constants.OLD_HOUSE_TAB_LIST, params);
		Logger.d(TAG, url);
		 
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE_OLD.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putSerializable("areaList", areaList);
                        	data.putSerializable("houseTypeList", houseTypeList);
                        	data.putSerializable("priceZoneList", priceZoneList);
                        	data.putSerializable("sortList", sortList);
                        	data.putSerializable("roomTypeList", roomTypeList);
                        	data.putSerializable("areaZoneList", areaZoneList);
                        	data.putSerializable("buildingAgeList", buildingAgeList);
                        	data.putSerializable("roomFaceList", roomFaceList);
                        	data.putSerializable("memberTypeList", memberTypeList);
                        	data.putSerializable("priceZoneHireList", priceZoneHireList);
                        	data.putSerializable("sortHireList", sortHireList);
                        	data.putSerializable("isShareList", isShareList);
                        	data.putSerializable("fitmentList", fitmentList);
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
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                JSONArray areaArray = dataObj.optJSONArray("getArea");
	        	if (areaArray != null && areaArray.length() > 0) {
	                for (int i = 0; i <= areaArray.length() - 1; i++) {
	                	JSONObject areaJson = areaArray.getJSONObject(i);
	                	Area area = new Area();
	                	area.setAreaId(areaJson.optString("aid"));
	                	area.setAreaName(areaJson.optString("areaName"));
	                	area.setLatitude(areaJson.optString("latitude"));
	                	area.setLongitude(areaJson.optString("longitude"));
	                	areaList.add(area);
	                }
	        	}
	        	
	        	JSONArray roomTypeArray = dataObj.optJSONArray("getRoomType");
	        	if (roomTypeArray != null && roomTypeArray.length() > 0) {
	                for (int i = 0; i <= roomTypeArray.length() - 1; i++) {
	                	JSONObject typeJson = roomTypeArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	roomTypeList.add(type);
	                }
	        	}
	        	
	        	JSONArray priceZoneArray = dataObj.optJSONArray("getPriceZone");
	        	if (priceZoneArray != null && priceZoneArray.length() > 0) {
	                for (int i = 0; i <= priceZoneArray.length() - 1; i++) {
	                	JSONObject typeJson = priceZoneArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	priceZoneList.add(type);
	                }
	        	}
	        	
	        	JSONArray sortArray = dataObj.optJSONArray("getSort");
	        	if (sortArray != null && sortArray.length() > 0) {
	                for (int i = 0; i <= sortArray.length() - 1; i++) {
	                	JSONObject typeJson = sortArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	sortList.add(type);
	                }
	        	}
	        	
	        	JSONArray houseTypeArray = dataObj.optJSONArray("getHouseType");
	        	if (houseTypeArray != null && houseTypeArray.length() > 0) {
	                for (int i = 0; i <= houseTypeArray.length() - 1; i++) {
	                	JSONObject typeJson = houseTypeArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	houseTypeList.add(type);
	                }
	        	}
	        	
	        	JSONArray areaZoneArray = dataObj.optJSONArray("getAreaZone");
	        	if (areaZoneArray != null && areaZoneArray.length() > 0) {
	                for (int i = 0; i <= areaZoneArray.length() - 1; i++) {
	                	JSONObject typeJson = areaZoneArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	areaZoneList.add(type);
	                }
	        	}
	        	
	        	JSONArray buildingAgeArray = dataObj.optJSONArray("getbuildingera");
	        	if (buildingAgeArray != null && buildingAgeArray.length() > 0) {
	                for (int i = 0; i <= buildingAgeArray.length() - 1; i++) {
	                	JSONObject typeJson = buildingAgeArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	buildingAgeList.add(type);
	                }
	        	}
	        	
	        	JSONArray roomFaceArray = dataObj.optJSONArray("getRoomFace");
	        	if (roomFaceArray != null && roomFaceArray.length() > 0) {
	                for (int i = 0; i <= roomFaceArray.length() - 1; i++) {
	                	JSONObject typeJson = roomFaceArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	roomFaceList.add(type);
	                }
	        	}
	        	
	        	JSONArray fitmentArray = dataObj.optJSONArray("getFitment");
	        	if (fitmentArray != null && fitmentArray.length() > 0) {
	                for (int i = 0; i <= fitmentArray.length() - 1; i++) {
	                	JSONObject typeJson = fitmentArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	fitmentList.add(type);
	                }
	        	}
	        	
	        	JSONArray memberTypeArray = dataObj.optJSONArray("getMemberType");
	        	if (memberTypeArray != null && memberTypeArray.length() > 0) {
	                for (int i = 0; i <= memberTypeArray.length() - 1; i++) {
	                	JSONObject typeJson = memberTypeArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	memberTypeList.add(type);
	                }
	        	}
	        	
	        	JSONArray isShareArray = dataObj.optJSONArray("getIsShare");
	        	if (isShareArray != null && isShareArray.length() > 0) {
	                for (int i = 0; i <= isShareArray.length() - 1; i++) {
	                	JSONObject typeJson = isShareArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	isShareList.add(type);
	                }
	        	}
	        	
	        	JSONArray sortHireArray = dataObj.optJSONArray("getsortHire");
	        	if (sortHireArray != null && sortHireArray.length() > 0) {
	                for (int i = 0; i <= sortHireArray.length() - 1; i++) {
	                	JSONObject typeJson = sortHireArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	sortHireList.add(type);
	                }
	        	}
	        	
	        	JSONArray priceZoneHireArray = dataObj.optJSONArray("getPriceZoneHire");
	        	if (priceZoneHireArray != null && priceZoneHireArray.length() > 0) {
	                for (int i = 0; i <= priceZoneHireArray.length() - 1; i++) {
	                	JSONObject typeJson = priceZoneHireArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("key"));
	                	type.setName(typeJson.optString("name"));
	                	priceZoneHireList.add(type);
	                }
	        	}
	        	
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
