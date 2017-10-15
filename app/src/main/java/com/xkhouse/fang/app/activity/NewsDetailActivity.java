package com.xkhouse.fang.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
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
import android.widget.Toast;

//import com.squareup.okhttp.Call;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.view.SharePopupWindow;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.user.activity.LoginActivity;
import com.xkhouse.fang.user.task.FavoriteAddRequest;
import com.xkhouse.fang.user.task.FavoriteStatusRequest;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/** 
 * @Description: web页面
 * @author wujian  
 * @date 2015-10-12 上午10:26:55  
 */
public class NewsDetailActivity extends AppBaseActivity {

	private View root_view;
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

    private ProgressBar pb;
    private WebView webView;
    private LinearLayout error_lay;
	private String currentUrl;
	
	private SharePopupWindow popWindow;
	private String webTitle;
	private String webDescription;
    private String imageUrl;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_news_detail);
		
	}
	
	@Override
	protected void init() {
		super.init();
        currentUrl = getIntent().getExtras().getString("url");

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


        pb = (ProgressBar) findViewById(R.id.pb);
        webView = (WebView) findViewById(R.id.webview);
		
		WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
//        String str = webSettings.getUserAgentString();
//        webSettings.setUserAgentString(str + "XKAPP");
        
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebClient());
        webView.setDownloadListener(new MyWebViewDownLoadListener());

        loadWebData(currentUrl);
        htmlParse(currentUrl);
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
		root_view = findViewById(R.id.root_view);
		
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);

        tv_head_title.setText("惠早订");

		
		iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
	}
	

    private void htmlParse(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document=  Jsoup.connect(url).get();
                    Elements metas = document.select("meta");
                    for (Element meta : metas){
                        if("description".equals(meta.attr("name").toLowerCase())){
                            webDescription = meta.attr("content");
                        }
                    }
                    Elements images = document.select("img");
                    if(images != null && images.size() > 0){
                        imageUrl = images.get(0).attr("src");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


	
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
            webTitle = view.getTitle();
//            if(!StringUtil.isEmpty(isAd) && AD_FLAG.equals(isAd)){
//                tv_head_title.setText(webTitle);
//            }
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
            webTitle = "";
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webTitle = view.getTitle();
            htmlParse(url);

		}
	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d("", "url  == " + url);

            if (!StringUtil.isEmpty(url) && url.startsWith("tel:")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
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


    //下载跳转到浏览器
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
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
                public void onFailure(Call call, IOException e) {
                    if(isFinishing()) return;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            webView.setVisibility(View.GONE);
                            error_lay.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(isFinishing()) return;
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
			if(webView.canGoBack()){
				webView.goBack();
				return true;
			}
			if (popWindow != null && popWindow.isShowing()) {
				popWindow.dismiss();
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}


    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }
}
