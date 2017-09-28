package com.xkhouse.fang.user.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.user.adapter.FavAndBrowseAdapter;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.task.BrowseListRequest;
import com.xkhouse.fang.user.task.FavoriteListRequest;
import com.xkhouse.fang.user.task.MessageDetailListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
 * 收藏/浏览列表
 */
public class FavAndBrowseActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private XListView listView;
	private FavAndBrowseAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

    private BrowseListRequest browseRequest;
	private FavoriteListRequest favoriteRequest;
	private ArrayList<BookedInfo> bookedList = new ArrayList<>();

    public static final int TYPE_FAV = 0;
    public static final int TYPE_BROWSE = 1;
    private int type;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startDataTask(1, true);
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_favorite_browse);
	}

	@Override
	protected void init() {
		super.init();

        type = getIntent().getExtras().getInt("type");
	}

	@Override
	protected void findViews() {
		initTitle();

        listView = (XListView) findViewById(R.id.listView);
        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);

	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        if (type == TYPE_BROWSE) {
            tv_head_title.setText("我的浏览");
        }else {
            tv_head_title.setText("我的收藏");
        }

		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {
        error_lay.setOnClickListener(this);

        listView.setPullLoadEnable(true);
        listView.setPullRefreshEnable(true);
        listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startDataTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startDataTask(currentPageIndex, false);
            }
        }, R.id.listView);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){
            case R.id.error_lay:
                startDataTask(1, true);
                break;
        }
	}
	
	
	private void fillData(){
		if(bookedList == null) return;
		if(adapter == null ){
			adapter = new FavAndBrowseAdapter(mContext, bookedList);
            listView.setAdapter(adapter);
		}else {
			adapter.setData(bookedList);
		}
	}


	private RequestListener requestListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {

            rotate_loading.stop();
            rotate_loading.setVisibility(View.GONE);

            if (isPullDown){
                currentPageIndex = 1;
            }

            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    if (bookedList == null || bookedList.size() == 0){
                        listView.setVisibility(View.GONE);
                        error_lay.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.NO_DATA_FROM_NET:
                    error_lay.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    if(bookedList == null || bookedList.size() ==0){
                        listView.setVisibility(View.GONE);
                    }
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    listView.setVisibility(View.VISIBLE);
                    error_lay.setVisibility(View.GONE);

                    ArrayList<BookedInfo> temp = (ArrayList<BookedInfo>) message.getData().getSerializable("bookedList");
                    //根据返回的数据量判断是否隐藏加载更多
                    if(temp.size() < pageSize){
                        listView.setPullLoadEnable(false);
                    }else{
                        listView.setPullLoadEnable(true);
                    }
                    //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                    if(isPullDown && bookedList != null){
                        bookedList.clear();
                        currentPageIndex = 1;
                    }
                    bookedList.addAll(temp);

                    fillData();
                    if (currentPageIndex > 1 && message.arg1 == bookedList.size()){
                        Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                    }
                    currentPageIndex++;
                    break;
            }
            isPullDown = false;
            listView.stopRefresh();
            listView.stopLoadMore();
        }
    };


	private void startDataTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(mContext)) {

            if (showLoading){
                listView.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }

            if (type == TYPE_FAV){
                startFavTask(page);
            } else if (type == TYPE_BROWSE) {
                startBrowseTask(page);
            }

		}else {
			isPullDown = false;
            listView.stopRefresh();
            listView.stopLoadMore();
            if (bookedList == null || bookedList.size() == 0){
                listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}
	}

	private void startFavTask(int page) {
        if(favoriteRequest == null){
            favoriteRequest = new FavoriteListRequest(modelApp.getUser().getToken(), page, pageSize, requestListener);
        }else {
            favoriteRequest.setData(modelApp.getUser().getToken(), page, pageSize);
        }
        favoriteRequest.doRequest();
    }

    private void startBrowseTask(int page) {
        if(browseRequest == null){
            browseRequest = new BrowseListRequest(modelApp.getUser().getToken(), page, pageSize, requestListener);
        }else {
            browseRequest.setData(modelApp.getUser().getToken(), page, pageSize);
        }
        browseRequest.doRequest();
    }

}
