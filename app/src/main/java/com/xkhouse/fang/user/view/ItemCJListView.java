package com.xkhouse.fang.user.view;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.CJFragment;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.CJInfo;
import com.xkhouse.fang.app.task.CJListRequest;
import com.xkhouse.fang.user.adapter.CJListAdapter;
import com.xkhouse.fang.user.adapter.CJyjxListAdapter;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
 * 抽奖列表
 */
public class ItemCJListView {

	private Context context;
	private View rootView;

    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

	private XListView cj_listView;
	private CJListAdapter adapter;
	private CJyjxListAdapter jyjxListAdapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private CJListRequest request;
	private ArrayList<CJInfo> cjInfoList = new ArrayList<>();

	private String status;

	private ModelApplication modelApp;


	public View getView() {
        return rootView;
    }

	public ItemCJListView(Context context, String status) {
		this.context = context;
		this.status = status;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		initView();
		setListener();

	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_cj_list, null);

		cj_listView = (XListView) rootView.findViewById(R.id.cj_listView);

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
	}
	
	public void refreshView(){
		isPullDown = true;
		startTask(1, true);
	}
	
	private void setListener() {
		cj_listView.setPullLoadEnable(true);
		cj_listView.setPullRefreshEnable(true);
		cj_listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startTask(currentPageIndex, false);
            }
        }, R.id.cj_listView);

        error_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPullDown = true;
                startTask(1, true);
            }
        });
	}
	
	
	private void startTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(context)) {
			if(request == null){
                request = new CJListRequest(status, modelApp.getSite().getSiteId(), page, pageSize,
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
                            if (cjInfoList == null || cjInfoList.size() == 0){
                                content_lay.setVisibility(View.GONE);
                                error_lay.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.NO_DATA_FROM_NET:
							if (cjInfoList == null || cjInfoList.size() == 0){
								content_lay.setVisibility(View.GONE);
								error_lay.setVisibility(View.GONE);
							}else {
								error_lay.setVisibility(View.GONE);
								content_lay.setVisibility(View.VISIBLE);
							}

                            String msg = (String) message.obj;
                            if (StringUtil.isEmpty(msg)) msg = "没有数据";
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
                            content_lay.setVisibility(View.VISIBLE);
                            error_lay.setVisibility(View.GONE);

							ArrayList<CJInfo> temp = (ArrayList<CJInfo>) message.obj;
							//根据返回的数据量判断是否隐藏加载更多
							if(temp.size() < pageSize){
								cj_listView.setPullLoadEnable(false);
							}else{
								cj_listView.setPullLoadEnable(true);
							}
							
							//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
							if(isPullDown && cjInfoList != null){
								cjInfoList.clear();
								currentPageIndex = 1;
							}
							cjInfoList.addAll(temp);
							fillData();
                            if (currentPageIndex > 1 && message.arg1 == cjInfoList.size()){
                                Toast.makeText(context, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                            }
                            currentPageIndex++;
							break;
						}
						isPullDown = false;
						cj_listView.stopRefresh();
						cj_listView.stopLoadMore();
					}
				});
			}else {
				request.setData(status, modelApp.getSite().getSiteId(), page, pageSize);
			}
			if (showLoading) {
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			request.doRequest();

		}else {
			isPullDown = false;
			cj_listView.stopRefresh();
			cj_listView.stopLoadMore();

            if (cjInfoList == null || cjInfoList.size() == 0){
                content_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}

        fillData();
	}
	
	
	private void fillData() {
		if(cjInfoList == null) return;

		if (CJFragment.CJ_YJX.equals(status)) {
			if(jyjxListAdapter == null){
				jyjxListAdapter = new CJyjxListAdapter(context, cjInfoList);
				cj_listView.setAdapter(jyjxListAdapter);
			}else {
				jyjxListAdapter.setData(cjInfoList);
			}
		} else {
			if(adapter == null){
				adapter = new CJListAdapter(context, cjInfoList, status);
				cj_listView.setAdapter(adapter);
			}else {
				adapter.setData(cjInfoList, status);
			}
		}

	}
	
	
	


    
}
