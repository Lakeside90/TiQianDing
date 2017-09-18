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
import com.xkhouse.fang.user.entity.MyBookedInfo;
import com.xkhouse.fang.user.entity.MyCommentInfo;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
* @Description: 我的评价
* @author wujian  
 */
public class MyCommentListRequest {

	private String TAG = MyCommentListRequest.class.getSimpleName();
	private RequestListener requestListener;

	private String token;
	private int num;		//条数
	private int page;		//页数

	private String code;	//返回状态
	private String msg;		//返回提示语
	private int count;

	private ArrayList<MyCommentInfo> commentList = new ArrayList<>();

	public MyCommentListRequest(String token, int page, int num, RequestListener requestListener) {
		
		this.token = token;
		this.num = num;
		this.page = page;
		
		this.requestListener = requestListener;
	}
	
	
	public void setData(String token, int page, int num) {
		this.token = token;
		this.num = num;
		this.page = page;
	}
	
	
	public void doRequest(){
		commentList.clear();

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("pagenum", String.valueOf(num));
		params.put("page", String.valueOf(page));

		String url = StringUtil.getRequestUrl(Constants.USER_COMMENT_LIST, params);
		Logger.d(TAG, url);
		
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	Bundle data = new Bundle();
                        	data.putSerializable("commentList", commentList);
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
            	code = jsonObject.optString("status");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");

                count = Integer.parseInt(dataObj.optString("count"));

                //新房
                JSONArray jsonArray = dataObj.optJSONArray("list");
                if(jsonArray != null && jsonArray.length() > 0){
                	for(int i = 0; i < jsonArray.length(); i++){
                		JSONObject json = jsonArray.getJSONObject(i);

						MyCommentInfo myCommentInfo = new MyCommentInfo();

						myCommentInfo.setBusiness_id(json.optString("business_id"));
						myCommentInfo.setComment_id(json.optString("comment_id"));
						myCommentInfo.setBusiness_name(json.optString("business_name"));
						myCommentInfo.setAverage_consump(json.optString("average_consump"));

						JSONArray labelArray = json.optJSONArray("business_label");
						if (labelArray != null && labelArray.length() > 0) {
							String[] labels = new String[labelArray.length()];
							for (int j = 0; j < labelArray.length(); j++) {
								labels[j] = labelArray.optString(j);
							}
							myCommentInfo.setBusiness_label(labels);
						}

						myCommentInfo.setCover_banner(json.optString("cover_banner"));
						myCommentInfo.setMember_content(json.optString("member_content"));

						JSONArray memberImgArray = json.optJSONArray("member_img");
						if (memberImgArray != null && memberImgArray.length() > 0) {
							String[] memberImg = new String[memberImgArray.length()];
							for (int j = 0; j < memberImgArray.length(); j++) {
								memberImg[j] = memberImgArray.optString(j);
							}
							myCommentInfo.setMember_img(memberImg);
						}

						myCommentInfo.setCreate_time(json.optString("create_time"));
						myCommentInfo.setBusiness_content(json.optString("business_content"));

						JSONArray businessImgArray = json.optJSONArray("business_img");
						if (businessImgArray != null && businessImgArray.length() > 0) {
							String[] businessImg = new String[businessImgArray.length()];
							for (int j = 0; j < businessImgArray.length(); j++) {
								businessImg[j] = businessImgArray.optString(j);
							}
							myCommentInfo.setBusiness_img(businessImg);
						}

                		commentList.add(myCommentInfo);
                	}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
