package com.xkhouse.fang.booked.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.booked.entity.BookAddInfo;
import com.xkhouse.fang.booked.task.AddressEditRequest;
import com.xkhouse.fang.booked.task.BookedAddRequest;
import com.xkhouse.fang.widget.TimePickerDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Calendar;

/**
 * 在线预定
 */
public class BookedMakeActivity extends AppBaseActivity implements TimePickerDialog.TimePickerDialogInterface{

    private ImageView iv_head_left;
    private TextView tv_head_title;


    private EditText people_num_txt;
    private TextView time_txt;
    private EditText name_txt;
    private RadioGroup radioGroup;
    private EditText phone_txt;
    private EditText content_txt;
    private TextView money_book_txt;
    private TextView money_discount_txt;
    private TextView book_dec_txt;
    private CheckBox check_box;
    private TextView commit_txt;

    private TimePickerDialog mTimePickerDialog;

    private BookedAddRequest request;
    private BookAddInfo bookAddInfo;

    private BookedInfo bookedInfo;
    private String time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookAddInfo = new BookAddInfo();

        fillData();
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_booked_make);
    }

    @Override
    protected void init() {
        super.init();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            bookedInfo = (BookedInfo) data.getSerializable("bookedInfo");
        }
    }

    @Override
    protected void findViews() {
        initTitle();

        people_num_txt = (EditText) findViewById(R.id.people_num_txt);
        time_txt = (TextView) findViewById(R.id.time_txt);
        name_txt = (EditText) findViewById(R.id.name_txt);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        phone_txt = (EditText) findViewById(R.id.phone_txt);
        content_txt = (EditText) findViewById(R.id.content_txt);
        money_book_txt = (TextView) findViewById(R.id.money_book_txt);
        money_discount_txt = (TextView) findViewById(R.id.money_discount_txt);
        book_dec_txt = (TextView) findViewById(R.id.book_dec_txt);
        check_box = (CheckBox) findViewById(R.id.check_box);
        commit_txt = (TextView) findViewById(R.id.commit_txt);

    }


    private void initTitle() {
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("在线预定");
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    @Override
    protected void setListeners() {

        time_txt.setOnClickListener(this);
        book_dec_txt.setOnClickListener(this);
        commit_txt.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) BookedMakeActivity.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                if ("先生".equals(rb.getText())) {
                    bookAddInfo.setGender("1");
                }else {
                    bookAddInfo.setGender("0");
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.time_txt:
                mTimePickerDialog = new TimePickerDialog(BookedMakeActivity.this);
                mTimePickerDialog.showDateAndTimePickerDialog();
                break;

            case R.id.book_dec_txt:

                break;

            case R.id.commit_txt:
                if (checkData()) {
                    getBookData();
                    startTask();
                }
                break;

        }
    }


    private void fillData() {
        money_book_txt.setText(bookedInfo.getPayment());
        money_discount_txt.setText(bookedInfo.getMortgage());
        radioGroup.check(R.id.female_rb);

    }

    private boolean checkData() {

        if (StringUtil.isEmpty(people_num_txt.getText().toString()) ||
                StringUtil.isEmpty(name_txt.getText().toString()) ||
                StringUtil.isEmpty(phone_txt.getText().toString()) ||
                StringUtil.isEmpty(time)) {
            Toast.makeText(mContext, "请填写完整", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!check_box.isChecked()) {
            Toast.makeText(mContext, "请阅读预定须知", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getBookData() {

        bookAddInfo.setCityId(modelApp.getSite().getSiteId());
        bookAddInfo.setMember_name(name_txt.getText().toString());
        bookAddInfo.setMember_phone(phone_txt.getText().toString());
        bookAddInfo.setPeople_num(people_num_txt.getText().toString());
        bookAddInfo.setMember_remarks(content_txt.getText().toString());
        bookAddInfo.setBusiness_id("1");
        bookAddInfo.setBooking_id("1");
        bookAddInfo.setUse_time("2019-09-09 12:12:12");
    }

    private void startTask() {
        if (NetUtil.detectAvailable(mContext)) {
            if (request == null) {
                request = new BookedAddRequest(modelApp.getUser().getToken(), bookAddInfo, requestListener);
            } else {
                request.setData(modelApp.getUser().getToken(), bookAddInfo);
            }
            showLoadingDialog("处理中...");
            request.doRequest();

        } else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
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


    @Override
    public void positiveListener() {
        int hour = mTimePickerDialog.getHour();
        int minute = mTimePickerDialog.getMinute();
        int month = mTimePickerDialog.getMonth();
        int day = mTimePickerDialog.getDay();
        time = String.valueOf(month) + "-" + String.valueOf(day)
                + "-" + String.valueOf(hour) + " : " + String.valueOf(minute);
        time_txt.setText(time);
    }

    @Override
    public void negativeListener() {

    }
}
