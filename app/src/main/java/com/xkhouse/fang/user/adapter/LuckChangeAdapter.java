package com.xkhouse.fang.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.user.entity.MSGNews;

import java.util.ArrayList;

/**
* 抽奖机会明细
 */
public class LuckChangeAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MSGNews> newsList;
	private ModelApplication modelApp;

	public LuckChangeAdapter(Context context, ArrayList<MSGNews> newsList){
		this.context = context;
		this.newsList = newsList;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
	}
	
	public void setData(ArrayList<MSGNews> newsList){
		this.newsList = newsList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_luck_change_list, null);
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
		
		TextView title_txt;
		TextView date_txt;
		TextView content_txt;
		TextView money_txt;

		public ViewHolder(View view){
			
			title_txt = (TextView) view.findViewById(R.id.title_txt);
			date_txt = (TextView) view.findViewById(R.id.date_txt);
			content_txt = (TextView) view.findViewById(R.id.content_txt);
            money_txt = (TextView) view.findViewById(R.id.money_txt);

		}
	}

}
