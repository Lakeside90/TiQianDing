package com.xkhouse.fang.user.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.TXRecord;
import com.xkhouse.fang.booked.task.TXRecordListRequest;
import com.xkhouse.fang.user.adapter.TXRecordAdapter;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.task.MessageDetailListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
 * 提现记录-员工
 */
public class TXRecordListActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;

	private XListView listView;
	private TXRecordAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private LinearLayout notice_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

	private TXRecordListRequest listRequest;
	private ArrayList<TXRecord> recordList = new ArrayList<>();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.employee_blue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		startDataTask(1, true);
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_employee_tx_record_list);
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
        notice_lay = (LinearLayout) findViewById(R.id.notice_lay);
		
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
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

		if(recordList == null) return;
		if(adapter == null ){
			adapter = new TXRecordAdapter(mContext, recordList);
			listView.setAdapter(adapter);
		}else {
			adapter.setData(recordList);
		}
	}
	
	private void startDataTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(mContext)) {
			if(listRequest == null){
				listRequest = new TXRecordListRequest(modelApp.getUser().getToken(),
						page, pageSize, new RequestListener() {
					
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
                                listView.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                error_lay.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.NO_DATA_FROM_NET:
                            error_lay.setVisibility(View.GONE);
                            notice_lay.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            if(recordList == null || recordList.size() ==0){
                                listView.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.VISIBLE);
                            }
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
                            listView.setVisibility(View.VISIBLE);
                            error_lay.setVisibility(View.GONE);
                            notice_lay.setVisibility(View.GONE);

							ArrayList<TXRecord> temp = (ArrayList<TXRecord>) message.obj;
							//根据返回的数据量判断是否隐藏加载更多
							if(temp.size() < pageSize){
								listView.setPullLoadEnable(false);
							}else{
								listView.setPullLoadEnable(true);
							}
							//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
							if(isPullDown && recordList != null){
								recordList.clear();
								currentPageIndex = 1;
							}
							recordList.addAll(temp);
                            if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                listView.setVisibility(View.GONE);
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
                notice_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			listRequest.doRequest();
		}else {
			isPullDown = false;
			listView.stopRefresh();
			listView.stopLoadMore();
            if (recordList == null || recordList.size() == 0){
                listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}
	}


}
