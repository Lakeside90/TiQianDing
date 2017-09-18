package com.xkhouse.fang.user.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.adapter.FavNewHouseAdapter;
import com.xkhouse.fang.user.adapter.FavNewsAdapter;
import com.xkhouse.fang.user.adapter.FavOldHouseAdapter;
import com.xkhouse.fang.user.adapter.FavRentHouseAdapter;
import com.xkhouse.fang.user.entity.FavHouse;
import com.xkhouse.fang.user.entity.FavNews;
import com.xkhouse.fang.user.task.FavoriteEditRequest;
import com.xkhouse.fang.user.task.FavoriteListRequest;
import com.xkhouse.fang.widget.CustomDialog;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
* @Description: 我的收藏--新房、二手房、租房、资讯 
* @author wujian  
* @date 2015-11-3 上午9:04:47
 */
public class FavoriteListItemView implements OnClickListener{
	
	private Context context;
	private View rootView;
	private XListView favorite_listView;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private LinearLayout notice_lay;

	private FavoriteEditDialog favoriteEditDialog;
	
	private FavNewHouseAdapter newHouseAdapter;
	private FavOldHouseAdapter oldHouseAdapter;
	private FavRentHouseAdapter rentHouseAdapter;
	private FavNewsAdapter newsAdapter;
	
	private FavoriteListRequest listRequest;
	private ArrayList<FavHouse> newHouseList = new ArrayList<FavHouse>();
	private ArrayList<FavHouse> oldHouseList = new ArrayList<FavHouse>();
	private ArrayList<FavHouse> rentHouseList = new ArrayList<FavHouse>();
	private ArrayList<FavNews> newsList = new ArrayList<FavNews>();
	
	/** 新房 **/
	public static final int FAV_TYPE_NEW = 1;
	/** 二手房 **/
	public static final int FAV_TYPE_OLD = 2;
	/** 租房 **/
	public static final int FAV_TYPE_RENT = 3;
	/** 资讯 **/
	public static final int FAV_TYPE_NEWS = 4;
	
	private int type = FAV_TYPE_NEW;
	
	//当前长按选中的列表索引
	private int currentPosition = -1;
	
	private ModelApplication modelApp;
	
