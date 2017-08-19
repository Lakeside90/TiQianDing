package com.xkhouse.fang.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.xkhouse.fang.widget.CustomDialog;
import com.xkhouse.frame.activity.BaseActivity;

public class AppBaseActivity extends BaseActivity {
	
	public ModelApplication modelApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		BaseApplication application = (BaseApplication) getApplication();
//		application.getActivityManager().popActivity(this);
	}

	@Override
	protected void setContentView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void init() {
		 modelApp = (ModelApplication)getApplication();
	}

	@Override
	protected void findViews() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void release() {
		// TODO Auto-generated method stub
		
	}

    CustomDialog dialog;

    public void showLoadingDialog(int showMessage) {
        if (dialog == null) {
            dialog = new CustomDialog(this, showMessage,
                    android.R.style.Theme_Translucent_NoTitleBar);
        }
        if (!isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void showLoadingDialog(String showMessage) {
        if (dialog == null) {
            dialog = new CustomDialog(this, showMessage,
                    android.R.style.Theme_Translucent_NoTitleBar);
        }
        if (!isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }
    
    /**
     * 隐藏正在提交疑问的环形进度条
     */
    public void hideLoadingDialog() {
        if (!this.isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
       
    }
    
    @Override
    protected void onResume() {
        super.onResume();
     
    }


    protected void closeSoftInput(){
        try{
            InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
