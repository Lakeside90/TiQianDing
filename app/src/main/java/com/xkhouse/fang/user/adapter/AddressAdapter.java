package com.xkhouse.fang.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.booked.entity.AddressInfo;
import com.xkhouse.fang.user.entity.MSGNews;

import java.util.ArrayList;

/**
* 收货地址
 */
public class AddressAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<AddressInfo> addressInfos;
	private ModelApplication modelApp;
	private AddressClickListener clickListener;


	public AddressAdapter(Context context, ArrayList<AddressInfo> addressInfos, AddressClickListener clickListener){
		this.context = context;
		this.addressInfos = addressInfos;
		this.clickListener = clickListener;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
	}
	
	public void setData(ArrayList<AddressInfo> addressInfos){
		this.addressInfos = addressInfos;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return addressInfos.size();
	}


	@Override
	public Object getItem(int position) {
		return addressInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_address_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		AddressInfo addressInfo = addressInfos.get(position);

		holder.name_txt.setText(addressInfo.getName());

		holder.phone_txt.setText(addressInfo.getPhone());

		holder.content_txt.setText(addressInfo.getContent());

		if ("1".equals(addressInfo.getIs_selected())) {
			holder.check_box.setVisibility(View.VISIBLE);
			holder.check_box.setTextIsSelectable(true);
		} else {
			holder.check_box.setVisibility(View.INVISIBLE);
			holder.check_box.setTextIsSelectable(false);
		}

		holder.edit_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (clickListener != null) clickListener.onEdit(position);
			}
		});

		holder.delete_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (clickListener != null) clickListener.onDelete(position);
			}
		});

		return convertView;
	}
	
	
	public class ViewHolder{
		
		TextView name_txt;
		TextView phone_txt;
		TextView content_txt;
		CheckBox check_box;
		TextView edit_txt;
		TextView delete_txt;

		public ViewHolder(View view){

			name_txt = (TextView) view.findViewById(R.id.name_txt);
			phone_txt = (TextView) view.findViewById(R.id.phone_txt);
			content_txt = (TextView) view.findViewById(R.id.content_txt);
			check_box = (CheckBox) view.findViewById(R.id.check_box);
			edit_txt = (TextView) view.findViewById(R.id.edit_txt);
			delete_txt = (TextView) view.findViewById(R.id.delete_txt);
		}
	}


	public interface AddressClickListener {

		void onEdit(int position);

		void onDelete(int position);
	}
}
