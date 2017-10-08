package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.activity.MyBookedDetailActivity;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.entity.MyBookedInfo;

import java.util.ArrayList;

/**
 * 我的预定列表
 */
public class MyBookedAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MyBookedInfo> myBookedinfoList;

	public MyBookedAdapter(Context context, ArrayList<MyBookedInfo> myBookedinfoList){
		this.context = context;
		this.myBookedinfoList = myBookedinfoList;
	}
	
	public void setData(ArrayList<MyBookedInfo> myBookedinfoList){
		this.myBookedinfoList = myBookedinfoList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return myBookedinfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return myBookedinfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_my_booked_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		final MyBookedInfo myBookedInfo = myBookedinfoList.get(position);

		holder.title_txt.setText(myBookedInfo.getBusiness_name());
        holder.money_txt.setText("预定金额：¥" + myBookedInfo.getMoney());
        holder.time_txt.setText("预定日期：" + myBookedInfo.getUse_time());
        holder.people_num_txt.setText(myBookedInfo.getPeople_num() + "人");

        holder.status_txt.setVisibility(View.VISIBLE);
        if ("1".equals(myBookedInfo.getStatus())) {
            holder.status_txt.setText("待付款");
        }else if ("2".equals(myBookedInfo.getStatus())) {
            holder.status_txt.setText("待审核");
        } else if ("3".equals(myBookedInfo.getStatus())) {
            holder.status_txt.setText("待使用");
        } else if ("4".equals(myBookedInfo.getStatus())) {
            holder.status_txt.setText("已完成");
        } else if ("5".equals(myBookedInfo.getStatus())) {
            holder.status_txt.setText("已取消");
        } else {
            holder.status_txt.setVisibility(View.INVISIBLE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyBookedDetailActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("myBookedInfo", myBookedInfo);
                intent.putExtras(data);
                context.startActivity(intent);
            }
        });
		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		TextView title_txt;
		TextView status_txt;
		TextView money_txt;
		TextView time_txt;
		TextView people_num_txt;

		public ViewHolder(View view){

            title_txt = (TextView) view.findViewById(R.id.title_txt);
            status_txt = (TextView) view.findViewById(R.id.status_txt);
            money_txt = (TextView) view.findViewById(R.id.money_txt);
            time_txt = (TextView) view.findViewById(R.id.time_txt);
            people_num_txt = (TextView) view.findViewById(R.id.people_num_txt);
		}
	}

}
