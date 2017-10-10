package com.xkhouse.fang.app.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

//import com.squareup.okhttp.Call;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.JJAppointmentActivity;
import com.xkhouse.fang.app.activity.JJFreeServiceActivity;
import com.xkhouse.fang.app.activity.NewsDetailActivity;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.io.IOException;

/** 
 * @Description: 新闻首页  webview 
 * @author wujian  
 * @date 2015-10-12 上午9:35:49  
 */
public class NewsItemView {
	
	
	private Context context;
	private View rootView;

    private ProgressBar pb;
    private SwipeRefreshLayout swipeLayout;
    private WebView webView;
    private LinearLayout error_lay;
	private String url;
	
	public View getView() {
        return rootView;
    } 
	
	public NewsItemView(Context context, String url) {
		this.context = context;
		this.url = url;
		
		initView();
		
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_news_item, null);

        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        error_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebData(url);
            }
        });

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //重新刷新页面
                loadWebData(url);
            }
        });

        webView = (WebView) rootView.findViewById(R.id.webview);
        pb = (ProgressBar) rootView.findViewById(R.id.pb);

		WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        String str = webSettings.getUserAgentString();
        webSettings.setUserAgentString(str+"XKAPP");
        
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebClient());

        loadWebData(url);
	}

    private void loadWebData(String url){
        if (NetUtil.detectAvailable(context)){
            webView.setVisibility(View.VISIBLE);
            error_lay.setVisibility(View.GONE);
            loadDataFromNet(url);
        }else{
            webView.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }
    }
	
	
	
	final class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);
            if(newProgress==100){
                pb.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);
            }else {
                if (pb.getVisibility() == View.GONE)
                    pb.setVisibility(View.VISIBLE);
                pb.setProgress(newProgress);
                if (!swipeLayout.isRefreshing())
                    swipeLayout.setRefreshing(true);
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
			
			if(url.contains("home/peigou")){
				Intent intent = new Intent(context, JJAppointmentActivity.class);
				Bundle data = new Bundle();
				
				if(url.contains("home/peigou/index1")){
					data.putInt("index", 1);
				}else if(url.contains("home/peigou/index")){
					data.putInt("index", 0);
				}
				intent.putExtras(data);
				context.startActivity(intent);
				return true;
				
			}else if(url.contains("home/baozhang")){
				context.startActivity(new Intent(context, JJFreeServiceActivity.class));
				return true;
				
			}else{
				
				Intent intent = new Intent(context, NewsDetailActivity.class);
				Bundle data = new Bundle();
				data.putString("url", url);
				
				String[] params = url.split("/");
				if(params != null){
					String ids = params[params.length-1];
					if(!StringUtil.isEmpty(ids) && ids.contains(".html")){
						data.putString("newsId", ids.replaceAll(".html", ""));
					}
				}
				// 拦截超链接跳转到新闻详情页
				
				intent.putExtras(data);
				context.startActivity(intent);
				return true;
			}
			
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
//                    ((Activity)context).runOnUiThread(new Runnable() {
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
//                    ((Activity)context).runOnUiThread(new Runnable() {
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
