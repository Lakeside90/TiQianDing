package com.xkhouse.fang.user.view;

import android.app.Activity;
import android.content.Intent;
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
import com.xkhouse.fang.user.activity.ReleaseManageActivity;
import com.xkhouse.fang.user.activity.RentInEditActivity;
import com.xkhouse.fang.user.activity.RentReleaseEditActivity;
import com.xkhouse.fang.user.activity.SellInEditActivity;
import com.xkhouse.fang.user.activity.SellReleaseEditActivity;
import com.xkhouse.fang.user.adapter.ReleaseManageAdapter;
import com.xkhouse.fang.user.entity.RentInEditBean;
import com.xkhouse.fang.user.entity.RentReleaseEditBean;
import com.xkhouse.fang.user.entity.SellInEditBean;
import com.xkhouse.fang.user.entity.SellReleaseEditBean;
import com.xkhouse.fang.user.entity.XKRelease;
import com.xkhouse.fang.user.task.ReleaseAgainRequest;
import com.xkhouse.fang.user.task.ReleaseDeleteRequest;
import com.xkhouse.fang.user.task.ReleaseRefreshRequest;
import com.xkhouse.fang.user.task.RentInInfoRequest;
import com.xkhouse.fang.user.task.RentInListRequest;
import com.xkhouse.fang.user.task.RentReleaseInfoRequest;
import com.xkhouse.fang.user.task.RentReleaseListRequest;
import com.xkhouse.fang.user.task.SellInInfoRequest;
import com.xkhouse.fang.user.task.SellInListRequest;
import com.xkhouse.fang.user.task.SellReleaseInfoRequest;
import com.xkhouse.fang.user.task.SellReleaseListRequest;
import com.xkhouse.fang.widget.ConfirmDialog;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
* @Description: 我的发布--出租、求租、出售、求购
* @author wujian  
* @date 2016-04-11
 */
public class ReleaseManageItemView implements OnClickListener{

	private ReleaseManageActivity context;
	private View rootView;
	private XListView release_listView;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉

    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private LinearLayout notice_lay;

    private ArrayList<XKRelease> sellOutList = new ArrayList<>();
    private ReleaseManageAdapter sellOutAdapter;

    private ArrayList<XKRelease> sellInList = new ArrayList<>();
    private ReleaseManageAdapter sellInAdapter;

    private ArrayList<XKRelease> rentOutList = new ArrayList<>();
    private ReleaseManageAdapter rentOutAdapter;

    private ArrayList<XKRelease> rentInList = new ArrayList<>();
    private ReleaseManageAdapter rentInAdapter;


    /** 出售 **/
    public static final int RELEASE_SELL_OUT = 1;
    /** 求购 **/
    public static final int RELEASE_SELL_IN = 2;
    /** 出租 **/
    public static final int RELEASE_RENT_OUT = 3;
    /** 求租 **/
    public static final int RELEASE_RENT_IN = 4;


	private int type;

	private ModelApplication modelApp;

	public View getView() {
        return rootView;
    }


