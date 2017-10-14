package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.PayDetail;
import com.xkhouse.fang.user.entity.EmployeeInfo;
import com.xkhouse.fang.user.entity.PayResult;
import com.xkhouse.fang.user.task.EmployeeInfoRequest;
import com.xkhouse.fang.user.task.GetAlipayOrderRequest;
import com.xkhouse.lib.utils.NetUtil;

import java.util.Map;

/**
 * 我是员工
 */
public class PayActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;



    Button pay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.employee_blue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_pay);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();

        pay = (Button) findViewById(R.id.pay);


    }

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}

    private static final int SDK_PAY_FLAG = 1;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                default:
                    break;
            }
        }
    };


    @Override
	protected void setListeners() {

        pay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                startOrderTask();

            }
        });
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){


        }


	}
	
	
	private void fillData(){



	}


	private void startOrderTask() {
		if(NetUtil.detectAvailable(this)){

            showLoadingDialog(R.string.data_loading);

            GetAlipayOrderRequest request = new GetAlipayOrderRequest(modelApp.getUser().getToken(), "0.01", "5", new RequestListener() {
                @Override
                public void sendMessage(Message message) {

                    hideLoadingDialog();

                    switch (message.what) {

                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String nodataStr = (String) message.obj;
                            Toast.makeText(mContext, nodataStr, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            String orderInfo = (String) message.obj;
                            startAlipay(orderInfo);
                            break;

                    }
                }
            });

            request.doRequest();

		}else {
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

	private void startAlipay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}
