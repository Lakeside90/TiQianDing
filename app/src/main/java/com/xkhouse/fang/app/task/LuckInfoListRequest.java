package com.xkhouse.fang.app.task;

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
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.app.entity.LuckInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页获取抽奖信息
 */
public class LuckInfoListRequest {

	private String TAG = LuckInfoListRequest.class.getSimpleName();

	private RequestListener requestListener;

	private int num;	//返回数量，
	private int page;	//分页，默认为1
	private String siteId;	//站点ID

	private String code;	//返回状态
	private String msg;				//返回提示语

	private ArrayList<LuckInfo> luckInfoList = new ArrayList<>();


    public LuckInfoListRequest(){
        super();
    }

	public LuckInfoListRequest(String siteId, int num, int page, RequestListener requestListener) {
		this.siteId = siteId;
		this.num = num;
		this.page = page;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, int num, int page, RequestListener requestListener) {
		this.siteId = siteId;
		this.num = num;
		this.page = page;
        this.requestListener = requestListener;
	}
	
	public void doRequest(){
		luckInfoList.clear();
		Map<String, String> params = new HashMap<>();
        params.put("num", String.valueOf(num));
        params.put("page", String.valueOf(page));
        params.put("siteid", siteId);
        String url = StringUtil.getRequestUrl(Constants.LUCKINFO_LIST, params);
        Logger.d(TAG, url);
	            
        StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                Bundle data = new Bundle();

                if(Constants.SUCCESS_CODE.equals(code)){
                    if (isNeedCache()) {
                        AppCache.writeHomeLuckJson(siteId, response);
                    }

                    data.putSerializable("luckInfoList", luckInfoList);
                    data.putString("siteId", siteId);
                    message.setData(data);
                	message.what = Constants.SUCCESS_DATA_FROM_NET;
                }else{
                    data.putString("msg", msg);
                    data.putString("siteId", siteId);
                    message.setData(data);
                   message.what = Constants.NO_DATA_FROM_NET;
                }
                requestListener.sendMessage(message);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            	Logger.e(TAG, error.toString());
                Message message = new Message();
                Bundle data = new Bundle();
                data.putString("siteId", siteId);
                message.setData(data);
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
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i < jsonArray.length(); i++) {
	                	JSONObject json = jsonArray.getJSONObject(i);

                        LuckInfo luckInfo = new LuckInfo();
                        luckInfo.setId(json.optString("id"));
                        luckInfo.setImg(json.optString("img"));
                        luckInfo.setImg(json.optString("title"));
                        luckInfo.setJoin_count(json.optString("join_count"));
                        luckInfo.setCount(json.optString("count"));
                        luckInfo.setPub_type(json.optString("pub_type"));

	                	luckInfoList.add(luckInfo);
	                }
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
            return  true;
        }
    }

    public ArrayList<LuckInfo> getLuckInfoList() {
        return luckInfoList;
    }
}
