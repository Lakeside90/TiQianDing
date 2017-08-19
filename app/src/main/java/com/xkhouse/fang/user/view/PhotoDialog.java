package com.xkhouse.fang.user.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xkhouse.fang.R;

public class PhotoDialog extends Dialog{

    public TextView takePhoto;
    public TextView takeFromPic;
    public TextView cancel;
    Context context;
    private View rootView;
    public PhotoDialog(Context context) {
        super(context);
        this.context = context;
    }

    public PhotoDialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    public PhotoDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_takephoto);
        
        takePhoto = (TextView) findViewById(R.id.take_photo);
        takeFromPic = (TextView) findViewById(R.id.take_img);
        
        cancel = (TextView) findViewById(R.id.cancel);
        rootView = findViewById(R.id.root_view);
    }
    
    @Override
    public void show() {
        super.show();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) rootView
                .getLayoutParams();
        int[] w_h = getScreenWidthHeight(context);
        layoutParams.width = w_h[0];
        rootView.setLayoutParams(layoutParams); 
    }
    
    /**
	 * 获取手机屏幕宽高
	 * @param context
	 * @return
	 */
    public int[] getScreenWidthHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new int[] { w_screen, h_screen };
    }
}
