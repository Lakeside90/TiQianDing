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
import com.xkhouse.fang.house.entity.HQuestion;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @Description: 获取楼盘问答列表
* @author wujian  
* @date 2016-6-23
 */
public class HouseQuestionListRequest {

	private String TAG = "HouseQuestionListRequest";
	private RequestListener requestListener;

	private String siteId;  //站点
	private String uid;  //用户id
	private String pid;  //小区id
	private int page;	//分页索引
	private int num;	//每页个数

	private String code;	//返回状态
	private String msg;		//返回提示语
    private int count;

	private ArrayList<HQuestion> questionList = new ArrayList<HQuestion>();

	public HouseQuestionListRequest(String siteId, String uid, String pid, int page, int num, RequestListener requestListener) {
		this.siteId = siteId;
		this.uid = uid;
		this.pid = pid;
		this.page = page;
		this.num = num;
		this.requestListener = requestListener;
	}
	
	public void setData(String siteId, String uid, String pid, int page, int num) {
		this.siteId = siteId;
        this.uid = uid;
        this.pid = pid;
		this.page = page;
		this.num = num;
	}
	
	public void doRequest(){
		questionList.clear();
		Map<String, String> params = new HashMap<String, String>();
		params.put("siteId", siteId);
		params.put("pid", pid);
        params.put("page", String.valueOf(page));
        params.put("num", String.valueOf(num));
        if (!StringUtil.isEmpty(uid)){
            params.put("uid", String.valueOf(uid));
        }
        String url = StringUtil.getRequestUrl(Constants.HOUSE_QUESTION_LIST, params);
        Logger.d(TAG, url);
        
		StringRequest request = new StringRequest(url, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	message.obj = questionList;
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
            	code = jsonObject.optString("code");
            	
                if (!Constants.SUCCESS_CODE.equals(code)) {
                	msg = jsonObject.optString("msg");
                    return;
                }
                
                JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("list");
               
	        	if (jsonArray != null && jsonArray.length() > 0) {
	                for (int i = 0; i <= jsonArray.length() - 1; i++) {
	                	JSONObject questionJson = jsonArray.getJSONObject(i);

                        HQuestion question = new HQuestion();
                        question.setId(questionJson.optString("id"));
                        question.setPid(questionJson.optString("pid"));
                        question.setQuestion(questionJson.optString("question"));
                        question.setReply(questionJson.optString("reply"));
                        question.setPostTime(questionJson.optString("postTime"));
                        question.setReplyTime(questionJson.optString("replyTime"));

	                	questionList.add(question);
	                }
	        	}

                count = Integer.parseInt(jsonObject.optJSONObject("data").optString("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
