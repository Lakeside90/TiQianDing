package com.xkhouse.fang.booked.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.booked.entity.BookAddInfo;
import com.xkhouse.fang.booked.entity.PayDetail;
import com.xkhouse.fang.booked.task.BookedAddRequest;
import com.xkhouse.fang.booked.task.PayDetailRequest;
import com.xkhouse.fang.widget.TimePickerDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * 在线买单--输入消费金额
 */
public class CheckMakeActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;


    private EditText total_money_txt;
    private EditText no_discount_txt;
    private TextView commit_txt;


    private String business_id;
    private PayDetailRequest request;
    String total_money = "0";
    String no_discount_money = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_check_make);
    }

    @Override
    protected void init() {
        super.init();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            business_id = data.getString("business_id");
        }
    }

    @Override
    protected void findViews() {
        initTitle();

        total_money_txt = (EditText) findViewById(R.id.total_money_txt);
        no_discount_txt = (EditText) findViewById(R.id.no_discount_txt);
        commit_txt = (TextView) findViewById(R.id.commit_txt);

    }


    private void initTitle() {
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("在线买单");
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    @Override
    protected void setListeners() {

        commit_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.commit_txt:

                startTask();
                break;

        }
    }


    private void startTask() {
        total_money = total_money_txt.getText().toString();
        no_discount_money = no_discount_txt.getText().toString();

        if(StringUtil.isEmpty(total_money)) {
            Toast.makeText(mContext, "请填写消费总额", Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(no_discount_money)) {
            no_discount_money = "0";
        }

        if (NetUtil.detectAvailable(mContext)) {
            if (request == null) {
                request = new PayDetailRequest(modelApp.getUser().getToken(), business_id,
                        total_money, no_discount_money, requestListener);
            } else {
                request.setData(modelApp.getUser().getToken(), business_id,
                        total_money, no_discount_money);
            }
            showLoadingDialog("处理中...");
            request.doRequest();

        } else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    RequestListener requestListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            hideLoadingDialog();
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                    break;

                case Constants.NO_DATA_FROM_NET:
                    Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    PayDetail payDetail = (PayDetail) message.obj;
                    Intent intent = new Intent(CheckMakeActivity.this, PayMakeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("payDetail", payDetail);
                    bundle.putString("business_id", business_id);
                    bundle.putString("total_money", total_money);
                    bundle.putString("no_discount_money", no_discount_money);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };



}
