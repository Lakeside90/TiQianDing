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
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.AccountInfo;
import com.xkhouse.fang.booked.task.AccountInfoListRequest;
import com.xkhouse.fang.booked.task.EmployeeAccountInfoListRequest;
import com.xkhouse.fang.user.adapter.AccountInfoAdapter;
import com.xkhouse.fang.user.adapter.EmployeeAccountInfoAdapter;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.task.MessageDetailListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
 * 账户明细--员工
 */
public class EmployeeAccountInfoListActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;

	private TextView money_txt;
	private XListView listView;
	private EmployeeAccountInfoAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private LinearLayout notice_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

	private EmployeeAccountInfoListRequest listRequest;
	private ArrayList<AccountInfo> accountList = new ArrayList<>();
	private String balance = "0";



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
		setContentView(R.layout.activity_employee_account_info_list);
	}

	@Override
	protected void init() {
		super.init();

	}

	@Override
	protected void findViews() {
		initTitle();

		money_txt = (TextView) findViewById(R.id.money_txt);
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
		if (StringUtil.isEmpty(balance)) {
			balance = "0";
		}
		money_txt.setText(balance);

		if(accountList == null) return;
		if(adapter == null ){
			adapter = new EmployeeAccountInfoAdapter(mContext, accountList);
			listView.setAdapter(adapter);
		}else {
			adapter.setData(accountList);
		}
	}

	private void startDataTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(mContext)) {
			if(listRequest == null){
				listRequest = new EmployeeAccountInfoListRequest(modelApp.getUser().getToken(), page, pageSize, new RequestListener() {

					@Override
					public void sendMessage(Message message) {

						rotate_loading.stop();
						rotate_loading.setVisibility(View.GONE);

						if (isPullDown){
							currentPageIndex = 1;
						}

						switch (message.what) {
							case Constants.ERROR_DATA_FROM_NET:
								if (accountList == null || accountList.size() == 0){
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
								if(accountList == null || accountList.size() ==0){
									listView.setVisibility(View.GONE);
									notice_lay.setVisibility(View.VISIBLE);
								}
								break;

							case Constants.SUCCESS_DATA_FROM_NET:
								listView.setVisibility(View.VISIBLE);
								error_lay.setVisibility(View.GONE);
								notice_lay.setVisibility(View.GONE);

								Bundle bundle = message.getData();
								ArrayList<AccountInfo> temp = (ArrayList<AccountInfo>) bundle.getSerializable("accountInfoList");
								balance = bundle.getString("balance");

								//根据返回的数据量判断是否隐藏加载更多
								if(temp.size() < pageSize){
									listView.setPullLoadEnable(false);
								}else{
									listView.setPullLoadEnable(true);
								}
								//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
								if(isPullDown && accountList != null){
									accountList.clear();
									currentPageIndex = 1;
								}
								accountList.addAll(temp);
								if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
									listView.setVisibility(View.GONE);
									notice_lay.setVisibility(View.VISIBLE);
									return;
								}

								fillData();
								if (currentPageIndex > 1 && message.arg1 == accountList.size()){
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
			if (accountList == null || accountList.size() == 0){
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
