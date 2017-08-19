package com.xkhouse.fang.house.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
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
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.activity.CalculatorActivity;
import com.xkhouse.fang.app.activity.NewsDetailActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.view.SharePopupWindow;
import com.xkhouse.fang.money.activity.CustomerAddActivity;
import com.xkhouse.fang.money.activity.XKLoanIndexActivity;
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

/** 
 * @Description: 房源详情页 
 * @author wujian  
 * @date 2015-10-12 上午11:53:23  
 */
public class HouseDetailActivity extends AppBaseActivity {
	
	private View root_view;
	private ImageView iv_head_left;
	private TextView tv_head_title;
	private ImageView iv_head_share;		//分享
	private ImageView iv_head_favorite;		//收藏

    private ProgressBar pb;
	private WebView webView;
    private LinearLayout error_lay;
	private String currentUrl;
	private String projectId;
	private String projectName;
	private int houseType;   //新房  二手房  租房
	
	private SharePopupWindow popWindow;
	private String webTitle;
    private String webDescription;
    private String imageUrl;

	private boolean isXKB = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startFavoriteStateTask();
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_house_detail);
	}
	
	@Override
	protected void init() {
		super.init();
		projectId = getIntent().getExtras().getString("projectId");
		projectName = getIntent().getExtras().getString("projectName");
		houseType = getIntent().getExtras().getInt("houseType");
		isXKB = getIntent().getExtras().getBoolean("isXKB", false);
		
		if(houseType == MapHousesActivity.HOUSE_TYPE_NEW){
            currentUrl = modelApp.getNewHouseDetail() + projectId;
			
		}else if(houseType == MapHousesActivity.HOUSE_TYPE_OLD) {
            currentUrl = modelApp.getOldHouseDetail() + projectId;
			
		}else if(houseType == MapHousesActivity.HOUSE_TYPE_RENT) {
            currentUrl = modelApp.getRentHouseDetail() + projectId;
		}
		
		if(isXKB){
            currentUrl = currentUrl + "?xkb=1";
		}
		
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
        webSettings.setUserAgentString(str+"XKAPP");
        
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
		iv_head_favorite = (ImageView) findViewById(R.id.iv_head_favorite);
		
		tv_head_title.setText(projectName);
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
                popWindow.showAtLocation(webTitle, webDescription, imageUrl, webView.getUrl(), false,
                        root_view, Gravity.BOTTOM, 0, 0);
            }
        });
		
		iv_head_favorite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Preference.getInstance().readIsLogin()) {
                    startFavoriteAddTask();
                } else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
	}

    private void htmlParse(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document =  Jsoup.connect(url).get();

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
		if(!Preference.getInstance().readIsLogin()){
			Toast.makeText(mContext, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivity(intent);
			return;
		}
		if(NetUtil.detectAvailable(mContext)){
			String type = "1";
			if(houseType == MapHousesActivity.HOUSE_TYPE_NEW){
				type = "1";
			}else if(houseType == MapHousesActivity.HOUSE_TYPE_OLD) {
				type = "2";
			}else if(houseType == MapHousesActivity.HOUSE_TYPE_RENT) {
				type = "3";
			}
			
			FavoriteAddRequest favoriteAddRequest = new FavoriteAddRequest(modelApp.getUser().getUid(),
					projectId, type, modelApp.getSite().getSiteId(), new RequestListener() {
						
						@Override
						public void sendMessage(Message message) {
							switch (message.what) {
							case Constants.ERROR_DATA_FROM_NET:
								Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
								break;
								
							case Constants.NO_DATA_FROM_NET:
							case Constants.SUCCESS_DATA_FROM_NET:
                                String msg = (String) message.obj;
                                if (!StringUtil.isEmpty(msg)){
                                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                }
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
		if(!Preference.getInstance().readIsLogin()){
			iv_head_favorite.setImageResource(R.drawable.nav_favorite);
			return;
		}
		if(NetUtil.detectAvailable(mContext)){
			String type = "1";
			if(houseType == MapHousesActivity.HOUSE_TYPE_NEW){
				type = "1";
			}else if(houseType == MapHousesActivity.HOUSE_TYPE_OLD) {
				type = "2";
			}else if(houseType == MapHousesActivity.HOUSE_TYPE_RENT) {
				type = "3";
			}
			
			FavoriteStatusRequest favoriteStatusRequest = new FavoriteStatusRequest(modelApp.getUser().getUid(),
					projectId, type, modelApp.getSite().getSiteId(), new RequestListener() {
						
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
			webTitle = title;
			if(!StringUtil.isEmpty(title)){
				String[] titles = title.split("_");
				if(titles != null && titles.length > 0){
					webTitle = titles[0];
					if(titles[0].contains("楼盘详情")){
						webTitle = titles[0].replace("楼盘详情", "");
					}
				}
			}
			if(StringUtil.isEmpty(projectName)){
				projectName = webTitle;
				tv_head_title.setText(projectName);
			}
			Logger.d(TAG, "webTitle---" + webTitle);
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
            htmlParse(url);
		}
	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (!StringUtil.isEmpty(url) && url.startsWith("tel:")){ 
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
                startActivity(intent);
                return true;
                
            }else if(!StringUtil.isEmpty(url) && url.contains("53kf")){
                //跳转到53客服
                Intent intent = new Intent(mContext, ConsultOnlineActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("projectName", projectName);
                bundle.putString("url", url);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            }else if (!StringUtil.isEmpty(url) && url.contains("tools/counter")){
                startActivity(new Intent(mContext, CalculatorActivity.class));
                return true;
                
            }else if(!StringUtil.isEmpty(url) && url.contains("dongtai.html")){
				//跳转到销售动态
				Intent intent = new Intent(mContext, HouseDynamicActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId", projectId);
				intent.putExtras(bundle);
				startActivity(intent);
                return true;
				
			}else if(!StringUtil.isEmpty(url) && url.contains("/huxing/")) {
                if(url.contains(".html")){
                    //跳转到户型详情
                    String[] params = url.split("/");
                    if(params != null && params.length > 1){
                        String roomId = params[params.length-1].replace(".html","");
                        Intent intent = new Intent(mContext, RoomDetailActivity.class);
                        Bundle data = new Bundle();
                        data.putInt("roomType", RoomDetailActivity.ROOM_TYPE_HX);
                        data.putString("projectId", projectId);
                        data.putString("roomId", roomId);
                        intent.putExtras(data);
                        startActivity(intent);
                    }
                }else{
                    //跳转到主力户型
                    Intent intent = new Intent(mContext, MainHouseTypeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("projectId", projectId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return true;
				
			}else if(!StringUtil.isEmpty(url) && url.contains("fangyuan/")) {
				//跳转到在售房源
				Intent intent = new Intent(mContext, OnsaleHouseActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId", projectId);
				intent.putExtras(bundle);
				startActivity(intent);
                return true;
				
			}else if(!StringUtil.isEmpty(url) && url.contains("/loan/")) {
				startActivity(new Intent(mContext, XKLoanIndexActivity.class));
                return true;
				
			}else if(!StringUtil.isEmpty(url) && url.contains("/Recommend/")){
				//推荐赚佣
				Bundle data = new Bundle();
				data.putString("houseID", "p" + projectId + ",");
				data.putString("houseName", projectName + ",");
				
				if(Preference.getInstance().readIsLogin()){
					Intent intent = new Intent(mContext, CustomerAddActivity.class);
					if(!StringUtil.isEmpty(projectId) && !StringUtil.isEmpty(projectName)){
						intent.putExtras(data);
					}
					startActivity(intent);
                    return true;
				}else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					intent.putExtra("classStr", CustomerAddActivity.class);
					if(!StringUtil.isEmpty(projectId) && !StringUtil.isEmpty(projectName)){
						intent.putExtras(data);
					}
					startActivity(intent);
                    return true;
				}
			}else if(url.contains("/xiangce")  || url.contains("/imgDetail/")){
                Intent intent = new Intent(mContext, HouseAlbumActivity.class);
                Bundle data = new Bundle();
                data.putString("url", url);
                intent.putExtras(data);
                startActivity(intent);
                return true;
			}else if(url.contains("/around.html")){
                Intent intent = new Intent(mContext, HouseSupportsActivity.class);
                Bundle data = new Bundle();
                data.putString("url", url);
                intent.putExtras(data);
                startActivity(intent);
                return true;

            }else if(url.contains("/newhouse/Detail/lpgk")){
                Intent intent  = new Intent(mContext, HouseProjectDetailActivity.class);
                Bundle data = new Bundle();
                data.putString("url", url);
                data.putString("projectId", projectId);
                intent.putExtras(data);
                startActivity(intent);
                return true;

            }else if(url.contains("/newhouse/detail/lpwd")){
                Intent intent  = new Intent(mContext, HouseQuestionListActivity.class);
                Bundle data = new Bundle();
                data.putString("pid", projectId);
                intent.putExtras(data);
                startActivity(intent);
                return true;

            }else if(url.contains("/newhouse/") || url.contains("/oldhouse/") || url.contains("/zufang/")){
				//周边房源
				String[] params = url.split("/");
				if(params != null && params.length == 5){
					Intent intent = new Intent(mContext, HouseAroundDetailActivity.class);
					Bundle data = new Bundle();
					data.putString("projectId", params[4]);
					data.putInt("houseType", houseType);
					intent.putExtras(data);
					startActivity(intent);
                    return true;
				}
				
			}else if(url.contains("/news/")){
				String[] params = url.split("/");
				if(params != null){
					String ids = params[params.length-1];
					if(!StringUtil.isEmpty(ids) && ids.contains(".html")){
						Intent intent = new Intent(mContext, NewsDetailActivity.class);
						Bundle data = new Bundle();
						data.putString("newsId", ids.replaceAll(".html",""));
						data.putString("url", url);
						intent.putExtras(data);
						startActivity(intent);
                        return true;
					}
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
	
	
}
