package com.xkhouse.fang.user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.StorageUtils;
//import com.squareup.okhttp.Call;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.Headers;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.MultipartBuilder;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.CommonConfigRequest;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.money.view.CenterListPopupWindow;
import com.xkhouse.fang.user.entity.SellReleaseEditBean;
import com.xkhouse.fang.user.task.SellReleaseEditRequest;
import com.xkhouse.fang.user.view.PhotoDialog;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.fang.widget.imagepicker.adapter.MainGVAdapter;
import com.xkhouse.fang.widget.imagepicker.ui.PhotoWallActivity;
import com.xkhouse.fang.widget.imagepicker.utils.ScreenUtils;
import com.xkhouse.fang.widget.imagepicker.utils.Utility;
import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.FileUtil;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author wujian
 * @Description: 发布二手房房源
 * @date 2016-04-12
 */
public class SellReleaseEditActivity extends AppBaseActivity {

    private View title_view;
    private ImageView iv_head_left;
    private TextView tv_head_title;


    private TextView fitment_txt;
    private TextView orientation_txt;
    private TextView sell_street_txt;
    private TextView sell_type_txt;
    private EditText housetype_shi_txt;
    private EditText housetype_ting_txt;
    private EditText housetype_wei_txt;
    private EditText floor_min_txt;
    private EditText floor_max_txt;
    private EditText sell_space_txt;
    private EditText sell_price_txt;
    private EditText sell_title_txt;
    private EditText sell_remark_txt;
    private EditText sell_name_txt;
    private EditText sell_phone_txt;
    private TextView submit_txt;


    private MainGVAdapter adapter;
    private ArrayList<String> imagePathList;
    private ArrayList<String> imageCachePathList;

    //小区
    private CommonType area;

    //物业类型
    private CenterListPopupWindow propertyTypeView;
    private CommonConfigRequest propertyTypeRequest;
    private ArrayList<CommonType> propertyTypeList = new ArrayList<CommonType>();
    private CommonType propertyType;

    //装修
    private CenterListPopupWindow fitmentView;
    private CommonConfigRequest fitmentRequest;
    private ArrayList<CommonType> fitmentList = new ArrayList<CommonType>();
    private CommonType fitmentType;

    //朝向
    private CenterListPopupWindow orientationView;
    private CommonConfigRequest orientationRequest;
    private ArrayList<CommonType> orientationList = new ArrayList<CommonType>();
    private CommonType orientationType;


    private ArrayList<String> delPhotos = new ArrayList<>();
    private ArrayList<String> newPhotos = new ArrayList<>();


    private SellReleaseEditBean sellReleaseEditBean;

    private int CAMERA_REQUEST_CODE = 11;
    private int READ_EXTERNAL_STORAGE_REQUEST_CODE = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initImagePickerView();

