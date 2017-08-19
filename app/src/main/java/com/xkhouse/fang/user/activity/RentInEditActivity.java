package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.CommonConfigRequest;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.money.activity.ExpectHouseActivity;
import com.xkhouse.fang.money.entity.XKBHouse;
import com.xkhouse.fang.money.view.CenterListPopupWindow;
import com.xkhouse.fang.user.entity.RentInEditBean;
import com.xkhouse.fang.user.task.ReleaseAreaListRequest;
import com.xkhouse.fang.user.task.RentInEditRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description: 编辑求租
* @author wujian  
* @date 2016-04-25
 */
public class RentInEditActivity extends AppBaseActivity {

    private View title_view;
	private ImageView iv_head_left;
	private TextView tv_head_title;
	

    private TextView zulin_txt;
    private TextView hezu_txt;
    private TextView expect_street_txt;
    private TextView rent_type_txt;
    private TextView house_type_txt;        //户型
    private EditText space_min_txt;         //面积
    private EditText space_max_txt;         //面积
    private EditText price_min_txt;         //售价
    private EditText price_max_txt;         //售价
    private EditText floor_min_txt;         //楼层
    private EditText floor_max_txt;         //楼层
    private EditText rentin_title_txt;      //房源标题
    private EditText rentin_remark_txt;     //描述
    private EditText rentin_name_txt;       //姓名
    private EditText rentin_phone_txt;      //手机号


    private TextView submit_txt;

    //合租方式
    private CenterListPopupWindow hezuTypeView;
    private CommonConfigRequest hezuTypeRequest;
    private ArrayList<CommonType> hezuTypeList = new ArrayList<CommonType>();
    private CommonType hezuType;

    //租赁方式
    private CenterListPopupWindow zulinTypeView;
    private CommonConfigRequest zulinTypeRequest;
    private ArrayList<CommonType> zulinTypeList = new ArrayList<CommonType>();
    private CommonType zulinType;

    //物业类型
    private CenterListPopupWindow propertyTypeView;
    private CommonConfigRequest propertyTypeRequest;
    private ArrayList<CommonType> propertyTypeList = new ArrayList<CommonType>();
    private CommonType propertyType;

    //户型
    private CenterListPopupWindow houseTypeView;
    private CommonConfigRequest houseTypeRequest;
    private ArrayList<CommonType> houseTypeList = new ArrayList<CommonType>();
    private CommonType houseType;

    //区域
    private ArrayList<CommonType> areaHouseList;
    public static final int RESULT_CODE = 101;
    public static final int RESULT_CODE_CITY = 102;
    public static final int REQUEST_CODE = 100;
    private String houseID;

