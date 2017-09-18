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
import com.xkhouse.fang.user.adapter.WalletRecordAdapter;
import com.xkhouse.fang.user.entity.WalletRecord;
import com.xkhouse.fang.user.task.WalletListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
* @Description: 提现记录 
* @author wujian  
* @date 2015-10-30 下午2:28:34
 */
public class WalletWithdrawActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private LinearLayout notice_lay;

	private XListView wallet_listview;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉
	private WalletRecordAdapter adapter;
	private ArrayList<WalletRecord> recordList = new ArrayList<WalletRecord>();
	
	private String type = WalletRecordListActivity.WALLET_OUT;
	
	private WalletListRequest walletListRequest;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		startDataTask(1, true);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_wallet_withdraw);
		
		
		wallet_listview = (XListView) findViewById(R.id.wallet_listview);
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();

        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);
        notice_lay = (LinearLayout) findViewById(R.id.notice_lay);
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("提现记录");
		
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {
		super.setListeners();

        error_lay.setOnClickListener(this);

		wallet_listview.setPullLoadEnable(true);
		wallet_listview.setPullRefreshEnable(true);
		wallet_listview.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				isPullDown = true;
				startDataTask(1, false);
			}
			
			@Override
			public void onLoadMore() {
				startDataTask(currentPageIndex, false);
			}
		}, R.id.wallet_listview);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

		case R.id.wallet_application_txt:
			startActivity(new Intent(mContext, CashWithdrawActivity.class));
			break;

            case R.id.error_lay:
                startDataTask(1, true);
                break;
		}
	}
	
	
	
	
	
	private void fillData() {
		if(recordList == null) return;
		
		if(adapter == null){
			adapter = new WalletRecordAdapter(mContext, recordList);
			wallet_listview.setAdapter(adapter);
		}else {
			adapter.setData(recordList);
		}
	}
	
	
	private void startDataTask(int page, boolean showLoading) {
        if(rotate_loading.getVisibility() == View.VISIBLE) return;

		if(NetUtil.detectAvailable(mContext)){

			if(walletListRequest == null){
				walletListRequest = new WalletListRequest(modelApp.getUser().getId(), type, page, pageSize,
						new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);

                        if (isPullDown){
                            currentPageIndex = 1;
                        }

						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
                            if (recordList == null || recordList.size() == 0){
                                wallet_listview.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                error_lay.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.NO_DATA_FROM_NET:
                            error_lay.setVisibility(View.GONE);
                            notice_lay.setVisibility(View.GONE);
                            wallet_listview.setVisibility(View.VISIBLE);
                            if(recordList == null || recordList.size() ==0){
                                wallet_listview.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.VISIBLE);
                            }
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:

                            wallet_listview.setVisibility(View.VISIBLE);
                            error_lay.setVisibility(View.GONE);
                            notice_lay.setVisibility(View.GONE);

							Bundle data = message.getData();
							ArrayList<WalletRecord> temp = (ArrayList<WalletRecord>) data.getSerializable("walletRecordList");
							//根据返回的数据量判断是否隐藏加载更多
							if(temp.size() < pageSize){
								wallet_listview.setPullLoadEnable(false);
							}else{
								wallet_listview.setPullLoadEnable(true);
							}
							
							//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
							if(isPullDown && recordList != null){
								recordList.clear();
								currentPageIndex = 1;
							}
							recordList.addAll(temp);

                            if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                wallet_listview.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.VISIBLE);
                                return;
                            }

							fillData();
                            if (currentPageIndex > 1 && message.arg1 == recordList.size()){
                                Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                            }
                            currentPageIndex++;
							break;
						}
						isPullDown = false;
						wallet_listview.stopRefresh();
						wallet_listview.stopLoadMore();
					}
				});
			}else {
				walletListRequest.setData(modelApp.getUser().getId(), type, page, pageSize);
			}
            if (showLoading){
                wallet_listview.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			walletListRequest.doRequest();
			
		}else{
			isPullDown = false;
			wallet_listview.stopRefresh();
			wallet_listview.stopLoadMore();

            if (recordList == null || recordList.size() ==0 ){
                wallet_listview.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}
	}

}
