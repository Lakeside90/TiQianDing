package com.xkhouse.fang.user.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.entity.PayResult;
import com.xkhouse.fang.user.task.GetAlipayOrderRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONObject;

import java.util.Map;

/**
 * 支付
 */
public class PayActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;

    private TextView money_txt;

    private LinearLayout weixin_pay_ly;
    private LinearLayout ali_pay_ly;
    private ImageView weixin_pay_iv;
    private ImageView ali_pay_iv;

    private TextView commit_txt;


    private String payType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




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

        weixin_pay_ly = (LinearLayout) findViewById(R.id.weixin_pay_ly);
        ali_pay_ly = (LinearLayout) findViewById(R.id.ali_pay_ly);

        weixin_pay_iv = (ImageView) findViewById(R.id.weixin_pay_iv);
        ali_pay_iv = (ImageView) findViewById(R.id.ali_pay_iv);

        commit_txt = (TextView) findViewById(R.id.commit_txt);
        money_txt = (TextView) findViewById(R.id.money_txt);


    }

	private void initTitle() {
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("支付");
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
        switch (v.getId()){
            case R.id.weixin_pay_ly:
                changePayType(0);
                break;

            case R.id.ali_pay_ly:
                changePayType(1);
                break;

            case R.id.commit_txt:
                startOrderTask();
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


    /******************************** 微信支付 ********************************/
    private IWXAPI api;

    private void weixinPay(String orderInfo) {

        api = WXAPIFactory.createWXAPI(this, "wxb4ba3c02aa476ea1");

        String url = "http://wxpay.wxutil.com/pub_v2/app/app_pay.php";
//        Button payBtn = (Button) findViewById(R.id.appay_btn);
//        payBtn.setEnabled(false);
        Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
        try{

//            byte[] buf = Util.httpGet(url);

            if (!StringUtil.isEmpty(orderInfo)) {
                Log.e("get server pay params:",orderInfo);
                JSONObject json = new JSONObject(orderInfo);
                if(null != json && !json.has("retcode") ){
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId			= json.getString("appid");
                    req.partnerId		= json.getString("partnerid");
                    req.prepayId		= json.getString("prepayid");
                    req.nonceStr		= json.getString("noncestr");
                    req.timeStamp		= json.getString("timestamp");
                    req.packageValue	= json.getString("package");
                    req.sign			= json.getString("sign");
                    req.extData			= "app data"; // optional
                    Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                }else{
                    Log.d("PAY_GET", "返回错误"+json.getString("retmsg"));
                    Toast.makeText(PayActivity.this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            }else{
                Log.d("PAY_GET", "服务器请求错误");
                Toast.makeText(PayActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.e("PAY_GET", "异常："+e.getMessage());
            Toast.makeText(PayActivity.this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
