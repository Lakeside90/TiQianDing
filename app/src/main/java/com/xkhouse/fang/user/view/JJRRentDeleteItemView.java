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
import com.xkhouse.fang.user.activity.JJRRentReleaseManageActivity;
import com.xkhouse.fang.user.activity.RentReleaseEditActivity;
import com.xkhouse.fang.user.adapter.JJRReleaseManageAdapter;
import com.xkhouse.fang.user.entity.RentReleaseEditBean;
import com.xkhouse.fang.user.entity.XKRelease;
import com.xkhouse.fang.user.task.ReleaseAgainRequest;
import com.xkhouse.fang.user.task.ReleaseClearRequest;
import com.xkhouse.fang.user.task.RentReleaseInfoRequest;
import com.xkhouse.fang.user.task.RentReleaseListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
* @Description: 经纪人出租房源管理--已删除
* @author wujian  
* @date 2016-04-15
 */
public class JJRRentDeleteItemView implements OnClickListener{

	private JJRRentReleaseManageActivity context;
	private View rootView;

    private TextView release_count_txt;
    private TextView release_operate_txt;

    private ListView release_listView;
    private JJRReleaseManageAdapter adapter;
    private int currentPageIndex = 1;  //分页索引
    private int pageSize = 10; //每次请求10条数据
    private ArrayList<XKRelease> rentOutList = new ArrayList<>();


    private LinearLayout page_lay;
    private TextView previous_page_txt;
    private TextView next_page_txt;

    private LinearLayout operate_lay;
    private ImageView release_cb;
    private TextView release_again_txt;
    private TextView clear_txt;

    private LinearLayout content_lay;


    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private LinearLayout notice_lay;

    boolean isAllSelected = false;
	private ModelApplication modelApp;

	public View getView() {
        return rootView;
    }


	public JJRRentDeleteItemView(JJRRentReleaseManageActivity context) {
		this.context = context;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		initView();
		setListener();
	    
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_jjr_sell_delete, null);
		release_listView = (ListView) rootView.findViewById(R.id.release_listView);
        release_count_txt = (TextView) rootView.findViewById(R.id.release_count_txt);
        release_operate_txt = (TextView) rootView.findViewById(R.id.release_operate_txt);

        page_lay = (LinearLayout) rootView.findViewById(R.id.page_lay);
        previous_page_txt = (TextView) rootView.findViewById(R.id.previous_page_txt);
        next_page_txt = (TextView) rootView.findViewById(R.id.next_page_txt);

