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
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 预定列表
 */
public class BookedInfoListRequest {

	private String TAG = BookedInfoListRequest.class.getSimpleName();

    public static final String LIST_RECOMMED = "1";
    public static final String LIST_ALL = "";

	private RequestListener requestListener;

	private int num;	//返回数量，
	private int page;	//分页，默认为1
	private String siteId;	//站点ID
	private String isRecommend;	// 1：推荐

	private String code;	//返回状态
	private String msg;				//返回提示语
	
	private ArrayList<BookedInfo> bookedInfoList = new ArrayList<>();


    public BookedInfoListRequest(){
        super();
    }

	public BookedInfoListRequest(String isRecommend, String siteId, int num, int page, RequestListener requestListener) {
        this.isRecommend = isRecommend;
		this.siteId = siteId; 
		this.num = num;
		this.page = page;
		this.requestListener = requestListener;
	}
	
	public void setData(String isRecommend, String siteId, int num, int page) {
        this.isRecommend = isRecommend;
		this.siteId = siteId; 
		this.num = num;
		this.page = page;
	}
	
	public void doRequest(){
		bookedInfoList.clear();
		Map<String, String> params = new HashMap<>();
        params.put("num", String.valueOf(num));
        params.put("page", String.valueOf(page));
        params.put("siteId", siteId);
        params.put("is_recommend", isRecommend);
        String url = StringUtil.getRequestUrl(Constants.BOOKEDINFO_LIST, params);
        Logger.d(TAG, url);
	            
        StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                Bundle data = new Bundle();

                if(Constants.SUCCESS_CODE.equals(code)){
                    AppCache.writeBookInfoRecommedJson(siteId, response);

                    data.putSerializable("bookedInfoList", bookedInfoList);
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
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i < jsonArray.length(); i++) {
	                	JSONObject json = jsonArray.getJSONObject(i);
	                	
	                	BookedInfo bookedInfo = new BookedInfo();
                        bookedInfo.setBookingId(json.optString("booking_id"));
                        bookedInfo.setBusinessId(json.optString("business_id"));
                        bookedInfo.setDiscount(json.optString("discount"));
                        bookedInfo.setBusinessName(json.optString("business_name"));
                        bookedInfo.setAverageConsump(json.optString("average_consump"));
                        bookedInfo.setBusinessAddress(json.optString("business_address"));

                        JSONArray labelArray = json.optJSONArray("business_label");
                        if (labelArray != null && labelArray.length() > 0) {
                            String[] labels = new String[labelArray.length()];
                            for (int j = 0; j < labelArray.length(); j++) {
                                labels[j] = labelArray.optString(j);
                            }
                            bookedInfo.setBusinessLabel(labels);
                        }
	                	
	                	bookedInfoList.add(bookedInfo);
	                }
	        	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}


    public ArrayList<BookedInfo> getNewsList() {
        return bookedInfoList;
    }
}
