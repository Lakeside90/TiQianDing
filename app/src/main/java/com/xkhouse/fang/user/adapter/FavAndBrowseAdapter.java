package com.xkhouse.fang.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.booked.activity.StoreDetailActivity;

import java.util.ArrayList;

/**
 * 收藏/浏览列表
 */
public class FavAndBrowseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<BookedInfo> bookedInfos;

    private LinearLayout.LayoutParams lps;
	private DisplayImageOptions options;
	private ModelApplication modelApp;

    public FavAndBrowseAdapter(Context context) {
        this.context = context;
        modelApp = (ModelApplication) ((Activity) context).getApplication();
    }

    public FavAndBrowseAdapter(Context context, ArrayList<BookedInfo> bookedInfos){
		this.context = context;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		this.bookedInfos = bookedInfos;

        lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.leftMargin = DisplayUtil.dip2px(context, 3);
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<BookedInfo> newsList) {
		this.bookedInfos = newsList;
        this.notifyDataSetChanged();
	}


	
	@Override
	public int getCount() {
		return bookedInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return bookedInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_favorite_browse, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		final BookedInfo bookedInfo = bookedInfos.get(position);

		ImageLoader.getInstance().displayImage(bookedInfo.getCover_banner(), holder.icon_iv, options);
        holder.title_txt.setText(bookedInfo.getBusinessName());
        holder.ave_consum_txt.setText("¥"+ bookedInfo.getAverageConsump() + "/人");

        String[] labels = bookedInfo.getBusinessLabel();
        if (labels != null && labels.length > 0) {
            holder.label_lay.setVisibility(View.VISIBLE);
            holder.label_lay.removeAllViews();
            for(String label : labels){
                TextView textView = new TextView(context);
                textView.setPadding(DisplayUtil.dip2px(context, 3),
                        DisplayUtil.dip2px(context, 2),
                        DisplayUtil.dip2px(context, 3),
                        DisplayUtil.dip2px(context, 2));
                textView.setTextColor(context.getResources().getColor(R.color.common_gray_txt));
                textView.setTextSize(12);
                textView.setBackground(context.getResources().getDrawable(R.drawable.gray_border_btn_bg));
                textView.setText(label);
                holder.label_lay.addView(textView, lps);
            }
        }else{
            holder.label_lay.setVisibility(View.GONE);
        }

        holder.address_txt.setText(bookedInfo.getBusinessAddress());

        holder.distance_txt.setText(""); // TODO: 17/9/9

		convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, StoreDetailActivity.class);
                intent.putExtra("id", bookedInfo.getBookingId());
                context.startActivity(intent);
			}
		});

		return convertView;
	}


	
	public class ViewHolder{

		ImageView icon_iv;
		TextView title_txt;
		TextView ave_consum_txt;
        LinearLayout label_lay;
        TextView address_txt;
        TextView distance_txt;

		public ViewHolder(View view){
            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            title_txt = (TextView) view.findViewById(R.id.title_txt);
            ave_consum_txt = (TextView) view.findViewById(R.id.ave_consum_txt);
            label_lay = (LinearLayout) view.findViewById(R.id.label_lay);
            address_txt = (TextView) view.findViewById(R.id.address_txt);
            distance_txt = (TextView) view.findViewById(R.id.distance_txt);
        }


	}

}
