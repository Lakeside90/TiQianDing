package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.CJFragment;
import com.xkhouse.fang.app.entity.CJInfo;
import com.xkhouse.fang.app.entity.LuckInfo;

import java.util.ArrayList;

/**
 * 抽奖--商家详情页
 */
public class LuckListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<LuckInfo> luckList;
    private DisplayImageOptions options;

	public LuckListAdapter(Context context, ArrayList<LuckInfo> luckList){
		this.context = context;
		this.luckList = luckList;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nopic)   // 加载的图片
                .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
                .showImageForEmptyUri(R.drawable.nopic)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<LuckInfo> luckList){
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_business_luck_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		

        LuckInfo luckInfo = luckList.get(position);

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

        if ("2".equals(luckInfo.getType())) {
            holder.join_txt.setVisibility(View.VISIBLE);
            holder.yjs_txt.setVisibility(View.GONE);
        }else {
            holder.join_txt.setVisibility(View.GONE);
            holder.yjs_txt.setVisibility(View.VISIBLE);
        }

		return convertView;
	}

	
	
	public class ViewHolder{

        ImageView icon_iv;
        TextView title_txt;
        ProgressBar progress_bar;
        TextView join_count_txt;
        TextView count_txt;
        TextView join_txt;
        TextView yjs_txt;


		public ViewHolder(View view){
            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);
            title_txt = (TextView) view.findViewById(R.id.title_txt);
            join_count_txt = (TextView) view.findViewById(R.id.join_count_txt);
            count_txt = (TextView) view.findViewById(R.id.count_txt);
            join_txt = (TextView) view.findViewById(R.id.join_txt);
            yjs_txt = (TextView) view.findViewById(R.id.yjs_txt);
        }
	}



}
