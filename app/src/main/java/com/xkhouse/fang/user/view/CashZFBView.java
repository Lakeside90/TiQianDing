package com.xkhouse.fang.user.view;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.task.CashWithdrawCommitRequest;
import com.xkhouse.fang.widget.CustomDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description: 提现至支付宝 
 * @author wujian  
 * @date 2015-10-21 下午2:56:54  
 */
public class CashZFBView implements OnClickListener{

	private Context context;
	private View rootView;
	
	private EditText count_txt;
	private TextView money_txt;
	private EditText name_txt;
	private EditText account_txt;
	private EditText phone_txt;
	private EditText code_txt;
	private TextView get_code_txt;
	private EditText pay_psw_txt;
	private TextView commit_txt;
	
	
	
	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	
	private CashWithdrawCommitRequest commitRequest;
	//参数
	private String phone;		//手机号
	private String userName;	//姓名
	private String verifyCode;	//验证码
	private String type = "0";		//提款类型0支付宝 >0各大银行
	private String cardNo;		//卡号或者支付宝账号
	private String money;		//金额
	private String zfpwd;		//支付密码
	
	
	private String sum;   //余额
	
	private ModelApplication modelApp;
	
	public View getView() {
        return rootView;
    } 
	
	public CashZFBView(Context context, String sum) {
		this.context = context;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		this.sum = sum;
		initView();
		setListener();
		
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_cash_zfb, null);
		
		count_txt = (EditText) rootView.findViewById(R.id.count_txt);
		money_txt = (TextView) rootView.findViewById(R.id.money_txt);
		name_txt = (EditText) rootView.findViewById(R.id.name_txt);
		account_txt = (EditText) rootView.findViewById(R.id.account_txt);
		phone_txt = (EditText) rootView.findViewById(R.id.phone_txt);
		code_txt = (EditText) rootView.findViewById(R.id.code_txt);
		get_code_txt = (TextView) rootView.findViewById(R.id.get_code_txt);
		pay_psw_txt = (EditText) rootView.findViewById(R.id.pay_psw_txt);
		commit_txt = (TextView) rootView.findViewById(R.id.commit_txt);
		
		if(StringUtil.isEmpty(sum)){
			money_txt.setText("--");
		}else {
			money_txt.setText(sum);
		}
		
	}
	
	
	private void setListener(){
		get_code_txt.setOnClickListener(this);
		commit_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.get_code_txt:
			startVerityCodeTask();
			break;
			
		case R.id.commit_txt:
			startCommitTask();
			break;
		}
	}
	
	
	

	/*********************** 提交表单   ***********************/
		
	private void startCommitTask(){
		
		userName = name_txt.getText().toString();
		cardNo = account_txt.getText().toString();
		phone = phone_txt.getText().toString();
		verifyCode = code_txt.getText().toString();
		money = count_txt.getText().toString();
		zfpwd = pay_psw_txt.getText().toString();
		
		//校验数据		
		if(StringUtil.isEmpty(userName)){
			Toast.makeText(context, "请填写您的姓名", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(cardNo)){
			Toast.makeText(context, "请填写您的银行卡账号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(verifyCode)){
			Toast.makeText(context, "请填写验证码", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(phone)){
			Toast.makeText(context, "请填写您的手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(money)){
			Toast.makeText(context, "请填写提现金额", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(zfpwd)){
			Toast.makeText(context, "请填写支付密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(cardNo)){
			Toast.makeText(context, "请选择银行卡", Toast.LENGTH_SHORT).show();
			return;
		}
				
		if(NetUtil.detectAvailable(context)){
			if(commitRequest == null){
				commitRequest = new CashWithdrawCommitRequest(modelApp.getUser().getId(),
						phone, userName,
						verifyCode, type, cardNo, 
						money, zfpwd, new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
							Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.NO_DATA_FROM_NET:
							String msg = (String) message.obj;
							Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							String msgs = (String) message.obj;
							Toast.makeText(context, msgs, Toast.LENGTH_SHORT).show();
							((Activity) context).finish();
							break;
						}
					}
				});
			}else {
				commitRequest.setData(modelApp.getUser().getId(),
						phone, userName,
						verifyCode, type, cardNo, 
						money, zfpwd);
			}
			
			showLoadingDialog("表单提交中...");
			commitRequest.doRequest();
			
		}else {
			Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	/*********************  获取验证码    ****************************/
	
	private void startVerityCodeTask() {
		phone = phone_txt.getText().toString();
		if(StringUtil.isEmpty(phone)){
			Toast.makeText(context, "请填写手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(NetUtil.detectAvailable(context)){
			if(codeRequest == null){
				codeRequest = new GetVerifyCodeRequest(phone, new RequestListener() {
                    @Override
                    public void sendMessage(Message message) {
                        switch (message.what){
                            case Constants.ERROR_DATA_FROM_NET:
                                Toast.makeText(context, R.string.sms_code_error, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                Toast.makeText(context, (String)message.obj, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                Toast.makeText(context, (String)message.obj, Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });
			}else {
				codeRequest.setData(phone);
			}
			codeRequest.doRequest();
			duration = 60;
			if(timer !=null){
				timer.cancel();
				timer= null;
			}
			timer = new Timer();
			timer.schedule(new MyTimerTask(), 0, 1000);
			
		}else {
			Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
		
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			duration = duration - 1;
			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					if(duration < 0 ){
						get_code_txt.setClickable(true);
						get_code_txt.setBackgroundResource(R.drawable.green_corner_btn_bg);
						get_code_txt.setTextColor(context.getResources().getColor(R.color.white));
						get_code_txt.setText("获取验证码");
					}else{
						get_code_txt.setClickable(false);
						get_code_txt.setBackgroundResource(R.drawable.gray_corner_btn_bg);
						get_code_txt.setTextColor(context.getResources().getColor(R.color.common_gray_txt));
						get_code_txt.setText("获取验证码("+duration+")");
					}
				}
			});
			
		}
	}
	
	
	
	
	
	
	CustomDialog dialog;

	public void showLoadingDialog(int showMessage) {
		if (dialog == null) {
			dialog = new CustomDialog(context, showMessage,
					android.R.style.Theme_Translucent_NoTitleBar);
		}
		if (!((Activity) context).isFinishing() && !dialog.isShowing()) {
			dialog.show();
		}
	}

	public void showLoadingDialog(String showMessage) {
		if (dialog == null) {
			dialog = new CustomDialog(context, showMessage,
					android.R.style.Theme_Translucent_NoTitleBar);
		}
		if (!((Activity) context).isFinishing() && !dialog.isShowing()) {
			dialog.show();
		}
	}

	/**
	 * 隐藏正在提交疑问的环形进度条
	 */
	public void hideLoadingDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}
	
	
}
