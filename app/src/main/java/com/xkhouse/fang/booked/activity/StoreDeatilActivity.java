package com.xkhouse.fang.booked.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.booked.adapter.CommentAdapter;
import com.xkhouse.fang.user.adapter.CJListAdapter;
import com.xkhouse.fang.widget.ScrollListView;
import com.xkhouse.fang.widget.ScrollXListView;

/**
 * 商户详情
 */
public class StoreDeatilActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
    private TextView tv_head_title;
	private ImageView iv_head_right;


    private ScrollListView luck_listview;
    private CJListAdapter luckAdapter;

    private ScrollXListView comment_listview;
    private CommentAdapter commentAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        luck_listview.setAdapter(new CJListAdapter(mContext, null));

        comment_listview.setAdapter(new CommentAdapter(mContext, null));

	}


	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_store_detail);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();

        luck_listview = (ScrollListView) findViewById(R.id.luck_listview);
        comment_listview = (ScrollXListView) findViewById(R.id.comment_listview);
    }


	private void initTitle() {

        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("商户详情");

        iv_head_right = (ImageView) findViewById(R.id.iv_head_right);
        iv_head_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
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



    private void fillAdData(){


    }



}
