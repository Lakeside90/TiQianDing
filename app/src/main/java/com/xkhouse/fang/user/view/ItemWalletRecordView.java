package com.xkhouse.fang.user.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.adapter.WalletRecordAdapter;
import com.xkhouse.fang.user.entity.WalletRecord;
import com.xkhouse.fang.user.task.WalletListRequest;
import com.xkhouse.fang.widget.CustomDialog;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
* @Description: 交易记录
* @author wujian  
* @date 2015-10-30 下午1:49:01
 */
public class ItemWalletRecordView {

	private Context context;
	private View rootView;
	
	private XListView record_listView; 
	private WalletRecordAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private LinearLayout notice_lay;

	private WalletListRequest walletListRequest;
	private ArrayList<WalletRecord> recordList = new ArrayList<WalletRecord>();
	
	private String type;
	
	private ModelApplication modelApp;
	
	
	public View getView() {
        return rootView;
    } 
	
	public ItemWalletRecordView(Context context, String type) {
		this.context = context;
		this.type = type;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		initView();
		setListener();
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_wallet_record_list, null);
		
		record_listView = (XListView) rootView.findViewById(R.id.record_listView);

        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        notice_lay = (LinearLayout) rootView.findViewById(R.id.notice_lay);
		
	}
	
	public void refreshView(){
		isPullDown = true;
		startDataTask(1, true);
	}
	
	private void setListener() {
        error_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshView();
            }
        });

		record_listView.setPullLoadEnable(true);
		record_listView.setPullRefreshEnable(true);
		record_listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startDataTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startDataTask(currentPageIndex, false);
            }
        }, R.id.record_listView);
	}
	
	
	private void fillData() {
		if(recordList == null) return;
		
		if(adapter == null){
			adapter = new WalletRecordAdapter(context, recordList);
			record_listView.setAdapter(adapter);
		}else {
			adapter.setData(recordList);
		}
	}
	
	
	private void startDataTask(int page, boolean showLoading) {
        if(rotate_loading.getVisibility() == View.VISIBLE) return;

		if(NetUtil.detectAvailable(context)){
			if(walletListRequest == null){
				walletListRequest = new WalletListRequest(modelApp.getUser().getUid(), type, page, pageSize,
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
                                record_listView.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                error_lay.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            }

							Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.NO_DATA_FROM_NET:
                            error_lay.setVisibility(View.GONE);
                            notice_lay.setVisibility(View.GONE);
                            record_listView.setVisibility(View.VISIBLE);
                            if(recordList == null || recordList.size() ==0){
                                record_listView.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.VISIBLE);
                            }

							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
                            record_listView.setVisibility(View.VISIBLE);
                            error_lay.setVisibility(View.GONE);
                            notice_lay.setVisibility(View.GONE);

							Bundle data = message.getData();
							ArrayList<WalletRecord> temp = (ArrayList<WalletRecord>) data.getSerializable("walletRecordList");
                            if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                record_listView.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.VISIBLE);
                            }

							//根据返回的数据量判断是否隐藏加载更多
							if(temp.size() < pageSize){
								record_listView.setPullLoadEnable(false);
							}else{
								record_listView.setPullLoadEnable(true);
							}
							
							//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
							if(isPullDown && recordList != null){
								recordList.clear();
								currentPageIndex = 1;
							}
							recordList.addAll(temp);

							fillData();
                            if (currentPageIndex > 1 && message.arg1 == recordList.size()){
                                Toast.makeText(context, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                            }
                            currentPageIndex++;
							break;
						}
						isPullDown = false;
						record_listView.stopRefresh();
						record_listView.stopLoadMore();
					}
				});
			}else {
				walletListRequest.setData(modelApp.getUser().getUid(), type, page, pageSize);
			}
			if (showLoading){
                record_listView.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			walletListRequest.doRequest();
			
		}else{
			isPullDown = false;
			record_listView.stopRefresh();
			record_listView.stopLoadMore();

            if (recordList == null || recordList.size() ==0 ){
                record_listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}
	}
	
	
	
	CustomDialog dialog;

    public void showLoadingDialog(int showMessage) {
        if (dialog == null) {
            dialog = new CustomDialog(context, showMessage,
                    android.R.style.Theme_Translucent_NoTitleBar);
        }
        if (!((Activity) context).isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void showLoadingDialog(String showMessage) {
        if (dialog == null) {
            dialog = new CustomDialog(context, showMessage,
                    android.R.style.Theme_Translucent_NoTitleBar);
        }
        if (!((Activity) context).isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }
    
    /**
     * 隐藏正在提交疑问的环形进度条
     */
    public void hideLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
    
}