        fillData();

//        if (Constants.USER_JING_JI_REN.equals(modelApp.getUser().getMemberType()) &&
//                StringUtil.isEmpty(modelApp.getUser().getOldhouseSaleExtAuth())) {
//            sell_name_txt.setFocusable(false);
//            sell_phone_txt.setFocusable(false);
//        }

    }

    @Override
    protected void init() {
        super.init();
        sellReleaseEditBean = (SellReleaseEditBean) getIntent().getExtras().getSerializable("sellReleaseEditBean");
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_sell_release);

    }

    @Override
    protected void findViews() {
        super.findViews();
        initTitle();

        orientation_txt = (TextView) findViewById(R.id.orientation_txt);
        fitment_txt = (TextView) findViewById(R.id.fitment_txt);
        sell_street_txt = (TextView) findViewById(R.id.sell_street_txt);
        sell_type_txt = (TextView) findViewById(R.id.sell_type_txt);
        housetype_shi_txt = (EditText) findViewById(R.id.housetype_shi_txt);
        housetype_ting_txt = (EditText) findViewById(R.id.housetype_ting_txt);
        housetype_wei_txt = (EditText) findViewById(R.id.housetype_wei_txt);
        floor_min_txt = (EditText) findViewById(R.id.floor_min_txt);
        floor_max_txt = (EditText) findViewById(R.id.floor_max_txt);
        sell_space_txt = (EditText) findViewById(R.id.sell_space_txt);
        sell_price_txt = (EditText) findViewById(R.id.sell_price_txt);
        sell_title_txt = (EditText) findViewById(R.id.sell_title_txt);
        sell_remark_txt = (EditText) findViewById(R.id.sell_remark_txt);
        sell_name_txt = (EditText) findViewById(R.id.sell_name_txt);
        sell_phone_txt = (EditText) findViewById(R.id.sell_phone_txt);
        submit_txt = (TextView) findViewById(R.id.submit_txt);

    }

    private void initTitle() {
        title_view = findViewById(R.id.title_view);
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("发布房源");
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

    private void initImagePickerView() {
        //获取屏幕像素
        ScreenUtils.initScreen(this);

        ScrollGridView mainGV = (ScrollGridView) findViewById(R.id.main_gridView);

        imagePathList = new ArrayList<String>();
        if (sellReleaseEditBean.getPhotos() != null && sellReleaseEditBean.getPhotos().size() > 0) {
            for (int i = 0; i < sellReleaseEditBean.getPhotos().size(); i++) {
                if (!StringUtil.isEmpty(sellReleaseEditBean.getPhotos().get(i))) {
                    imagePathList.add(sellReleaseEditBean.getPhotos().get(i));
                    if (imagePathList.size() == 8) break;
                }
            }
        }
        if (imagePathList.size() < 8) {
            imagePathList.add("ADD_FLAG");
        }


        adapter = new MainGVAdapter(this, imagePathList, new MainGVAdapter.PhotoOperateListener() {
            @Override
            public void onAdd() {
                //添加照片
                addPhoto();
            }

            @Override
            public void onDelete(int position) {

                //记住删除的提交过的图片地址
                if (imagePathList.get(position).contains("http://")) {
                    if (!delPhotos.contains(imagePathList.get(position))) {
                        delPhotos.add(imagePathList.get(position));
                    }
                }

                imagePathList.remove(position);
                if (!imagePathList.get(imagePathList.size() - 1).equals("ADD_FLAG")) {
                    imagePathList.add("ADD_FLAG");
                }
                adapter.notifyDataSetChanged();
            }
        });
        mainGV.setAdapter(adapter);
    }

    PhotoDialog photoDialog;
    private void addPhoto() {
        photoDialog = new PhotoDialog(mContext, R.style.takePhotoDialog);
        photoDialog.show();

        photoDialog.takePhoto.setOnClickListener(this);
        photoDialog.takeFromPic.setOnClickListener(this);
        photoDialog.cancel.setOnClickListener(this);
    }
    private void hidePhotoDialog() {
        if (photoDialog != null && photoDialog.isShowing()) {
            photoDialog.dismiss();
        }
    }


    @Override
    protected void setListeners() {
        super.setListeners();

        fitment_txt.setOnClickListener(this);
        orientation_txt.setOnClickListener(this);
        sell_street_txt.setOnClickListener(this);
        sell_type_txt.setOnClickListener(this);
        submit_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.sell_street_txt:
                Intent intent = new Intent(SellReleaseEditActivity.this, ReleaseStreetSearchActivity.class);
                Bundle data = new Bundle();
                data.putInt("activity", ReleaseStreetSearchActivity.SELL_EDIT_ACTIVITY);
                intent.putExtras(data);
                startActivity(intent);
                break;

            case R.id.sell_type_txt:
                startPropertyTypeListTask();
                break;

            case R.id.fitment_txt:
                startFitmentListTask();
                break;

            case R.id.orientation_txt:
                startOrientationListTask();
                break;

            case R.id.submit_txt:
                startCommitTask();
                break;

            case R.id.take_photo:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                            CAMERA_REQUEST_CODE);
                } else {
                    camera();
                }
                break;

            case R.id.take_img:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    Intent photoIntent = new Intent(SellReleaseEditActivity.this, PhotoWallActivity.class);
                    Bundle photoData = new Bundle();
                    photoData.putInt("activity", PhotoWallActivity.SELL_EDIT_ACTIVITY);
                    photoData.putInt("maxCount", 9 - imagePathList.size());
                    photoIntent.putExtras(photoData);
                    startActivity(photoIntent);
                    hidePhotoDialog();
                }
                break;

            case R.id.cancel:
                hidePhotoDialog();
                break;

        }
    }


    private void fillData() {

        if (!StringUtil.isEmpty(sellReleaseEditBean.getpName())) {
            area = new CommonType();
            area.setId(sellReleaseEditBean.getpId());
            area.setName(sellReleaseEditBean.getpName());
            sell_street_txt.setText(sellReleaseEditBean.getpName());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getPropertyTypeName())) {
            propertyType = new CommonType();
            propertyType.setId(sellReleaseEditBean.getPropertyType());
            propertyType.setName(sellReleaseEditBean.getPropertyTypeName());
            sell_type_txt.setText(sellReleaseEditBean.getPropertyTypeName());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getBeedroom())) {
            housetype_shi_txt.setText(sellReleaseEditBean.getBeedroom());
        }
        if (!StringUtil.isEmpty(sellReleaseEditBean.getOffice())) {
            housetype_ting_txt.setText(sellReleaseEditBean.getOffice());
        }
        if (!StringUtil.isEmpty(sellReleaseEditBean.getToilet())) {
            housetype_wei_txt.setText(sellReleaseEditBean.getToilet());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getFloor())) {
            floor_min_txt.setText(sellReleaseEditBean.getFloor());
        }
        if (!StringUtil.isEmpty(sellReleaseEditBean.getTotalFloor())) {
            floor_max_txt.setText(sellReleaseEditBean.getTotalFloor());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getHouseArea())) {
            sell_space_txt.setText(sellReleaseEditBean.getHouseArea());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getPrice())) {
            sell_price_txt.setText(sellReleaseEditBean.getPrice());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getFitmentName())) {
            fitmentType = new CommonType();
            fitmentType.setId(sellReleaseEditBean.getFitment());
            fitmentType.setName(sellReleaseEditBean.getFitmentName());
            fitment_txt.setText(sellReleaseEditBean.getFitmentName());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getOrientationName())) {
            orientationType = new CommonType();
            orientationType.setId(sellReleaseEditBean.getOrientation());
            orientationType.setName(sellReleaseEditBean.getOrientationName());
            orientation_txt.setText(sellReleaseEditBean.getOrientationName());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getTitle())) {
            sell_title_txt.setText(sellReleaseEditBean.getTitle());
        }
        if (!StringUtil.isEmpty(sellReleaseEditBean.getDetail())) {
            sell_remark_txt.setText(sellReleaseEditBean.getDetail());
        }

        if (!StringUtil.isEmpty(sellReleaseEditBean.getContacter())) {
            sell_name_txt.setText(sellReleaseEditBean.getContacter());
        }
        if (!StringUtil.isEmpty(sellReleaseEditBean.getContactPhone())) {
            sell_phone_txt.setText(sellReleaseEditBean.getContactPhone());
        }
    }


    private void startCommitTask() {
        if (NetUtil.detectAvailable(mContext)) {

            //校验参数
            if(StringUtil.isEmpty(sell_street_txt.getText().toString())){
                Toast.makeText(mContext, "请选择小区", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(sell_type_txt.getText().toString())){
                Toast.makeText(mContext, "请选择物业类型", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(housetype_shi_txt.getText().toString())){
                Toast.makeText(mContext, "请填写户型几室", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(housetype_ting_txt.getText().toString())){
                Toast.makeText(mContext, "请填写户型几厅", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(housetype_wei_txt.getText().toString())){
                Toast.makeText(mContext, "请填写户型几卫", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(floor_min_txt.getText().toString())){
                Toast.makeText(mContext, "请填写第几层", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(floor_max_txt.getText().toString())){
                Toast.makeText(mContext, "请填写共几层", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(sell_space_txt.getText().toString())){
                Toast.makeText(mContext, "请填写面积", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(sell_price_txt.getText().toString())){
                Toast.makeText(mContext, "请填写售价", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(fitment_txt.getText().toString())){
                Toast.makeText(mContext, "请选择装修类型", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(orientation_txt.getText().toString())){
                Toast.makeText(mContext, "请选择朝向", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(sell_title_txt.getText().toString())){
                Toast.makeText(mContext, "请填写房源标题", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(sell_remark_txt.getText().toString())){
                Toast.makeText(mContext, "请填写房源描述", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(sell_name_txt.getText().toString())){
                Toast.makeText(mContext, "请填写联系人姓名", Toast.LENGTH_SHORT).show();
                return;
            }
            if(StringUtil.isEmpty(sell_phone_txt.getText().toString())){
                Toast.makeText(mContext, "请填写联系人手机号", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sell_title_txt.getText().toString().length() < 8 ||
                    sell_title_txt.getText().toString().length() > 28) {
                Toast.makeText(mContext, "房源标题在8-28字之间", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sell_remark_txt.getText().toString().length() < 10) {
                Toast.makeText(mContext, "描述至少10字", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imagePathList != null && imagePathList.size() > 1) {
                for (int i = 0; i < imagePathList.size(); i++) {
                    if (!imagePathList.get(i).contains("http://") && !"ADD_FLAG".equals(imagePathList.get(i))) {
                        newPhotos.add(imagePathList.get(i));
                    }
                }
                if (newPhotos != null && newPhotos.size() > 0 && (photoUrls == null || photoUrls.size() < 1)) {
                    commitPhotos();
                } else {
                    commitForm();
                }
            } else {
                commitForm();
            }


        } else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    private void commitForm() {
        sellReleaseEditBean.setpId(area.getId());
        sellReleaseEditBean.setPropertyType(propertyType.getId());
        sellReleaseEditBean.setBeedroom(housetype_shi_txt.getText().toString());
        sellReleaseEditBean.setOffice(housetype_ting_txt.getText().toString());
        sellReleaseEditBean.setToilet(housetype_wei_txt.getText().toString());
        sellReleaseEditBean.setFloor(floor_min_txt.getText().toString());
        sellReleaseEditBean.setTotalFloor(floor_max_txt.getText().toString());
        sellReleaseEditBean.setHouseArea(sell_space_txt.getText().toString());
        sellReleaseEditBean.setPrice(sell_price_txt.getText().toString());
        sellReleaseEditBean.setFitment(fitmentType.getId());
        sellReleaseEditBean.setOrientation(orientationType.getId());
        sellReleaseEditBean.setTitle(sell_title_txt.getText().toString());
        sellReleaseEditBean.setDetail(sell_remark_txt.getText().toString());
        sellReleaseEditBean.setContacter(sell_name_txt.getText().toString());
        sellReleaseEditBean.setContactPhone(sell_phone_txt.getText().toString());

        String NewPhotoUrl = "";
        if (photoUrls != null && photoUrls.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < photoUrls.size(); i++) {
                sb.append(photoUrls.get(i));
                sb.append(",");
            }
            NewPhotoUrl = sb.substring(0, sb.length() - 1);
        }

        String DeletePhotoUrl = "";
        if (delPhotos != null && delPhotos.size() > 0) {
            StringBuilder delSB = new StringBuilder();
            for (int i = 0; i < delPhotos.size(); i++) {
                delSB.append(delPhotos.get(i));
                delSB.append(",");
            }
            DeletePhotoUrl = delSB.substring(0, delSB.length() - 1);
        }

        SellReleaseEditRequest request = new SellReleaseEditRequest(modelApp.getUser().getId(),
                modelApp.getSite().getSiteId(),
                sellReleaseEditBean, NewPhotoUrl, DeletePhotoUrl,
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
                                if (StringUtil.isEmpty(msg)) {
                                    Toast.makeText(mContext, "信息提交失败", Toast.LENGTH_SHORT).show();
                                } else {
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
    }


    /******************************
     * 朝向
     ******************************/
    private RequestListener orientationListener = new RequestListener() {

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
                    orientationList = (ArrayList<CommonType>) message.obj;
                    showOrientationView();

                    break;
            }
        }
    };

    private void startOrientationListTask() {
        if (orientationList != null && orientationList.size() > 0) {
            showOrientationView();
            return;
        }

        if (NetUtil.detectAvailable(mContext)) {
            if (orientationRequest == null) {
                orientationRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "DIRECTION", orientationListener);
            }
            orientationRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        } else {
            Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
        }
    }

    private CenterListPopupWindow.CommonTypeListClickListener orientationClickListener = new CenterListPopupWindow.CommonTypeListClickListener() {

        @Override
        public void onTypeClick(int position) {
            orientationView.dismiss();
            orientationType = orientationList.get(position);
            orientation_txt.setText(orientationType.getName());
        }
    };

    private void showOrientationView() {
        if (orientationView == null) {
            orientationView = new CenterListPopupWindow(mContext, orientationClickListener);
        }
        if (orientationList != null && orientationList.size() > 0) {
            List<String> categoryStr = new ArrayList<String>();
            for (int i = 0; i < orientationList.size(); i++) {
                categoryStr.add(orientationList.get(i).getName());
            }
            orientationView.fillData(categoryStr);
            orientationView.showAsDropDown(title_view);
        }
    }


    /******************************
     * 装修
     ******************************/
    private RequestListener fitmentListener = new RequestListener() {

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
                    fitmentList = (ArrayList<CommonType>) message.obj;
                    showFitmentView();

                    break;
            }
        }
    };

    private void startFitmentListTask() {
        if (fitmentList != null && fitmentList.size() > 0) {
            showFitmentView();
            return;
        }

        if (NetUtil.detectAvailable(mContext)) {
            if (fitmentRequest == null) {
                fitmentRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "OLDHOUSE_RENOVATE_STATE", fitmentListener);
            }
            fitmentRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        } else {
            Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
        }
    }

    private CenterListPopupWindow.CommonTypeListClickListener fitmentClickListener = new CenterListPopupWindow.CommonTypeListClickListener() {

        @Override
        public void onTypeClick(int position) {
            fitmentView.dismiss();
            fitmentType = fitmentList.get(position);
            fitment_txt.setText(fitmentType.getName());
        }
    };

    private void showFitmentView() {
        if (fitmentView == null) {
            fitmentView = new CenterListPopupWindow(mContext, fitmentClickListener);
        }
        if (fitmentList != null && fitmentList.size() > 0) {
            List<String> categoryStr = new ArrayList<String>();
            for (int i = 0; i < fitmentList.size(); i++) {
                categoryStr.add(fitmentList.get(i).getName());
            }
            fitmentView.fillData(categoryStr);
            fitmentView.showAsDropDown(title_view);
        }
    }


    /******************************* 物业类型 ******************************/
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
        if (propertyTypeList != null && propertyTypeList.size() > 0) {
            showPropertyTypeView();
            return;
        }

        if (NetUtil.detectAvailable(mContext)) {
            if (propertyTypeRequest == null) {
                propertyTypeRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "OLDHOUSE_PROPERTY_TYPE", propertyTypeListener);
            }
            propertyTypeRequest.doRequest();
            showLoadingDialog(R.string.data_loading);
        } else {
            Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
        }
    }

    private CenterListPopupWindow.CommonTypeListClickListener propertyListClickListener = new CenterListPopupWindow.CommonTypeListClickListener() {

        @Override
        public void onTypeClick(int position) {
            propertyTypeView.dismiss();
            propertyType = propertyTypeList.get(position);
            sell_type_txt.setText(propertyType.getName());
        }
    };

    private void showPropertyTypeView() {
        if (propertyTypeView == null) {
            propertyTypeView = new CenterListPopupWindow(mContext, propertyListClickListener);
        }
        if (propertyTypeList != null && propertyTypeList.size() > 0) {
            List<String> categoryStr = new ArrayList<String>();
            for (int i = 0; i < propertyTypeList.size(); i++) {
                categoryStr.add(propertyTypeList.get(i).getName());
            }
            propertyTypeView.fillData(categoryStr);
            propertyTypeView.showAsDropDown(title_view);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int code = intent.getIntExtra("code", -1);

        if (code == 100) {
            //图片相册相关
            ArrayList<String> paths = intent.getStringArrayListExtra("paths");
            //添加，去重
            boolean hasUpdate = false;
            for (String path : paths) {
                if (!imagePathList.contains(path)) {

                    if (imagePathList.size() == 8 && "ADD_FLAG".equals(imagePathList.get(imagePathList.size() - 1))) {
                        imagePathList.remove(imagePathList.size() - 1);
                    }

                    //最多9张
                    if (imagePathList.size() == 8) {
                        Utility.showToast(this, "最多可添加8张图片。");
                        break;
                    }
                    imagePathList.add(0, path);
                    hasUpdate = true;
                }
            }

            if (hasUpdate) {
                adapter.notifyDataSetChanged();
            }

        } else if (code == 200) {
            //搜索小区相关
            area = (CommonType) intent.getExtras().getSerializable("area");
            sell_street_txt.setText(area.getName());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_CAMERA:
                if (!tempPhotoFile.exists()) {
                    return;
                }
                if (!imagePathList.contains(tempPhotoFile.getAbsolutePath())) {

                    if (imagePathList.size() == 8 && "ADD_FLAG".equals(imagePathList.get(imagePathList.size()-1))) {
                        imagePathList.remove(imagePathList.size()-1);
                    }

                    //最多9张
                    if (imagePathList.size() == 8) {
                        Utility.showToast(this, "最多可添加8张图片。");
                        break;
                    }
                    imagePathList.add(0, tempPhotoFile.getAbsolutePath());
                    adapter.notifyDataSetChanged();
                }

                break;
        }
    }


    /*****************************
     * 发布图片
     **********************************/

    public static final int PHOTO_CAMERA = 0x1;
    File tempPhotoFile = null;
    //启动拍照
    private void camera() {
        tempPhotoFile = new File(StorageUtils.getCacheDirectory(mContext),
                "XKP" + System.currentTimeMillis() + ".jpg");
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempPhotoFile));
        startActivityForResult(intent, PHOTO_CAMERA);

        hidePhotoDialog();
    }


    private int photos = 0;
    private ArrayList<String> photoUrls = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Logger.d("", "图片上传失败 == ");
                    break;

                case 1:
                    photoUrls.add((String) msg.obj);
                    Logger.d("", "图片地址 == " + (String) msg.obj);
                    break;
            }
            photos++;
            if (photos == imageCachePathList.size()) {
                hideLoadingDialog();
                if (photoUrls.size() == 0) {
                    Toast.makeText(mContext, "图片上传失败", Toast.LENGTH_SHORT).show();
                    commitForm();
                } else {
                    Toast.makeText(mContext, "图片上传完成， 上传成功" + photoUrls.size() +
                            "张;上传失败" + (photos - photoUrls.size()) + "张", Toast.LENGTH_SHORT).show();
                    commitForm();
                }
            }

        }
    };


    private void commitPhotos() {
        photoUrls.clear();
        photos = 0;
        showLoadingDialog("上传图片中");
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeCommitBitmaps();
            }
        }).start();

    }

    private void commitPhoto(File file) {

//        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//
//        RequestBody requestBody = new MultipartBuilder()
//                .type(MultipartBuilder.FORM)
//                .addPart(Headers.of(
//                        "Content-Disposition",
//                        "form-data; name=\"upfile\"; filename=\"a.jpg\""), fileBody)
//                .build();
//
//        Request request = new Request.Builder()
//                .url(Constants.UPLOAD_PHOTO_URL)
//                .post(requestBody)
//                .build();
//
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Message message = new Message();
//                message.what = 0;
//                mHandler.sendMessage(message);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                parse(response.body().string());
//            }
//        });
    }

    private void parse(String result) {
        if (StringUtil.isEmpty(result)) {
            return;
        }

        try {
            if (result != null && result.startsWith("\ufeff")) {
                result = result.substring(1);
            }

            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject != null) {
                if ("SUCCESS".equals(jsonObject.optString("state"))) {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = "http://upload.xkhouse.com" + jsonObject.optString("url");
                    mHandler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Handler bitmapHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 111:
                    for (int i = 0; i < imageCachePathList.size(); i++) {
                        File file = new File(imageCachePathList.get(i));
                        if (file.exists()) {
                            commitPhoto(file);
                        }
                    }
                    break;
            }
        }
    };

    private void writeCommitBitmaps() {
        String sdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Properties properties = BaseConfig.getInstance().getConfig();
        String path = sdcardDir + File.separator + properties.getProperty("imagepath")
                + File.separator + "photoCache";
        FileUtil.createFolder(path);

        //先清除之前缓存
        clearBitmaopCache();

        for (int i = 0; i < newPhotos.size(); i++) {
            String fileName = path + File.separator + System.currentTimeMillis() + ".xkp";
            try {
                saveMyBitmap(fileName, compressImageFromFile(newPhotos.get(i)));
            } catch (Exception e) {
                Logger.e("", "图片压缩失败");
            }
        }

        //获取压缩后图片地址
        imageCachePathList = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                imageCachePathList.add(files[i].getAbsolutePath());
            }
        }

        bitmapHandler.sendEmptyMessage(111);
    }

    //清除缓存
    private void clearBitmaopCache() {
        String sdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Properties properties = BaseConfig.getInstance().getConfig();
        String path = sdcardDir + File.separator + properties.getProperty("imagepath")
                + File.separator + "photoCache";
        File file = new File(path);
        if (file.exists()) {
            File files[] = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
    }


    //图片压缩
    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 600f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return bitmap;//      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
    }


    //将bitmap写成文件
    public void saveMyBitmap(String fileNmae, Bitmap mBitmap) throws IOException {
        File f = new File(fileNmae);
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            if(fitmentView != null && fitmentView.isShowing()){
                fitmentView.dismiss();
                return true;
            }
            if(orientationView != null && orientationView.isShowing()){
                orientationView.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // 6.0 权限处理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CAMERA_REQUEST_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camera();

            } else {
                //没有取得权限
                if(photoDialog != null) photoDialog.dismiss();
                Snackbar.make(title_view, "星房惠没有取得拍照权限，请在设置>应用管理中获取！",
                        Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri packageURI = Uri.parse("package:" + "com.xkhouse.fang");
                        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        startActivity(intent);
                    }
                }).show();
            }
        }else if(requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE){
        //相册权限
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Intent photoIntent = new Intent(SellReleaseEditActivity.this, PhotoWallActivity.class);
            Bundle photoData = new Bundle();
            photoData.putInt("activity", PhotoWallActivity.SELL_EDIT_ACTIVITY);
            photoData.putInt("maxCount", 9-imagePathList.size());
            photoIntent.putExtras(photoData);
            startActivity(photoIntent);
            hidePhotoDialog();

        } else {
            //没有取得权限
            if(photoDialog != null) photoDialog.dismiss();
            Snackbar.make(title_view, "星房惠没有取得读取SD卡数据权限，请在设置>应用管理中获取！",
                    Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Uri packageURI = Uri.parse("package:" + "com.xkhouse.fang");
                    Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                    startActivity(intent);
                }
            }).show();
        }
    }
    }

}
