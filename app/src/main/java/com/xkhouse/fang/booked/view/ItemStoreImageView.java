package com.xkhouse.fang.booked.view;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.adapter.StoreImageAdapter;
import com.xkhouse.fang.user.adapter.CJListAdapter;
import com.xkhouse.fang.user.entity.XKRecommend;
import com.xkhouse.fang.user.task.XKRecommendListRequest;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
 * 商家相册列表
 */
public class ItemStoreImageView {

	private Context context;
	private View rootView;

    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

    private GridView gridView;

	private StoreImageAdapter adapter;


	private String status;

	private ModelApplication modelApp;


	public View getView() {
        return rootView;
    }

	public ItemStoreImageView(Context context, String status) {
		this.context = context;
		this.status = status;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		initView();
		setListener();

	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_store_image_list, null);

        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
	}
	
	public void refreshView(){

        if (adapter == null) {
            adapter = new StoreImageAdapter(context, null);
            gridView.setAdapter(adapter);
        }

	}
	
	private void setListener() {


        error_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
	}
	
	

	
	private void fillRecommendData() {

	}
	
	
	


    
}
