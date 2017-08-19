package com.xkhouse.fang.user.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.entity.TXRecord;
import com.xkhouse.fang.user.task.CashWithdrawCommitRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 提现到支付宝 一、手动填写支付宝账号 二、提现到指定支付宝账号
 */
public class TXZfbActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;
    private TextView tv_head_right;

    //手动填写支付宝账号
    private LinearLayout select_lay;
    private ImageView bank_iv;
    private TextView bank_txt;
    private EditText name_txt;
    private EditText account_txt;
    private EditText phone_txt;
    private EditText code_txt;
    private TextView get_code_txt;

    //根据提现记录提现到指定账户
    private LinearLayout no_select_lay;
    private ImageView icon_iv;
    private TextView kh_name_txt;
    private TextView type_name_txt;

    private EditText count_txt;
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
    private String type = "0";		//提款类型0支付宝 >0各大银行
    private String cardNo;		//卡号或者支付宝账号
    private String money;		//金额
    private String zfpwd;		//支付密码


    private TXRecord record;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_txzfb);
    }


    @Override
    protected void init() {
        super.init();

        Bundle data = getIntent().getExtras();
        if (data != null){
            record = (TXRecord) data.getSerializable("txrecord");
        }

    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitle();

        if (record != null){
            no_select_lay = (LinearLayout) findViewById(R.id.no_select_lay);
            no_select_lay.setVisibility(View.VISIBLE);
            icon_iv = (ImageView) findViewById(R.id.icon_iv);
            kh_name_txt = (TextView) findViewById(R.id.kh_name_txt);
            type_name_txt = (TextView) findViewById(R.id.type_name_txt);

            icon_iv.setImageResource(R.drawable.tixian_ico_zhifubao);
            type_name_txt.setText(record.getCardNo());
            kh_name_txt.setText(record.getName());

        }else{
            select_lay = (LinearLayout) findViewById(R.id.select_lay);
            select_lay.setVisibility(View.VISIBLE);
            bank_iv = (ImageView) findViewById(R.id.bank_iv);
            name_txt = (EditText) findViewById(R.id.name_txt);
            account_txt = (EditText) findViewById(R.id.account_txt);
            phone_txt = (EditText) findViewById(R.id.phone_txt);
            code_txt = (EditText) findViewById(R.id.code_txt);
            get_code_txt = (TextView) findViewById(R.id.get_code_txt);

        }

        count_txt = (EditText) findViewById(R.id.count_txt);
        pay_psw_txt = (EditText) findViewById(R.id.pay_psw_txt);
        commit_txt = (TextView) findViewById(R.id.commit_txt);

    }


    private void initTitle() {
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_right = (TextView) findViewById(R.id.tv_head_right);
        tv_head_title.setText("提现");
        tv_head_right.setText("提现说明");
        tv_head_right.setVisibility(View.VISIBLE);

        tv_head_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(mContext, CashWithdrawDeclareActivity.class));
            }
        });

        iv_head_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeSoftInput();
                finish();
            }
        });
    }


    @Override
    protected void setListeners() {
        super.setListeners();
        if (get_code_txt != null) get_code_txt.setOnClickListener(this);
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

        if (record != null){
            userName = record.getName();
            cardNo = record.getCardNo();
            phone = record.getPhone();

        }else{
            userName = name_txt.getText().toString();
            cardNo = account_txt.getText().toString();
            phone = phone_txt.getText().toString();
            verifyCode = code_txt.getText().toString();

            //校验数据
            if(StringUtil.isEmpty(userName)){
                Toast.makeText(mContext, "请填写您的姓名", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(cardNo)){
                Toast.makeText(mContext, "请填写您的支付宝账号", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(verifyCode)){
                Toast.makeText(mContext, "请填写验证码", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(phone)){
                Toast.makeText(mContext, "请填写您的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        money = count_txt.getText().toString();
        zfpwd = pay_psw_txt.getText().toString();

        if(StringUtil.isEmpty(money)){
            Toast.makeText(mContext, "请填写提现金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isEmpty(zfpwd)){
            Toast.makeText(mContext, "请填写支付密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(NetUtil.detectAvailable(mContext)){
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
                                Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                String msg = (String) message.obj;
                                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                String msgs = (String) message.obj;
                                Toast.makeText(mContext, msgs, Toast.LENGTH_SHORT).show();
                                finish();
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
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }



    /*********************  获取验证码    ****************************/

    private void startVerityCodeTask() {
        phone = phone_txt.getText().toString();
        if(StringUtil.isEmpty(phone)){
            Toast.makeText(mContext, "请填写手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if(NetUtil.detectAvailable(mContext)){
            if(codeRequest == null){
                codeRequest = new GetVerifyCodeRequest(phone, new RequestListener() {
                    @Override
                    public void sendMessage(Message message) {
                        switch (message.what){
                            case Constants.ERROR_DATA_FROM_NET:
                                Toast.makeText(mContext, R.string.sms_code_error, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                Toast.makeText(mContext, (String)message.obj, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                Toast.makeText(mContext, (String)message.obj, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            duration = duration - 1;
            ((Activity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    if(duration < 0 ){
                        get_code_txt.setClickable(true);
                        get_code_txt.setBackgroundResource(R.drawable.green_corner_btn_bg);
                        get_code_txt.setTextColor(mContext.getResources().getColor(R.color.white));
                        get_code_txt.setText("获取验证码");
                    }else{
                        get_code_txt.setClickable(false);
                        get_code_txt.setBackgroundResource(R.drawable.gray_corner_btn_bg);
                        get_code_txt.setTextColor(mContext.getResources().getColor(R.color.common_gray_txt));
                        get_code_txt.setText("获取验证码("+duration+")");
                    }
                }
            });

        }
    }


}
