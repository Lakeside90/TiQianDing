package com.xkhouse.fang.money.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.squareup.okhttp.Call;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;

import java.io.IOException;

/**
* @Description: 星空贷 
* @author wujian  
* @date 2015-10-21 上午9:10:39
 */
public class XKLoanIndexActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private ImageView iv_head_right;
	
	private WebView webView;
    private LinearLayout error_lay;
    private String currentUrl;
    private ProgressBar pb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_loan_index);
		
	}
	
	@Override
	protected void init() {
		super.init();
		
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();

        error_lay = (LinearLayout) findViewById(R.id.error_lay);
        error_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebData(currentUrl);
            }
        });

		webView = (WebView) findViewById(R.id.webview);
        pb = (ProgressBar) findViewById(R.id.pb);

		WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        String str = webSettings.getUserAgentString();
        webSettings.setUserAgentString(str + "XKAPP");
        
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebClient());

        currentUrl = modelApp.getXKLoan();
        loadWebData(currentUrl);
	}

    private void loadWebData(String url){
        if (NetUtil.detectAvailable(mContext)){
            webView.setVisibility(View.VISIBLE);
            error_lay.setVisibility(View.GONE);
            loadDataFromNet(url);
        }else{
            webView.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }
    }

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		iv_head_right = (ImageView) findViewById(R.id.iv_head_right);
		iv_head_right.setVisibility(View.VISIBLE);
		iv_head_right.setImageResource(R.drawable.loan_nav_icon_tel);
		iv_head_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:4008871216,86666"));
	             startActivity(intent); 
			}
		});
		
		tv_head_title.setText("星空贷");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(webView.canGoBack()){
					webView.goBack();
				}else{
					finish();
				}
			}
		});
		
		
	}
	
	
	final class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);
            if(newProgress==100){
                pb.setVisibility(View.GONE);
            }else {
                if (pb.getVisibility() == View.GONE)
                    pb.setVisibility(View.VISIBLE);
                pb.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

	}
	
	final class MyWebClient extends WebViewClient{
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}
	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			if(url.contains("loan/xinyong.html")){
                dispatchLoadActivity(XKLoanActivity.XIN_YONG_DAI);
				return true;
				
			}else if(url.contains("loan/xiangmu.html")){
                dispatchLoadActivity(XKLoanActivity.XIANG_MU_DAI);
				return true;

			}else if(url.contains("loan/zhuangxiu.html")){
                dispatchLoadActivity(XKLoanActivity.ZHUANG_XIU_DAI);
                return true;

            }else if(url.contains("loan/diya.html")){
                dispatchLoadActivity(XKLoanActivity.DI_YA_DAI);
                return true;

            }else if(url.contains("loan/xiaowei.html")){
                dispatchLoadActivity(XKLoanActivity.XIAO_WEI_DAI);
                return true;
            }

            if (!NetUtil.detectAvailable(mContext)){
                webView.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
                currentUrl = url;
                return true;
            }

            loadDataFromNet(url);

			return true;
		}

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Logger.e("", " onReceivedError ");
        }

	}


    /**
     * 解决500， 404 等等错误，webview拦截不到回调
     * @param url
     */
    private void loadDataFromNet(final String url) {
//        try{
//            //创建okHttpClient对象
//            OkHttpClient mOkHttpClient = new OkHttpClient();
//            //创建一个Request
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//            //new call
//            Call call = mOkHttpClient.newCall(request);
//            //请求加入调度
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            webView.setVisibility(View.GONE);
//                            error_lay.setVisibility(View.VISIBLE);
//                        }
//                    });
//                }
//
//                @Override
//                public void onResponse(final Response response) throws IOException {
//                    final String htmlStr = response.body().string();
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            webView.loadUrl(url);
//                            webView.setVisibility(View.VISIBLE);
//                            error_lay.setVisibility(View.GONE);
//                        }
//                    });
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(webView.canGoBack()){
				webView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

    private void dispatchLoadActivity(int flag){
        Intent intent = new Intent(mContext, XKLoanActivity.class);
        Bundle data = new Bundle();
        data.putInt("type", flag);
        intent.putExtras(data);
        startActivity(intent);
    }


	
}