    private RentInEditBean rentInEditBean;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        fillData();

	}
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_rent_in);
		
	}

    @Override
    protected void init() {
        super.init();

        rentInEditBean = (RentInEditBean) getIntent().getExtras().getSerializable("rentInEditBean");
    }

    @Override
	protected void findViews() {
		super.findViews();
		initTitle();

        zulin_txt = (TextView) findViewById(R.id.zulin_txt);
        hezu_txt = (TextView) findViewById(R.id.hezu_txt);
        expect_street_txt = (TextView) findViewById(R.id.expect_street_txt);
        rent_type_txt = (TextView) findViewById(R.id.rent_type_txt);
        house_type_txt = (TextView) findViewById(R.id.house_type_txt);
        space_min_txt = (EditText) findViewById(R.id.space_min_txt);
        space_max_txt = (EditText) findViewById(R.id.space_max_txt);
        price_min_txt = (EditText) findViewById(R.id.price_min_txt);
        price_max_txt = (EditText) findViewById(R.id.price_max_txt);
        floor_min_txt = (EditText) findViewById(R.id.floor_min_txt);
        floor_max_txt = (EditText) findViewById(R.id.floor_max_txt);
        rentin_title_txt = (EditText) findViewById(R.id.rentin_title_txt);
        rentin_remark_txt = (EditText) findViewById(R.id.rentin_remark_txt);
        rentin_name_txt = (EditText) findViewById(R.id.rentin_name_txt);
        rentin_phone_txt = (EditText) findViewById(R.id.rentin_phone_txt);
        submit_txt = (TextView) findViewById(R.id.submit_txt);

    }
	
	private void initTitle() {
        title_view = findViewById(R.id.title_view);
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("编辑求租");
		iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                closeSoftInput();
                //解決黑屏問題
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
	}

    @Override
    protected void setListeners() {
        super.setListeners();

        zulin_txt.setOnClickListener(this);
        hezu_txt.setOnClickListener(this);
        expect_street_txt.setOnClickListener(this);
        rent_type_txt.setOnClickListener(this);
        house_type_txt.setOnClickListener(this);
        submit_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
            case R.id.expect_street_txt:
                if(areaHouseList != null && areaHouseList.size() > 0){
                    startExpectHouseActivity();
                }else{
                    startExpectHouseTask();
                }
                break;

            case R.id.zulin_txt:
                startZulinTypeListTask();
                break;

            case R.id.hezu_txt:
                startHezuTypeListTask();
                break;

            case R.id.rent_type_txt:
                startPropertyTypeListTask();
                break;

            case R.id.house_type_txt:
                startHouseTypeListTask();
                break;

            case R.id.submit_txt:
                startCommitTask();
                break;
        }
    }


    private void fillData(){

        if(rentInEditBean.getAreaList() != null){
            StringBuffer sb = new StringBuffer();
            StringBuffer sbName = new StringBuffer();

            for (CommonType area:rentInEditBean.getAreaList()){
                sb.append(area.getId());
                sb.append(",");

                sbName.append(area.getName());
                sbName.append(",");
            }
            if (sb.length() > 2){
                houseID = sb.substring(0, sb.length() - 1);
                expect_street_txt.setText(sbName.substring(0, sbName.length() - 1));
            }
        }

        if(!StringUtil.isEmpty(rentInEditBean.getPropertyTypeName())){
            propertyType = new CommonType();
            propertyType.setId(rentInEditBean.getPropertyType());
            propertyType.setName(rentInEditBean.getPropertyTypeName());
            rent_type_txt.setText(rentInEditBean.getPropertyTypeName());
        }

        if(!StringUtil.isEmpty(rentInEditBean.getHouseType())){
            houseType = new CommonType();
            houseType.setId(rentInEditBean.getHouseType());
            houseType.setName(rentInEditBean.getHouseTypeName());
            house_type_txt.setText(rentInEditBean.getHouseTypeName());
        }
        if(!StringUtil.isEmpty(rentInEditBean.getRentType())){
            zulinType = new CommonType();
            zulinType.setId(rentInEditBean.getRentType());
            zulinType.setName(rentInEditBean.getRentTypeName());
            zulin_txt.setText(rentInEditBean.getRentTypeName());
        }
        if(!StringUtil.isEmpty(rentInEditBean.getSharedTypeName())){
            hezuType = new CommonType();
            hezuType.setId(rentInEditBean.getSharedType());
            hezuType.setName(rentInEditBean.getSharedTypeName());
            hezu_txt.setText(rentInEditBean.getSharedTypeName());
        }


        if(!StringUtil.isEmpty(rentInEditBean.getArea_start())){
            space_min_txt.setText(rentInEditBean.getArea_start());
        }
        if(!StringUtil.isEmpty(rentInEditBean.getArea_end())){
            space_max_txt.setText(rentInEditBean.getArea_end());
        }

        if(!StringUtil.isEmpty(rentInEditBean.getPrice_start())){
            price_min_txt.setText(rentInEditBean.getPrice_start());
        }
        if(!StringUtil.isEmpty(rentInEditBean.getPrice_end())){
            price_max_txt.setText(rentInEditBean.getPrice_end());
        }

        if(!StringUtil.isEmpty(rentInEditBean.getFloor_start())){
            floor_min_txt.setText(rentInEditBean.getFloor_start());
        }
        if(!StringUtil.isEmpty(rentInEditBean.getFloor_end())){
            floor_max_txt.setText(rentInEditBean.getFloor_end());
        }

        if(!StringUtil.isEmpty(rentInEditBean.getTitle())){
            rentin_title_txt.setText(rentInEditBean.getTitle());
        }
        if(!StringUtil.isEmpty(rentInEditBean.getDetail())){
            rentin_remark_txt.setText(rentInEditBean.getDetail());
        }

        if(!StringUtil.isEmpty(rentInEditBean.getContacter())){
            rentin_name_txt.setText(rentInEditBean.getContacter());
        }
        if(!StringUtil.isEmpty(rentInEditBean.getContactPhone())){
            rentin_phone_txt.setText(rentInEditBean.getContactPhone());
        }

    }



    private void startCommitTask(){
        if (NetUtil.detectAvailable(mContext)) {
            //校验参数
            if(StringUtil.isEmpty(zulin_txt.getText().toString())){
                Toast.makeText(mContext, "请选择租赁方式", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(hezu_txt.getText().toString())){
                Toast.makeText(mContext, "请选择合租方式", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(expect_street_txt.getText().toString())){
                Toast.makeText(mContext, "请选择期望区域", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(rent_type_txt.getText().toString())){
                Toast.makeText(mContext, "请选择物业类型", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(house_type_txt.getText().toString())){
                Toast.makeText(mContext, "请选择户型", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(space_min_txt.getText().toString())){
                Toast.makeText(mContext, "请填写面积", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(space_max_txt.getText().toString())){
                Toast.makeText(mContext, "请填写面积", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(price_min_txt.getText().toString())){
                Toast.makeText(mContext, "请填写租金", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(price_max_txt.getText().toString())){
                Toast.makeText(mContext, "请填写租金", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(floor_min_txt.getText().toString())){
                Toast.makeText(mContext, "请填写楼层", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(floor_max_txt.getText().toString())){
                Toast.makeText(mContext, "请填写楼层", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(rentin_title_txt.getText().toString())){
                Toast.makeText(mContext, "请填写房源标题", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(rentin_remark_txt.getText().toString())){
                Toast.makeText(mContext, "请填写描述", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(rentin_name_txt.getText().toString())){
                Toast.makeText(mContext, "请填写联系人名称", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(rentin_phone_txt.getText().toString())){
                Toast.makeText(mContext, "请填写联系人手机号", Toast.LENGTH_SHORT).show();
                return;
            }

            if(rentin_title_txt.getText().toString().length() < 8 ||
                    rentin_title_txt.getText().toString().length() > 28 ){
                Toast.makeText(mContext, "房源标题在8-28字之间", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rentin_remark_txt.getText().toString().length() < 10){
                Toast.makeText(mContext, "描述至少10字", Toast.LENGTH_SHORT).show();
                return;
            }

            rentInEditBean.setArea(houseID);
            rentInEditBean.setPropertyType(propertyType.getId());
            rentInEditBean.setHouseType(houseType.getId());
            rentInEditBean.setArea_start(space_min_txt.getText().toString());
            rentInEditBean.setArea_end(space_max_txt.getText().toString());
            rentInEditBean.setPrice_start(price_min_txt.getText().toString());
            rentInEditBean.setPrice_end(price_max_txt.getText().toString());
            rentInEditBean.setFloor_start(floor_min_txt.getText().toString());
            rentInEditBean.setFloor_end(floor_max_txt.getText().toString());
            rentInEditBean.setTitle(rentin_title_txt.getText().toString());
            rentInEditBean.setDetail(rentin_remark_txt.getText().toString());
            rentInEditBean.setContacter(rentin_name_txt.getText().toString());
            rentInEditBean.setContactPhone(rentin_phone_txt.getText().toString());
            rentInEditBean.setSharedType(hezuType.getId());
            rentInEditBean.setRentType(zulinType.getId());

            RentInEditRequest request = new RentInEditRequest(rentInEditBean,
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            hideLoadingDialog();
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                    break;

                                case Constants.NO_DATA_FROM_NET:
                                    String msg = (String) message.obj;
                                    if(StringUtil.isEmpty(msg)){
                                        Toast.makeText(mContext, "信息提交失败", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                    }
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    Toast.makeText(mContext, "信息提交成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;
                            }
                        }
                    });
            request.doRequest();
            showLoadingDialog("信息提交中");
        }else{
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }



    /******************************  合租方式   ******************************/
    private RequestListener hezuTypeListener = new RequestListener() {

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
                    hezuTypeList = (ArrayList<CommonType>) message.obj;
                    showHezuTypeView();
                    break;
            }
        }
    };

    private void startHezuTypeListTask() {
        if(hezuTypeList != null && hezuTypeList.size() > 0){
            showHezuTypeView();
            return;
        }

        if(NetUtil.detectAvailable(mContext)){
            if(hezuTypeRequest == null){
                hezuTypeRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "OLDHOUSE_JOINTHIRE_TYPE", hezuTypeListener);
            }
            hezuTypeRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        }else {
            Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
        }
    }

    private CenterListPopupWindow.CommonTypeListClickListener hezuListClickListener = new CenterListPopupWindow.CommonTypeListClickListener() {

        @Override
        public void onTypeClick(int position) {
            hezuTypeView.dismiss();
            hezuType = hezuTypeList.get(position);
            hezu_txt.setText(hezuType.getName());
        }
    };
    private void showHezuTypeView() {
        if(hezuTypeView == null){
            hezuTypeView = new CenterListPopupWindow(mContext, hezuListClickListener);
        }
        if(hezuTypeList != null && hezuTypeList.size() > 0){
            List<String> categoryStr = new ArrayList<String>();
            for (int i = 0; i < hezuTypeList.size(); i++) {
                categoryStr.add(hezuTypeList.get(i).getName());
            }
            hezuTypeView.fillData(categoryStr);
            hezuTypeView.showAsDropDown(title_view);
        }
    }


    /******************************  租赁方式   ******************************/
    private RequestListener zulinTypeListener = new RequestListener() {

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
                    zulinTypeList = (ArrayList<CommonType>) message.obj;
                    showZulinTypeView();

                    break;
            }
        }
    };

    private void startZulinTypeListTask() {
        if(zulinTypeList != null && zulinTypeList.size() > 0){
            showZulinTypeView();
            return;
        }

        if(NetUtil.detectAvailable(mContext)){
            if(zulinTypeRequest == null){
                zulinTypeRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "OLDHOUSE_LEASEHIRE_TYPE", zulinTypeListener);
            }
            zulinTypeRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        }else {
            Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
        }
    }

    private CenterListPopupWindow.CommonTypeListClickListener zulinListClickListener = new CenterListPopupWindow.CommonTypeListClickListener() {

        @Override
        public void onTypeClick(int position) {
            zulinTypeView.dismiss();
            zulinType = zulinTypeList.get(position);
            zulin_txt.setText(zulinType.getName());
        }
    };
    private void showZulinTypeView() {
        if(zulinTypeView == null){
            zulinTypeView = new CenterListPopupWindow(mContext, zulinListClickListener);
        }
        if(zulinTypeList != null && zulinTypeList.size() > 0){
            List<String> categoryStr = new ArrayList<String>();
            for (int i = 0; i < zulinTypeList.size(); i++) {
                categoryStr.add(zulinTypeList.get(i).getName());
            }
            zulinTypeView.fillData(categoryStr);
            zulinTypeView.showAsDropDown(title_view);
        }
    }


    /******************************  物业类型   ******************************/
    private RequestListener propertyTypeListener = new RequestListener() {

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
                    propertyTypeList = (ArrayList<CommonType>) message.obj;
                    showPropertyTypeView();

                    break;
            }
        }
    };

    private void startPropertyTypeListTask() {
        if(propertyTypeList != null && propertyTypeList.size() > 0){
            showPropertyTypeView();
            return;
        }

        if(NetUtil.detectAvailable(mContext)){
            if(propertyTypeRequest == null){
                propertyTypeRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "OLDHOUSE_PROPERTY_TYPE", propertyTypeListener);
            }
            propertyTypeRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        }else {
            Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
        }
    }

    private CenterListPopupWindow.CommonTypeListClickListener propertyListClickListener = new CenterListPopupWindow.CommonTypeListClickListener() {

        @Override
        public void onTypeClick(int position) {
            propertyTypeView.dismiss();
            propertyType = propertyTypeList.get(position);
            rent_type_txt.setText(propertyType.getName());
        }
    };
    private void showPropertyTypeView() {
        if(propertyTypeView == null){
            propertyTypeView = new CenterListPopupWindow(mContext, propertyListClickListener);
        }
        if(propertyTypeList != null && propertyTypeList.size() > 0){
            List<String> categoryStr = new ArrayList<String>();
            for (int i = 0; i < propertyTypeList.size(); i++) {
                categoryStr.add(propertyTypeList.get(i).getName());
            }
            propertyTypeView.fillData(categoryStr);
            propertyTypeView.showAsDropDown(title_view);
        }
    }

    /******************************  户型   ******************************/
    private RequestListener houseTypeListener = new RequestListener() {

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
                    houseTypeList = (ArrayList<CommonType>) message.obj;
                    showHouseTypeView();

                    break;
            }
        }
    };

    private void startHouseTypeListTask() {
        if(houseTypeList != null && houseTypeList.size() > 0){
            showHouseTypeView();
            return;
        }

        if(NetUtil.detectAvailable(mContext)){
            if(houseTypeRequest == null){
                houseTypeRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "OLDHOUSE_DEMAND_HOUSETYPE", houseTypeListener);
            }
            houseTypeRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        }else {
            Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
        }
    }

    private CenterListPopupWindow.CommonTypeListClickListener categoryListClickListener = new CenterListPopupWindow.CommonTypeListClickListener() {

        @Override
        public void onTypeClick(int position) {
            houseTypeView.dismiss();
            houseType = houseTypeList.get(position);
            house_type_txt.setText(houseType.getName());
        }
    };
    private void showHouseTypeView() {
        if(houseTypeView == null){
            houseTypeView = new CenterListPopupWindow(mContext, categoryListClickListener);
        }
        if(houseTypeList != null && houseTypeList.size() > 0){
            List<String> categoryStr = new ArrayList<String>();
            for (int i = 0; i < houseTypeList.size(); i++) {
                categoryStr.add(houseTypeList.get(i).getName());
            }
            houseTypeView.fillData(categoryStr);
            houseTypeView.showAsDropDown(title_view);
        }
    }






    private void startExpectHouseTask() {
        if (NetUtil.detectAvailable(mContext)) {
            ReleaseAreaListRequest request = new ReleaseAreaListRequest(modelApp.getSite().getSiteId(), new RequestListener() {

                @Override
                public void sendMessage(Message message) {
                    hideLoadingDialog();

                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            Toast.makeText(mContext, "没有数据", Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            areaHouseList = (ArrayList<CommonType>) message.obj;
                            startExpectHouseActivity();

                            break;
                    }
                }
            });

            showLoadingDialog(R.string.data_loading);
            request.doRequest();

        }else{
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    private void startExpectHouseActivity() {
        Intent intent = new Intent(mContext, ExpectHouseActivity.class);
        Bundle data = new Bundle();
        data.putSerializable("areaHouseList", areaHouseList);
        data.putString("houseID", houseID);
        intent.putExtras(data);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //选择的期望区域
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_CODE) {
                HashMap<String, List<XKBHouse>> childHouses = (HashMap<String, List<XKBHouse>>) data.getExtras().getSerializable("childHouses");
                StringBuilder houseStr = new StringBuilder();
                StringBuilder houseId = new StringBuilder();
                for(Map.Entry<String, List<XKBHouse>> entry : childHouses.entrySet()){
                    List<XKBHouse> houseList = entry.getValue();
                    for (XKBHouse house : houseList) {
                        if(house.isSelected()){
                            houseStr = houseStr.append(house.getName()).append(",");
                            houseId = houseId.append(house.getId()).append(",");

                        }
                    }
                }
                expect_street_txt.setText(houseStr.toString());
                houseID = houseId.toString();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(propertyTypeView != null && propertyTypeView.isShowing()){
                propertyTypeView.dismiss();
                return true;
            }
            if(zulinTypeView != null && zulinTypeView.isShowing()){
                zulinTypeView.dismiss();
                return true;
            }
            if(hezuTypeView != null && hezuTypeView.isShowing()){
                hezuTypeView.dismiss();
                return true;
            }
            if(houseTypeView != null && houseTypeView.isShowing()){
                houseTypeView.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
