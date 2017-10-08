package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.MSGSystem;
import com.xkhouse.fang.user.entity.MyLuckInfo;

import java.util.ArrayList;

/**
 * 我的活动
 */
public class MyLuckAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MyLuckInfo> luckList;
	private DisplayImageOptions options;

	public MyLuckAdapter(Context context, ArrayList<MyLuckInfo> luckList){
		this.context = context;
		this.luckList = luckList;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<MyLuckInfo> luckList){
		this.luckList = luckList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return luckList.size();
	}

	@Override
	public Object getItem(int position) {
		return luckList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_my_luck_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		MyLuckInfo myLuckInfo = luckList.get(position);

        ImageLoader.getInstance().displayImage(myLuckInfo.getImg(), holder.icon_iv, options);

        holder.number_txt.setText("编号：" + myLuckInfo.getExpress_id());

        if ("-1".equals(myLuckInfo.getType())) {
            holder.type_txt.setText("活动已取消");
            holder.type_txt.setTextColor(context.getResources().getColor(R.color.common_gray_txt));
            holder.status_txt.setText("消耗已返还");
            holder.status_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
        } else if ("1".equals(myLuckInfo.getType())) {
            holder.type_txt.setText("待开奖");
            holder.type_txt.setTextColor(context.getResources().getColor(R.color.common_red));
            holder.status_txt.setText("请等待开奖");
            holder.status_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
        } else if ("2".equals(myLuckInfo.getType())) {
            holder.type_txt.setText("未中奖");
            holder.type_txt.setTextColor(context.getResources().getColor(R.color.common_gray_txt));
            holder.status_txt.setText("抱歉没有中奖");
            holder.status_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
        } else if ("3".equals(myLuckInfo.getType()) || "4".equals(myLuckInfo.getType())) {
            holder.type_txt.setText("已中奖");
            holder.type_txt.setTextColor(context.getResources().getColor(R.color.common_green));
            holder.status_txt.setText("填写收货地址");
            holder.status_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
        }

        holder.title_txt.setText(myLuckInfo.getTitle());

        holder.price_txt.setText(myLuckInfo.getPrice());

        holder.result_txt.setText(myLuckInfo.getNumber());

        holder.time_txt.setText(myLuckInfo.getCreate_time());

		return convertView;
	}
	
	
	public class ViewHolder{


		TextView number_txt;
		TextView type_txt;
        ImageView icon_iv;
        TextView title_txt;
        TextView price_txt;
        TextView result_txt;
        TextView time_txt;
        TextView status_txt;

		public ViewHolder(View view){

            number_txt = (TextView) view.findViewById(R.id.number_txt);
            type_txt = (TextView) view.findViewById(R.id.type_txt);
            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            title_txt = (TextView) view.findViewById(R.id.title_txt);
            price_txt = (TextView) view.findViewById(R.id.price_txt);
            result_txt = (TextView) view.findViewById(R.id.result_txt);
            time_txt = (TextView) view.findViewById(R.id.time_txt);
            status_txt = (TextView) view.findViewById(R.id.status_txt);

        }
	}

}
