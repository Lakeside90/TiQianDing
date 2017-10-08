package com.xkhouse.fang.booked.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.adapter.CheckDiscountAdapter;
import com.xkhouse.fang.booked.adapter.CheckRuleAdapter;
import com.xkhouse.fang.booked.entity.PayDetail;
import com.xkhouse.fang.booked.task.AddressAddRequest;
import com.xkhouse.fang.booked.task.CheckAddRequest;
import com.xkhouse.fang.widget.ScrollListView;
import com.xkhouse.lib.utils.NetUtil;

/**
 * 在线买单
 */
public class PayMakeActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;


    private TextView total_money_txt;
    private TextView no_discount_txt;
    private TextView booking_name_txt;
    private TextView booking_discount_txt;
    private TextView booking_money_txt;
    private ScrollListView discount_listview;
    private TextView real_price_txt;
    private ScrollListView rule_listview;

    private TextView commit_txt;

    private PayDetail payDetail;
    private String business_id;
    private String total_money;
    private String no_discount_money;
    private String real_moeny;


    private CheckDiscountAdapter discountAdapter;
    private CheckRuleAdapter ruleAdapter;

    private CheckAddRequest addRequest;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


        fillData();
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_pay_make);
	}

	@Override
	protected void init() {
		super.init();

        Bundle bundle = getIntent().getExtras();

        payDetail = (PayDetail) bundle.getSerializable("payDetail");
        business_id = bundle.getString("business_id");
        total_money = bundle.getString("total_money");
        no_discount_money = bundle.getString("no_discount_money");
    }

	@Override
	protected void findViews() {

		initTitle();

        total_money_txt = (TextView) findViewById(R.id.total_money_txt);
        no_discount_txt = (TextView) findViewById(R.id.no_discount_txt);
        booking_name_txt = (TextView) findViewById(R.id.booking_name_txt);
        booking_discount_txt = (TextView) findViewById(R.id.booking_discount_txt);
        booking_money_txt = (TextView) findViewById(R.id.booking_money_txt);
        real_price_txt = (TextView) findViewById(R.id.real_price_txt);
        commit_txt = (TextView) findViewById(R.id.commit_txt);

        discount_listview = (ScrollListView) findViewById(R.id.discount_listview);
        rule_listview = (ScrollListView) findViewById(R.id.rule_listview);
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
        commit_txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddTask();
            }
        });
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	
	
	private void fillData(){

        if (payDetail == null) return;

        total_money_txt.setText(total_money);
        no_discount_txt.setText(no_discount_money);

        if (payDetail.getBooking() != null) {
            booking_name_txt.setText(payDetail.getBooking().getDiscount_name());
            booking_discount_txt.setText(payDetail.getBooking().getDiscount());
            booking_money_txt.setText("-¥ " + payDetail.getBooking().getMortgage());
        }

        if (payDetail.getDiscountArray() != null) {
            if (discountAdapter == null) {
                discountAdapter = new CheckDiscountAdapter(mContext, payDetail.getDiscountArray());
                discount_listview.setAdapter(discountAdapter);
            }
            discountAdapter.setData(payDetail.getDiscountArray());
        }

        if (payDetail.getRules() != null) {
            if (ruleAdapter == null) {
                ruleAdapter = new CheckRuleAdapter(mContext, payDetail.getRules());
                rule_listview.setAdapter(ruleAdapter);
            }
            ruleAdapter.setData(payDetail.getRules());
        }

        try {
            Float totalPrice = Float.valueOf(total_money);
            Float bookingPrice = 0f;
            if (payDetail.getBooking() != null) {
                bookingPrice = Float.valueOf(payDetail.getBooking().getMortgage());
            }
            Float discountPrice = 0f;
            if (payDetail.getDiscountArray() != null) {
                for (PayDetail.Discount discount : payDetail.getDiscountArray()) {
                    discountPrice = discountPrice + Float.valueOf(discount.getCheck_discount_money());
                }
            }

            Float real_moeny1 = totalPrice - bookingPrice - discountPrice;
            if (real_moeny1 < 0) real_moeny1 = 0f;

            real_moeny = String.valueOf(real_moeny1);

        }catch (Exception e) {
            e.printStackTrace();
        }

        real_price_txt.setText(real_moeny);
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

    private void startAddTask() {
        StringBuilder contentsb = new StringBuilder();
        if (payDetail.getBooking() != null) {
            contentsb.append(payDetail.getBooking().getDiscount()).append(",");
        }
        if (payDetail.getDiscountArray() != null) {
            for (PayDetail.Discount discount : payDetail.getDiscountArray()) {
                contentsb.append(discount.getCheck_discount_name()).append(",");
            }
        }

        if(NetUtil.detectAvailable(mContext)){
            if(addRequest == null){
                addRequest = new CheckAddRequest(modelApp.getUser().getToken(), total_money, real_moeny,
                        business_id, contentsb.toString(), requestListener);
            }else {
                addRequest.setData(modelApp.getUser().getToken(), total_money, real_moeny,
                        business_id, contentsb.toString());
            }
            showLoadingDialog("处理中...");
            addRequest.doRequest();

        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


}
