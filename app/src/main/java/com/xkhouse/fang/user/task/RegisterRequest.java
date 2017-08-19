package com.xkhouse.fang.user.task;

import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.service.UserService;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
* @Description: 注册接口 
* @author wujian  
* @date 2015-10-23 上午10:18:41
 */
public class RegisterRequest {

	private String TAG = "RegisterRequest";
	private RequestListener requestListener;
	
	private String memberType;  	//用户类型id 1个人 2经纪人
	private String mobile; 			//手机号
	private String passWord; 		//密码
	private String mobileCode; 		//验证码
	private String userId;			//百度userId
	private String channelId;		//百度channelId
	private String inviteCode;		//邀请码
	private String mobileMac;		//手机标识mac地址

	
	private String code;	//返回状态
	private String msg;		//返回提示语
	private User user;
	
	
	public RegisterRequest(String memberType, String mobile, String passWord,
			String mobileCode, String userId, String channelId,String inviteCode,String mobileMac,
			RequestListener requestListener) {
		
		this.memberType = memberType;
		this.mobile = mobile;
		this.passWord = passWord;
		this.mobileCode = mobileCode;
		this.requestListener = requestListener;
		if(userId == null){
			userId = "";
		}
		if(channelId == null){
			channelId = "";
		}
		this.userId = userId;
		this.channelId = channelId;

        if(inviteCode == null){
            inviteCode = "";
        }
        this.inviteCode = inviteCode;
        this.mobileMac = mobileMac;

	}
	
	
	public void setData(String memberType, String mobile, String passWord, 
			String mobileCode, String userId, String channelId,String inviteCode,String mobileMac) {
		this.memberType = memberType;
		this.mobile = mobile;
		this.passWord = passWord;
		this.mobileCode = mobileCode;
		if(userId == null){
			userId = "";
		}
		if(channelId == null){
			channelId = "";
		}
		this.userId = userId;
		this.channelId = channelId;
        if(inviteCode == null){
            inviteCode = "";
        }
        this.inviteCode = inviteCode;
        this.mobileMac = mobileMac;
	}
	
	public void doRequest(){
		
		StringRequest request = new StringRequest(Method.POST,
				Constants.USER_REGISTER, new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Logger.d(TAG, response);
                       
                        parseResult(response);
                        
                        Message message = new Message();
                        if(Constants.SUCCESS_CODE.equals(code)){
                        	
                        	UserService userService = new UserService(); 
                        	userService.insertUser(user);
                        	Preference.getInstance().writeIsLogin(true);
                        	Preference.getInstance().writeUID(user.getUid());
                        	Preference.getInstance().writePassword(user.getPassword());
                        	
                        	message.obj = "注册成功";
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
                })
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<>();

				params.put("memberType", memberType);
				params.put("mobile", mobile);
				params.put("mobileCode", mobileCode);
                if (!StringUtil.isEmpty(passWord)){
                    params.put("passWord", passWord);
                }
				params.put("userId", userId);
				params.put("channelId", channelId);
				params.put("device_type", "3");
				params.put("inviteCode", inviteCode);
				params.put("mobileMac", mobileMac);

				Logger.d(TAG, StringUtil.getRequestUrl(Constants.USER_REGISTER, params));

				return params;
			}

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
                	msg = jsonObject.optString("data");
                    return;
                }
                
                JSONObject dataObj = jsonObject.optJSONObject("data");
                
                user = new User();
                user.setPassword(passWord);
                user.setUid(dataObj.optString("member_id"));
                user.setUserName(dataObj.optString("member_username"));
                user.setRealName(dataObj.optString("member_realname"));
                user.setNickName(dataObj.optString("member_nickname"));
                user.setEmail(dataObj.optString("member_email"));
                user.setPhone(dataObj.optString("member_phone"));
                user.setMobile(dataObj.optString("member_mobile"));
                user.setAge(dataObj.optString("member_age"));
                user.setLastLogintTime(dataObj.optString("member_lastelogintime"));
                user.setLoginNum(dataObj.optString("member_loginnum"));
                user.setSex(dataObj.optString("member_sex"));
                user.setCity(dataObj.optString("member_city"));
                user.setHeadPhoto(dataObj.optString("member_headphoto"));
                user.setNuid(dataObj.optString("member_salt"));
                user.setMemberType(dataObj.optString("member_type"));
                user.setOldhouseSaleExtAuth(dataObj.optString("oldhouse_sale_ext_auth"));
                user.setOldhouseHireExtAuth(dataObj.optString("oldhouse_hire_ext_auth"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
