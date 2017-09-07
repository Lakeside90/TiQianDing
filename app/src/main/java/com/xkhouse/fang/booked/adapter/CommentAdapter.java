package com.xkhouse.fang.booked.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.XKRecommend;

import java.util.ArrayList;

/**
 * 评论
 */
public class CommentAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRecommend> recommendList;


	public CommentAdapter(Context context, ArrayList<XKRecommend> recommendList){
		this.context = context;
		this.recommendList = recommendList;

	}
	
	public void setData(ArrayList<XKRecommend> recommendList){
		this.recommendList = recommendList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
//		return recommendList.size();
        return 10;
	}

	@Override
	public Object getItem(int position) {
//		return recommendList.get(position);
        return 0;
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