	public View getView() {
        return rootView;
    } 

	
	public FavoriteListItemView(Context context, int houseType) {
		this.context = context;
		this.type = houseType;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		initView();
		setListener();
	    
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_favorite_list, null);
		favorite_listView = (XListView) rootView.findViewById(R.id.favorite_listView);

        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        notice_lay = (LinearLayout) rootView.findViewById(R.id.notice_lay);
	}
	
	
	private void setListener(){
		
		favorite_listView.setPullLoadEnable(true);
		favorite_listView.setPullRefreshEnable(true);
		favorite_listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startFavoriteListTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startFavoriteListTask(currentPageIndex, false);
            }
        }, R.id.favorite_listView);

        error_lay.setOnClickListener(this);
	}
	
	public void refreshView(){
		switch (type) {
		case FAV_TYPE_NEW:
			if(newHouseList == null || newHouseList.size() == 0 ){
				startFavoriteListTask(1,true);
			}
			break;

		case FAV_TYPE_OLD:
			if(oldHouseList == null || oldHouseList.size() == 0){
				startFavoriteListTask(1, true);
			}
			break;
			
		case FAV_TYPE_RENT:
			if(rentHouseList == null || rentHouseList.size() == 0){
				startFavoriteListTask(1, true);
			}
			break;
			
		case FAV_TYPE_NEWS:
			if(newsList == null || newsList.size() == 0){
				startFavoriteListTask(1, true);
			}
			break;
		}
	}
	
	
	/**************************************** 网络请求 ****************************************/
	
	private void startFavoriteListTask(int page, boolean showLoading){

        if(rotate_loading.getVisibility() == View.VISIBLE) return;

		if (NetUtil.detectAvailable(context)) {
			if(listRequest == null){
				listRequest = new FavoriteListRequest(modelApp.getUser().getId(), modelApp.getSite().getSiteId(),
						type, page, pageSize, new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {

                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);
                        if (isPullDown){
                            currentPageIndex = 1;
                        }

						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
                            if (newHouseList == null || newHouseList.size() == 0){
                                favorite_listView.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                error_lay.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.NO_DATA_FROM_NET:
                            error_lay.setVisibility(View.GONE);
                            notice_lay.setVisibility(View.GONE);
                            favorite_listView.setVisibility(View.VISIBLE);
                            switch (type) {
                                case FAV_TYPE_NEW:
                                    if(newHouseList == null || newHouseList.size() ==0){
                                        favorite_listView.setVisibility(View.GONE);
                                        notice_lay.setVisibility(View.VISIBLE);
                                    }
                                    break;

                                case FAV_TYPE_OLD:
                                    if(oldHouseList == null || oldHouseList.size() ==0){
                                        favorite_listView.setVisibility(View.GONE);
                                        notice_lay.setVisibility(View.VISIBLE);
                                    }
                                    break;

                                case FAV_TYPE_RENT:
                                    if(rentHouseList == null || rentHouseList.size() ==0){
                                        favorite_listView.setVisibility(View.GONE);
                                        notice_lay.setVisibility(View.VISIBLE);
                                    }
                                    break;

                                case FAV_TYPE_NEWS:
                                    if(newsList == null || newsList.size() ==0){
                                        favorite_listView.setVisibility(View.GONE);
                                        notice_lay.setVisibility(View.VISIBLE);
                                    }
                                    break;
                            }

							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
                            favorite_listView.setVisibility(View.VISIBLE);
                            error_lay.setVisibility(View.GONE);
                            notice_lay.setVisibility(View.GONE);
							dispatchMessage(message);
							break;
						}
						isPullDown = false;
						favorite_listView.stopRefresh();
						favorite_listView.stopLoadMore();
					}
				});
			}else {
				listRequest.setData(modelApp.getUser().getId(), modelApp.getSite().getSiteId(),
						type, page, pageSize);
			}
			if (showLoading) {
                favorite_listView.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			listRequest.doRequest();
		}else {
			isPullDown = false;
			favorite_listView.stopRefresh();
			favorite_listView.stopLoadMore();

            if (newHouseList == null || newHouseList.size() ==0 ){
                favorite_listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}
	}
	
	
	
	private void dispatchMessage(Message message){
		Bundle data = message.getData();
		
		switch (type) {
		case FAV_TYPE_NEW:
			ArrayList<FavHouse> temp1 = (ArrayList<FavHouse>) data.getSerializable("newHouseList");
            if(currentPageIndex == 1 && (temp1 == null || temp1.size() ==0)){
                favorite_listView.setVisibility(View.GONE);
                notice_lay.setVisibility(View.VISIBLE);
                return;
            }
            //根据返回的数据量判断是否隐藏加载更多
			if(temp1.size() < pageSize){
				favorite_listView.setPullLoadEnable(false);
			}else{
				favorite_listView.setPullLoadEnable(true);
			}
			//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
			if(isPullDown && newHouseList != null){
				newHouseList.clear();
				currentPageIndex = 1;
			}
            if (currentPageIndex > 1 && message.arg1 == newHouseList.size()){
                Toast.makeText(context, R.string.data_load_end, Toast.LENGTH_SHORT).show();
            }
			newHouseList.addAll(temp1);
			fillNewHouseData();
            currentPageIndex++;
			break;

		case FAV_TYPE_OLD:
			ArrayList<FavHouse> temp2 = (ArrayList<FavHouse>) data.getSerializable("oldHouseList");
            if(currentPageIndex == 1 && (temp2 == null || temp2.size() ==0)){
                favorite_listView.setVisibility(View.GONE);
                notice_lay.setVisibility(View.VISIBLE);
                return;
            }
			//根据返回的数据量判断是否隐藏加载更多
			if(temp2.size() < pageSize){
				favorite_listView.setPullLoadEnable(false);
			}else{
				favorite_listView.setPullLoadEnable(true);
			}
			//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
			if(isPullDown && oldHouseList != null){
				oldHouseList.clear();
				currentPageIndex = 1;
			}
			oldHouseList.addAll(temp2);
			fillOldHouseData();
            if (currentPageIndex > 1 && message.arg1 == oldHouseList.size()){
                Toast.makeText(context, R.string.data_load_end, Toast.LENGTH_SHORT).show();
            }
            currentPageIndex++;
			break;
			
		case FAV_TYPE_RENT:
			ArrayList<FavHouse> temp3 = (ArrayList<FavHouse>) data.getSerializable("rentHouseList");
            if(currentPageIndex == 1 && (temp3 == null || temp3.size() ==0)){
                favorite_listView.setVisibility(View.GONE);
                notice_lay.setVisibility(View.VISIBLE);
                return;
            }
			//根据返回的数据量判断是否隐藏加载更多
			if(temp3.size() < pageSize){
				favorite_listView.setPullLoadEnable(false);
			}else{
				favorite_listView.setPullLoadEnable(true);
			}
			//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
			if(isPullDown && rentHouseList != null){
				rentHouseList.clear();
				currentPageIndex = 1;
			}
			rentHouseList.addAll(temp3);
			fillRentHouseData();
            if (currentPageIndex > 1 && message.arg1 == rentHouseList.size()){
                Toast.makeText(context, R.string.data_load_end, Toast.LENGTH_SHORT).show();
            }
            currentPageIndex++;
			break;
			
		case FAV_TYPE_NEWS:
			ArrayList<FavNews> temp4 = (ArrayList<FavNews>) data.getSerializable("newsList");
            if(currentPageIndex == 1 && (temp4 == null || temp4.size() ==0)){
                favorite_listView.setVisibility(View.GONE);
                notice_lay.setVisibility(View.VISIBLE);
                return;
            }
			//根据返回的数据量判断是否隐藏加载更多
			if(temp4.size() < pageSize){
				favorite_listView.setPullLoadEnable(false);
			}else{
				favorite_listView.setPullLoadEnable(true);
			}
			//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
			if(isPullDown && newsList != null){
				newsList.clear();
				currentPageIndex = 1;
			}
			newsList.addAll(temp4);
			fillNewsData();
            if (currentPageIndex > 1 && message.arg1 == newsList.size()){
                Toast.makeText(context, R.string.data_load_end, Toast.LENGTH_SHORT).show();
            }
            currentPageIndex++;
			break;
		}
	}
	
	
	private ItemLongClickListener itemLongClickListener =  new ItemLongClickListener() {
		
		@Override
		public void onLongClick(int position) {
			currentPosition = position;
			showFavoriteEditDialog();
		}
	};
	
	private void showFavoriteEditDialog(){
		if(favoriteEditDialog == null){
			favoriteEditDialog = new FavoriteEditDialog(context, R.style.takePhotoDialog);
		}
		
		favoriteEditDialog.show();
		favoriteEditDialog.delete_txt.setOnClickListener(this);
		favoriteEditDialog.clear_txt.setOnClickListener(this);
		favoriteEditDialog.cancel.setOnClickListener(this);
	}
	
	private void hideFavoriteEditDialog() {
		if (favoriteEditDialog != null && favoriteEditDialog.isShowing()) {
			favoriteEditDialog.dismiss();
		}
	}
	
	private void fillNewHouseData() {
		if(newHouseList == null) return;
		if(newHouseAdapter == null){
			newHouseAdapter = new FavNewHouseAdapter(context, newHouseList, itemLongClickListener);
			favorite_listView.setAdapter(newHouseAdapter);
		}else {
			newHouseAdapter.setData(newHouseList);
		}
	}
	

	private void fillOldHouseData() {
		if(oldHouseList == null) return;
		if(oldHouseAdapter == null){
			oldHouseAdapter = new FavOldHouseAdapter(context, oldHouseList, itemLongClickListener);
			favorite_listView.setAdapter(oldHouseAdapter);
		}else {
			oldHouseAdapter.setData(oldHouseList);
		}
	}
	
	
	
	private void fillRentHouseData() {
		if(rentHouseList == null) return;
		if(rentHouseAdapter == null){
			rentHouseAdapter = new FavRentHouseAdapter(context, rentHouseList, itemLongClickListener);
			favorite_listView.setAdapter(rentHouseAdapter);
		}else {
			rentHouseAdapter.setData(rentHouseList);
		}
	}
	
	
	private void fillNewsData() {
		if(newsList == null) return;
		if(newsAdapter == null){
			newsAdapter = new FavNewsAdapter(context, newsList, itemLongClickListener);
			favorite_listView.setAdapter(newsAdapter);
		}else {
			newsAdapter.setData(newsList);
		}
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.delete_txt:
                startEditTask(1);
                break;

            case R.id.clear_txt:
                startEditTask(2);
                break;

            case R.id.cancel:
                hideFavoriteEditDialog();
                break;

            case R.id.error_lay:
                refreshView();
                break;
		}
	}
	
	
	private void startEditTask(int all){
		
		if (NetUtil.detectAvailable(context)) {
			String mId = "-1";
			switch (type) {
			case FAV_TYPE_NEW:
				mId = newHouseList.get(currentPosition).getProjectId();
				break;

			case FAV_TYPE_OLD:
				mId = oldHouseList.get(currentPosition).getProjectId();
				break;
				
			case FAV_TYPE_RENT:
				mId = rentHouseList.get(currentPosition).getProjectId();
				break;
				
			case FAV_TYPE_NEWS:
				mId = newsList.get(currentPosition).getNewsId();
				break;
			}
			
			FavoriteEditRequest request = new FavoriteEditRequest(modelApp.getUser().getId(),
					mId, type, all, modelApp.getSite().getSiteId(), 
					new RequestListener() {

				@Override
				public void sendMessage(Message message) {

					hideLoadingDialog();
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT)
								.show();
						break;

					case Constants.NO_DATA_FROM_NET:
						Toast.makeText(context, message.obj.toString(), Toast.LENGTH_SHORT).show();
						break;

					case Constants.SUCCESS_DATA_FROM_NET:
						Toast.makeText(context, message.obj.toString(), Toast.LENGTH_SHORT).show();
						isPullDown = true;
						startFavoriteListTask(1, true);
						
						break;
					}
					currentPosition = -1;
				}
			});
			
			request.doRequest();
		}else{
			Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
		hideFavoriteEditDialog();
	}
	
	

	public interface ItemLongClickListener {
		
		void onLongClick(int position);
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
