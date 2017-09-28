package com.xkhouse.fang.app.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.entity.AppUpgrade;
import com.xkhouse.fang.app.task.VersionCheckRequest;
import com.xkhouse.fang.money.activity.TQDFragment;
import com.xkhouse.fang.user.activity.UserFragment;
import com.xkhouse.fang.widget.AppUpdateDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;


/**
 * 首页
 */
public class MainActivity extends FragmentActivity implements OnClickListener{


	private TextView home_tv;
	private TextView cj_tv;
	private TextView tqd_tv;
	private TextView user_tv;

	private HomeFragment homeFragment;
    private CJFragment cjFragment;
	private TQDFragment tqdFragment;
	private UserFragment userFragment;
	
	private Fragment currentFrag = null;

    /**
     * 上一次界面 onSaveInstanceState 之前的tab被选中的状态 key 和 value
     */
    private static final  String PRV_SELINDEX="PREV_SELINDEX";
    private int selindex=0;
    /**
     * Fragment的TAG 用于解决app内存被回收之后导致的fragment重叠问题
     */
    private static final  String[] FRAGMENT_TAG = {"homeFragment","cjFragment","tqdFragment","userFragment"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initPush();
		findViews();
		setListeners();

        //注意Fragment重叠问题，其实是由Activity被回收后重启所导致的Fragment重复创建和重叠的问题。
        //需要判断状态是否保存过了
		if (savedInstanceState != null){
            //读取上一次界面Save的时候tab选中的状态
            selindex = savedInstanceState.getInt(PRV_SELINDEX,selindex);
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG[0]);
            cjFragment = (CJFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG[1]);
            tqdFragment = (TQDFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG[2]);
            userFragment = (UserFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG[3]);
            currentFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG[selindex]);
        }

        selectFragment(selindex);

		//应用升级
//        appUpdate();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		 MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		MobclickAgent.onPause(this);
	}


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PRV_SELINDEX, selindex);
    }

    private void findViews() {
		home_tv = (TextView) findViewById(R.id.home_tv);
		cj_tv = (TextView) findViewById(R.id.cj_tv);
		tqd_tv = (TextView) findViewById(R.id.tqd_tv);
		user_tv = (TextView) findViewById(R.id.user_tv);

	
	}

	private void setListeners() {

		home_tv.setOnClickListener(this);
		cj_tv.setOnClickListener(this);
		tqd_tv.setOnClickListener(this);
		user_tv.setOnClickListener(this);

		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_tv:
			selectFragment(0);
			break;

		case R.id.cj_tv:
			selectFragment(1);
			break;

		case R.id.tqd_tv:
			selectFragment(2);
			break;

		case R.id.user_tv:
			selectFragment(3);
			break;
		}
		
	}
	
	public void showFragment(Fragment to, int index) {
		if (currentFrag == null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.container_lay, to, FRAGMENT_TAG[index]).commit();
			currentFrag = to;
		}else{
			if(currentFrag != to){
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				 if (!to.isAdded()) { 
					 transaction.hide(currentFrag).add(R.id.container_lay, to, FRAGMENT_TAG[index]).commit();
				 }else{
					 transaction.hide(currentFrag).show(to).commit();
				 }
				currentFrag = to;
			}
		}
    }
	
	private void freashBottomUI(int index){
		switch (index) {
		case 0:
			home_tv.setSelected(true);
			cj_tv.setSelected(false);
			tqd_tv.setSelected(false);
			user_tv.setSelected(false);
			break;
			
		case 1:
			home_tv.setSelected(false);
			cj_tv.setSelected(true);
			tqd_tv.setSelected(false);
			user_tv.setSelected(false);
			break;
			
		case 2:
			home_tv.setSelected(false);
			cj_tv.setSelected(false);
			tqd_tv.setSelected(true);
			user_tv.setSelected(false);
			break;
			
		case 3:
			home_tv.setSelected(false);
			cj_tv.setSelected(false);
			tqd_tv.setSelected(false);
			user_tv.setSelected(true);
			break;
		}
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.getExtras() != null){
			int fragmentFlag = intent.getExtras().getInt("fragmentFlag");
			selectFragment(fragmentFlag);
		}
	}
	
	public void selectFragment(int fragmentFlag){
        switch (fragmentFlag){
            case 0:
                if (homeFragment == null) homeFragment = new HomeFragment();
                showFragment(homeFragment, fragmentFlag);
                freashBottomUI(fragmentFlag);
                selindex = fragmentFlag;
                break;

            case 1:
                if(cjFragment == null) cjFragment = new CJFragment();
                showFragment(cjFragment, fragmentFlag);
                freashBottomUI(fragmentFlag);
                selindex = fragmentFlag;
                break;

            case 2:
                if(tqdFragment == null) tqdFragment = new TQDFragment();
                showFragment(tqdFragment, fragmentFlag);
                freashBottomUI(fragmentFlag);
                selindex = fragmentFlag;
                break;

            case 3:
                if(userFragment == null) userFragment = new UserFragment();
                showFragment(userFragment, fragmentFlag);
                freashBottomUI(fragmentFlag);
                selindex = fragmentFlag;
                break;

            default:
                break;
        }
	}
	
	
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {


//            if(tqdFragment != null && tqdFragment.isVisible()){
//                if (tqdFragment.closeTypeView()) return  true;
//            }
			
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出惠早订",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
//				MobclickAgent.onKillProcess(MainActivity.this);
				finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	/**
	* @Description: 初始化百度推送
	* @return void    返回类型
	 */
	private void initPush() {
		
		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
//         PushManager.enableLbs(getApplicationContext());
         if(Preference.getInstance().readIsPushClose()){
        	 PushManager.stopWork(getApplicationContext());
         }else {
        	 PushManager.startWork(getApplicationContext(), 
     				PushConstants.LOGIN_TYPE_API_KEY,
     				"1wK0jleZjhG4fee33xXiNGtz");
         }
	}


    private void appUpdate(){

        if (NetUtil.detectAvailable(this)) {
            VersionCheckRequest request = new VersionCheckRequest(getVersionCode(), new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                        case Constants.NO_DATA_FROM_NET:
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            final AppUpgrade appUpgrade = (AppUpgrade) message.obj;

                            if (appUpgrade != null && !StringUtil.isEmpty(appUpgrade.getDownLoadUrl())){
                                final AppUpdateDialog confirmDialog = new AppUpdateDialog(MainActivity.this, appUpgrade.getContent());
                                confirmDialog.show();
                                confirmDialog.setClicklistener(new AppUpdateDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        confirmDialog.dismiss();

                                        Uri uri = Uri.parse(appUpgrade.getDownLoadUrl());
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void doCancel() {
                                        confirmDialog.dismiss();
                                    }
                                });
                            }
                            break;
                    }
                }
            });
            request.doRequest();
        }

    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    private String getVersionCode() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = String.valueOf(info.versionCode);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }



}
