package com.xkhouse.fang.user.activity;

import android.content.Intent;
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
import com.xkhouse.fang.booked.entity.AddressInfo;
import com.xkhouse.fang.booked.task.AddressInfoListRequest;
import com.xkhouse.fang.user.adapter.AddressAdapter;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.task.MessageDetailListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
 * 收货地址
 */
public class AddressListActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

	private XListView listView;
	private AddressAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private LinearLayout address_add_lay;

	private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

	private AddressInfoListRequest listRequest;
	private ArrayList<AddressInfo> addressInfos = new ArrayList<>();

//	private MessageDetailListRequest listRequest;
//	private ArrayList<MSGNews> newsList = new ArrayList<MSGNews>();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startDataTask(1, true);

	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_address_list);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();
		
		listView = (XListView) findViewById(R.id.listView);

        address_add_lay = (LinearLayout) findViewById(R.id.address_add_lay);

		content_lay = (LinearLayout) findViewById(R.id.content_lay);
		rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);

	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("收货地址");
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
        address_add_lay.setOnClickListener(this);

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

            case R.id.address_add_lay:
                startActivity(new Intent(mContext, AddressEditActivity.class));
                break;
        }
	}
	
	
	private void fillData(){

		if(addressInfos == null) return;
		if(adapter == null ){
			adapter = new AddressAdapter(mContext, addressInfos, new AddressAdapter.AddressClickListener() {
				@Override
				public void onEdit(int position) {
					if (addressInfos == null || addressInfos.size() <= position) return;
					Intent intent = new Intent(mContext, AddressListActivity.class);
					Bundle data = new Bundle();
					data.putSerializable("addressInfo", addressInfos.get(position));
					intent.putExtras(data);
					startActivity(intent);
				}

				@Override
				public void onDelete(int position) {

				}
			});

			listView.setAdapter(adapter);
		}else {
			adapter.setData(addressInfos);
		}
	}


	RequestListener requestListener = new RequestListener() {

		@Override
		public void sendMessage(Message message) {

			rotate_loading.stop();
			rotate_loading.setVisibility(View.GONE);

			if (isPullDown) currentPageIndex = 1;

			switch (message.what) {
				case Constants.ERROR_DATA_FROM_NET:
					if (addressInfos == null || addressInfos.size() == 0){
						content_lay.setVisibility(View.GONE);
						error_lay.setVisibility(View.VISIBLE);
					}else{
						Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
					}
					break;

				case Constants.NO_DATA_FROM_NET:
					error_lay.setVisibility(View.GONE);
					content_lay.setVisibility(View.VISIBLE);
					break;

				case Constants.SUCCESS_DATA_FROM_NET:
					content_lay.setVisibility(View.VISIBLE);
					error_lay.setVisibility(View.GONE);

					ArrayList<AddressInfo> temp = (ArrayList<AddressInfo>) message.obj;
					//根据返回的数据量判断是否隐藏加载更多
					if(temp.size() < pageSize){
						listView.setPullLoadEnable(false);
					}else{
						listView.setPullLoadEnable(true);
					}
					//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
					if(isPullDown && addressInfos != null){
						addressInfos.clear();
						currentPageIndex = 1;
					}
					addressInfos.addAll(temp);

					fillData();

					if (currentPageIndex > 1 && message.arg1 == addressInfos.size()){
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
			if(listRequest == null){
				listRequest = new AddressInfoListRequest(modelApp.getUser().getToken(),
						page, pageSize, requestListener);
			}else {
				listRequest.setData(modelApp.getUser().getToken(), page, pageSize);
			}
			if (showLoading){
				content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			listRequest.doRequest();
		}else {
			isPullDown = false;
			listView.stopRefresh();
			listView.stopLoadMore();
            if (addressInfos == null || addressInfos.size() == 0){
				content_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}
	}

}
