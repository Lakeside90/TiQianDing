package com.xkhouse.fang.user.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.activity.JJRSellReleaseManageActivity;
import com.xkhouse.fang.user.activity.SellReleaseEditActivity;
import com.xkhouse.fang.user.adapter.JJRReleaseManageAdapter;
import com.xkhouse.fang.user.entity.SellReleaseEditBean;
import com.xkhouse.fang.user.entity.XKRelease;
import com.xkhouse.fang.user.task.ReleaseDeleteRequest;
import com.xkhouse.fang.user.task.ReleaseRefreshRequest;
import com.xkhouse.fang.user.task.ReleaseShangXiaJiaRequest;
import com.xkhouse.fang.user.task.SellReleaseInfoRequest;
import com.xkhouse.fang.user.task.SellReleaseListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
* @Description: 经纪人出售房源管理--已发布
* @author wujian  
* @date 2016-04-14
 */
public class JJRSellReleaseItemView implements OnClickListener{

	private JJRSellReleaseManageActivity context;
	private View rootView;

    private TextView release_count_txt;
    private TextView release_operate_txt;

    private ListView release_listView;
    private JJRReleaseManageAdapter adapter;
    private int currentPageIndex = 1;  //分页索引
    private int pageSize = 10; //每次请求10条数据
    private ArrayList<XKRelease> sellOutList = new ArrayList<>();


    private LinearLayout page_lay;
    private TextView previous_page_txt;
    private TextView next_page_txt;

    private LinearLayout operate_lay;
    private ImageView release_cb;
    private TextView refresh_txt;
    private TextView xiajia_txt;
    private TextView delete_txt;

    private LinearLayout content_lay;

    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private LinearLayout notice_lay;

    boolean isAllSelected = false;
	private ModelApplication modelApp;

	public View getView() {
        return rootView;
    }


	public JJRSellReleaseItemView(JJRSellReleaseManageActivity context) {
		this.context = context;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		initView();
		setListener();
        if(isAllSelected){
            release_cb.setImageResource(R.drawable.checkbox_checked);
        }else{
            release_cb.setImageResource(R.drawable.checkbox_normal);
        }
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_jjr_sell_release, null);
		release_listView = (ListView) rootView.findViewById(R.id.release_listView);
        release_count_txt = (TextView) rootView.findViewById(R.id.release_count_txt);
        release_operate_txt = (TextView) rootView.findViewById(R.id.release_operate_txt);

        page_lay = (LinearLayout) rootView.findViewById(R.id.page_lay);
        previous_page_txt = (TextView) rootView.findViewById(R.id.previous_page_txt);
        next_page_txt = (TextView) rootView.findViewById(R.id.next_page_txt);

