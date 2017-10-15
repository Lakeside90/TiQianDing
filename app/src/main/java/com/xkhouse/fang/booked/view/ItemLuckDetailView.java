package com.xkhouse.fang.booked.view;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.adapter.CJListAdapter;
import com.xkhouse.fang.user.entity.XKRecommend;
import com.xkhouse.fang.user.task.XKRecommendListRequest;
import com.xkhouse.fang.widget.ScrollXListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import com.xkhouse.fang.booked.activity.LuckDetailBakActivity;

import java.util.ArrayList;

/**
 * 活动详情  tab
 */
public class ItemLuckDetailView {

	private Context context;
	private View rootView;

    private WebView luck_detail_webview;

	private ScrollXListView luck_join_listView;
	private CJListAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

	private XKRecommendListRequest recommendListRequest;
	private ArrayList<XKRecommend> recommendList = new ArrayList<XKRecommend>();

	private String status;

	private ModelApplication modelApp;


	public View getView() {
        return rootView;
    }

	public ItemLuckDetailView(Context context, String status) {

		this.context = context;
		this.status = status;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		initView();

        webviewSettings(luck_detail_webview);

        setListener();

        fillData();

	}

	private void fillData() {
        switch (status) {
            case LuckDetailBakActivity.LUCK_DETAIL:
                luck_join_listView.setVisibility(View.GONE);
                luck_detail_webview.setVisibility(View.VISIBLE);
                luck_detail_webview.loadUrl("http://baidu.com");
                break;

            case LuckDetailBakActivity.LUCK_RULE:
                luck_join_listView.setVisibility(View.GONE);
                luck_detail_webview.setVisibility(View.VISIBLE);
                luck_detail_webview.loadUrl("http://baidu.com");
                break;


            case LuckDetailBakActivity.LUCK_JOIN:
                luck_join_listView.setVisibility(View.VISIBLE);
                luck_detail_webview.setVisibility(View.GONE);
//                luck_join_listView.setAdapter(new CJListAdapter(context, null));
                break;

        }
    }

    private void webviewSettings(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }



	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_luck_detail_list, null);

		luck_join_listView = (ScrollXListView) rootView.findViewById(R.id.luck_join_listView);

        luck_detail_webview = (WebView) rootView.findViewById(R.id.luck_detail_webview);
    }
	
	public void refreshView(){
		isPullDown = true;
		startRecommendListTask(1, true);
	}
	
	private void setListener() {
		luck_join_listView.setPullLoadEnable(true);
		luck_join_listView.setPullRefreshEnable(true);
		luck_join_listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startRecommendListTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startRecommendListTask(currentPageIndex, false);
            }
        }, R.id.luck_join_listView);


	}
	
	
	private void startRecommendListTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(context)) {
			if(recommendListRequest == null){
				recommendListRequest = new XKRecommendListRequest("1000", status, page, pageSize,
						new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {


                        if (isPullDown){
                            currentPageIndex = 1;
                        }

						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
							break;
							
						case Constants.NO_DATA_FROM_NET:
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:

							ArrayList<XKRecommend> temp = (ArrayList<XKRecommend>) message.obj;
							//根据返回的数据量判断是否隐藏加载更多
							if(temp.size() < pageSize){
								luck_join_listView.setPullLoadEnable(false);
							}else{
								luck_join_listView.setPullLoadEnable(true);
							}
							
							//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
							if(isPullDown && recommendList != null){
								recommendList.clear();
								currentPageIndex = 1;
							}
							recommendList.addAll(temp);
							fillRecommendData();
                            if (currentPageIndex > 1 && message.arg1 == recommendList.size()){
                                Toast.makeText(context, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                            }
                            currentPageIndex++;
							break;
						}
						isPullDown = false;
						luck_join_listView.stopRefresh();
						luck_join_listView.stopLoadMore();
					}
				});
			}else {
				recommendListRequest.setData("100", status, page, pageSize);
			}

			recommendListRequest.doRequest();

		}else {
			isPullDown = false;
			luck_join_listView.stopRefresh();
			luck_join_listView.stopLoadMore();


		}

        fillRecommendData();
	}
	
	
	private void fillRecommendData() {
//		if(recommendList == null) return;
//
//		if(adapter == null){
//			adapter = new CJListAdapter(context, recommendList);
//			luck_join_listView.setAdapter(adapter);
//		}else {
//			adapter.setData(recommendList);
//		}
	}
	
	
	


    
}
