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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.entity.XKBank;
import com.xkhouse.fang.user.task.BankListRequest;
import com.xkhouse.fang.user.task.CashWithdrawCommitRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 提现到银行卡：一、手动选择银行卡；二、根据提现记录提现到指定账户
 *
 * 2016/7/19
 */

public class TXBankActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;
    private TextView tv_head_right;

    //手动选择银行卡
    private LinearLayout select_lay;
    private LinearLayout bank_lay;
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
    private String type;		//提款类型0支付宝 >0各大银行
    private String cardNo;		//卡号或者支付宝账号
    private String money;		//金额
    private String zfpwd;		//支付密码


    private ArrayList<XKBank> bankList = new ArrayList<XKBank>();
    private BankListRequest bankListRequest;


//    private TXRecord record;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_txbank);
    }


    @Override
    protected void init() {
        super.init();

        Bundle data = getIntent().getExtras();
        if (data != null){
//            record = (TXRecord) data.getSerializable("txrecord");
        }
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitle();

//        if (record != null){
//            no_select_lay = (LinearLayout) findViewById(R.id.no_select_lay);
//            no_select_lay.setVisibility(View.VISIBLE);
//            icon_iv = (ImageView) findViewById(R.id.icon_iv);
//            kh_name_txt = (TextView) findViewById(R.id.kh_name_txt);
//            type_name_txt = (TextView) findViewById(R.id.type_name_txt);
//
//            ImageLoader.getInstance().displayImage(record.getIcon(), icon_iv);
//            type_name_txt.setText(record.getBankName() + "(" +
//                    record.getCardNo().substring(record.getCardNo().length() - 4, record.getCardNo().length())
//                    + ")");
//            kh_name_txt.setText(record.getName());
//
//        }else{
//            select_lay = (LinearLayout) findViewById(R.id.select_lay);
//            select_lay.setVisibility(View.VISIBLE);
//            bank_lay = (LinearLayout) findViewById(R.id.bank_lay);
//            bank_iv = (ImageView) findViewById(R.id.bank_iv);
//            bank_txt = (TextView) findViewById(R.id.bank_txt);
//            name_txt = (EditText) findViewById(R.id.name_txt);
//            account_txt = (EditText) findViewById(R.id.account_txt);
//            phone_txt = (EditText) findViewById(R.id.phone_txt);
//            code_txt = (EditText) findViewById(R.id.code_txt);
//            get_code_txt = (TextView) findViewById(R.id.get_code_txt);
//
//        }

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
        if (bank_lay != null) bank_lay.setOnClickListener(this);
        if (get_code_txt != null) get_code_txt.setOnClickListener(this);
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

        if(NetUtil.detectAvailable(mContext)){
            if(bankListRequest == null){
                bankListRequest = new BankListRequest(new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {
                        hideLoadingDialog();
                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                                Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                Toast.makeText(mContext, "没有数据", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }

    }

    private void startBankListActivity() {
        Intent intent = new Intent(mContext, CashBankListActivity.class);
        Bundle data = new Bundle();
        data.putSerializable("bankList", bankList);
        intent.putExtras(data);
        startActivityForResult(intent, REQUEST_CODE);
    }









    /*********************** 提交表单   ***********************/

    private void startCommitTask(){

//        if (record != null) {
//            userName = record.getName();
//            cardNo = record.getCardNo();
//            type = record.getType();
//            phone = record.getPhone();
//
//        }else {
//            userName = name_txt.getText().toString();
//            cardNo = account_txt.getText().toString();
//            phone = phone_txt.getText().toString();
//            verifyCode = code_txt.getText().toString();
//
//            //校验数据
//            if(StringUtil.isEmpty(userName)){
//                Toast.makeText(mContext, "请填写您的姓名", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(StringUtil.isEmpty(type)){
//                Toast.makeText(mContext, "请选择银行卡", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(StringUtil.isEmpty(cardNo)){
//                Toast.makeText(mContext, "请填写您的银行卡账号", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(StringUtil.isEmpty(verifyCode)){
//                Toast.makeText(mContext, "请填写验证码", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(StringUtil.isEmpty(phone)){
//                Toast.makeText(mContext, "请填写您的手机号", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//        }

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
                commitRequest = new CashWithdrawCommitRequest(modelApp.getUser().getId(),
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
                commitRequest.setData(modelApp.getUser().getId(),
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //选择的银行
        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_CODE){
                XKBank bank = (XKBank) data.getExtras().getSerializable("bank");
                setBankData(bank);
            }
        }
    }




}
