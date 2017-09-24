package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.xkhouse.fang.user.entity.XKRecommend;
import com.xkhouse.fang.user.view.ItemCJListView;

import java.util.ArrayList;

/**
 * 抽奖--首页
 */
public class CJListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CJInfo> cjInfoList;
    private String type;
    private DisplayImageOptions options;

	public CJListAdapter(Context context, ArrayList<CJInfo> cjInfoList, String type){
		this.context = context;
		this.cjInfoList = cjInfoList;
        this.type = type;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nopic)   // 加载的图片
                .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
                .showImageForEmptyUri(R.drawable.nopic)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<CJInfo> cjInfoList, String type){
		this.cjInfoList = cjInfoList;
        this.type = type;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_cj_all_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		

        CJInfo cjInfo = cjInfoList.get(position);

        ImageLoader.getInstance().displayImage(cjInfo.getImg(), holder.icon_iv, options);

        holder.title_txt.setText(cjInfo.getTitle());

        holder.progress_bar.setMax(Integer.valueOf(cjInfo.getCount()));
        holder.progress_bar.setProgress(Integer.valueOf(cjInfo.getJoin_count()));
        if (Integer.valueOf(cjInfo.getCount()) == Integer.valueOf(cjInfo.getJoin_count())) {
            holder.join_count_txt.setText("人数已满");
            holder.count_txt.setVisibility(View.GONE);
        }else{
            holder.join_count_txt.setText(cjInfo.getJoin_count());
            holder.count_txt.setText("/" + cjInfo.getCount());
            holder.count_txt.setVisibility(View.VISIBLE);
        }

        if (type.equals(CJFragment.CJ_JXZ)) {
            holder.join_txt.setVisibility(View.VISIBLE);
            holder.djx_txt.setVisibility(View.GONE);
        }else if (type.equals(CJFragment.CJ_DJX)) {
            holder.join_txt.setVisibility(View.GONE);
            holder.djx_txt.setVisibility(View.VISIBLE);
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
        TextView djx_txt;


		public ViewHolder(View view){
            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);
            title_txt = (TextView) view.findViewById(R.id.title_txt);
            join_count_txt = (TextView) view.findViewById(R.id.join_count_txt);
            count_txt = (TextView) view.findViewById(R.id.count_txt);
            join_txt = (TextView) view.findViewById(R.id.join_txt);
            djx_txt = (TextView) view.findViewById(R.id.djx_txt);
        }
	}



}
