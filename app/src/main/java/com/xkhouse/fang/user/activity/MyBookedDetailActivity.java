package com.xkhouse.fang.user.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.booked.activity.BookedMakeActivity;
import com.xkhouse.fang.booked.task.AddressEditRequest;
import com.xkhouse.fang.booked.task.MyBookedEditRequest;
import com.xkhouse.fang.user.adapter.MyBookedAdapter;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.entity.MyBookedDetail;
import com.xkhouse.fang.user.entity.MyBookedInfo;
import com.xkhouse.fang.user.task.MessageDetailListRequest;
import com.xkhouse.fang.user.task.MyBookedDetailRequest;
import com.xkhouse.fang.widget.TimePickerDialog;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
 * 预定详情
 */
public class MyBookedDetailActivity extends AppBaseActivity implements TimePickerDialog.TimePickerDialogInterface{
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	

    private ScrollView content_lay;
    private ImageView icon_iv;
    private TextView title_txt;
    private TextView money_txt;
    private LinearLayout label_lay;
    private TextView name_people_txt;
    private TextView phone_txt;
    private TextView pay_money_txt;
    private TextView time_txt;
    private TextView cancel_txt;
    private TextView edit_txt;

    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

    private LinearLayout.LayoutParams lps;
    private DisplayImageOptions options;

    private MyBookedDetail myBookedDetail;

    private MyBookedInfo myBookedInfo;

    private String use_time;
    private TimePickerDialog mTimePickerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.leftMargin = DisplayUtil.dip2px(this, 3);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nopic)   // 加载的图片
                .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
                .showImageForEmptyUri(R.drawable.nopic)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();

        startDataTask();
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_my_booked_detail);
	}

	@Override
	protected void init() {
		super.init();
        myBookedInfo = (MyBookedInfo) getIntent().getExtras().getSerializable("myBookedInfo");
	}

	@Override
	protected void findViews() {
		initTitle();

        content_lay = (ScrollView) findViewById(R.id.content_lay);
        icon_iv = (ImageView) findViewById(R.id.icon_iv);
        title_txt = (TextView) findViewById(R.id.title_txt);
        money_txt = (TextView) findViewById(R.id.money_txt);
        label_lay = (LinearLayout) findViewById(R.id.label_lay);
        name_people_txt = (TextView) findViewById(R.id.name_people_txt);
        phone_txt = (TextView) findViewById(R.id.phone_txt);
        pay_money_txt = (TextView) findViewById(R.id.pay_money_txt);
        time_txt = (TextView) findViewById(R.id.time_txt);
        cancel_txt = (TextView) findViewById(R.id.cancel_txt);
        edit_txt = (TextView) findViewById(R.id.edit_txt);


        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);

	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("预定详情");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {
        error_lay.setOnClickListener(this);
        cancel_txt.setOnClickListener(this);
        edit_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){
            case R.id.error_lay:
                startDataTask();
                break;

            case R.id.cancel_txt:
                startEditTask("1O");
                break;

            case R.id.edit_txt:
                mTimePickerDialog = new TimePickerDialog(MyBookedDetailActivity.this);
                mTimePickerDialog.showDateAndTimePickerDialog();
                break;
        }
	}
	
	
	private void fillData(){
        if (myBookedDetail == null) return;

        ImageLoader.getInstance().displayImage(myBookedDetail.getCover_banner(), icon_iv, options);
        title_txt.setText(myBookedDetail.getBusiness_name());
        money_txt.setText("¥"+ myBookedDetail.getAverage_consump() + "/人");

//        String[] labels = myBookedDetail.getbu();
//        if (labels != null && labels.length > 0) {
//            label_lay.setVisibility(View.VISIBLE);
//            label_lay.removeAllViews();
//            for(String label : labels){
//                TextView textView = new TextView(this);
//                textView.setPadding(DisplayUtil.dip2px(this, 3),
//                        DisplayUtil.dip2px(this, 2),
//                        DisplayUtil.dip2px(this, 3),
//                        DisplayUtil.dip2px(this, 2));
//                textView.setTextColor(this.getResources().getColor(R.color.common_gray_txt));
//                textView.setTextSize(12);
//                textView.setBackground(this.getResources().getDrawable(R.drawable.gray_border_btn_bg));
//                textView.setText(label);
//                label_lay.addView(textView, lps);
//            }
//        }else{
//            label_lay.setVisibility(View.GONE);
//        }

        name_people_txt.setText(myBookedDetail.getMember_name() + "    " + myBookedDetail.getPeople_num() + "人");
        phone_txt.setText("手机号码：" + myBookedDetail.getMember_phone());
        pay_money_txt.setText("预定金额：" + myBookedDetail.getMoney());
        time_txt.setText("预定日期：" + myBookedDetail.getUse_time());

	}


	private void startDataTask() {
        if (NetUtil.detectAvailable(mContext)) {

            MyBookedDetailRequest myBookedDetailRequest = new MyBookedDetailRequest(modelApp.getUser().getToken(),
                    myBookedInfo.getOrder_number(),
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            rotate_loading.stop();
                            rotate_loading.setVisibility(View.GONE);

                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                case Constants.NO_DATA_FROM_NET:
                                    content_lay.setVisibility(View.GONE);
                                    error_lay.setVisibility(View.VISIBLE);
                                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    content_lay.setVisibility(View.VISIBLE);
                                    error_lay.setVisibility(View.GONE);
                                    myBookedDetail = (MyBookedDetail) message.getData().getSerializable("myBookedDetail");

                                    fillData();

                                    break;
                            }
                        }
                    });
            myBookedDetailRequest.doRequest();

        }else {
            content_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }
    }



    RequestListener requestListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            hideLoadingDialog();
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                    break;

                case Constants.NO_DATA_FROM_NET:
                    Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };

    private MyBookedEditRequest  editRequest;

    private void startEditTask(String type) {
        if(NetUtil.detectAvailable(mContext)){
            if(editRequest == null){
                editRequest = new MyBookedEditRequest(modelApp.getUser().getToken(), myBookedInfo.getOrder_number(),
                        type, use_time, requestListener);
            }else {
                editRequest.setData(modelApp.getUser().getToken(), myBookedInfo.getOrder_number(),
                        type, use_time);
            }
            showLoadingDialog("处理中...");
            editRequest.doRequest();

        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void positiveListener() {
        int year = mTimePickerDialog.getYear();
        int hour = mTimePickerDialog.getHour();
        int minute = mTimePickerDialog.getMinute();
        int month = mTimePickerDialog.getMonth();
        int day = mTimePickerDialog.getDay();
        use_time = timeFormat(year) + "-" + timeFormat(month) + "-" + timeFormat(day)
                + "    " + timeFormat(hour) + ":" + timeFormat(minute);
        startEditTask("2");
    }

    @Override
    public void negativeListener() {

    }

    private String timeFormat(int date) {
        if (date < 10) {
            return "0" + date;
        }else {
            return date+"";
        }
    }
}
