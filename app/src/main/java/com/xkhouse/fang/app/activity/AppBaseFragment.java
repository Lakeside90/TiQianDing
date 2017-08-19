package com.xkhouse.fang.app.activity;

import android.support.v4.app.Fragment;

import com.xkhouse.fang.widget.CustomDialog;

/** 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author wujian  
 * @date 2015-5-6 下午1:59:57  
 */
public class AppBaseFragment extends Fragment {
	
	 	
	CustomDialog dialog;
	
	 public void showLoadingDialog(int showMessage) {
	        if (dialog == null) {
	            dialog = new CustomDialog(getActivity(), showMessage,
	                    android.R.style.Theme_Translucent_NoTitleBar);
	        }
	        if (!getActivity().isFinishing() && !dialog.isShowing()) {
	            dialog.show();
	        }
	    }
	 
    public void showLoadingDialog(String showMessage) {
	        if (dialog == null) {
	            dialog = new CustomDialog(getActivity(), showMessage,
	                    android.R.style.Theme_Translucent_NoTitleBar);
	        }
	        if (!getActivity().isFinishing() && !dialog.isShowing()) {
	            dialog.show();
	        }
	    }
	    
	    /**
	     * 隐藏正在提交疑问的环形进度条
	     */
	    public void hideLoadingDialog() {
	        if (dialog != null && dialog.isShowing()) {
	            dialog.dismiss();
	            dialog = null;
	        }
	    }
}
