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
import com.xkhouse.fang.user.adapter.MyBookedAdapter;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.entity.MyBookedInfo;
import com.xkhouse.fang.user.task.MessageDetailListRequest;
import com.xkhouse.fang.user.task.MyBookedListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
 * 我的预定列表
 */
public class MyBookedListActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private XListView listView;
	private MyBookedAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

	private MyBookedListRequest listRequest;
	private ArrayList<MyBookedInfo> myBookedInfoList = new ArrayList<>();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startDataTask(1, true);
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_my_booked);
	}

	@Override
	protected void init() {
		super.init();
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
        tv_head_title.setText("我的预定");
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
        }, R.id.favorite_listView);
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

		if(myBookedInfoList == null) return;
		if(adapter == null ){
			adapter = new MyBookedAdapter(mContext, myBookedInfoList);
			listView.setAdapter(adapter);
		}else {
			adapter.setData(myBookedInfoList);
		}
	}
	
	private void startDataTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(mContext)) {
			if(listRequest == null){
				listRequest = new MyBookedListRequest(modelApp.getUser().getToken(), page, pageSize, new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {

                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);

                        if (isPullDown){
                            currentPageIndex = 1;
                        }

						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
                            if (myBookedInfoList == null || myBookedInfoList.size() == 0){
                                listView.setVisibility(View.GONE);
                                error_lay.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.NO_DATA_FROM_NET:
                            error_lay.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            if(myBookedInfoList == null || myBookedInfoList.size() ==0){
                                listView.setVisibility(View.GONE);
                            }
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
                            listView.setVisibility(View.VISIBLE);
                            error_lay.setVisibility(View.GONE);

							ArrayList<MyBookedInfo> temp = (ArrayList<MyBookedInfo>) message.getData().getSerializable("bookedList");
							//根据返回的数据量判断是否隐藏加载更多
							if(temp.size() < pageSize){
								listView.setPullLoadEnable(false);
							}else{
								listView.setPullLoadEnable(true);
							}
							//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
							if(isPullDown && myBookedInfoList != null){
								myBookedInfoList.clear();
								currentPageIndex = 1;
							}
							myBookedInfoList.addAll(temp);

							fillData();
                            if (currentPageIndex > 1 && message.arg1 == myBookedInfoList.size()){
                                Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                            }
                            currentPageIndex++;
							break;
						}
						isPullDown = false;
						listView.stopRefresh();
						listView.stopLoadMore();
					}
				});
			}else {
				listRequest.setData(modelApp.getUser().getToken(), page, pageSize);
			}
			if (showLoading){
                listView.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			listRequest.doRequest();
		}else {
			isPullDown = false;
			listView.stopRefresh();
			listView.stopLoadMore();
            if (myBookedInfoList == null || myBookedInfoList.size() == 0){
                listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}
	}

}
