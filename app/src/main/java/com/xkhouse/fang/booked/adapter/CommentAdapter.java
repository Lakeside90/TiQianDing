package com.xkhouse.fang.booked.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xkhouse.fang.R;
import com.xkhouse.fang.booked.entity.CommentInfo;

import java.util.ArrayList;

/**
 * 评论
 */
public class CommentAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CommentInfo> commentInfoList;


	public CommentAdapter(Context context, ArrayList<CommentInfo> recommendList){
		this.context = context;
		this.commentInfoList = recommendList;

	}
	
	public void setData(ArrayList<CommentInfo> recommendList){
		this.commentInfoList = recommendList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return commentInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return commentInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		


		



		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});


		return convertView;
	}
	
	
	public class ViewHolder{
		



		public ViewHolder(View view){
			


		}
	}



}
