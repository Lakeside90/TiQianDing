package com.xkhouse.fang.user.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.activity.MyRecommendActivity;
import com.xkhouse.fang.user.activity.RecommendDetailActivity;
import com.xkhouse.fang.user.entity.XKRecommend;
import com.xkhouse.lib.utils.DateUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
* @Description: 星空宝客户推荐列表
* @author wujian  
* @date 2015-10-22 上午10:47:36
 */
public class XKRecommendListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRecommend> recommendList;
	private OnRecomendItemClickListener onRecomendItemClickListener;

	public XKRecommendListAdapter(Context context, ArrayList<XKRecommend> recommendList,
                                  OnRecomendItemClickListener onRecomendItemClickListener){
		this.context = context;
		this.recommendList = recommendList;
		this.onRecomendItemClickListener = onRecomendItemClickListener;

	}
	
	public void setData(ArrayList<XKRecommend> recommendList){
		this.recommendList = recommendList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return recommendList.size();
	}

	@Override
	public Object getItem(int position) {
		return recommendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_recommend_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		
		XKRecommend recommend = recommendList.get(position);
		

		
        if("-1".equals(recommend.getStatus())) {
			holder.recommend_status_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
            holder.recommend_txt.setVisibility(View.VISIBLE);

		}else if("4".equals(recommend.getStatus())) {
			holder.recommend_status_txt.setTextColor(context.getResources().getColor(R.color.common_green));
            holder.recommend_txt.setVisibility(View.INVISIBLE);

		}else {
            holder.recommend_status_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
            holder.recommend_txt.setVisibility(View.INVISIBLE);
        }
		holder.recommend_status_txt.setText(recommend.getStatusName());


		long time = 0;
		try {
			time = Long.parseLong(recommend.getDate());
		} catch (Exception e) {
		}
		holder.recommend_date_txt.setText(DateUtil.getDateFromLong(time, "yyyy-MM-dd"));

        if (!StringUtil.isEmpty(recommend.getPhone()) && recommend.getPhone().length() == 11){
            holder.account_name_txt.setText(recommend.getCustomerName()+"(" + recommend.getPhone().substring(0, 3)
                    + "****" + recommend.getPhone().substring(7, 11) + ")");
        }else{
            holder.account_name_txt.setText(recommend.getCustomerName());
        }



        holder.project_content_txt.setText(recommend.getSiteName()+"-"+recommend.getPropertyName()+"-"+recommend.getProjectStr());


        holder.recommend_txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRecomendItemClickListener != null) onRecomendItemClickListener.onClick(position);
            }
        });
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(StringUtil.isEmpty(recommendList.get(position).getRecommendId())) return;
				
				Intent intent = new Intent(context, RecommendDetailActivity.class);
				Bundle data = new Bundle();
				data.putString("recommendId", recommendList.get(position).getRecommendId());
				intent.putExtras(data);
				context.startActivity(intent);
			}
		});


		return convertView;
	}
	
	
	public class ViewHolder{
		

		TextView recommend_status_txt;
		TextView recommend_date_txt;
		TextView account_name_txt;
		TextView recommend_txt;
		TextView project_content_txt;

		public ViewHolder(View view){
			

			recommend_status_txt = (TextView) view.findViewById(R.id.recommend_status_txt);
			recommend_date_txt = (TextView) view.findViewById(R.id.recommend_date_txt);
            account_name_txt = (TextView) view.findViewById(R.id.account_name_txt);
            recommend_txt = (TextView) view.findViewById(R.id.recommend_txt);
            project_content_txt = (TextView) view.findViewById(R.id.project_content_txt);
		}
	}

    public interface OnRecomendItemClickListener {

        public void onClick(int position);
    }

}
