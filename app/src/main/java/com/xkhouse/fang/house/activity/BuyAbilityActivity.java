package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.app.service.HouseConfigDbService;
import com.xkhouse.fang.app.task.CommonConfigRequest;
import com.xkhouse.fang.house.adapter.BuyAbilityAreaAdapter;
import com.xkhouse.fang.house.adapter.NHMultyTypeAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.task.AreaListRequest;
import com.xkhouse.fang.house.task.BuyAbilityResultRequest;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
 * 购房能力评估
 */
public class BuyAbilityActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;

    private ScrollGridView area_grid;
    private EditText space_txt;
    private EditText down_payment_txt;
    private ScrollGridView loan_time_grid;
    private EditText month_payment_txt;

    private TextView submit_txt;

    private AreaListRequest areaRequest;	//地区
    private ArrayList<CommonType> areaList = new ArrayList<CommonType>();
    private BuyAbilityAreaAdapter areaAdapter;

    private CommonConfigRequest loanTimeRequest;	//价格
    private ArrayList<CommonType> loanTimeList = new ArrayList<CommonType>();
    private NHMultyTypeAdapter loanTimeAdapter;

    private HouseConfigDbService configDbService = new HouseConfigDbService();

    private int loanTimeIndex = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAreaListData();
        startLoanTimeListTask();

    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_buy_ability);
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitle();

        area_grid = (ScrollGridView) findViewById(R.id.area_grid);
        space_txt = (EditText) findViewById(R.id.space_txt);
        down_payment_txt = (EditText) findViewById(R.id.down_payment_txt);
        loan_time_grid = (ScrollGridView) findViewById(R.id.loan_time_grid);
        month_payment_txt = (EditText) findViewById(R.id.month_payment_txt);

        submit_txt = (TextView) findViewById(R.id.submit_txt);
    }

    private void initTitle() {
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("购房能力评估");
        iv_head_left.setOnClickListener(this);
    }


    @Override
    protected void setListeners() {
        super.setListeners();
        submit_txt.setOnClickListener(this);

        area_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (areaList.get(position).isSelected()) {
                    areaList.get(position).setSelected(false);
                } else {
                    areaList.get(position).setSelected(true);
                }
                areaAdapter.setData(areaList);
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.iv_head_left:
                closeSoftInput();
                //解決黑屏問題
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
                break;

            case R.id.submit_txt:
                startResultTask();
                break;
        }
    }

    private void getAreaListFromDB(){
        ArrayList<Area> areaTypes = configDbService.getAreaListBySite(modelApp.getSite().getSiteId());
        if(areaTypes != null && areaTypes.size() > 0){

            for (Area area : areaTypes) {
                CommonType type = new CommonType();
                type.setId(area.getAreaId());
                type.setName(area.getAreaName());
                areaList.add(type);
            }
        }
    }

    private void getAreaListData(){
        getAreaListFromDB();
        if (areaList == null || areaList.size() == 0){
            startAreaListTask();
        }else{
            fillAreaData();
        }

    }
    /******************************  行政区域   ******************************/
    private RequestListener areaListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            hideLoadingDialog();
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
                    break;

                case Constants.NO_DATA_FROM_NET:
                    Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    ArrayList<Area> temp = (ArrayList<Area>) message.obj;
                    configDbService.insertAreaList(temp, modelApp.getSite().getSiteId());
                    getAreaListFromDB();
                    fillAreaData();
                    break;
            }
        }
    };

    private void startAreaListTask() {
        if(NetUtil.detectAvailable(mContext)){
            if(areaRequest == null){
                areaRequest = new AreaListRequest(modelApp.getSite().getSiteId(), areaListener);
            }
            areaRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }



    /******************************  贷款年限   ******************************/
    private RequestListener loanTimeListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            hideLoadingDialog();
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
                    break;

                case Constants.NO_DATA_FROM_NET:
                    Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    ArrayList<CommonType> temp = (ArrayList<CommonType>) message.obj;
                    configDbService.insertLoanTimeList(temp, modelApp.getSite().getSiteId());
                    loanTimeList = configDbService.getLoanTimeListBySite(modelApp.getSite().getSiteId());
                    fillLoanTimeData();
                    break;
            }
        }
    };

    private void startLoanTimeListTask() {
        loanTimeList = configDbService.getLoanTimeListBySite(modelApp.getSite().getSiteId());
        if(loanTimeList != null && loanTimeList.size() > 0){
            fillLoanTimeData();
            return;
        }

        if(NetUtil.detectAvailable(mContext)){
            if(loanTimeRequest == null){
                loanTimeRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "LOAN_PINGGU_TIME", loanTimeListener);
            }
            loanTimeRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        }else {
            Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
        }
    }


    private void fillLoanTimeData(){
        if (loanTimeList == null) return;
        if(loanTimeAdapter == null){
            loanTimeAdapter = new NHMultyTypeAdapter(mContext, loanTimeList,
                    new NHMultyTypeAdapter.NHMultyTypeAdapterClick() {
                        @Override
                        public void onItemClick(int position) {
                            loanTimeIndex = position;
                            loanTimeAdapter.setSelectIndex(loanTimeIndex);
                        }
                    });
            loan_time_grid.setAdapter(loanTimeAdapter);
        }
    }

    private void fillAreaData(){
        if (areaList == null) return;
        if(areaAdapter == null){
            areaAdapter = new BuyAbilityAreaAdapter(mContext, areaList);
            area_grid.setAdapter(areaAdapter);
        }
    }


    private void startResultTask(){
        if (areaList == null || areaList.size() == 0 || loanTimeList == null || loanTimeList.size() == 0) return;
        String areaId = getSelectedArea();
        String currentMoney = down_payment_txt.getText().toString();
        String expense = month_payment_txt.getText().toString();
        String houseSize = space_txt.getText().toString();
        String loanTime = loanTimeList.get(loanTimeIndex).getId();

        if (StringUtil.isEmpty(areaId)) {
            Toast.makeText(mContext, "请选择购房区域", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(currentMoney)) {
            Toast.makeText(mContext, "请填写首付金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(expense)) {
            Toast.makeText(mContext, "请填写每月可用于购房支出", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(houseSize)) {
            Toast.makeText(mContext, "填写期望购房面积", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(loanTime)) {
            Toast.makeText(mContext, "请选择期望贷款年限", Toast.LENGTH_SHORT).show();
            return;
        }

        if (NetUtil.detectAvailable(mContext)) {

            BuyAbilityResultRequest request = new BuyAbilityResultRequest(modelApp.getSite().getSiteId(), 1, 10,
                    areaId, currentMoney, expense, houseSize, loanTime, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if (StringUtil.isEmpty(msg)){
                                msg = "提交失败";
                            }
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            Bundle data = message.getData();
                            Intent intent = new Intent(BuyAbilityActivity.this, BuyAbilityResultActivity.class);
                            data.putString("areaId", getSelectedArea());
                            data.putString("currentMoney",down_payment_txt.getText().toString());
                            data.putString("expense",month_payment_txt.getText().toString());
                            data.putString("houseSize",space_txt.getText().toString());
                            data.putString("loanTime",loanTimeList.get(loanTimeIndex).getId());
                            intent.putExtras(data);
                            startActivity(intent);
                            finish();
                            break;
                    }
                }
            });
            request.doRequest();
            showLoadingDialog(R.string.data_loading);
        }else{
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    private String getSelectedArea(){
        if (areaList == null || areaList.size() == 0) return null;
        StringBuffer sb = new StringBuffer();
        for (CommonType type : areaList){
            if (type.isSelected()){
                sb.append(type.getId()).append(",");
            }
        }
       String ids = sb.toString();
        if (!StringUtil.isEmpty(ids) && ids.contains(",")){
            ids = ids.substring(0, ids.length()-1);
        }
        return  ids;
    }

}
