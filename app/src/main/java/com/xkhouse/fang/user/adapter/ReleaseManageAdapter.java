package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.XKRelease;

import java.util.ArrayList;

/**
* @Description: 我的发布--房源管理
* @author wujian  
* @date 2016-04-11
 */
public class ReleaseManageAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRelease> xkReleaseList;
    private ReleaseManageListener manageListener;

	public ReleaseManageAdapter(Context context, ArrayList<XKRelease> xkReleaseList, ReleaseManageListener manageListener){
		
		this.context = context;
		this.xkReleaseList = xkReleaseList;
        this.manageListener = manageListener;
	}
	
	public void setData(ArrayList<XKRelease> newHouseList){
		this.xkReleaseList = newHouseList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return xkReleaseList.size();
	}

	@Override
	public Object getItem(int position) {
		return xkReleaseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_release_manage_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

        XKRelease release = xkReleaseList.get(position);

        holder.release_title_txt.setText(release.getTitle());
        holder.release_time.setText("刷新时间：" + release.getRefreshTime());

        if("1".equals(release.getStatus())){
            holder.release_status_txt.setText("显示中");
            holder.release_status_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
            holder.release_one_txt.setText("刷新");
            holder.release_two_txt.setText("编辑");
            holder.release_two_txt.setVisibility(View.VISIBLE);
            holder.release_three_txt.setText("删除");
            holder.release_one_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onRefresh(position);
                }
            });
            holder.release_two_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onEdit(position);
                }
            });
            holder.release_three_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onDelete(position);
                }
            });

        }else if("2".equals(release.getStatus())){
            holder.release_status_txt.setText("审核中");
            holder.release_status_txt.setTextColor(context.getResources().getColor(R.color.common_green));
            holder.release_one_txt.setText("编辑");
            holder.release_two_txt.setText("");
            holder.release_two_txt.setVisibility(View.INVISIBLE);
            holder.release_three_txt.setText("删除");
            holder.release_one_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onEdit(position);
                }
            });
            holder.release_three_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onDelete(position);
                }
            });

        }else if("3".equals(release.getStatus())){
            holder.release_status_txt.setText("已过期");
            holder.release_status_txt.setTextColor(context.getResources().getColor(R.color.common_gray_txt));
            holder.release_one_txt.setText("重新发布");
            holder.release_two_txt.setText("");
            holder.release_two_txt.setVisibility(View.INVISIBLE);
            holder.release_three_txt.setText("删除");
            holder.release_one_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onRefresh(position);
                }
            });
            holder.release_three_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onDelete(position);
                }
            });
        }


		return convertView;
	}
	
	public class ViewHolder{
		TextView release_title_txt;
		TextView release_status_txt;
		TextView release_time;
		TextView release_one_txt;
		TextView release_two_txt;
		TextView release_three_txt;

		public ViewHolder(View view){
            release_title_txt = (TextView) view.findViewById(R.id.release_title_txt);
            release_status_txt = (TextView) view.findViewById(R.id.release_status_txt);
            release_time = (TextView) view.findViewById(R.id.release_time);
            release_one_txt = (TextView) view.findViewById(R.id.release_one_txt);
            release_two_txt = (TextView) view.findViewById(R.id.release_two_txt);
            release_three_txt = (TextView) view.findViewById(R.id.release_three_txt);
		}
	}
	
	
	public interface ReleaseManageListener {

         void onRefresh(int position);

         void onRelease(int position);

         void onEdit(int position);

         void onDelete(int position);

    }
	

}
