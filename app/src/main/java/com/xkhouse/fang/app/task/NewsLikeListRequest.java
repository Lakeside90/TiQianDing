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
import com.xkhouse.fang.app.entity.Atlas;
import com.xkhouse.fang.app.entity.News;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** 
 * @Description: 猜你喜欢（资讯推荐） 
 * @author wujian  
 * @date 2015-9-6 下午3:50:31  
 */
public class NewsLikeListRequest {

	private String TAG = "NewsLikeListRequest";
	private RequestListener requestListener;
	
	private int num;	//返回数量，
	private int page;	//分页，默认为1
	private String siteId;	//站点ID
	
	private String code;	//返回状态
	private String msg;				//返回提示语
	
	private ArrayList<News> newsList = new ArrayList<News>();


    public NewsLikeListRequest(){
        super();
    }

	public NewsLikeListRequest(String siteId, int num, int page, RequestListener requestListener) {
		this.siteId = siteId; 
		this.num = num;
		this.page = page;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, int num, int page) {
		this.siteId = siteId; 
		this.num = num;
		this.page = page;
	}
	
	public void doRequest(){
		newsList.clear();
		Map<String, String> params = new HashMap<String, String>();
        params.put("num", String.valueOf(num));
        params.put("page", String.valueOf(page));
        params.put("siteId", siteId);
        String url = StringUtil.getRequestUrl(Constants.NEWS_LIKE, params);
        Logger.d(TAG, url);
	            
        StringRequest request = new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
               
                parseResult(response);
                
                Message message = new Message();
                Bundle data = new Bundle();

                if(Constants.SUCCESS_CODE_OLD.equals(code)){
                    AppCache.writeNewsLikeJson(siteId, response);

                    data.putSerializable("newsList", newsList);
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
            	
                if (!Constants.SUCCESS_CODE_OLD.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i < jsonArray.length(); i++) {
	                	JSONObject newsJson = jsonArray.getJSONObject(i);
	                	
	                	News news = new News();
	                	news.setCreateTime(newsJson.optString("createTime"));
	                	news.setNewsId(newsJson.optString("newsId"));
	                	news.setPhotoUrl(newsJson.optString("photoUrl"));
	                	news.setTitle(newsJson.optString("title"));
	                	
	                	newsList.add(news);
	                }
	        	}


                JSONObject atlasObj = jsonObject.optJSONObject("data").optJSONObject("atlas");
                Atlas atlas = null;
                if(atlasObj != null){
                    atlas = new Atlas();
                    atlas.setAid(atlasObj.optString("aid"));
                    atlas.setTitle(atlasObj.optString("title"));
                    atlas.setaPhotoUrl(atlasObj.optString("aPhotoUrl"));
                    atlas.setbPhotoUrl(atlasObj.optString("bPhotoUrl"));
                    atlas.setcPhotoUrl(atlasObj.optString("cPhotoUrl"));
                    atlas.setType(atlasObj.optString("type"));
                }

                if(newsList != null && newsList.size()>0){
                    newsList.get(newsList.size() - 1).setAtlas(atlas);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}


    public ArrayList<News> getNewsList() {
        return newsList;
    }
}
