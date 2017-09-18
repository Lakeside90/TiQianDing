package com.xkhouse.fang.booked.view;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.adapter.StoreImageAdapter;
import com.xkhouse.fang.booked.entity.StoreAlbum;
import com.xkhouse.fang.booked.task.StoreAlbumListRequest;
import com.xkhouse.fang.user.adapter.CJListAdapter;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.entity.XKRecommend;
import com.xkhouse.fang.user.task.MessageDetailListRequest;
import com.xkhouse.fang.user.task.XKRecommendListRequest;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 商家相册列表
 */
public class ItemStoreImageView {

	private Context context;
	private View rootView;

    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

    private GridView gridView;
	private StoreImageAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉


	private String typeId;
	private String business_id;
	private ModelApplication modelApp;

	private StoreAlbumListRequest listRequest;
	private ArrayList<StoreAlbum> albumList = new ArrayList<>();

	public View getView() {
        return rootView;
    }



	public ItemStoreImageView(Context context, String business_id, String typeId) {
		this.context = context;
		this.business_id = business_id;
		this.typeId = typeId;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		initView();
		setListener();
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_store_image_list, null);

        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
	}
	
	public void refreshView(){

		startDataTask(1, true);
	}
	
	private void setListener() {
        error_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				startDataTask(1, true);
            }
        });
	}
	
	

	
	private void fillData() {
		if (albumList == null) return;
		if (adapter == null) {
			adapter = new StoreImageAdapter(context, albumList);
			gridView.setAdapter(adapter);
		}else {
			adapter.setData(albumList);
		}
	}



	RequestListener requestListener = new RequestListener() {

		@Override
		public void sendMessage(Message message) {

			rotate_loading.stop();
			rotate_loading.setVisibility(View.GONE);

			if (isPullDown){
				currentPageIndex = 1;
			}

			switch (message.what) {
				case Constants.ERROR_DATA_FROM_NET:
					if (albumList == null || albumList.size() == 0){
						gridView.setVisibility(View.GONE);
						error_lay.setVisibility(View.VISIBLE);
					}else{
						Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
					}
					break;

				case Constants.NO_DATA_FROM_NET:
					error_lay.setVisibility(View.GONE);
					gridView.setVisibility(View.VISIBLE);
					if(albumList == null || albumList.size() ==0){
						gridView.setVisibility(View.GONE);
					}
					break;

				case Constants.SUCCESS_DATA_FROM_NET:
					gridView.setVisibility(View.VISIBLE);
					error_lay.setVisibility(View.GONE);

					ArrayList<StoreAlbum> temp = (ArrayList<StoreAlbum>) message.obj;
					//根据返回的数据量判断是否隐藏加载更多
					if(temp.size() < pageSize){
//						msg_listView.setPullLoadEnable(false);
					}else{
//						msg_listView.setPullLoadEnable(true);
					}
					//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
					if(isPullDown && albumList != null){
						albumList.clear();
						currentPageIndex = 1;
					}
					albumList.addAll(temp);

					fillData();
					if (currentPageIndex > 1 && message.arg1 == albumList.size()){
						Toast.makeText(context, R.string.data_load_end, Toast.LENGTH_SHORT).show();
					}
					currentPageIndex++;
					break;
			}
			isPullDown = false;
//			msg_listView.stopRefresh();
//			msg_listView.stopLoadMore();
		}
	};



	private void startDataTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(context)) {
			if(listRequest == null){
				listRequest = new StoreAlbumListRequest(business_id, typeId,
						 page, pageSize, requestListener);
			}else {
				listRequest.setData(business_id, typeId,
						page, pageSize);
			}
			if (showLoading){
				gridView.setVisibility(View.GONE);
				error_lay.setVisibility(View.GONE);
				rotate_loading.setVisibility(View.VISIBLE);
				rotate_loading.start();
			}
			listRequest.doRequest();
		}else {
			isPullDown = false;
//			msg_listView.stopRefresh();
//			msg_listView.stopLoadMore();
			if (albumList == null || albumList.size() == 0){
				gridView.setVisibility(View.GONE);
				rotate_loading.setVisibility(View.GONE);
				error_lay.setVisibility(View.VISIBLE);
			}else{
				Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
			}
		}
	}
	


    
}
