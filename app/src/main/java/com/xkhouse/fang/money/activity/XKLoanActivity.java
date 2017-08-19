package com.xkhouse.fang.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.app.task.CommonConfigRequest;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.money.task.XKLoanCommitRequest;
import com.xkhouse.fang.money.view.CenterListPopupWindow;
import com.xkhouse.fang.money.view.CenterListPopupWindow.CommonTypeListClickListener;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
* @Description: 星空贷 
* @author wujian  
* @date 2015-10-12 下午3:31:54
 */
public class XKLoanActivity extends AppBaseActivity {

	private View title_view;
	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;
	
	
	private EditText name_txt;
	private EditText phone_txt;
	private EditText code_txt;
	private TextView get_code_txt;
	private EditText count_txt;
	private TextView time_txt;
	private TextView city_txt;
	private TextView city_name_txt;
    private LinearLayout project_lay;
	private TextView project_name_txt;
	private EditText project_txt;
	private EditText remark_txt;
	private TextView commit_txt;

	public static final int XIN_YONG_DAI = 1;    //信用贷
	public static final int XIANG_MU_DAI = 2;    //项目贷
	public static final int ZHUANG_XIU_DAI = 3;    //装修贷
	public static final int DI_YA_DAI = 4;    //抵押贷
	public static final int XIAO_WEI_DAI = 5;    //小微贷

	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	private XKLoanCommitRequest commitRequest;
	//参数
	private int type = XIN_YONG_DAI;
	private String userName;
	private String phone;
	private String verifyCode;
	private String money;
	private String deadline;
	private String siteName;
	private String projectName;
	private String content;
	
	//贷款期限
	private CommonConfigRequest configRequest;
	private ArrayList<CommonType> typeList = new ArrayList<CommonType>(); 
	private String configType = "";
	private CenterListPopupWindow timeTypeView;

