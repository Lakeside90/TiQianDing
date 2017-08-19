package com.xkhouse.fang.house.activity;

import android.content.Intent;
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
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.house.adapter.BuyAbilityResultAdapter;
import com.xkhouse.fang.house.task.BuyAbilityResultRequest;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
 * 购房能力评估结果
 */
public class BuyAbilityResultActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;

    private TextView total_price_txt;
    private TextView price_txt;

    private TextView count_txt;
    private LinearLayout result_view;

    private XListView house_listView;
    private int currentPageIndex = 2;  //分页索引
    private int pageSize = 10; //每次请求10条数据
    private BuyAbilityResultAdapter houseAdapter;
    private ArrayList<House> houseList = new ArrayList<House>();
    private String count;
    private String allPrice;
    private String billPrice;


    private BuyAbilityResultRequest resultRequest;
    private String areaId;
    private String currentMoney;
    private String expense;
    private String houseSize;
    private String loanTime;

    private LinearLayout no_result_view;
    private TextView no_data_txt;

    private TextView test_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (StringUtil.isEmpty(allPrice)){
            total_price_txt.setText("0");
        }else{
            total_price_txt.setText(allPrice);
        }

        if (StringUtil.isEmpty(billPrice)){
            price_txt.setText("0");
        }else{
            price_txt.setText(billPrice);
        }

        if (StringUtil.isEmpty(count) || "0".equals(count)){
            result_view.setVisibility(View.GONE);
            no_result_view.setVisibility(View.VISIBLE);
            no_data_txt.setText("小主，您太穷了，没有找到合适您的楼盘，努力去赚钱吧~");

        }else{
            result_view.setVisibility(View.VISIBLE);
            no_result_view.setVisibility(View.GONE);
            count_txt.setText(count);
            fillData();
        }

    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_buy_ability_result);
    }

    @Override
    protected void init() {
        super.init();

        Bundle data = getIntent().getExtras();
        houseList = (ArrayList<House>) data.getSerializable("houseList");
        count = data.getString("count");
        allPrice = data.getString("allPrice");
        billPrice = data.getString("billPrice");

        areaId = data.getString("areaId");
        currentMoney = data.getString("currentMoney");
        expense = data.getString("expense");
        houseSize = data.getString("houseSize");
        loanTime = data.getString("loanTime");
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitle();

        total_price_txt = (TextView) findViewById(R.id.total_price_txt);
        price_txt = (TextView) findViewById(R.id.price_txt);
        count_txt = (TextView) findViewById(R.id.count_txt);
        result_view = (LinearLayout) findViewById(R.id.result_view);
        house_listView = (XListView) findViewById(R.id.house_listView);
        no_result_view = (LinearLayout) findViewById(R.id.no_result_view);
        no_data_txt = (TextView) findViewById(R.id.no_data_txt);

        test_txt = (TextView) findViewById(R.id.test_txt);
    }

    private void initTitle() {
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("评估结果");
        iv_head_left.setOnClickListener(this);

    }


    @Override
    protected void setListeners() {
        super.setListeners();

        test_txt.setOnClickListener(this);

        house_listView.setPullLoadEnable(true);
        house_listView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                startResultTask(currentPageIndex);
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

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.iv_head_left:
                finish();
                break;

            case R.id.test_txt:
                startActivity(new Intent(BuyAbilityResultActivity.this, BuyAbilityActivity.class));
                finish();
                break;
        }
    }


    private void fillData(){
        if (houseAdapter == null){
            houseAdapter = new BuyAbilityResultAdapter(mContext, houseList);
            house_listView.setAdapter(houseAdapter);
        }else{
            houseAdapter.setData(houseList);
        }
    }


    private void startResultTask(int page){
        if (NetUtil.detectAvailable(mContext)) {
            if(resultRequest == null){

                resultRequest = new BuyAbilityResultRequest(modelApp.getSite().getSiteId(), page, pageSize,
                        areaId, currentMoney, expense, houseSize, loanTime, new RequestListener() {
                    @Override
                    public void sendMessage(Message message) {

                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                                Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                Bundle data = message.getData();
                                ArrayList<House> temp = (ArrayList<House>) data.getSerializable("houseList");
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    house_listView.setPullLoadEnable(false);
                                }else{
                                    house_listView.setPullLoadEnable(true);
                                }
                                houseList.addAll(temp);
                                fillData();
                                if (currentPageIndex > 1 && data.getString("count").equals(String.valueOf(houseList.size()))){
                                    Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                                }
                                currentPageIndex++;
                                break;
                        }
                    }
                });
            }else {
                resultRequest.setData(page, pageSize);
            }
            resultRequest.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            house_listView.stopLoadMore();
        }
    }


}
