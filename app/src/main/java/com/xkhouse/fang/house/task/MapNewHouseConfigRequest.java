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
import com.xkhouse.fang.app.service.HouseConfigDbService;
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
* @Description: 通过配置标识获取相应配置子选项列表 (新房--更多，价格，类型)
* @author wujian  
* @date 2015-9-18 下午1:48:46
 */
public class MapNewHouseConfigRequest {

	private String TAG = "MapNewHouseConfigRequest";
	private RequestListener requestListener;
	
	private String siteId;	//站点ID
	//配置项Key[价格  类型  排序 楼盘特色  户型 面积  装修状态  销售状态]
	private String configName = "PRICE_RANGE$PROPERTY_TYPE$ORDER$FEATURE_LIST$BEDROOM$AREA_TYPE$RENOVATE_STATE$SALE_STATE";	
	
	private String code;	//返回状态
	private String msg;		//返回提示语
	
	private ArrayList<CommonType> priceList = new ArrayList<CommonType>();			//价格
	private ArrayList<CommonType> propertyList = new ArrayList<CommonType>();			//类型
	private ArrayList<CommonType> orderList = new ArrayList<CommonType>();			//排序
	private ArrayList<CommonType> featureList = new ArrayList<CommonType>();		//楼盘特色
	private ArrayList<CommonType> houseTypeList = new ArrayList<CommonType>();		//户型
	private ArrayList<CommonType> spaceList = new ArrayList<CommonType>();			//面积
	private ArrayList<CommonType> renovateList = new ArrayList<CommonType>();		//装修状态
	private ArrayList<CommonType> saleStateList = new ArrayList<CommonType>();		//销售状态
	
	public MapNewHouseConfigRequest(String siteId, RequestListener requestListener) {
		this.siteId = siteId; 
		this.requestListener = requestListener;
	}
	
	//星空宝佣金排序
	public MapNewHouseConfigRequest(String siteId, String configName, RequestListener requestListener) {
		this.siteId = siteId; 
		this.configName = configName;
		this.requestListener = requestListener;
	}

	
	public void doRequest(){ 
		priceList.clear();
		propertyList.clear();
		orderList.clear();
		featureList.clear();
		houseTypeList.clear();
		spaceList.clear();
		renovateList.clear();
		saleStateList.clear();
		  
		Map<String, String> params = new HashMap<String, String>();
        params.put("siteId", siteId);
        params.put("configName", configName);
        String url = StringUtil.getRequestUrl(Constants.CONFIG_LIST, params);
        Logger.d(TAG, url);
	        
		StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                if(Constants.SUCCESS_CODE_OLD.equals(code)){
                	message.what = Constants.SUCCESS_DATA_FROM_NET;
                	//插入数据库
                	HouseConfigDbService dbService = new HouseConfigDbService();
                	dbService.insertPriceList(priceList, siteId);
                	dbService.insertBuildList(propertyList, siteId);
                	dbService.insertOrderList(orderList, siteId);
                	dbService.insertFeatureList(featureList, siteId);
                	dbService.insertHouseTypeList(houseTypeList, siteId);
                	dbService.insertSpaceList(spaceList, siteId);
                	dbService.insertRenovateList(renovateList, siteId);
                	dbService.insertSaleStateList(saleStateList, siteId);
                	
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
                
                JSONArray priceArray = dataObj.optJSONArray("PRICE_RANGE");
	        	if (priceArray != null && priceArray.length() > 0) {
	                for (int i = 0; i < priceArray.length(); i++) {
	                	JSONObject typeJson = priceArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	priceList.add(type);
	                }
	        	}
	        	
	        	JSONArray propertyArray = dataObj.optJSONArray("PROPERTY_TYPE");
	        	if (propertyArray != null && propertyArray.length() > 0) {
	                for (int i = 0; i < propertyArray.length(); i++) {
	                	JSONObject typeJson = propertyArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	propertyList.add(type);
	                }
	        	}
	        	
	        	JSONArray orderArray = dataObj.optJSONArray("ORDER");
	        	if (orderArray != null && orderArray.length() > 0) {
	                for (int i = 0; i < orderArray.length(); i++) {
	                	JSONObject typeJson = orderArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	orderList.add(type);
	                }
	        	}
	        	
	        	JSONArray featureArray = dataObj.optJSONArray("FEATURE_LIST");
	        	if (featureArray != null && featureArray.length() > 0) {
	                for (int i = 0; i < featureArray.length(); i++) {
	                	JSONObject typeJson = featureArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	featureList.add(type);
	                }
	        	}
	        	
	        	JSONArray houseTypeArray = dataObj.optJSONArray("BEDROOM");
	        	if (houseTypeArray != null && houseTypeArray.length() > 0) {
	                for (int i = 0; i < houseTypeArray.length(); i++) {
	                	JSONObject typeJson = houseTypeArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	houseTypeList.add(type);
	                }
	        	}
	        	
	        	JSONArray spaceArray = dataObj.optJSONArray("AREA_TYPE");
	        	if (spaceArray != null && spaceArray.length() > 0) {
	                for (int i = 0; i < spaceArray.length(); i++) {
	                	JSONObject typeJson = spaceArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	spaceList.add(type);
	                }
	        	}
	        	
	        	JSONArray renovateArray = dataObj.optJSONArray("RENOVATE_STATE");
	        	if (renovateArray != null && renovateArray.length() > 0) {
	                for (int i = 0; i < renovateArray.length(); i++) {
	                	JSONObject typeJson = renovateArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	renovateList.add(type);
	                }
	        	}
	        	
	        	JSONArray saleArray = dataObj.optJSONArray("SALE_STATE");
	        	if (saleArray != null && saleArray.length() > 0) {
	                for (int i = 0; i < saleArray.length(); i++) {
	                	JSONObject typeJson = saleArray.getJSONObject(i);
	                	CommonType type = new CommonType();
	                	type.setId(typeJson.optString("enumId"));
	                	type.setName(typeJson.optString("enumName"));
	                	saleStateList.add(type);
	                }
	        	}
	        	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
