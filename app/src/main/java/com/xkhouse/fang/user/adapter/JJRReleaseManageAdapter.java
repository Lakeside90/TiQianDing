package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.XKRelease;

import java.util.ArrayList;

/**
* @Description: 经纪人房源管理列表适配器
* @author wujian  
* @date 2016-04-15
 */
public class JJRReleaseManageAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRelease> xkReleaseList;
    private OnItemSelectListener listener;
    private OnItemEditListener editListener;

    private boolean operate = false;

	public JJRReleaseManageAdapter(Context context, ArrayList<XKRelease> xkReleaseList,
                                   OnItemSelectListener listener, OnItemEditListener editListener){
		
		this.context = context;
		this.xkReleaseList = xkReleaseList;
        this.listener = listener;
        this.editListener = editListener;
	}
	
	public void setData(ArrayList<XKRelease> newHouseList){
		this.xkReleaseList = newHouseList;
		notifyDataSetChanged();
	}

    public void showOperate(){
        operate = true;
        notifyDataSetChanged();
    }

    public void closeOperate(){
        operate = false;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_jjr_release_manage_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

        XKRelease release = xkReleaseList.get(position);
		if (operate){
            holder.release_cb.setVisibility(View.VISIBLE);
        }else{
            holder.release_cb.setVisibility(View.GONE);
        }

        if(release.isSelected()){
            holder.release_cb.setImageResource(R.drawable.checkbox_checked);
        }else{
            holder.release_cb.setImageResource(R.drawable.checkbox_normal);
        }
        holder.release_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onClick(position);
            }
        });


        holder.release_title_txt.setText(release.getTitle());
        holder.release_time_txt.setText("刷新时间：" + release.getRefreshTime());

        holder.release_edit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 editListener.onEdit(position);
            }
        });

		return convertView;
	}
	
	public class ViewHolder{
        ImageView release_cb;
		TextView release_title_txt;
		TextView release_time_txt;
		TextView release_edit_txt;


		public ViewHolder(View view){
            release_cb = (ImageView) view.findViewById(R.id.release_cb);
            release_title_txt = (TextView) view.findViewById(R.id.release_title_txt);
            release_time_txt = (TextView) view.findViewById(R.id.release_time_txt);
            release_edit_txt = (TextView) view.findViewById(R.id.release_edit_txt);
		}
	}
	


    public interface OnItemSelectListener{
        void onClick(int position);
    }

    public interface OnItemEditListener{
        void onEdit(int position);
    }
	

}
