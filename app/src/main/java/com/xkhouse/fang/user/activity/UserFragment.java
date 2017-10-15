package com.xkhouse.fang.user.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseFragment;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.task.UserInfoRequest;
import com.xkhouse.fang.widget.circle.CircleImageView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description:  个人中心
 * @author wujian  
 * @date 2015-8-25 下午4:28:25  
 */
public class UserFragment extends AppBaseFragment implements OnClickListener{
	
	
	private View rootView;

    private ImageView setting_iv;		//设置

	private LinearLayout user_lay;
	private CircleImageView user_icon_iv;
	private TextView username_txt;

	//余额
    private LinearLayout money_lay;
    private TextView money_num_txt;

    //抽奖机会
    private LinearLayout luck_changes_lay;
    private TextView changes_num_txt;

    //我是员工
    private TextView employee_txt;

	private LinearLayout booked_lay;		//我的预定
	private LinearLayout luck_lay;		    //我的抽奖
	private LinearLayout check_lay;		    //我的买单
	private LinearLayout comment_lay;	    //我的评论
	private LinearLayout favorite_lay;	    //我的收藏
	private LinearLayout message_lay;	    //我的消息
	private LinearLayout browse_lay;	    //我的浏览

    private TextView server_call_txt;


	private DisplayImageOptions options;
	
	private ModelApplication modelApp;


    private String DEVICE_ID;      //设备ID
    private int READ_PHONE_STATE_REQUEST_CODE = 100;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_user, container, false);
		
		init();
		
		findViews();
		setListeners();

		return rootView;
	}

	private void init(){
		modelApp = (ModelApplication) getActivity().getApplication();
		
		options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.user_sethead_default)   
        .showImageOnFail(R.drawable.user_sethead_default) 
        .showImageForEmptyUri(R.drawable.user_sethead_default)
        .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
        .cacheOnDisk(true).build();

//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions( new String[]{Manifest.permission.READ_PHONE_STATE},
//                        READ_PHONE_STATE_REQUEST_CODE);
//
//        } else {
//            TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//            DEVICE_ID = tm.getDeviceId();
//        }
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		
		if(!hidden){
			refreshView();
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		refreshView();
	}
	
	
	
	private void setListeners() {
        setting_iv.setOnClickListener(this);

		user_lay.setOnClickListener(this);

        money_lay.setOnClickListener(this);
        luck_changes_lay.setOnClickListener(this);

        employee_txt.setOnClickListener(this);

        booked_lay.setOnClickListener(this);
        luck_lay.setOnClickListener(this);
        check_lay.setOnClickListener(this);
		comment_lay.setOnClickListener(this);
        favorite_lay.setOnClickListener(this);
        message_lay.setOnClickListener(this);
        browse_lay.setOnClickListener(this);

        server_call_txt.setOnClickListener(this);
	}
	
	@SuppressLint("CutPasteId")
	private void findViews() {
		user_lay = (LinearLayout) rootView.findViewById(R.id.user_lay);
		user_icon_iv = (CircleImageView) rootView.findViewById(R.id.user_icon_iv);
		username_txt = (TextView) rootView.findViewById(R.id.username_txt);

        money_lay = (LinearLayout) rootView.findViewById(R.id.money_lay);
        money_num_txt = (TextView) rootView.findViewById(R.id.money_num_txt);

        luck_changes_lay = (LinearLayout) rootView.findViewById(R.id.luck_changes_lay);
        changes_num_txt = (TextView) rootView.findViewById(R.id.changes_num_txt);

        employee_txt = (TextView) rootView.findViewById(R.id.employee_txt);

        booked_lay = (LinearLayout) rootView.findViewById(R.id.booked_lay);
        luck_lay = (LinearLayout) rootView.findViewById(R.id.luck_lay);
        check_lay = (LinearLayout) rootView.findViewById(R.id.check_lay);
        comment_lay = (LinearLayout) rootView.findViewById(R.id.comment_lay);
        favorite_lay = (LinearLayout) rootView.findViewById(R.id.favorite_lay);
        message_lay = (LinearLayout) rootView.findViewById(R.id.message_lay);
        browse_lay = (LinearLayout) rootView.findViewById(R.id.browse_lay);

        setting_iv = (ImageView) rootView.findViewById(R.id.setting_iv);
        server_call_txt = (TextView) rootView.findViewById(R.id.server_call_txt);

    }
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.user_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), UserInfoActivity.class));
                }else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;

            case R.id.money_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), AccountInfoListActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", AccountInfoListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.luck_changes_lay:
                startActivity(new Intent(getActivity(), LuckChangeListActivity.class));
                break;

            case R.id.employee_txt:
                startActivity(new Intent(getActivity(), EmployeeInfoActivity.class));
                break;


            case R.id.booked_lay:
                startActivity(new Intent(getActivity(), PayActivity.class));
