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
import com.xkhouse.fang.money.activity.CustomerAddActivity;
import com.xkhouse.fang.user.adapter.WalletRecordAdapter;
import com.xkhouse.fang.user.entity.WalletRecord;
import com.xkhouse.fang.user.task.WalletListRequest;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;


/** 
 * @Description: 钱包 
 * @author wujian  
 * @date 2015-10-10 上午10:54:06  
 */
public class WalletActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_right;
	private TextView tv_head_title;

	private TextView wallet_money_txt;

    //累计赚佣
    private LinearLayout yong_num_lay;
    private TextView yong_num_txt;

    //累计提现
    private LinearLayout tixian_num_lay;
    private TextView tixian_num_txt;

	private TextView customer_txt;
	private TextView wallet_application_txt;

	
	private XListView wallet_listview;
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉
	private WalletRecordAdapter adapter;
	private ArrayList<WalletRecord> recordList = new ArrayList<WalletRecord>();
	
	private WalletListRequest walletListRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fillData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isPullDown = true;
		startDataTask(true);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_wallet);
		
		wallet_money_txt = (TextView) findViewById(R.id.wallet_money_txt);

        yong_num_lay = (LinearLayout) findViewById(R.id.yong_num_lay);
        yong_num_txt = (TextView) findViewById(R.id.yong_num_txt);

        tixian_num_lay = (LinearLayout) findViewById(R.id.tixian_num_lay);
        tixian_num_txt = (TextView) findViewById(R.id.tixian_num_txt);

        customer_txt = (TextView) findViewById(R.id.customer_txt);
        wallet_application_txt = (TextView) findViewById(R.id.wallet_application_txt);

		wallet_listview = (XListView) findViewById(R.id.wallet_listview);
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();
		
		
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_right = (TextView) findViewById(R.id.tv_head_right);
        tv_head_right.setText("账单");
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putInt("index", 0);
                startActivity(new Intent(mContext, WalletRecordListActivity.class).putExtras(data));
            }
        });
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("我的钱包");

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
		
		wallet_listview.setPullLoadEnable(false);
		wallet_listview.setPullRefreshEnable(true);
		wallet_listview.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startDataTask(false);
            }

            @Override
            public void onLoadMore() {
            }
        }, R.id.wallet_listview);

        yong_num_lay.setOnClickListener(this);
        tixian_num_lay.setOnClickListener(this);

        customer_txt.setOnClickListener(this);
        wallet_application_txt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

            case R.id.yong_num_lay:
                Bundle yong = new Bundle();
                yong.putInt("index", 1);
                startActivity(new Intent(mContext, WalletRecordListActivity.class).putExtras(yong));
                break;

            case R.id.tixian_num_lay:
                Bundle tixian = new Bundle();
                tixian.putInt("index", 2);
                startActivity(new Intent(mContext, WalletRecordListActivity.class).putExtras(tixian));
                break;

            case R.id.customer_txt:
                startActivity(new Intent(mContext, CustomerAddActivity.class));
                break;

            case R.id.wallet_application_txt:
                startActivity(new Intent(mContext, TXIndexActivity.class));
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
	
	
	private void startDataTask(boolean showLoading) {
		if(NetUtil.detectAvailable(mContext)){
			if(walletListRequest == null){
				walletListRequest = new WalletListRequest(modelApp.getUser().getUid(),
						WalletRecordListActivity.WALLET_ALL, 1, pageSize,
						new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();

						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
							Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.NO_DATA_FROM_NET:
							Toast.makeText(mContext, "您还没有进账，快去荐房赚取佣金！", Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							Bundle data = message.getData();


                            if(StringUtil.isEmpty(data.getString("sum"))){
                                wallet_money_txt.setText("0.00");
                            }else{
                                wallet_money_txt.setText(data.getString("sum"));
                            }
                            if(StringUtil.isEmpty(data.getString("totalRecom"))){
                                yong_num_txt.setText("0.00");
                            }else{
                                yong_num_txt.setText(data.getString("totalRecom"));
                            }
                            if(StringUtil.isEmpty(data.getString("totalWithdrawals"))){
                                tixian_num_txt.setText("0.00");
                            }else{
                                tixian_num_txt.setText(data.getString("totalWithdrawals"));
                            }

							ArrayList<WalletRecord> temp = (ArrayList<WalletRecord>) data.getSerializable("walletRecordList");
							
							//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
							if(recordList != null){
								recordList.clear();
							}
							recordList.addAll(temp);
							fillData();

                            if(message.arg1 == 0){
                                Toast.makeText(mContext, " 您还没有进账，快去荐房赚取佣金", Toast.LENGTH_SHORT).show();
                            }

							break;
						}
						isPullDown = false;
						wallet_listview.stopRefresh();
					}
				});
			}else {
				walletListRequest.setData(modelApp.getUser().getUid(),
						WalletRecordListActivity.WALLET_ALL, 1, pageSize);
			}
            if (showLoading){
                showLoadingDialog(R.string.data_loading);
            }
			walletListRequest.doRequest();
			
		}else{
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
			isPullDown = false;
			wallet_listview.stopRefresh();
		}
	}

}
