package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.user.entity.MSGSystem;
import com.xkhouse.fang.user.entity.MyCheckInfo;

import java.util.ArrayList;

/**
 * 我的买单
 */
public class MyCheckAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MyCheckInfo> checkList;

	private DisplayImageOptions options;
    private LinearLayout.LayoutParams lps;

	public MyCheckAdapter(Context context, ArrayList<MyCheckInfo> checkList){
		this.context = context;
		this.checkList = checkList;

        lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.leftMargin = DisplayUtil.dip2px(context, 3);

		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<MyCheckInfo> checkList){
		this.checkList = checkList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return checkList.size();
	}

	@Override
	public Object getItem(int position) {
		return checkList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_my_check_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		MyCheckInfo myCheckInfo = checkList.get(position);

        ImageLoader.getInstance().displayImage(myCheckInfo.getConver_banner(), holder.icon_iv, options);
        holder.title_txt.setText(myCheckInfo.getBusiness_name());
        holder.money_txt.setText("¥"+ myCheckInfo.getAverage_consump() + "/人");

        String[] labels = myCheckInfo.getBusiness_label();
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

        holder.order_number_txt.setText("订单号：" + myCheckInfo.getPay_number());
        holder.consumer_txt.setText("消费：" + myCheckInfo.getPay_number());
        holder.pay_money_txt.setText("实付：" + myCheckInfo.getPay_number());
        holder.time_txt.setText("日期：" + myCheckInfo.getPay_number());

		if ("1".equals(myCheckInfo.getStatus())) {
            holder.status_txt.setText("待付款");
        }else if("1".equals(myCheckInfo.getStatus())){
            holder.status_txt.setText("已支付");
        }

		return convertView;
	}
	
	
	public class ViewHolder{
		
        ImageView icon_iv;
        TextView title_txt;
        TextView money_txt;
        LinearLayout label_lay;
        TextView order_number_txt;
        TextView consumer_txt;
        TextView pay_money_txt;
        TextView time_txt;
        TextView status_txt;

		public ViewHolder(View view){

            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            title_txt = (TextView) view.findViewById(R.id.title_txt);
            money_txt = (TextView) view.findViewById(R.id.money_txt);
            order_number_txt = (TextView) view.findViewById(R.id.order_number_txt);
            consumer_txt = (TextView) view.findViewById(R.id.consumer_txt);
            pay_money_txt = (TextView) view.findViewById(R.id.pay_money_txt);
            time_txt = (TextView) view.findViewById(R.id.time_txt);
            status_txt = (TextView) view.findViewById(R.id.status_txt);
            label_lay = (LinearLayout) view.findViewById(R.id.label_lay);

        }
	}

}
