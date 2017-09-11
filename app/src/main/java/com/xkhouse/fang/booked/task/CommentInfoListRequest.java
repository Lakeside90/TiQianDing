package com.xkhouse.fang.booked.task;

import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.CommentInfo;
import com.xkhouse.fang.house.entity.CustomHouse;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 获取评论列表
* @author wujian  
* @date 2015-9-21 下午2:20:00
 */
public class CommentInfoListRequest {

	private String TAG = CommentInfoListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String business_id;  //站点
	private int page;	//分页索引
	private int num;	//每页个数

	private String code;	//返回状态
	private String msg;		//返回提示语
    private int count;

	private ArrayList<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();

	public CommentInfoListRequest(String business_id, int page, int num, RequestListener requestListener) {
		this.business_id = business_id;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	public void setData(String business_id, int page, int num) {
		this.business_id = business_id;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		commentInfoList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("business_id", business_id);
        params.put("page", String.valueOf(page));
        params.put("num", String.valueOf(num));

        String url = StringUtil.getRequestUrl(Constants.STORE_COMMENT, params);
        Logger.d(TAG, url);
        
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = commentInfoList;
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
            	code = jsonObject.optString("status");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONArray("data");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject customJson = jsonArray.getJSONObject(i);
	                	
	                	CommentInfo commentInfo = new CommentInfo();

                        commentInfo.setId(customJson.optString("id"));
                        commentInfo.setMember_id(customJson.optString("member_id"));
                        commentInfo.setBusiness_id(customJson.optString("business_id"));
                        commentInfo.setStar_num(customJson.optString("star_num"));
                        commentInfo.setContent(customJson.optString("content"));
                        commentInfo.setReply_content(customJson.optString("reply_content"));
                        commentInfo.setCreate_time(customJson.optString("create_time"));
                        commentInfo.setUpdate_time(customJson.optString("update_time"));
                        commentInfo.setNickname(customJson.optString("nickname"));
                        commentInfo.setHead_img(customJson.optString("head_img"));

                        JSONArray imgArray = customJson.optJSONArray("img");
                        if (imgArray != null && imgArray.length() > 0) {
                            String[] imgs = new String[imgArray.length()];
                            for (int j = 0; j < imgArray.length(); j++) {
                                imgs[j] = imgArray.optString(j);
                            }
                            commentInfo.setImageUrls(imgs);
                        }

                        JSONArray replyArray = customJson.optJSONArray("reply_img");
                        if (replyArray != null && replyArray.length() > 0) {
                            String[] reply = new String[replyArray.length()];
                            for (int j = 0; j < replyArray.length(); j++) {
                                reply[j] = replyArray.optString(j);
                            }
                            commentInfo.setReply_img(reply);
                        }

	                	commentInfoList.add(commentInfo);
	                }
	        	}

//                count = Integer.parseInt(jsonObject.optJSONObject("data").optString("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
