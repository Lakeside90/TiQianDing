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

import java.util.ArrayList;

/**
 * 抽奖--已揭晓
 */
public class CJyjxListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CJInfo> cjInfoList;
    private DisplayImageOptions options;

	public CJyjxListAdapter(Context context, ArrayList<CJInfo> cjInfoList){
		this.context = context;
		this.cjInfoList = cjInfoList;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nopic)   // 加载的图片
                .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
                .showImageForEmptyUri(R.drawable.nopic)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<CJInfo> cjInfoList){
		this.cjInfoList = cjInfoList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return cjInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return cjInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_cj_yjx_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		

        CJInfo cjInfo = cjInfoList.get(position);

        ImageLoader.getInstance().displayImage(cjInfo.getImg(), holder.icon_iv, options);

        holder.title_txt.setText(cjInfo.getTitle());
        holder.name_txt.setText("恭喜：" + cjInfo.getNickname());
        holder.number_txt.setText(cjInfo.getWinning_number());
        holder.number_txt.setText("揭晓时间：" + cjInfo.getReal_winning_time());

		return convertView;
	}

	
	
	public class ViewHolder{

        ImageView icon_iv;
        TextView title_txt;
        TextView name_txt;
        TextView number_txt;
        TextView time_txt;

		public ViewHolder(View view){
            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            title_txt = (TextView) view.findViewById(R.id.title_txt);
            name_txt = (TextView) view.findViewById(R.id.name_txt);
            number_txt = (TextView) view.findViewById(R.id.number_txt);
            time_txt = (TextView) view.findViewById(R.id.time_txt);
        }
	}



}
