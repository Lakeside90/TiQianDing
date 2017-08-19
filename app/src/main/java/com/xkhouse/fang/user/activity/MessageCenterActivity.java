package com.xkhouse.fang.user.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.user.adapter.MessageCenterAdapter;
import com.xkhouse.fang.user.entity.XKMessage;
import com.xkhouse.fang.user.task.MessageListRequest;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
* @Description: 我的消息
* @author wujian  
* @date 2015-10-10 下午2:29:08
 */
public class MessageCenterActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private ListView msg_listview;
	private MessageCenterAdapter adapter;
	private ArrayList<XKMessage> messageList;
	
	private MessageListRequest request;
	
	/*** 每日要闻  **/
	public static final int MSG_NEWS = 1;
	/*** 最新活动  **/
	public static final int MSG_ACTIVITY = 2;
	/*** 系统消息 **/
	public static final int MSG_SYSTEM = 3;

    private String DEVICE_ID;      //设备ID
    private int READ_PHONE_STATE_REQUEST_CODE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_REQUEST_CODE);

        } else {
            //执行获取权限后的操作
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            DEVICE_ID = tm.getDeviceId();
        }

		startDataTask();
	}
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_message);
		
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();
		
		msg_listview = (ListView) findViewById(R.id.msg_listview);
		
		msg_listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                messageList.get(position).setIsRead(false);
                adapter.setData(messageList);

                if (position == 0) {
                    startActivity(new Intent(mContext, MSGNewsListActivity.class));

                } else if (position == 1) {
                    startActivity(new Intent(mContext, MSGActivityListActivity.class));

                } else if (position == 2) {
                    if (Preference.getInstance().readIsLogin()) {
                        startActivity(new Intent(mContext, MSGSystemListActivity.class));
                    } else {
                        Toast.makeText(mContext, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("classStr", MSGSystemListActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
		
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("我的消息");
		iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}
	
	
	private void fillData() {
		if(messageList == null) return;
		
		if(adapter == null){
			adapter = new MessageCenterAdapter(mContext, messageList);
			msg_listview.setAdapter(adapter);
		}else {
			adapter.setData(messageList);
		}
	}
	
	private void startDataTask(){
		if(NetUtil.detectAvailable(mContext)){
			String uid = "";
			if(Preference.getInstance().readIsLogin()){
				uid = modelApp.getUser().getUid();
			}
			if(request == null){
				request = new MessageListRequest(uid, DEVICE_ID, modelApp.getSite().getSiteId(), new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
							Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.NO_DATA_FROM_NET:
							
							Toast.makeText(mContext, "没有最新数据！", Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							ArrayList<XKMessage> temp =  (ArrayList<XKMessage>) message.obj;
							if(messageList != null){
								messageList.clear();
							}else {
								messageList = new ArrayList<XKMessage>();
							}
							messageList.addAll(temp);
							fillData();
							
							break;
						}
					}
				});
			}else {
				request.setData("", DEVICE_ID,modelApp.getSite().getSiteId());
			}
			
			showLoadingDialog(R.string.data_loading);
			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_PHONE_STATE_REQUEST_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                DEVICE_ID = tm.getDeviceId();

            } else {
                //没有取得权限
            }
        }
    }


}