        operate_lay = (LinearLayout) rootView.findViewById(R.id.operate_lay);
        release_cb = (ImageView) rootView.findViewById(R.id.release_cb);
        release_again_txt = (TextView) rootView.findViewById(R.id.release_again_txt);
        clear_txt = (TextView) rootView.findViewById(R.id.clear_txt);

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);

        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        notice_lay = (LinearLayout) rootView.findViewById(R.id.notice_lay);
	}
	
	
	private void setListener(){
        release_operate_txt.setOnClickListener(this);
        error_lay.setOnClickListener(this);
        next_page_txt.setOnClickListener(this);
        previous_page_txt.setOnClickListener(this);

        release_again_txt.setOnClickListener(this);
        clear_txt.setOnClickListener(this);
        release_cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllSelected){
                    isAllSelected = false;
                }else{
                    isAllSelected = true;
                }

                for (XKRelease release : rentOutList) {
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
        if(rentOutList == null || rentOutList.size() == 0 ){
            currentPageIndex = 1;
            next = false;
            startRentReleaseListTask(1);
        }
        fillData();
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
                    startRentReleaseListTask(currentPageIndex - 1);
                }
                break;

            case R.id.next_page_txt:
                if(rentOutList != null && rentOutList.size() < pageSize){
                    Toast.makeText(context, "已经是最后一页了", Toast.LENGTH_SHORT).show();
                }else{
                    next = true;
                    startRentReleaseListTask(currentPageIndex + 1);
                }
                break;

            case R.id.release_again_txt:
                String releaseIds = getSelectedReleaseIds();
                if (StringUtil.isEmpty(releaseIds)){
                    Toast.makeText(context, "请选择要重新发布的房源", Toast.LENGTH_SHORT).show();
                }else{
                    release(releaseIds, ReleaseAgainRequest.RENT_RELEASE);
                }
                break;

            case R.id.clear_txt:
                String clearIds = getSelectedReleaseIds();
                if (StringUtil.isEmpty(clearIds)){
                    Toast.makeText(context, "请选择要彻底删除的房源", Toast.LENGTH_SHORT).show();
                }else{
                    clear(clearIds, ReleaseClearRequest.RENT_RELEASE);
                }
                break;

            case R.id.error_lay:
                refreshView();
                break;
        }
    }




    /**************************************** 网络请求 ****************************************/
    private RentReleaseListRequest rentReleaseListRequest;
    private boolean next = false;

    private void startRentReleaseListTask(int page){

        if(rotate_loading.getVisibility() == View.VISIBLE) return;

        if (NetUtil.detectAvailable(context)) {
            if(rentReleaseListRequest == null){
                rentReleaseListRequest = new RentReleaseListRequest(modelApp.getUser().getId(), modelApp.getSite().getSiteId(),"3",
                        page, pageSize, new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {

                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);

                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                                if (rentOutList == null || rentOutList.size() == 0){
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
                                if(rentOutList == null || rentOutList.size() ==0){
                                    content_lay.setVisibility(View.GONE);
                                    notice_lay.setVisibility(View.VISIBLE);
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                content_lay.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);
                                notice_lay.setVisibility(View.GONE);

                                release_count_txt.setText("当期" + message.getData().getString("yishanchu") + "条房源"
                                        + "    刷新次数："+message.getData().getString("todat_refresh_num"));
                                ArrayList<XKRelease> temp = (ArrayList<XKRelease>) message.getData().getSerializable("rentList");
                                if(currentPageIndex == 1 && (temp == null || temp.size() ==0)){
                                    Toast.makeText(context, R.string.no_release, Toast.LENGTH_SHORT).show();
                                }
                                rentOutList.clear();
                                rentOutList.addAll(temp);
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
                rentReleaseListRequest.setData(modelApp.getUser().getId(), modelApp.getSite().getSiteId(),
                        "3", page, pageSize);
            }

            content_lay.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            notice_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.VISIBLE);
            rotate_loading.start();

            rentReleaseListRequest.doRequest();
        }else {
            if (rentOutList == null || rentOutList.size() ==0 ){
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
        if(rentOutList == null) return;
        if(adapter == null){
            adapter = new JJRReleaseManageAdapter(context, rentOutList, new JJRReleaseManageAdapter.OnItemSelectListener() {
                @Override
                public void onClick(int position) {
                    if (rentOutList.get(position).isSelected()) {
                        rentOutList.get(position).setIsSelected(false);
                    } else {
                        rentOutList.get(position).setIsSelected(true);
                    }
                    adapter.notifyDataSetChanged();

                    isAllSelected = true;
                    for (XKRelease release : rentOutList) {
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
                    rentReleaseInfoTask(rentOutList.get(position).getId());
                }
            });

            release_listView.setAdapter(adapter);
        }else{
            adapter.setData(rentOutList);
        }
    }

    /***********************************  房源操作 ******************************************/
    //获取选中的房源的id，逗号隔开
    private String getSelectedReleaseIds(){
        if(rentOutList == null || rentOutList.size() == 0) return null;
        StringBuffer sb = new StringBuffer();
        for (int i=0; i< rentOutList.size(); i++){
            if (rentOutList.get(i).isSelected()){
                sb.append(rentOutList.get(i).getId());
                sb.append(",");
            }
        }
        if(!StringUtil.isEmpty(sb.toString()) &&sb.toString().contains(",")){
            return sb.substring(0, sb.length() - 1);
        }else{
            return sb.toString();
        }

    }

    //重新发布
    private void release(String releaseId, String type){
        if (NetUtil.detectAvailable(context)) {
            ReleaseAgainRequest request = new ReleaseAgainRequest(releaseId, modelApp.getUser().getId(),
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

    //彻底删除
    private void clear(String releaseId, String type){
        if (NetUtil.detectAvailable(context)) {
            ReleaseClearRequest request = new ReleaseClearRequest(releaseId, modelApp.getUser().getId(),
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

    //编辑出租
    private void rentReleaseInfoTask(String releaseId){
        if (NetUtil.detectAvailable(context)) {
            RentReleaseInfoRequest request = new RentReleaseInfoRequest( modelApp.getUser().getId(),
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



    //删除、刷新、编辑、重新发布等操作成功后刷新数据
    public void refreshAfterOperate(){
        if (rentOutList != null && adapter != null){
            rentOutList.clear();
            adapter.setData(rentOutList);
            release_operate_txt.setText("管理");
            adapter.closeOperate();
            operate_lay.setVisibility(View.GONE);
            page_lay.setVisibility(View.VISIBLE);
        }

        currentPageIndex = 1;
        next = false;
        startRentReleaseListTask(1);
    }



	
	

	


	
}