    public static final int RESULT_CODE = 101;
    public static final int RESULT_CODE_CITY = 102;
    public static final int REQUEST_CODE = 100;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(type == XIN_YONG_DAI){
			city_name_txt.setText("购房城市");
			project_name_txt.setText("意向楼盘");
            city_txt.setHint("请选择购房城市");
            project_txt.setHint("请选择您的意向楼盘");
			configType = "LOAN_XINYONG_TIME";

		}else if(type == XIANG_MU_DAI){
			city_name_txt.setText("所在城市");
			project_name_txt.setText("抵押楼盘");
            city_txt.setHint("请选择所在城市");
            project_txt.setHint("请选择您的抵押楼盘");
			configType = "LOAN_XIANGMU_TIME";

		}else if(type == ZHUANG_XIU_DAI){
            city_name_txt.setText("所在城市");
            city_txt.setHint("请选择所在城市");
            project_lay.setVisibility(View.GONE);
            configType = "LOAN_ZHUANGXIU_TIME";

        }else if(type == DI_YA_DAI){
            city_name_txt.setText("所在城市");
            city_txt.setHint("请选择所在城市");
            project_lay.setVisibility(View.GONE);
            configType = "LOAN_DIYA_TIME";

        }else if(type == XIAO_WEI_DAI){
            city_name_txt.setText("所在城市");
            city_txt.setHint("请选择所在城市");
            project_lay.setVisibility(View.GONE);
            configType = "LOAN_XIAOWEI_TIME";
        }

	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_loan);
	}

	@Override
	protected void init() {
		super.init();
		type = getIntent().getExtras().getInt("type");
	}

    @Override
	protected void findViews() {
		initTitle();
		
		name_txt = (EditText) findViewById(R.id.name_txt);
		phone_txt = (EditText) findViewById(R.id.phone_txt);
		code_txt = (EditText) findViewById(R.id.code_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		count_txt = (EditText) findViewById(R.id.count_txt);
		time_txt = (TextView) findViewById(R.id.time_txt); 
		city_txt = (TextView) findViewById(R.id.city_txt); 
		city_name_txt = (TextView) findViewById(R.id.city_name_txt);
        project_lay = (LinearLayout) findViewById(R.id.project_lay);
        project_name_txt = (TextView) findViewById(R.id.project_name_txt);
		project_txt = (EditText) findViewById(R.id.project_txt);
		remark_txt = (EditText) findViewById(R.id.remark_txt);
		commit_txt = (TextView) findViewById(R.id.commit_txt);
	}

	private void initTitle() {
		title_view = findViewById(R.id.title_view);
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		tv_head_title.setText("我要贷款");
		tv_head_right.setText("重置");
		tv_head_right.setVisibility(View.VISIBLE);
		
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
		
		tv_head_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				resetData();
			}
		});
	}
	
	@Override
	protected void setListeners() {
		get_code_txt.setOnClickListener(this);
		time_txt.setOnClickListener(this);
		commit_txt.setOnClickListener(this);
		city_txt.setOnClickListener(this);

	}

	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		
		case R.id.get_code_txt:
			startVerityCodeTask();
			break;

            case R.id.city_txt:
                Intent cityIntent = new Intent(mContext, XKBCitySelectActivity.class);
                Bundle location = new Bundle();
                location.putString("city",  null);
                cityIntent.putExtras(location);
                startActivityForResult(cityIntent, REQUEST_CODE);
                break;
			
		case R.id.time_txt:
			startTimeTypeTask();
			break;
					
		case R.id.commit_txt:
			startCommitTask();
			break;
		
		}
	}
	
	
	
	private void resetData() {
		name_txt.setText("");
		phone_txt.setText("");
		code_txt.setText("");
		count_txt.setText("");
		time_txt.setText("");
		deadline = "";
		city_txt.setText("");
		project_txt.setText("");
		remark_txt.setText("");
	}
	
	/*********************** 提交表单   ***********************/
	private void startCommitTask(){
		
		userName = name_txt.getText().toString();
		phone = phone_txt.getText().toString();
		verifyCode = code_txt.getText().toString();
		siteName = city_txt.getText().toString();
		money = count_txt.getText().toString();
		projectName = project_txt.getText().toString();
		content = remark_txt.getText().toString();
		
		//校验数据		
		if(StringUtil.isEmpty(userName)){
			Toast.makeText(mContext, "您的姓名忘记填写了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(phone)){
			Toast.makeText(mContext, "您的手机号忘记填写了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(verifyCode)){
			Toast.makeText(mContext, "您的验证码忘记填写了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(siteName)){
			Toast.makeText(mContext, "您的城市忘记填写了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(money)){
			Toast.makeText(mContext, "您的贷款金额忘记填写了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(deadline)){
			Toast.makeText(mContext, "您的贷款期限忘记填写了", Toast.LENGTH_SHORT).show();
			return;
		}
        if(type == XIN_YONG_DAI && StringUtil.isEmpty(projectName)){
            Toast.makeText(mContext, "您的意向楼盘忘记填写了", Toast.LENGTH_SHORT).show();
            return;
        }else if(type == XIANG_MU_DAI && StringUtil.isEmpty(projectName)){
            Toast.makeText(mContext, "您的抵押楼盘忘记填写了", Toast.LENGTH_SHORT).show();
            return;
        }


		if(NetUtil.detectAvailable(mContext)){
			if(commitRequest == null){
				commitRequest = new XKLoanCommitRequest(userName, phone, verifyCode,
					 siteName, money, deadline,
					 projectName, content, type, new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
							Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.NO_DATA_FROM_NET:
							String msg = (String) message.obj;
							Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							String msgs = (String) message.obj;
							Toast.makeText(mContext, msgs, Toast.LENGTH_SHORT).show();
							finish();
							break;
						}
					}
				});
			}else {
				commitRequest.setData(userName, phone, verifyCode,
						 siteName, money, deadline,
						 projectName, content, type);
			}
			
			showLoadingDialog("表单提交中...");
			commitRequest.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	

	private CommonTypeListClickListener categoryListClickListener = new CommonTypeListClickListener() {
		
		@Override
		public void onTypeClick(int position) {
			timeTypeView.dismiss();
			time_txt.setText(typeList.get(position).getName());
			deadline = typeList.get(position).getId();
		}
	};
	
	private void showTimeTypeView() {
		if(!this.isFinishing()){
			if(typeList == null || typeList.size() < 1) return;
			
			if(timeTypeView == null){
				timeTypeView = new CenterListPopupWindow(mContext, categoryListClickListener);
			}
			
			List<String> timeList = new ArrayList<String>();
			for(CommonType type : typeList){
				timeList.add(type.getName());
			}
			
			timeTypeView.fillData(timeList);
			timeTypeView.showAsDropDown(title_view);
		}
	}
	
	//贷款期限
	private RequestListener typeListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			hideLoadingDialog();
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.NO_DATA_FROM_NET:
				Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<CommonType> temp  = (ArrayList<CommonType>) message.obj;
				if(typeList != null){
					typeList.clear();
				}else{
					typeList = new ArrayList<CommonType>();
				}
				typeList.addAll(temp);
				
				showTimeTypeView();
				break;
			}
		}
	};
		
	private void startTimeTypeTask() {
		if(typeList != null && typeList.size() > 0) {
			showTimeTypeView();
			return;
		}
		
		if(NetUtil.detectAvailable(mContext)){
			if(configRequest == null){
				configRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), configType, typeListener);
			}
			showLoadingDialog(R.string.data_loading);
			configRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	
	/*********************  获取验证码    ****************************/
	
	private void startVerityCodeTask() {
		phone = phone_txt.getText().toString();
		if(StringUtil.isEmpty(phone)){
			Toast.makeText(mContext, "请填写手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(NetUtil.detectAvailable(mContext)){
			if(codeRequest == null){
				codeRequest = new GetVerifyCodeRequest(phone, new RequestListener() {
                    @Override
                    public void sendMessage(Message message) {
                        switch (message.what){
                            case Constants.ERROR_DATA_FROM_NET:
                                Toast.makeText(mContext, R.string.sms_code_error, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                Toast.makeText(mContext, (String)message.obj, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                Toast.makeText(mContext, (String)message.obj, Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });
			}else {
				codeRequest.setData(phone);
			}
			codeRequest.doRequest();
			duration = 60;
			if(timer !=null){
				timer.cancel();
				timer= null;
			}
			timer = new Timer();
			timer.schedule(new MyTimerTask(), 0, 1000);
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
		
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			duration = duration - 1;
			runOnUiThread(new Runnable() {
				public void run() {
					if(duration < 0 ){
						get_code_txt.setClickable(true);
						get_code_txt.setBackgroundResource(R.drawable.green_corner_btn_bg);
						get_code_txt.setTextColor(getResources().getColor(R.color.white));
						get_code_txt.setText("获取验证码");
					}else{
						get_code_txt.setClickable(false);
						get_code_txt.setBackgroundResource(R.drawable.gray_corner_btn_bg);
						get_code_txt.setTextColor(getResources().getColor(R.color.common_gray_txt));
						get_code_txt.setText("获取验证码("+duration+")");
					}
				}
			});
			
		}
	}



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //选择的期望楼盘
        if (requestCode == REQUEST_CODE) {

            if(resultCode == RESULT_CODE_CITY){
                Site site = (Site) data.getExtras().getSerializable("site");
                city_txt.setText(site.getArea());
            }
        }

    }
	
}