	public ReleaseManageItemView(ReleaseManageActivity context, int houseType) {
		this.context = context;
		this.type = houseType;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		initView();
		setListener();
	    
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_release_manage_list, null);
		release_listView = (XListView) rootView.findViewById(R.id.release_listView);

        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        notice_lay = (LinearLayout) rootView.findViewById(R.id.notice_lay);
	}
	
	
	private void setListener(){
		
		release_listView.setPullLoadEnable(true);
		release_listView.setPullRefreshEnable(true);
		release_listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                switch (type) {
                    case RELEASE_SELL_OUT:
                        startSellReleaseListTask(1, false);
                        break;

                    case RELEASE_SELL_IN:
                        startSellInListTask(1, false);
                        break;

                    case RELEASE_RENT_OUT:
                        startRentReleaseListTask(1, false);
                        break;

                    case RELEASE_RENT_IN:
                        startRentInListTask(1, false);
                        break;
                }
            }

            @Override
            public void onLoadMore() {
                switch (type) {
                    case RELEASE_SELL_OUT:
                        startSellReleaseListTask(currentPageIndex, false);
                        break;

                    case RELEASE_SELL_IN:
                        startSellInListTask(currentPageIndex, false);
                        break;

                    case RELEASE_RENT_OUT:
                        startRentReleaseListTask(currentPageIndex, false);
                        break;

                    case RELEASE_RENT_IN:
                        startRentInListTask(currentPageIndex, false);
                        break;
                }
            }
        }, R.id.release_listView);

        error_lay.setOnClickListener(this);
	}
	
	public void refreshView(){
		switch (type) {
		case RELEASE_SELL_OUT:
			if(sellOutList == null || sellOutList.size() == 0 ){
				startSellReleaseListTask(1, true);
			}
			break;

		case RELEASE_SELL_IN:
            if(sellInList == null || sellInList.size() == 0 ){
                startSellInListTask(1, true);
            }
			break;
			
		case RELEASE_RENT_OUT:
            if(rentOutList == null || rentOutList.size() == 0 ){
                startRentReleaseListTask(1, true);
            }
			break;
			
		case RELEASE_RENT_IN:
            if(rentInList == null || rentInList.size() == 0 ){
                startRentInListTask(1, true);
            }
			break;
		}
	}
	





	/**************************************** 出租网络请求  ****************************************/

    private RentReleaseListRequest rentReleaseListRequest;

    private void startRentReleaseListTask(int page, boolean showLoading){

        if(rotate_loading.getVisibility() == View.VISIBLE) return;

        if (NetUtil.detectAvailable(context)) {
            if(rentReleaseListRequest == null){
                rentReleaseListRequest = new RentReleaseListRequest(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),null,
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
                                if (rentOutList == null || rentOutList.size() == 0){
                                    release_listView.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.GONE);
                                    error_lay.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                release_listView.setVisibility(View.VISIBLE);
                                if(rentOutList == null || rentOutList.size() ==0){
                                    release_listView.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.VISIBLE);
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                release_listView.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                ArrayList<XKRelease> temp = (ArrayList<XKRelease>) message.getData().getSerializable("rentList");
                                if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                    Toast.makeText(context, R.string.no_release, Toast.LENGTH_SHORT).show();
                                }
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    release_listView.setPullLoadEnable(false);
                                }else{
                                    release_listView.setPullLoadEnable(true);
                                }
                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && rentOutList != null){
                                    rentOutList.clear();
                                    currentPageIndex = 1;
                                }
                                rentOutList.addAll(temp);
                                fillRentOutData();
                                currentPageIndex++;
                                break;
                        }
                        isPullDown = false;
                        release_listView.stopRefresh();
                        release_listView.stopLoadMore();
                    }
                });
            }else {
                rentReleaseListRequest.setData(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),
                        null, page, pageSize);
            }
            if (showLoading) {
                release_listView.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            rentReleaseListRequest.doRequest();
        }else {
            isPullDown = false;
            release_listView.stopRefresh();
            release_listView.stopLoadMore();

            if (rentOutList == null || rentOutList.size() ==0 ){
                release_listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fillRentOutData() {
        if(rentOutList == null) return;
        if(rentOutAdapter == null){
            rentOutAdapter = new ReleaseManageAdapter(context, rentOutList,
                    new ReleaseManageAdapter.ReleaseManageListener() {
                @Override
                public void onRefresh(int position) {
                    refresh(rentOutList.get(position).getId(), ReleaseRefreshRequest.RENT_RELEASE);
                }

                @Override
                public void onRelease(int position) {
                    release(rentOutList.get(position).getId(), ReleaseAgainRequest.SELL_IN);
                }

                @Override
                public void onEdit(int position) {
                    rentReleaseInfoTask(rentOutList.get(position).getId());
                }

                @Override
                public void onDelete(int position) {
                    showChangeSiteDialog(rentOutList.get(position).getId(),
                            ReleaseDeleteRequest.RENT_RELEASE);
                }
            });
            release_listView.setAdapter(rentOutAdapter);
        }else{
            rentOutAdapter.setData(rentOutList);
        }
    }


    /**************************************** 求租网络请求  ****************************************/

    private RentInListRequest rentInListRequest;

    private void startRentInListTask(int page, boolean showLoading){

        if(rotate_loading.getVisibility() == View.VISIBLE) return;

        if (NetUtil.detectAvailable(context)) {
            if(rentInListRequest == null){
                rentInListRequest = new RentInListRequest(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),null,
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
                                if (rentInList == null || rentInList.size() == 0){
                                    release_listView.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.GONE);
                                    error_lay.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                release_listView.setVisibility(View.VISIBLE);
                                if(rentInList == null || rentInList.size() ==0){
                                    release_listView.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.VISIBLE);
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                release_listView.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                ArrayList<XKRelease> temp = (ArrayList<XKRelease>) message.getData().getSerializable("rentList");
                                if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                    Toast.makeText(context, R.string.no_favorite, Toast.LENGTH_SHORT).show();
                                }
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    release_listView.setPullLoadEnable(false);
                                }else{
                                    release_listView.setPullLoadEnable(true);
                                }
                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && rentInList != null){
                                    rentInList.clear();
                                    currentPageIndex = 1;
                                }
                                rentInList.addAll(temp);
                                fillRentInData();
                                currentPageIndex++;
                                break;
                        }
                        isPullDown = false;
                        release_listView.stopRefresh();
                        release_listView.stopLoadMore();
                    }
                });
            }else {
                rentInListRequest.setData(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),
                        null, page, pageSize);
            }
            if (showLoading) {
                release_listView.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            rentInListRequest.doRequest();
        }else {
            isPullDown = false;
            release_listView.stopRefresh();
            release_listView.stopLoadMore();

            if (rentInList == null || rentInList.size() ==0 ){
                release_listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fillRentInData() {
        if(rentInList == null) return;
        if(rentInAdapter == null){
            rentInAdapter = new ReleaseManageAdapter(context, rentInList,
                    new ReleaseManageAdapter.ReleaseManageListener() {
                        @Override
                        public void onRefresh(int position) {
                            refresh(rentInList.get(position).getId(), ReleaseRefreshRequest.RENT_IN);
                        }

                        @Override
                        public void onRelease(int position) {
                            release(rentInList.get(position).getId(), ReleaseAgainRequest.SELL_IN);
                        }

                        @Override
                        public void onEdit(int position) {
                            rentInInfoTask(rentInList.get(position).getId());
                        }

                        @Override
                        public void onDelete(int position) {
                            showChangeSiteDialog(rentInList.get(position).getId(),
                                    ReleaseDeleteRequest.RENT_IN);
                        }
                    });
            release_listView.setAdapter(rentInAdapter);
        }else{
            rentInAdapter.setData(rentInList);
        }
    }



    /**************************************** 出售网络请求  ****************************************/

    private SellReleaseListRequest sellReleaseListRequest;

    private void startSellReleaseListTask(int page, boolean showLoading){

        if(rotate_loading.getVisibility() == View.VISIBLE) return;

        if (NetUtil.detectAvailable(context)) {
            if(sellReleaseListRequest == null){
                sellReleaseListRequest = new SellReleaseListRequest(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),null,
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
                                if (sellOutList == null || sellOutList.size() == 0){
                                    release_listView.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.GONE);
                                    error_lay.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                release_listView.setVisibility(View.VISIBLE);
                                if(sellOutList == null || sellOutList.size() ==0){
                                    release_listView.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.VISIBLE);
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                release_listView.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                ArrayList<XKRelease> temp = (ArrayList<XKRelease>) message.getData().getSerializable("sellList");
                                if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                    Toast.makeText(context, R.string.no_favorite, Toast.LENGTH_SHORT).show();
                                }
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    release_listView.setPullLoadEnable(false);
                                }else{
                                    release_listView.setPullLoadEnable(true);
                                }
                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && sellOutList != null){
                                    sellOutList.clear();
                                    currentPageIndex = 1;
                                }
                                sellOutList.addAll(temp);
                                fillSellOutData();
                                currentPageIndex++;
                                break;
                        }
                        isPullDown = false;
                        release_listView.stopRefresh();
                        release_listView.stopLoadMore();
                    }
                });
            }else {
                sellReleaseListRequest.setData(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),
                        null, page, pageSize);
            }
            if (showLoading) {
                release_listView.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            sellReleaseListRequest.doRequest();
        }else {
            isPullDown = false;
            release_listView.stopRefresh();
            release_listView.stopLoadMore();

            if (sellOutList == null || sellOutList.size() ==0 ){
                release_listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }

	private void fillSellOutData() {
        if(sellOutList == null) return;
        if(sellOutAdapter == null){
            sellOutAdapter = new ReleaseManageAdapter(context, sellOutList,
                    new ReleaseManageAdapter.ReleaseManageListener() {
                        @Override
                        public void onRefresh(int position) {
                            refresh(sellOutList.get(position).getId(), ReleaseRefreshRequest.SELL_RELEASE);
                        }

                        @Override
                        public void onRelease(int position) {
                            release(sellOutList.get(position).getId(), ReleaseAgainRequest.SELL_IN);
                        }

                        @Override
                        public void onEdit(int position) {
                            sellReleaseInfoTask(sellOutList.get(position).getId());
                        }

                        @Override
                        public void onDelete(int position) {
                            showChangeSiteDialog(sellOutList.get(position).getId(),
                                    ReleaseDeleteRequest.SELL_RELEASE);
                        }
                    });
            release_listView.setAdapter(sellOutAdapter);
        }else{
            sellOutAdapter.setData(sellOutList);
        }
	}



    /**************************************** 求购网络请求  ****************************************/

    private SellInListRequest sellInListRequest;

    private void startSellInListTask(int page, boolean showLoading){

        if(rotate_loading.getVisibility() == View.VISIBLE) return;

        if (NetUtil.detectAvailable(context)) {
            if(sellInListRequest == null){
                sellInListRequest = new SellInListRequest(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),
                        null, page, pageSize, new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {

                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);
                        if (isPullDown){
                            currentPageIndex = 1;
                        }

                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                                if (sellInList == null || sellInList.size() == 0){
                                    release_listView.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.GONE);
                                    error_lay.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                release_listView.setVisibility(View.VISIBLE);
                                if(sellInList == null || sellInList.size() ==0){
                                    release_listView.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.VISIBLE);
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                release_listView.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                ArrayList<XKRelease> temp = (ArrayList<XKRelease>) message.getData().getSerializable("sellList");
                                if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                    Toast.makeText(context, R.string.no_favorite, Toast.LENGTH_SHORT).show();
                                }
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    release_listView.setPullLoadEnable(false);
                                }else{
                                    release_listView.setPullLoadEnable(true);
                                }
                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && sellInList != null){
                                    sellInList.clear();
                                    currentPageIndex = 1;
                                }
                                sellInList.addAll(temp);
                                fillSellInData();
                                currentPageIndex++;
                                break;
                        }
                        isPullDown = false;
                        release_listView.stopRefresh();
                        release_listView.stopLoadMore();
                    }
                });
            }else {
                sellInListRequest.setData(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),
                        null, page, pageSize);
            }
            if (showLoading) {
                release_listView.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            sellInListRequest.doRequest();
        }else {
            isPullDown = false;
            release_listView.stopRefresh();
            release_listView.stopLoadMore();

            if (sellInList == null || sellInList.size() ==0 ){
                release_listView.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }

	private void fillSellInData() {
        if(sellInList == null) return;
        if(sellInAdapter == null){
            sellInAdapter = new ReleaseManageAdapter(context, sellInList,
                    new ReleaseManageAdapter.ReleaseManageListener() {
                        @Override
                        public void onRefresh(int position) {
                            refresh(sellInList.get(position).getId(), ReleaseRefreshRequest.SELL_IN);
                        }

                        @Override
                        public void onRelease(int position) {
                            release(sellInList.get(position).getId(), ReleaseAgainRequest.SELL_IN);
                        }

                        @Override
                        public void onEdit(int position) {
                            sellInInfoTask(sellInList.get(position).getId());
                        }

                        @Override
                        public void onDelete(int position) {
                            showChangeSiteDialog(sellInList.get(position).getId(),
                                    ReleaseDeleteRequest.SELL_IN);
                        }
                    });
            release_listView.setAdapter(sellInAdapter);
        }else{
            sellInAdapter.setData(sellInList);
        }
	}


    /***********************************      房源操作   ***********************************************/

    //刷新
    private void refresh(String releaseId, String type){
        if (NetUtil.detectAvailable(context)) {
            ReleaseRefreshRequest request = new ReleaseRefreshRequest(releaseId, modelApp.getUser().getUid(),
                    modelApp.getSite().getSiteId(), type, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    context.hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if(!StringUtil.isEmpty(msg)){
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                            }

                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                            refreshAfterOperate();
                            break;
                    }
                }
            });
            context.showLoadingDialog("操作处理中");
            request.doRequest();
        }else{
            Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    //重新发布
    private void release(String releaseId, String type){
        if (NetUtil.detectAvailable(context)) {
            ReleaseAgainRequest request = new ReleaseAgainRequest(releaseId, modelApp.getUser().getUid(),
                    modelApp.getSite().getSiteId(), type, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    context.hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if(!StringUtil.isEmpty(msg)){
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                            }

                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                            refreshAfterOperate();
                            break;
                    }
                }
            });
            context.showLoadingDialog("操作处理中");
            request.doRequest();
        }else{
            Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    //删除
    private void delete(String releaseId, String type){
        if (NetUtil.detectAvailable(context)) {
            ReleaseDeleteRequest request = new ReleaseDeleteRequest(releaseId, modelApp.getUser().getUid(),
                    modelApp.getSite().getSiteId(), type, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    context.hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if(!StringUtil.isEmpty(msg)){
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                            }

                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                            refreshAfterOperate();
                            break;
                    }
                }
            });
            context.showLoadingDialog("操作处理中");
            request.doRequest();
        }else{
            Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    //切换站点对话框
    public void showChangeSiteDialog(final String releaseId, final String type) {
        final ConfirmDialog confirmDialog = new ConfirmDialog(context, "是否删除？", "确定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                delete(releaseId,type);
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }


	//编辑求购
    private void sellInInfoTask(String releaseId){
        if (NetUtil.detectAvailable(context)) {
            SellInInfoRequest request = new SellInInfoRequest( modelApp.getUser().getUid(),
                    modelApp.getSite().getSiteId(), releaseId, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    context.hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if(!StringUtil.isEmpty(msg)){
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "获取房源信息失败", Toast.LENGTH_SHORT).show();
                            }

                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            SellInEditBean sellInEditBean = (SellInEditBean) message.obj;
                            Intent intent = new Intent(context, SellInEditActivity.class);
                            Bundle data = new Bundle();
                            data.putSerializable("sellInEditBean", (Serializable) sellInEditBean);
                            intent.putExtras(data);
                            context.startActivity(intent);
                            break;
                    }
                }
            });
            context.showLoadingDialog("操作处理中");
            request.doRequest();
        }else{
            Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    //编辑求租
    private void rentInInfoTask(String releaseId){
        if (NetUtil.detectAvailable(context)) {
            RentInInfoRequest request = new RentInInfoRequest( modelApp.getUser().getUid(),
                    modelApp.getSite().getSiteId(), releaseId, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    context.hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if(!StringUtil.isEmpty(msg)){
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "获取房源信息失败", Toast.LENGTH_SHORT).show();
                            }

                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            RentInEditBean rentInEditBean = (RentInEditBean) message.obj;
                            Intent intent = new Intent(context, RentInEditActivity.class);
                            Bundle data = new Bundle();
                            data.putSerializable("rentInEditBean", (Serializable) rentInEditBean);
                            intent.putExtras(data);
                            context.startActivity(intent);
                            break;
                    }
                }
            });
            context.showLoadingDialog("操作处理中");
            request.doRequest();
        }else{
            Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    //编辑出租
    private void rentReleaseInfoTask(String releaseId){
        if (NetUtil.detectAvailable(context)) {
            RentReleaseInfoRequest request = new RentReleaseInfoRequest( modelApp.getUser().getUid(),
                    modelApp.getSite().getSiteId(), releaseId, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    context.hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if(!StringUtil.isEmpty(msg)){
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "获取房源信息失败", Toast.LENGTH_SHORT).show();
                            }

                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            RentReleaseEditBean rentReleaseEditBean = (RentReleaseEditBean) message.obj;
                            Intent intent = new Intent(context, RentReleaseEditActivity.class);
                            Bundle data = new Bundle();
                            data.putSerializable("rentReleaseEditBean", (Serializable) rentReleaseEditBean);
                            intent.putExtras(data);
                            context.startActivity(intent);
                            break;
                    }
                }
            });
            context.showLoadingDialog("操作处理中");
            request.doRequest();
        }else{
            Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    //编辑出售
    private void sellReleaseInfoTask(String releaseId){
     if (NetUtil.detectAvailable(context)) {
            SellReleaseInfoRequest request = new SellReleaseInfoRequest( modelApp.getUser().getUid(),
                    modelApp.getSite().getSiteId(), releaseId, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    context.hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if(!StringUtil.isEmpty(msg)){
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "获取房源信息失败", Toast.LENGTH_SHORT).show();
                            }

                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            SellReleaseEditBean sellReleaseEditBean = (SellReleaseEditBean) message.obj;
                            Intent intent = new Intent(context, SellReleaseEditActivity.class);
                            Bundle data = new Bundle();
                            data.putSerializable("sellReleaseEditBean", (Serializable) sellReleaseEditBean);
                            intent.putExtras(data);
                            context.startActivity(intent);
                            break;
                    }
                }
            });
            context.showLoadingDialog("操作处理中");
            request.doRequest();
        }else{
            Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }





	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

            case R.id.error_lay:
                refreshView();
                break;
		}
	}


    //删除、刷新、编辑、重新发布等操作成功后刷新数据
    public void refreshAfterOperate(){
        switch (type) {
            case RELEASE_SELL_OUT:
                if (sellOutList != null && sellOutAdapter!= null){
                    sellOutList.clear();
                    sellOutAdapter.setData(sellOutList);
                }
                startSellReleaseListTask(1, true);
                break;

            case RELEASE_SELL_IN:
                if (sellInList != null && sellInAdapter!= null){
                    sellInList.clear();
                    sellInAdapter.setData(sellInList);
                }
                startSellInListTask(1, true);
                break;

            case RELEASE_RENT_OUT:
                if (rentOutList != null && rentOutAdapter!= null){
                    rentOutList.clear();
                    rentOutAdapter.setData(rentOutList);
                }
                startRentReleaseListTask(1, true);
                break;

            case RELEASE_RENT_IN:
                if (rentInList != null && rentInAdapter!= null){
                    rentInList.clear();
                    rentInAdapter.setData(rentInList);
                }
                startRentInListTask(1, true);
                break;
        }
    }


	
}
