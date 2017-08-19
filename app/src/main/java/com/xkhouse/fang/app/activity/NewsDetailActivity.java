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

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
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

/** 
 * @Description: 新闻详情
 * @author wujian  
 * @date 2015-10-12 上午10:26:55  
 */
public class NewsDetailActivity extends AppBaseActivity {

	private View root_view;
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	private ImageView iv_head_share;		//分享
	private ImageView iv_head_favorite;		//收藏

    private ProgressBar pb;
    private WebView webView;
    private LinearLayout error_lay;
	private String currentUrl;
	
	private String newsId;
    private String isAd;
	public static final String AD_FLAG = "AD";


	private SharePopupWindow popWindow;
	private String webTitle;
	private String webDescription;
    private String imageUrl;

	//符合这种形式的Url，才能收藏，王蓉说的。2015-11-24
	private Pattern pattern = Pattern.compile("/news/+[0-9a-zA-Z_]+.html");  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(pattern.matcher(currentUrl).find()){
			startFavoriteStateTask();
		}
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
		newsId = getIntent().getExtras().getString("newsId");
        isAd = getIntent().getExtras().getString("isAd");
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

		if(pattern.matcher(currentUrl).find()){
			iv_head_favorite.setVisibility(View.VISIBLE);
		}else{
			iv_head_favorite.setVisibility(View.GONE);
		}

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
		
		iv_head_favorite = (ImageView) findViewById(R.id.iv_head_favorite);
		iv_head_share = (ImageView) findViewById(R.id.iv_head_share);
        if(!StringUtil.isEmpty(isAd) && AD_FLAG.equals(isAd)){
            tv_head_title.setText(webTitle);
        }else{
            if(!StringUtil.isEmpty(currentUrl) && currentUrl.contains("/home/")){
                tv_head_title.setText("家居");
            }else{
                tv_head_title.setText("房产资讯");
            }
        }

		
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
		
		iv_head_favorite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startFavoriteAddTask();
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


	private void startFavoriteAddTask(){
		if(StringUtil.isEmpty(newsId)) return;
		
		if(!Preference.getInstance().readIsLogin()){
			Toast.makeText(mContext, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivity(intent);
			return;
		}
		if(NetUtil.detectAvailable(mContext)){
			if(!Preference.getInstance().readIsLogin()){
				Toast.makeText(mContext, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
				return;
			}
			
			FavoriteAddRequest favoriteAddRequest = new FavoriteAddRequest(modelApp.getUser().getUid(),
					newsId, "4", modelApp.getSite().getSiteId(), new RequestListener() {
						
						@Override
						public void sendMessage(Message message) {
							switch (message.what) {
							case Constants.ERROR_DATA_FROM_NET:
								Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
								break;
								
							case Constants.NO_DATA_FROM_NET:
							case Constants.SUCCESS_DATA_FROM_NET:
								Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
								startFavoriteStateTask();
								break;
							}
						}
					});
			Toast.makeText(mContext, "收藏操作处理中...", Toast.LENGTH_SHORT).show();
			favoriteAddRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	//是否已经收藏
	private void startFavoriteStateTask(){
		if(StringUtil.isEmpty(newsId)) return;
		
		if(!Preference.getInstance().readIsLogin()){
			iv_head_favorite.setImageResource(R.drawable.nav_favorite);
			return;
		}
		if(NetUtil.detectAvailable(mContext)){
			FavoriteStatusRequest favoriteStatusRequest = new FavoriteStatusRequest(modelApp.getUser().getUid(),
					newsId, "4", modelApp.getSite().getSiteId(), new RequestListener() {
						
						@Override
						public void sendMessage(Message message) {
							switch (message.what) {
							case Constants.ERROR_DATA_FROM_NET:
								Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
								break;
								
							case Constants.NO_DATA_FROM_NET:
							case Constants.SUCCESS_DATA_FROM_NET:
								String msg = (String) message.obj;
								if("1".equals(msg)){
									//已收藏
									iv_head_favorite.setImageResource(R.drawable.nav_favorite_pressed);
								}else{
									iv_head_favorite.setImageResource(R.drawable.nav_favorite);
								}
								break;
							}
						}
					});
			favoriteStatusRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
            webTitle = view.getTitle();
            if(!StringUtil.isEmpty(isAd) && AD_FLAG.equals(isAd)){
                tv_head_title.setText(webTitle);
            }
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
            if(!StringUtil.isEmpty(isAd) && AD_FLAG.equals(isAd)){
                tv_head_title.setText(webTitle);
            }

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
            if(!StringUtil.isEmpty(isAd) && AD_FLAG.equals(isAd)){
                tv_head_title.setText(webTitle);
            }
            htmlParse(url);

			if(pattern.matcher(url).find()){
				iv_head_favorite.setVisibility(View.VISIBLE);
				if(url.contains("news")){
					String[] params = url.split("/");
					if(params != null){
						String ids = params[params.length-1];
						if(!StringUtil.isEmpty(ids) && ids.contains(".html")){
							newsId = ids.replaceAll(".html", "");
							startFavoriteStateTask();
						}
					}
				}
			}else{
				iv_head_favorite.setVisibility(View.GONE);
			}
		}
	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d("", "url  == " + url);

            if (!StringUtil.isEmpty(url) && url.startsWith("tel:")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;

            }else if(!StringUtil.isEmpty(url) && url.contains("/newhouse/")){
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
            }else if(url.contains("home/peigou")){
                Intent intent = new Intent(mContext, JJAppointmentActivity.class);
                Bundle data = new Bundle();

                if(url.contains("home/peigou/index1")){
                    data.putInt("index", 1);
                }else {
                    data.putInt("index", 0);
                }
                intent.putExtras(data);
                startActivity(intent);
                return true;

            }else if(url.contains("home/baozhang/index2")){
				Intent intent = new Intent(mContext, JJFreeServiceActivity.class);
				Bundle data = new Bundle();
				data.putInt("index", 2);
				intent.putExtras(data);
				startActivity(intent);
				
				return true;
				
			}else if(url.contains("news")){
				if(pattern.matcher(url).find()){
					iv_head_favorite.setVisibility(View.VISIBLE);
					String[] params = url.split("/");
					if(params != null){
						String ids = params[params.length-1];
						if(!StringUtil.isEmpty(ids) && ids.contains(".html")){
							newsId = ids.replaceAll(".html", "");
							startFavoriteStateTask();
						}
					}
				}else{
					iv_head_favorite.setVisibility(View.GONE);
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
                public void onFailure(Request request, IOException e) {
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
                public void onResponse(final Response response) throws IOException {

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
