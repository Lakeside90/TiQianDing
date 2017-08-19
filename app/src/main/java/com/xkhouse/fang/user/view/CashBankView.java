package com.xkhouse.fang.user.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.activity.CashBankListActivity;
import com.xkhouse.fang.user.entity.XKBank;
import com.xkhouse.fang.user.task.BankListRequest;
import com.xkhouse.fang.user.task.CashWithdrawCommitRequest;
import com.xkhouse.fang.widget.CustomDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description: 提现至银行卡 
 * @author wujian  
 * @date 2015-10-21 下午2:56:54  
 */
public class CashBankView implements OnClickListener{

	private Context context;
	private View rootView;
	
	private EditText count_txt;
	private TextView money_txt;
	private LinearLayout bank_lay;
	private ImageView bank_iv;
	private TextView bank_txt;
	private EditText name_txt;
	private EditText account_txt;
	private EditText phone_txt;
	private EditText code_txt;
	private TextView get_code_txt;
	private EditText pay_psw_txt;
	private TextView commit_txt;
	
	public static final int RESULT_CODE = 201;
	public static final int REQUEST_CODE = 200;
	
	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	
	private CashWithdrawCommitRequest commitRequest;
	//参数
	private String phone;		//手机号
	private String userName;	//姓名
	private String verifyCode;	//验证码
	private String type;		//提款类型0支付宝 >0各大银行
	private String cardNo;		//卡号或者支付宝账号
	private String money;		//金额
	private String zfpwd;		//支付密码
	
	
	private ArrayList<XKBank> bankList = new ArrayList<XKBank>();
	
	private BankListRequest bankListRequest;
	
	private String sum;   //余额
	
	private ModelApplication modelApp;
	
	public View getView() {
        return rootView;
    } 
	
	public CashBankView(Context context, String sum) {
		this.context = context;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		this.sum = sum;
		initView();
		setListener();
		
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_cash_bank, null);
		
		count_txt = (EditText) rootView.findViewById(R.id.count_txt);
		money_txt = (TextView) rootView.findViewById(R.id.money_txt);
		bank_lay = (LinearLayout) rootView.findViewById(R.id.bank_lay);
		bank_iv = (ImageView) rootView.findViewById(R.id.bank_iv);
		bank_txt = (TextView) rootView.findViewById(R.id.bank_txt);
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
		bank_lay.setOnClickListener(this);
		get_code_txt.setOnClickListener(this);
		commit_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bank_lay:
			startBankListTask();
			break;

		case R.id.get_code_txt:
			startVerityCodeTask();
			break;
			
		case R.id.commit_txt:
			startCommitTask();
			break;
		}
	}
	
	
	
	public void setBankData(XKBank bank){
		type = bank.getBankId();
		bank_txt.setText(bank.getBankName());
		ImageLoader.getInstance().displayImage(bank.getBankIcon(), bank_iv);
	}
	
	//获取银行列表数据
	private void startBankListTask() {
		if(bankList != null && bankList.size() > 0){
			startBankListActivity();
			return;
		}
		
		if(NetUtil.detectAvailable(context)){
			if(bankListRequest == null){
				bankListRequest = new BankListRequest(new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
							Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.NO_DATA_FROM_NET:
							Toast.makeText(context, "没有数据", Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							ArrayList<XKBank> temp = (ArrayList<XKBank>) message.obj;
							if(bankList != null){
								bankList.clear();
							}
							bankList.addAll(temp);
							startBankListActivity();
							break;
						}
					}
				});
			}
			showLoadingDialog(R.string.data_loading);
			bankListRequest.doRequest();
		}else {
			Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void startBankListActivity() {
		Intent intent = new Intent(context, CashBankListActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("bankList", bankList);
		intent.putExtras(data);
		((Activity) context).startActivityForResult(intent, REQUEST_CODE);
	}
	

	
	
	
	
	
	
	
	/*********************** 提交表单   ***********************/
	
	public String getParams() {
		
		StringBuilder sb = new StringBuilder();
		
		if(!StringUtil.isEmpty(phone)){
			sb.append("&phone=" + phone);
		}
		if(!StringUtil.isEmpty(userName)){
			sb.append("&userName=" + userName);
		}
		if(!StringUtil.isEmpty(verifyCode)){
			sb.append("&verifyCode=" + verifyCode);
		}
		if(!StringUtil.isEmpty(type)){
			sb.append("&type=" + type);
		}
		if(!StringUtil.isEmpty(cardNo)){
			sb.append("&cardNo=" + cardNo);
		}
		if(!StringUtil.isEmpty(money)){
			sb.append("&money=" + money);
		}
		if(!StringUtil.isEmpty(zfpwd)){
			sb.append("&zfpwd=" + zfpwd);
		}
		
		return sb.toString();
	}
		
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
				commitRequest = new CashWithdrawCommitRequest(modelApp.getUser().getUid(), 
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
				commitRequest.setData(modelApp.getUser().getUid(),
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