        operate_lay = (LinearLayout) rootView.findViewById(R.id.operate_lay);
        release_cb = (ImageView) rootView.findViewById(R.id.release_cb);
        refresh_txt = (TextView) rootView.findViewById(R.id.refresh_txt);
        xiajia_txt = (TextView) rootView.findViewById(R.id.xiajia_txt);
        delete_txt = (TextView) rootView.findViewById(R.id.delete_txt);

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);

        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        notice_lay = (LinearLayout) rootView.findViewById(R.id.notice_lay);
	}
	
	
	private void setListener(){
		
        error_lay.setOnClickListener(this);
        next_page_txt.setOnClickListener(this);
        previous_page_txt.setOnClickListener(this);
        release_operate_txt.setOnClickListener(this);

        refresh_txt.setOnClickListener(this);
        xiajia_txt.setOnClickListener(this);
        delete_txt.setOnClickListener(this);
        release_cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllSelected){
                    isAllSelected = false;
                }else{
                    isAllSelected = true;
                }

                for (XKRelease release : sellOutList) {
                    release.setIsSelected(isAllSelected);
                }
                if(adapter != null) adapter.notifyDataSetChanged();

                if(isAllSelected){
                    release_cb.setImageResource(R.drawable.checkbox_checked);
                }else{
                    release_cb.setImageResource(R.drawable.checkbox_normal);
                }

            }
        });
	}
	
	public void refreshView(){

        if(sellOutList == null || sellOutList.size() == 0 ){
            currentPageIndex = 1;
            next = false;
            startSellReleaseListTask(1);
        }
	}


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.release_operate_txt:
                if("管理".equals(release_operate_txt.getText())){
                    release_operate_txt.setText("取消");
                    operate_lay.setVisibility(View.VISIBLE);
                    page_lay.setVisibility(View.GONE);
                    if(adapter != null) adapter.showOperate();
                }else{
                    release_operate_txt.setText("管理");
                    operate_lay.setVisibility(View.GONE);
                    page_lay.setVisibility(View.VISIBLE);
                    if(adapter != null) adapter.closeOperate();
                }
                break;

            case R.id.previous_page_txt:
                if (currentPageIndex == 1) {
                    Toast.makeText(context, "已经是第一页", Toast.LENGTH_SHORT).show();
                }else{
                    next = false;
                    startSellReleaseListTask(currentPageIndex - 1);
                }
                break;

            case R.id.next_page_txt:
                if(sellOutList != null && sellOutList.size() < pageSize){
                    Toast.makeText(context, "已经是最后一页了", Toast.LENGTH_SHORT).show();
                }else{
                    next = true;
                    startSellReleaseListTask(currentPageIndex + 1);
                }
                break;

            case R.id.refresh_txt:
                String refreshIds = getSelectedReleaseIds();
                if (StringUtil.isEmpty(refreshIds)){
                    Toast.makeText(context, "请选择要刷新的房源", Toast.LENGTH_SHORT).show();
                }else{
                    refresh(refreshIds,ReleaseRefreshRequest.SELL_RELEASE );
                }
                break;

            case R.id.xiajia_txt:
                String xiajiaIds = getSelectedReleaseIds();
                if (StringUtil.isEmpty(xiajiaIds)){
                    Toast.makeText(context, "请选择要下架的房源", Toast.LENGTH_SHORT).show();
                }else{
                    xiajia(xiajiaIds, ReleaseRefreshRequest.SELL_RELEASE);
                }
                break;

            case R.id.delete_txt:
                String deleteIds = getSelectedReleaseIds();
                if (StringUtil.isEmpty(deleteIds)){
                    Toast.makeText(context, "请选择要删除的房源", Toast.LENGTH_SHORT).show();
                }else{
                    delete(deleteIds, ReleaseRefreshRequest.SELL_RELEASE);
                }
                break;

            case R.id.error_lay:
                refreshView();
                break;
        }
    }



	/**************************************** 网络请求 ****************************************/
    private SellReleaseListRequest sellReleaseListRequest;
    private boolean next = false;

    private void startSellReleaseListTask(int page){

        if(rotate_loading.getVisibility() == View.VISIBLE) return;

        if (NetUtil.detectAvailable(context)) {
            if(sellReleaseListRequest == null){
                sellReleaseListRequest = new SellReleaseListRequest(modelApp.getUser().getId(), modelApp.getSite().getSiteId(),"1",
                        page, pageSize, new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {

                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);

                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                                if (sellOutList == null || sellOutList.size() == 0){
                                    content_lay.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.GONE);
                                    error_lay.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);
                                content_lay.setVisibility(View.VISIBLE);
                                if(sellOutList == null || sellOutList.size() ==0){
                                    content_lay.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.VISIBLE);
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                content_lay.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);

                                release_count_txt.setText("当期" + message.getData().getString("yifabu") + "条房源"
                                        + "    刷新次数："+message.getData().getString("todat_refresh_num"));
                                ArrayList<XKRelease> temp = (ArrayList<XKRelease>) message.getData().getSerializable("sellList");
                                if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                    Toast.makeText(context, R.string.no_release, Toast.LENGTH_SHORT).show();
                                }
                                sellOutList.clear();
                                sellOutList.addAll(temp);
                                fillData();
                                if (next) {
                                    currentPageIndex++;
                                }else {
                                    if(currentPageIndex > 1) currentPageIndex--;
                                }
                                break;
                        }
                    }
                });
            }else {
                sellReleaseListRequest.setData(modelApp.getUser().getId(), modelApp.getSite().getSiteId(),
                        "1", page, pageSize);
            }

            content_lay.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            notice_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.VISIBLE);
            rotate_loading.start();

            sellReleaseListRequest.doRequest();
        }else {
            if (sellOutList == null || sellOutList.size() ==0 ){
                content_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                notice_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }

	

	
	private void fillData() {
        if(sellOutList == null) return;
        if(adapter == null){
            adapter = new JJRReleaseManageAdapter(context, sellOutList, new JJRReleaseManageAdapter.OnItemSelectListener() {
                @Override
                public void onClick(int position) {
                    if (sellOutList.get(position).isSelected()) {
                        sellOutList.get(position).setIsSelected(false);
                    } else {
                        sellOutList.get(position).setIsSelected(true);
                    }
                    adapter.notifyDataSetChanged();

                    isAllSelected = true;
                    for (XKRelease release : sellOutList) {
                        if (!release.isSelected()) {
                            isAllSelected = false;
                            break;
                        }
                    }
                    if (isAllSelected) {
                        release_cb.setImageResource(R.drawable.checkbox_checked);
                    } else {
                        release_cb.setImageResource(R.drawable.checkbox_normal);
                    }
                }
            }, new JJRReleaseManageAdapter.OnItemEditListener() {
                @Override
                public void onEdit(int position) {
                    sellReleaseInfoTask(sellOutList.get(position).getId());
                }
            });

            release_listView.setAdapter(adapter);
        }else{
            adapter.setData(sellOutList);
        }
	}


    /***********************************  房源操作 ******************************************/
    //获取选中的房源的id，逗号隔开
    private String getSelectedReleaseIds(){
        if(sellOutList == null || sellOutList.size() == 0) return null;
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<sellOutList.size(); i++){
            if (sellOutList.get(i).isSelected()){
                sb.append(sellOutList.get(i).getId());
                sb.append(",");
            }
        }
        if(!StringUtil.isEmpty(sb.toString()) &&sb.toString().contains(",")){
            return sb.substring(0, sb.length() - 1);
        }else{
            return sb.toString();
        }

    }


    //刷新
    private void refresh(String releaseId, String type){
        if (NetUtil.detectAvailable(context)) {
            ReleaseRefreshRequest request = new ReleaseRefreshRequest(releaseId, modelApp.getUser().getId(),
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
                            isAllSelected = false;
                            release_cb.setImageResource(R.drawable.checkbox_normal);
//                            refreshAfterOperate();
                            context.refreshAll();
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


    //上架
    private void xiajia(String releaseId, String type){
        if (NetUtil.detectAvailable(context)) {
            ReleaseShangXiaJiaRequest request = new ReleaseShangXiaJiaRequest(releaseId, modelApp.getUser().getId(),
                    modelApp.getSite().getSiteId(), type, ReleaseShangXiaJiaRequest.XIA_JIA, new RequestListener() {
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
                            isAllSelected = false;
                            release_cb.setImageResource(R.drawable.checkbox_normal);
//                            refreshAfterOperate();
                            context.refreshAll();
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
            ReleaseDeleteRequest request = new ReleaseDeleteRequest(releaseId, modelApp.getUser().getId(),
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
                            isAllSelected = false;
                            release_cb.setImageResource(R.drawable.checkbox_normal);
//                            refreshAfterOperate();
                            context.refreshAll();
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
            SellReleaseInfoRequest request = new SellReleaseInfoRequest( modelApp.getUser().getId(),
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




    //删除、刷新、编辑、重新发布等操作成功后刷新数据
    public void refreshAfterOperate(){
        if (sellOutList != null && adapter != null){
            sellOutList.clear();
            adapter.setData(sellOutList);
            release_operate_txt.setText("管理");
            adapter.closeOperate();
            operate_lay.setVisibility(View.GONE);
            page_lay.setVisibility(View.VISIBLE);
        }

        currentPageIndex = 1;
        next = false;
        startSellReleaseListTask(1);
    }
	

	


	
}
