package com.xkhouse.fang.user.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.XKRecommendStatus;

/**
* @Description:  星空宝推荐客户状态跟踪
* @author wujian  
* @date 2015-11-12 下午1:52:24
 */
public class XKRecommendStatusListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRecommendStatus> recommendStatusList;
	
	public XKRecommendStatusListAdapter(Context context, ArrayList<XKRecommendStatus> recommendStatusList){
		this.context = context;
		this.recommendStatusList = recommendStatusList;
		
	}
	
	public void setData(ArrayList<XKRecommendStatus> recommendStatusList){
		this.recommendStatusList = recommendStatusList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return recommendStatusList.size();
	}

	@Override
	public Object getItem(int position) {
		return recommendStatusList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_recommend_detail_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		
		XKRecommendStatus recommend = recommendStatusList.get(position);
		
		if("0".equals(recommend.getStatus())){
			holder.status_icon.setImageResource(R.drawable.recommend_icon_xiayibu);
			
		}else if("1".equals(recommend.getStatus())) {
			holder.status_icon.setImageResource(R.drawable.recommend_icon_wancheng);
			
		}else if("2".equals(recommend.getStatus())) {
			holder.status_icon.setImageResource(R.drawable.recommend_icon_weiwancheng);
		}
		
		holder.status_content_txt.setText(Html.fromHtml(recommend.getContent()));
		holder.status_date_txt.setText(recommend.getDate());
		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		ImageView status_icon;
		TextView status_content_txt;
		TextView status_date_txt;
		
		public ViewHolder(View view){
			status_icon = (ImageView) view.findViewById(R.id.status_icon);
			status_content_txt = (TextView) view.findViewById(R.id.status_content_txt);
			status_date_txt = (TextView) view.findViewById(R.id.status_date_txt);
		}
	}

}
