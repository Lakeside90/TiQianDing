package com.xkhouse.fang.user.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.task.PaypswFindRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 找回支付密码
 */
public class FindPaypswActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;

    private EditText paypsw_txt;
    private EditText re_paypsw_txt;
    private TextView phone_txt;
    private EditText code_txt;
    private TextView get_code_txt;

    private TextView commit_txt;

    private GetVerifyCodeRequest codeRequest;
    private Timer timer;
    private int duration = 60;

    private PaypswFindRequest paypswFindRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone_txt.setText(modelApp.getUser().getPhone());
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_find_paypsw);
    }

    @Override
    protected void findViews() {
        super.findViews();
        initTitle();

        paypsw_txt = (EditText) findViewById(R.id.paypsw_txt);
        re_paypsw_txt = (EditText) findViewById(R.id.re_paypsw_txt);
        phone_txt = (TextView) findViewById(R.id.phone_txt);
        code_txt = (EditText) findViewById(R.id.code_txt);

        get_code_txt = (TextView) findViewById(R.id.get_code_txt);
        commit_txt = (TextView) findViewById(R.id.commit_txt);
    }

    private void initTitle() {
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("找回支付密码");
        iv_head_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                closeSoftInput();
                //解決黑屏問題
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }


    @Override
    protected void setListeners() {
        super.setListeners();
        get_code_txt.setOnClickListener(this);
        commit_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.get_code_txt:
                startVerityCodeTask();
                break;

            case R.id.commit_txt:
                startPaypswFindTask();
                break;
        }
    }


    private void startPaypswFindTask() {
        String mobile = phone_txt.getText().toString();
        String paypsw = paypsw_txt.getText().toString();
        String rePaypsw = re_paypsw_txt.getText().toString();
        String code = code_txt.getText().toString();
        if (StringUtil.isEmpty(mobile) || StringUtil.isEmpty(paypsw) ||
                StringUtil.isEmpty(rePaypsw) || StringUtil.isEmpty(code)) {
            Toast.makeText(mContext, "请填写完整", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!paypsw.equals(rePaypsw)) {
            Toast.makeText(mContext, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (NetUtil.detectAvailable(mContext)) {
            if (paypswFindRequest == null) {
                paypswFindRequest = new PaypswFindRequest(modelApp.getUser().getId(), paypsw, mobile, code,
                        new RequestListener() {
                            @Override
                            public void sendMessage(Message message) {
                                hideLoadingDialog();
                                switch (message.what) {
                                    case Constants.ERROR_DATA_FROM_NET:
                                        Toast.makeText(mContext, "设置支付密码失败！", Toast.LENGTH_SHORT).show();
                                        break;

                                    case Constants.NO_DATA_FROM_NET:
                                        String msgStr = (String) message.obj;
                                        Toast.makeText(mContext, msgStr, Toast.LENGTH_SHORT).show();
                                        break;

                                    case Constants.SUCCESS_DATA_FROM_NET:
                                        Toast.makeText(mContext, "设置支付密码成功！", Toast.LENGTH_SHORT).show();
                                        finish();
                                        break;
                                }
                            }
                        });
            } else {
                paypswFindRequest.setData(paypsw, mobile, code);
            }
            showLoadingDialog(R.string.data_loading);
            paypswFindRequest.doRequest();
        } else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }

    }

    /*********************
     * 获取验证码
     ****************************/

    private void startVerityCodeTask() {
        String mobile = phone_txt.getText().toString();
        if (StringUtil.isEmpty(mobile)) {
            Toast.makeText(mContext, "请填写手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (NetUtil.detectAvailable(mContext)) {
            if (codeRequest == null) {
                codeRequest = new GetVerifyCodeRequest(mobile, new RequestListener() {
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
            } else {
                codeRequest.setData(mobile);
            }
            codeRequest.doRequest();
            duration = 60;
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            timer = new Timer();
            timer.schedule(new MyTimerTask(), 0, 1000);

        } else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            duration = duration - 1;
            runOnUiThread(new Runnable() {
                public void run() {
                    if (duration < 0) {
                        get_code_txt.setClickable(true);
                        get_code_txt.setBackgroundResource(R.drawable.green_corner_btn_bg);
                        get_code_txt.setTextColor(mContext.getResources().getColor(R.color.white));
                        get_code_txt.setText("获取验证码");
                    } else {
                        get_code_txt.setClickable(false);
                        get_code_txt.setBackgroundResource(R.drawable.gray_corner_btn_bg);
                        get_code_txt.setTextColor(mContext.getResources().getColor(R.color.common_gray_txt));
                        get_code_txt.setText("获取验证码(" + duration + ")");
                    }
                }
            });

        }
    }





}