//                if(Preference.getInstance().readIsLogin()){
//                    startActivity(new Intent(getActivity(), MyBookedListActivity.class));
//                }else {
//                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    intent.putExtra("classStr", MyBookedListActivity.class);
//                    startActivity(intent);
//                }
                break;

            case R.id.luck_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), MyLuckListActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", MyLuckListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.check_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), MyCheckListActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", MyCheckListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.comment_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), MyCommentListActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", MyCommentListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.favorite_lay:
                if(Preference.getInstance().readIsLogin()){
                    Intent intent = new Intent(getActivity(), FavAndBrowseActivity.class);
                    Bundle favBundle = new Bundle();
                    favBundle.putInt("type", FavAndBrowseActivity.TYPE_FAV);
                    intent.putExtras(favBundle);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", FavAndBrowseActivity.class);
                    Bundle favBundle = new Bundle();
                    favBundle.putInt("type", FavAndBrowseActivity.TYPE_FAV);
                    intent.putExtras(favBundle);
                    startActivity(intent);
                }
                break;

            case R.id.message_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), MessageListActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", MessageListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.browse_lay:
                if(Preference.getInstance().readIsLogin()){
                    Intent intent = new Intent(getActivity(), FavAndBrowseActivity.class);
                    Bundle broBundle = new Bundle();
                    broBundle.putInt("type", FavAndBrowseActivity.TYPE_BROWSE);
                    intent.putExtras(broBundle);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", FavAndBrowseActivity.class);
                    Bundle broBundle = new Bundle();
                    broBundle.putInt("type", FavAndBrowseActivity.TYPE_BROWSE);
                    intent.putExtras(broBundle);
                    startActivity(intent);
                }
                break;

            case R.id.setting_iv:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;

            case R.id.server_call_txt:
                // 检查是否获得了权限（Android6.0运行时权限）
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(getActivity(), "请授权！", Toast.LENGTH_LONG).show();

                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent callIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                        callIntent.setData(uri);
                        startActivity(callIntent);
                    }else{
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                }else {
                    // 已经获得授权，可以打电话
                    CallPhone();
                }
            break;
		}
	}
	
	
	public void refreshView() {
		
		if(Preference.getInstance().readIsLogin()){
			if(StringUtil.isEmpty(modelApp.getUser().getNickname())){
				username_txt.setText(modelApp.getUser().getNickname());
			}else {
				username_txt.setText(modelApp.getUser().getNickname());
			}
			ImageLoader.getInstance().displayImage(modelApp.getUser().getHead_img(),
					user_icon_iv, options);
            if ("1".equals(modelApp.getUser().getIs_staff())) {
                employee_txt.setVisibility(View.VISIBLE);
            }else {
                employee_txt.setVisibility(View.GONE);
            }

		}else {
			username_txt.setText("您还没有登录哦~");
			ImageLoader.getInstance().displayImage("", user_icon_iv, options);
            employee_txt.setVisibility(View.GONE);
		}
		
		getCount();
	}	
	

    private void getCount() {

        if(!Preference.getInstance().readIsLogin()){
            money_num_txt.setText("--");
            changes_num_txt.setText("--");
            return;
        }

        if(NetUtil.detectAvailable(getContext())){
            UserInfoRequest request = new UserInfoRequest(modelApp.getUser().getToken(), new RequestListener() {

                @Override
                public void sendMessage(Message message) {
                    hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                        case Constants.NO_DATA_FROM_NET:
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            User user = (User) message.obj;
                            modelApp.setUser(user);
                            money_num_txt.setText(user.getAccount_balance());
                            changes_num_txt.setText(user.getActivity_num());
                            if(StringUtil.isEmpty(modelApp.getUser().getNickname())){
                                username_txt.setText(modelApp.getUser().getNickname());
                            }else {
                                username_txt.setText(modelApp.getUser().getNickname());
                            }
                            ImageLoader.getInstance().displayImage(modelApp.getUser().getHead_img(),
                                    user_icon_iv, options);
                            if ("1".equals(modelApp.getUser().getIs_staff())) {
                                employee_txt.setVisibility(View.VISIBLE);
                            }else {
                                employee_txt.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
            });
            request.doRequest();

        }else {
            changes_num_txt.setText(modelApp.getUser().getActivity_num());
            money_num_txt.setText(modelApp.getUser().getAccount_balance());
        }
    }




    private void CallPhone() {
        Intent intent = new Intent(); // 意图对象：动作 + 数据
        intent.setAction(Intent.ACTION_CALL); // 设置动作
        Uri data = Uri.parse("tel:400-888-8888"); // 设置数据
        intent.setData(data);
        startActivity(intent); // 激活Activity组件
    }

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    // 处理权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    CallPhone();
                } else {
                    // 授权失败！
                    Toast.makeText(getActivity(), "授权失败！", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }

}
