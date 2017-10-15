package com.xkhouse.fang.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.maps2d.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.app.entity.LuckInfo;
import com.xkhouse.fang.booked.activity.LuckDetailActivity;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description: 猜你喜欢 (楼盘)
 * @author wujian  
 * @date 2015-8-27 下午6:58:09  
 */
public class HomeLuckAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<LuckInfo> luckList;
	private LatLng startLatlng;
	
	
	private DisplayImageOptions options;
	
	public HomeLuckAdapter(Context context, ArrayList<LuckInfo> luckList, LatLng startLatlng){
		this.context = context;
		this.luckList = luckList;
		this.startLatlng = startLatlng;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<LuckInfo> luckList, LatLng startLatlng) {
		this.luckList = luckList;
		this.startLatlng = startLatlng;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_home_luck_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		final LuckInfo luckInfo = luckList.get(position);
		

		ImageLoader.getInstance().displayImage(luckInfo.getImg(), holder.icon_iv, options);

        holder.title_txt.setText(luckInfo.getTitle());

        holder.progress_bar.setMax(Integer.valueOf(luckInfo.getCount()));
        holder.progress_bar.setProgress(Integer.valueOf(luckInfo.getJoin_count()));
        if (Integer.valueOf(luckInfo.getCount()) == Integer.valueOf(luckInfo.getJoin_count())) {
            holder.join_count_txt.setText("人数已满");
            holder.count_txt.setVisibility(View.GONE);
        }else{
            holder.join_count_txt.setText(luckInfo.getJoin_count());
            holder.count_txt.setText("/" + luckInfo.getCount());
            holder.count_txt.setVisibility(View.VISIBLE);
        }

        if (StringUtil.isEmpty(luckInfo.getPub_type())) {
            holder.pub_type_iv.setVisibility(View.INVISIBLE);
        }else {
            holder.pub_type_iv.setVisibility(View.VISIBLE);
            holder.pub_type_txt.setText(luckInfo.getPub_type());
        }

		//跳转到详情页
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                Intent intent = new Intent(context, LuckDetailActivity.class);
                intent.putExtra("luckId", luckInfo.getId());
                context.startActivity(intent);
			}
		});
				
		return convertView;
	}
	
	public class ViewHolder {

		ImageView icon_iv;
		TextView title_txt;
        ProgressBar progress_bar;
        TextView join_count_txt;
        TextView count_txt;
        TextView join_txt;
        LinearLayout pub_type_iv;
        TextView pub_type_txt;

		public ViewHolder(View view){
            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            title_txt = (TextView) view.findViewById(R.id.title_txt);
            progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);
            join_count_txt = (TextView) view.findViewById(R.id.join_count_txt);
            count_txt = (TextView) view.findViewById(R.id.count_txt);
            join_txt = (TextView) view.findViewById(R.id.join_txt);
            pub_type_txt = (TextView) view.findViewById(R.id.pub_type_txt);
            pub_type_iv = (LinearLayout) view.findViewById(R.id.pub_type_iv);
        }
	}

}
