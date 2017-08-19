package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.config.Constants;

/**
* @Description: 我的发布（房源发布）
* @author wujian  
* @date 2016-04-11
 */
public class MyReleaseActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private LinearLayout release_sell_lay;
	private LinearLayout release_rent_lay;






	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_my_release);
		
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();

        release_sell_lay = (LinearLayout) findViewById(R.id.release_sell_lay);
        release_rent_lay = (LinearLayout) findViewById(R.id.release_rent_lay);
    }
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("我的发布");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

    @Override
    protected void setListeners() {
        super.setListeners();

        release_sell_lay.setOnClickListener(this);
        release_rent_lay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        Intent intent = null;
        Bundle data = null;

        switch (v.getId()){
            case R.id.release_sell_lay:

                if(Constants.USER_GE_REN.equals(modelApp.getUser().getMemberType())){
                    intent = new Intent(MyReleaseActivity.this, ReleaseManageActivity.class);
                    data = new Bundle();
                    data.putInt("release_type", ReleaseManageActivity.RELEASE_SELL);
                    intent.putExtras(data);
                    startActivity(intent);
                }else if(Constants.USER_JING_JI_REN.equals(modelApp.getUser().getMemberType())){
                    startActivity(new Intent(MyReleaseActivity.this, JJRSellReleaseManageActivity.class));
                }
                break;

            case R.id.release_rent_lay:
                if(Constants.USER_GE_REN.equals(modelApp.getUser().getMemberType())){
                    intent = new Intent(MyReleaseActivity.this, ReleaseManageActivity.class);
                    data = new Bundle();
                    data.putInt("release_type", ReleaseManageActivity.RELEASE_RENT);
                    intent.putExtras(data);
                    startActivity(intent);
                }else if(Constants.USER_JING_JI_REN.equals(modelApp.getUser().getMemberType())){
                    startActivity(new Intent(MyReleaseActivity.this, JJRRentReleaseManageActivity.class));
                }
                break;

        }
    }
}
