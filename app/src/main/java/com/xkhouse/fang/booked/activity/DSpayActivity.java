package com.xkhouse.fang.booked.activity;

import android.media.Image;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xkhouse.fang.booked.task.BookedAddRequest;
import com.xkhouse.fang.widget.TimePickerDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * 打赏支付
 */
public class DSpayActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;

    private LinearLayout content_lay;
    private ImageView user_icon_iv;
    private TextView username_txt;
    private TextView user_num_txt;
    private EditText comment_txt;
    private EditText money_txt;

    private LinearLayout weixin_pay_ly;
    private LinearLayout ali_pay_ly;
    private ImageView weixin_pay_iv;
    private ImageView ali_pay_iv;

    private TextView commit_txt;



    private String url;
    private String payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changePayType(0);
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_ds_pay);
    }

    @Override
    protected void init() {
        super.init();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            url = (String) data.getSerializable("url");
        }
    }

    @Override
    protected void findViews() {
        initTitle();

        content_lay = (LinearLayout) findViewById(R.id.content_lay);
        user_icon_iv = (ImageView) findViewById(R.id.user_icon_iv);
        username_txt = (TextView) findViewById(R.id.username_txt);
        user_num_txt = (TextView) findViewById(R.id.user_num_txt);
        comment_txt = (EditText) findViewById(R.id.comment_txt);
        money_txt = (EditText) findViewById(R.id.money_txt);

        weixin_pay_ly = (LinearLayout) findViewById(R.id.weixin_pay_ly);
        ali_pay_ly = (LinearLayout) findViewById(R.id.ali_pay_ly);

        weixin_pay_iv = (ImageView) findViewById(R.id.weixin_pay_iv);
        ali_pay_iv = (ImageView) findViewById(R.id.ali_pay_iv);

        commit_txt = (TextView) findViewById(R.id.commit_txt);

    }


    private void initTitle() {
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("打赏评价");
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

        weixin_pay_ly.setOnClickListener(this);
        ali_pay_ly.setOnClickListener(this);
        commit_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.weixin_pay_ly:
                changePayType(0);
                break;

            case R.id.ali_pay_ly:
                changePayType(1);
                break;

            case R.id.commit_txt:

                break;

        }
    }


    private void changePayType(int index) {
        if (index == 0) {
            weixin_pay_iv.setImageResource(R.drawable.selected_on);
            ali_pay_iv.setImageResource(R.drawable.selected);
            payType = "0";
        }else {
            weixin_pay_iv.setImageResource(R.drawable.selected);
            ali_pay_iv.setImageResource(R.drawable.selected_on);
            payType = "1";
        }
    }

    private void fillData() {


    }



    private void startTask() {
        if (NetUtil.detectAvailable(mContext)) {
//            if (request == null) {
//                request = new BookedAddRequest(modelApp.getUser().getToken(), bookAddInfo, requestListener);
//            } else {
//                request.setData(modelApp.getUser().getToken(), bookAddInfo);
//            }
            showLoadingDialog("处理中...");
//            request.doRequest();

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
                    Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };



}
