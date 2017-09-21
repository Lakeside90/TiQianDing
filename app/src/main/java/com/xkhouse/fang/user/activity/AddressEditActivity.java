package com.xkhouse.fang.user.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.AddressInfo;
import com.xkhouse.fang.booked.task.AddressAddRequest;
import com.xkhouse.fang.booked.task.AddressEditRequest;
import com.xkhouse.fang.user.task.ResetPasswordRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * 新增收货地址
 */
public class AddressEditActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

	private EditText name_txt;
	private EditText phone_txt;
	private TextView city_txt;
	private EditText content_txt;

	private CheckBox check_box;
	private TextView commit_txt;


	private AddressInfo addressInfo;

	private boolean isEdit;

	private AddressAddRequest addRequest;
	private AddressEditRequest editRequest;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isEdit) {
			name_txt.setText(addressInfo.getName());
			phone_txt.setText(addressInfo.getPhone());
			content_txt.setText(addressInfo.getContent());
			if ("1".equals(addressInfo.getIs_selected())) {
				check_box.setChecked(true);
			}else {
				check_box.setChecked(false);
			}
		}
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_address_edit);
	}

	@Override
	protected void init() {
		super.init();
		if (getIntent().getExtras() != null) {
			addressInfo = (AddressInfo) getIntent().getExtras().getSerializable("addressInfo");
		}

		if (addressInfo != null) {
			isEdit = true;
		}
	}

	@Override
	protected void findViews() {
		initTitle();

		name_txt = (EditText) findViewById(R.id.name_txt);
		phone_txt = (EditText) findViewById(R.id.phone_txt);
		city_txt = (TextView) findViewById(R.id.city_txt);
		content_txt = (EditText) findViewById(R.id.content_txt);

		check_box = (CheckBox) findViewById(R.id.check_box);
		commit_txt = (TextView) findViewById(R.id.commit_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		if (isEdit) {
			tv_head_title.setText("编辑地址");
		}else {
			tv_head_title.setText("新增地址");
		}
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {
		city_txt.setOnClickListener(this);
		commit_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.city_txt:

				break;

			case R.id.commit_txt:
				if(!checkData()) return;

				setAddressInfo();

				if (isEdit) {
					startEditTask();
				}else {
					startAddTask();
				}
				break;
		}
	}

	private boolean checkData() {
		if (StringUtil.isEmpty(name_txt.getText().toString()) ||
				StringUtil.isEmpty(phone_txt.getText().toString()) ||
				StringUtil.isEmpty(content_txt.getText().toString())) {
			Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void setAddressInfo() {
		if (addressInfo == null)  addressInfo = new AddressInfo();
		addressInfo.setName(name_txt.getText().toString());
		addressInfo.setPhone(phone_txt.getText().toString());
		addressInfo.setContent(content_txt.getText().toString());
		if (check_box.isChecked()) {
			addressInfo.setIs_selected("1");
		}else {
			addressInfo.setIs_selected("0");
		}
		//TODO test data
		addressInfo.setProvince_id("1");
		addressInfo.setCity_id("1");
		addressInfo.setArea_id("1");
	}


	private void startAddTask() {
		if(NetUtil.detectAvailable(mContext)){
			if(addRequest == null){
				addRequest = new AddressAddRequest(modelApp.getUser().getToken(), addressInfo, requestListener);
			}else {
				addRequest.setData(modelApp.getUser().getToken(), addressInfo);
			}
			showLoadingDialog("处理中...");
			addRequest.doRequest();

		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

	private void startEditTask() {
		if(NetUtil.detectAvailable(mContext)){
			if(editRequest == null){
				editRequest = new AddressEditRequest(modelApp.getUser().getToken(), addressInfo, requestListener);
			}else {
				editRequest.setData(modelApp.getUser().getToken(), addressInfo);
			}
			showLoadingDialog("处理中...");
			editRequest.doRequest();

		}else {
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

}
