package com.xkhouse.fang.user.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.widget.loading.RotateLoading;

/**
 * 评论
 */
public class CommentAddActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		startDataTask(1, true);
        fillData();
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_comment_add);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();
		



	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("评论");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {


	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){

        }
	}
	
	
	private void fillData(){


	}
	


}
