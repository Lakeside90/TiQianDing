package com.xkhouse.fang.discount.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
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

public class DiscountActivity extends AppBaseActivity {


    private ImageView iv_head_left;
    private TextView tv_head_title;


    private ProgressBar pb;
    private WebView webView;
    private LinearLayout error_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadWebData(modelApp.getHuoDong());
    }

    @Override
    protected void setContentView() {
        super.setContentView();

        setContentView(R.layout.fragment_discount);
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
        error_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebData(modelApp.getHuoDong());
            }
        });

        pb = (ProgressBar) findViewById(R.id.pb);
        webView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        String str = webSettings.getUserAgentString();
        webSettings.setUserAgentString(str + "XKAPP");

        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebClient());
    }

    private void loadWebData(String url){
        if (NetUtil.detectAvailable(this)){
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
        iv_head_left.setVisibility(View.VISIBLE);

        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("热门活动");
        iv_head_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
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

    final class MyWebClient extends WebViewClient {
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
            Logger.d("", "url---" + url);
            Intent intent = new Intent(DiscountActivity.this, DiscountDetailActivity.class);
            Bundle data = new Bundle();
            data.putString("url", url);
            intent.putExtras(data);
            startActivity(intent);
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






}
