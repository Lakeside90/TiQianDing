package com.xkhouse.fang.house.activity;


import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.adapter.CommunityAdapter;
import com.xkhouse.fang.house.entity.CommunityInfo;
import com.xkhouse.fang.house.task.CommunityListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;


/**
 * 划片小区
 * wujian 2016/8/5
 */
public class CommunityListActivity extends AppBaseActivity {



    private ImageView iv_head_left;
    private TextView tv_head_title;

    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

    //列表
    private XListView house_listView;
    private int currentPageIndex = 1;  //分页索引
    private int pageSize = 20; //每次请求20条数据
    private boolean isPullDown = false; // 下拉
    private CommunityAdapter communityAdapter;
    private ArrayList<CommunityInfo> communityInfos = new ArrayList<>();
    private CommunityListRequest communityListRequest;


    private TextView house_count_txt;

    private ImageView scroll_top_iv;


    private String schoolName;
    private String schoolId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startNewHouseListTask(1, true);

    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_community_list);
    }

    @Override
    protected void init() {
        super.init();

        Bundle data = getIntent().getExtras();
        if(data != null){
            schoolName = data.getString("schoolName");
            schoolId = data.getString("schoolId");
        }
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitleView();

        house_count_txt = (TextView) findViewById(R.id.house_count_txt);
        house_listView = (XListView) findViewById(R.id.house_listView);
        scroll_top_iv = (ImageView) findViewById(R.id.scroll_top_iv);

        content_lay = (LinearLayout) findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);
    }

    private void initTitleView(){
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText(schoolName);
        iv_head_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityListActivity.this.finish();
            }
        });
    }


    @Override
    protected void setListeners() {
        super.setListeners();

        scroll_top_iv.setOnClickListener(this);
        error_lay.setOnClickListener(this);

        house_listView.setPullLoadEnable(true);
        house_listView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {

                startNewHouseListTask(currentPageIndex, false);
            }
        }, R.id.house_listView);

        house_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //滑到底部自动加载下一页数据
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && house_listView.getEnablePullLoad()) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1
                            && !house_listView.getPullLoading()) {
                        house_listView.startLoadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 5) {
                    if (scroll_top_iv.getVisibility() == View.GONE) {
                        scroll_top_iv.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (scroll_top_iv.getVisibility() == View.VISIBLE) {
                        scroll_top_iv.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {

            case R.id.scroll_top_iv:
                house_listView.post(new Runnable() {
                    @Override
                    public void run() {
                        house_listView.smoothScrollToPosition(0);
                    }
                });
                break;

            case R.id.error_lay:
                startNewHouseListTask(1, true);
                break;
        }
    }


    private void startNewHouseListTask(int page, boolean showLoading){
        if (NetUtil.detectAvailable(mContext)) {
            if(communityListRequest == null){
                communityListRequest = new CommunityListRequest(modelApp.getSite().getSiteId(), schoolId, page, pageSize,
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
                                error_lay.setVisibility(View.VISIBLE);
                                content_lay.setVisibility(View.GONE);
                                Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();

                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                content_lay.setVisibility(View.VISIBLE);
                                if (currentPageIndex == 1){
                                    if(communityInfos != null){
                                        communityInfos.clear();
                                        fillData();
                                    }
                                    house_count_txt.setText("0");
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                content_lay.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);

                                house_count_txt.setText(String.valueOf(message.arg1));
                                Bundle data = message.getData();
                                tv_head_title.setText(data.getString("schoolName"));
                                ArrayList<CommunityInfo> temp = (ArrayList<CommunityInfo>) data.getSerializable("communityList");
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    house_listView.setPullLoadEnable(false);
                                }else{
                                    house_listView.setPullLoadEnable(true);
                                }

                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && communityInfos != null){
                                    if(communityInfos.size() > 0){
                                        house_listView.smoothScrollToPosition(0);
                                    }
                                    communityInfos.clear();
                                    currentPageIndex = 1;
                                }
                                communityInfos.addAll(temp);
                                fillData();
                                if (currentPageIndex > 1 && message.arg1 == communityInfos.size()){
                                    Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                                }
                                currentPageIndex++;
                                break;
                        }
                        isPullDown = false;
                        house_listView.stopRefresh();
                        house_listView.stopLoadMore();
                    }
                });
            }else {
                communityListRequest.setData(modelApp.getSite().getSiteId(), schoolId, page, pageSize);
            }
            if(showLoading){
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            communityListRequest.doRequest();

        }else {
            isPullDown = false;
            house_listView.stopRefresh();
            house_listView.stopLoadMore();

            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();

        }
    }


    public void fillData() {

        if (communityInfos == null) return;
        if (communityAdapter == null) {
            communityAdapter = new CommunityAdapter(mContext, communityInfos);
            house_listView.setAdapter(communityAdapter);
        }else {
            communityAdapter.setData(communityInfos);
        }
    }






}
