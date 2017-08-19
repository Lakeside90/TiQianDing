package com.xkhouse.fang.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
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

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.view.SharePopupWindow;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/** 
 * @Description: 看房路线
 * @author wujian  
 * @date 2015-10-12 上午10:26:55  
 */
public class KanFangDetailActivity extends AppBaseActivity {

	private View root_view;
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
    private ImageView iv_head_share;		//分享


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
		
		setContentView(R.layout.activity_kanfang_detail);
		
	}
	
	@Override
	protected void init() {
		super.init();
        currentUrl = getIntent().getExtras().getString("url");

		Logger.d("", "init  url  == " + currentUrl);
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
        String str = webSettings.getUserAgentString();
        webSettings.setUserAgentString(str + "XKAPP");
        
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebClient());

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
        iv_head_share = (ImageView) findViewById(R.id.iv_head_share);

        tv_head_title.setText("看房路线");

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


        iv_head_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popWindow == null) {
                    popWindow = new SharePopupWindow(mContext);
                }
                //处理title 把"-"后面的内容去掉，这个需求不知为何
                if(StringUtil.isEmpty(webTitle)) webTitle = "星房惠";
                String title;
                if (webTitle.contains("-")){
                    String[] titles = webTitle.split("-");
                    if (titles != null && titles.length > 0){
                        title = titles[0];
                    }else{
                        title = webTitle;
                    }
                }else{
                    title = webTitle;
                }
                popWindow.showAtLocation(title, webDescription, imageUrl, webView.getUrl(), true,
                        root_view, Gravity.BOTTOM, 0, 0);
            }
        });


	}


    private void htmlParse(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document=  Jsoup.connect(url).get();
                    Logger.d(TAG, "title ==== " + document.title());
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
            webTitle = title;
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
            if(url.contains("/imgDetail/")){
                iv_head_share.setVisibility(View.INVISIBLE);
            }else{
                iv_head_share.setVisibility(View.VISIBLE);
            }

		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webTitle = view.getTitle();
		}
	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d("", "url  == " + url);
            htmlParse(url);

            if (!StringUtil.isEmpty(url) && url.startsWith("tel:")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;

            }if(!StringUtil.isEmpty(url) && url.contains("/newhouse/")){
                String params[] = url.split("/");
                if(params != null && params.length == 5){
                    Intent intent = new Intent(mContext, HouseDetailActivity.class);
                    Bundle data = new Bundle();
                    data.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
                    data.putString("projectId", params[params.length-1]);
                    data.putString("projectName", "");
                    intent.putExtras(data);
                    startActivity(intent);
                    return true;
                }
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
			if(webView.canGoBack()){
				webView.goBack();
				return true;
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
