package com.xkhouse.fang.user.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
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
import com.xkhouse.fang.app.activity.CalculatorActivity;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.house.activity.BuyAbilityActivity;
import com.xkhouse.fang.house.activity.MyCustomHouseListActivity;
import com.xkhouse.fang.user.task.MessageReadRequest;
import com.xkhouse.fang.user.task.MsgFavoriteNumRequest;
import com.xkhouse.fang.user.task.WalletListRequest;
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
	
	private LinearLayout user_lay;
	private CircleImageView user_icon_iv;
	private TextView username_txt;

	//消息
    private LinearLayout message_lay;
    private TextView message_num_txt;
    private ImageView message_iv;
	
	//钱包
	private LinearLayout wallet_lay;
	private TextView wallet_txt;
	
	//收藏
	private LinearLayout favorite_lay;
    private TextView favorite_num_txt;

	private LinearLayout recommend_lay;		//我的推荐
	private LinearLayout custom_lay;		//我的定制
	private LinearLayout release_lay;		//我的发布
	private LinearLayout calculator_lay;	//贷款计算器
	private LinearLayout house_baike_lay;	//购房百科
	private LinearLayout buy_ability_lay;	//购房能力评估
	private LinearLayout zuolin_lay;	    //左邻右里
	private LinearLayout setting_lay;		//设置

    private TextView server_call_txt;
	
	private DisplayImageOptions options;
	
	private ModelApplication modelApp;
	
	private MsgFavoriteNumRequest msgNumRequest;	//消息条数
	private MsgFavoriteNumRequest favoriteNumRequest;	//我的收藏
	private WalletListRequest walletRequest;		//我的钱包

    private String DEVICE_ID;      //设备ID
    private int READ_PHONE_STATE_REQUEST_CODE = 100;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_user, container, false);
		
		init();
		
		findViews();
		setListeners();

        startMessageReadTask();

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

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( new String[]{Manifest.permission.READ_PHONE_STATE},
                        READ_PHONE_STATE_REQUEST_CODE);

        } else {
            TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            DEVICE_ID = tm.getDeviceId();
        }
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
		user_lay.setOnClickListener(this);

        message_lay.setOnClickListener(this);
		wallet_lay.setOnClickListener(this);
		favorite_lay.setOnClickListener(this);
		
		recommend_lay.setOnClickListener(this);
        custom_lay.setOnClickListener(this);
		release_lay.setOnClickListener(this);
		calculator_lay.setOnClickListener(this);
        house_baike_lay.setOnClickListener(this);
		buy_ability_lay.setOnClickListener(this);
        zuolin_lay.setOnClickListener(this);
		setting_lay.setOnClickListener(this);

        server_call_txt.setOnClickListener(this);
	}
	
	@SuppressLint("CutPasteId")
	private void findViews() {
		user_lay = (LinearLayout) rootView.findViewById(R.id.user_lay);
		user_icon_iv = (CircleImageView) rootView.findViewById(R.id.user_icon_iv);
		username_txt = (TextView) rootView.findViewById(R.id.username_txt);

        message_lay = (LinearLayout) rootView.findViewById(R.id.message_lay);
        message_num_txt = (TextView) rootView.findViewById(R.id.message_num_txt);
        message_iv = (ImageView) rootView.findViewById(R.id.message_iv);
		
		wallet_lay = (LinearLayout) rootView.findViewById(R.id.wallet_lay);
		wallet_txt = (TextView) rootView.findViewById(R.id.wallet_txt);

		favorite_lay = (LinearLayout) rootView.findViewById(R.id.favorite_lay);
        favorite_num_txt = (TextView) rootView.findViewById(R.id.favorite_num_txt);

        recommend_lay = (LinearLayout) rootView.findViewById(R.id.recommend_lay);
        custom_lay = (LinearLayout) rootView.findViewById(R.id.custom_lay);
        release_lay = (LinearLayout) rootView.findViewById(R.id.release_lay);
        calculator_lay = (LinearLayout) rootView.findViewById(R.id.calculator_lay);
        house_baike_lay = (LinearLayout) rootView.findViewById(R.id.house_baike_lay);
        buy_ability_lay = (LinearLayout) rootView.findViewById(R.id.buy_ability_lay);
        zuolin_lay = (LinearLayout) rootView.findViewById(R.id.zuolin_lay);
        setting_lay = (LinearLayout) rootView.findViewById(R.id.setting_lay);

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

            case R.id.message_lay:
                message_iv.setVisibility(View.INVISIBLE);
                startActivity(new Intent(getActivity(), MessageCenterActivity.class));
                break;

            case R.id.wallet_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), WalletActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", WalletActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.favorite_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), FavoriteListActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", FavoriteListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.recommend_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), MyRecommendActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", MyRecommendActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.custom_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), MyCustomHouseListActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", MyCustomHouseListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.release_lay:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(getActivity(), MyReleaseActivity.class));
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", MyReleaseActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.calculator_lay:
                startActivity(new Intent(getActivity(), CalculatorActivity.class));
                break;

            case R.id.house_baike_lay:
                break;

            case R.id.buy_ability_lay:
                startActivity(new Intent(getActivity(), BuyAbilityActivity.class));
                break;

            case R.id.zuolin_lay:
                Uri uri = Uri.parse("https://www.nextdoors.com.cn/down.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;

            case R.id.setting_lay:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;

            case R.id.server_call_txt:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:400-887-1216")));
            break;
		}
	}
	
	
	public void refreshView() {
		
		if(Preference.getInstance().readIsLogin()){
			if(StringUtil.isEmpty(modelApp.getUser().getUserName())){
				username_txt.setText(modelApp.getUser().getMobile());
			}else {
				username_txt.setText(modelApp.getUser().getUserName());
			}
			ImageLoader.getInstance().displayImage(modelApp.getUser().getHeadPhoto(), 
					user_icon_iv, options);
		}else {
			username_txt.setText("您还没有登录哦~");
			ImageLoader.getInstance().displayImage("", user_icon_iv, options);
		}
		
		getCount();
	}	
	
	

    private void startMessageReadTask(){
        if (NetUtil.detectAvailable(getActivity())){
            String userId = null;
            if (Preference.getInstance().readIsLogin()){
                userId = modelApp.getUser().getUid();
            }
            MessageReadRequest request = new MessageReadRequest(userId, DEVICE_ID, modelApp.getSite().getSiteId(),
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                case Constants.NO_DATA_FROM_NET:
                                    message_iv.setVisibility(View.INVISIBLE);
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    if(1 == message.arg1){
                                        message_iv.setVisibility(View.VISIBLE);
                                    }else{
                                        message_iv.setVisibility(View.INVISIBLE);
                                    }
                                    break;
                            }
                        }
                    });
            request.doRequest();
        }
    }


    private void getCount() {

        if(!Preference.getInstance().readIsLogin()){
            message_num_txt.setText("--");
            wallet_txt.setText("--");
            favorite_num_txt.setText("--");
            return;
        }

        if(NetUtil.detectAvailable(getActivity())){
            // 消息条数
            if(msgNumRequest == null){
                msgNumRequest = new MsgFavoriteNumRequest(modelApp.getUser().getUid(), "1", new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {
                        hideLoadingDialog();
                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                            case Constants.NO_DATA_FROM_NET:
                                message_num_txt.setText("--");
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                String count = (String) message.obj;
                                message_num_txt.setText(count);
                                break;
                        }
                    }
                });
            }else {
                msgNumRequest.setData(modelApp.getUser().getUid(), "1");
            }
            msgNumRequest.doRequest();

            //收藏条数
            if(favoriteNumRequest == null){
                favoriteNumRequest = new MsgFavoriteNumRequest(modelApp.getUser().getUid(), "2", new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {
                        hideLoadingDialog();
                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                            case Constants.NO_DATA_FROM_NET:
                                favorite_num_txt.setText("--");
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                String count = (String) message.obj;
                                favorite_num_txt.setText(count);
                                break;
                        }
                    }
                });
            }else {
                favoriteNumRequest.setData(modelApp.getUser().getUid(), "2");
            }
            favoriteNumRequest.doRequest();

            //钱包
            if(walletRequest == null){
                walletRequest = new WalletListRequest(modelApp.getUser().getUid(),
                        WalletRecordListActivity.WALLET_ALL, 1, 1,
                        new RequestListener() {

                            @Override
                            public void sendMessage(Message message) {
                                hideLoadingDialog();
                                switch (message.what) {
                                    case Constants.ERROR_DATA_FROM_NET:
                                    case Constants.NO_DATA_FROM_NET:
                                        wallet_txt.setText("--");
                                        break;

                                    case Constants.SUCCESS_DATA_FROM_NET:
                                        Bundle data = message.getData();
                                        if (StringUtil.isEmpty(data.getString("sum"))){
                                            wallet_txt.setText("0.00");
                                        }else {
                                            wallet_txt.setText(data.getString("sum"));
                                        }
                                        break;
                                }
                            }
                        });
            }else {
                walletRequest.setData(modelApp.getUser().getUid(), WalletRecordListActivity.WALLET_ALL, 1, 1);
            }
            walletRequest.doRequest();

        }else {
            message_num_txt.setText("--");
            wallet_txt.setText("--");
            favorite_num_txt.setText("--");
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_PHONE_STATE_REQUEST_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                DEVICE_ID = tm.getDeviceId();

            } else {
                //没有取得权限
            }
        }
    }


}
