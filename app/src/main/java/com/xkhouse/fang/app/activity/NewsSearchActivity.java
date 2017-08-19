package com.xkhouse.fang.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xkhouse.fang.R;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.io.IOException;

/** 
 * @Description: 新闻搜索
 * @author wujian  
 * @date 2015-10-12 上午10:26:55  
 */
public class NewsSearchActivity extends AppBaseActivity {

	
	private ImageView back_iv;
	private EditText news_search_txt;

    private ProgressBar pb;
	private WebView webView;
    private LinearLayout error_lay;
	private String keyword; 
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		news_search_txt.setText(keyword);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_news_search);
		
	}
	
	@Override
	protected void init() {
		super.init();
		keyword = getIntent().getExtras().getString("keyword");
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();

        error_lay = (LinearLayout) findViewById(R.id.error_lay);
        error_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebData(modelApp.getWebHost() + "/news/search?keyword=" + keyword);
            }
        });

        pb = (ProgressBar) findViewById(R.id.pb);
		webView = (WebView) findViewById(R.id.webview);
		
		WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        String str = webSettings.getUserAgentString();
        webSettings.setUserAgentString(str+"XKAPP");
        
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebClient());

        loadWebData(modelApp.getWebHost() + "/news/search?keyword=" + keyword);
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
		news_search_txt = (EditText) findViewById(R.id.news_search_txt);
		back_iv = (ImageView) findViewById(R.id.back_iv);
		back_iv.setOnClickListener(new OnClickListener() {
			
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
		
		//软键盘按下搜索键
		news_search_txt.setOnEditorActionListener(new OnEditorActionListener() {
					
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					
					closeSoftInput();
					webView.loadUrl(modelApp.getWebHost() + "/news/search?keyword=" + news_search_txt.getText());
                    return true;
                }
				return false;
			}
		});
		
	}
	
	
	
	
	
	
	
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
		}

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
			// 拦截超链接跳转到新闻详情页
			Intent intent = new Intent(mContext, NewsDetailActivity.class);
			Bundle data = new Bundle();
			data.putString("url", url);
			
			String[] params = url.split("/");
			if(params != null){
				String ids = params[params.length-1];
				if(!StringUtil.isEmpty(ids) && ids.contains(".html")){
					data.putString("newsId", ids.replaceAll(".html", ""));
				}
			}
			intent.putExtras(data);
			startActivity(intent);
			
			return true;
			
		}
		
	}


    /**
     * 解决500， 404 等等错误，webview拦截不到回调
     * @param url
     */
    private void loadDataFromNet(final String url) {
        try{
            //创建okHttpClient对象
            OkHttpClient mOkHttpClient = new OkHttpClient();
            //创建一个Request
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            //new call
            Call call = mOkHttpClient.newCall(request);
            //请求加入调度
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            webView.setVisibility(View.GONE);
                            error_lay.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    final String htmlStr = response.body().string();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            webView.loadUrl(url);
                            webView.setVisibility(View.VISIBLE);
                            error_lay.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	

	
}
