package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.user.entity.FavHouse;
import com.xkhouse.fang.user.entity.TXRecord;
import com.xkhouse.fang.user.view.FavoriteListItemView.ItemLongClickListener;

import java.util.ArrayList;

/**
* @Description: 最近提现记录
* @author wujian  
* @date 2016-7-19
 */
public class TXRecordListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<TXRecord> recordList;
	private DisplayImageOptions options;


	public TXRecordListAdapter(Context context, ArrayList<TXRecord> recordList){
		
		this.context = context;
		this.recordList = recordList;
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<TXRecord> recordList){
		this.recordList = recordList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return recordList.size();
	}

	@Override
	public Object getItem(int position) {
		return recordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_tx_record_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		TXRecord record = recordList.get(position);
		

		holder.name_txt.setText(record.getName());
        if ("0".equals(record.getType())){
            holder.icon_iv.setImageResource(R.drawable.tixian_ico_zhifubao);
            holder.type_name_txt.setText(record.getCardNo());
        }else{
            ImageLoader.getInstance().displayImage(record.getIcon(), holder.icon_iv, options);
            holder.type_name_txt.setText(record.getBankName() + "(" +
                    record.getCardNo().substring(record.getCardNo().length()-4,record.getCardNo().length())
                    +")");
        }


		return convertView;
	}
	
	public class ViewHolder{
		ImageView icon_iv;
		TextView name_txt;
		TextView type_name_txt;
		
		public ViewHolder(View view){
            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            name_txt = (TextView) view.findViewById(R.id.name_txt);
            type_name_txt = (TextView) view.findViewById(R.id.type_name_txt);
		}
	}

}
