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
import com.xkhouse.fang.booked.adapter.CommentImageAdapter;
import com.xkhouse.fang.booked.entity.CommentInfo;
import com.xkhouse.fang.user.entity.MSGSystem;
import com.xkhouse.fang.user.entity.MyCommentInfo;
import com.xkhouse.fang.widget.ScrollGridView;

import java.util.ArrayList;

/**
 * 我的评价
 */
public class MyCommentAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MyCommentInfo> commentInfoList;
	private ArrayList<Boolean> openStatus;
	private DisplayImageOptions options;
    private MyCommentItemClickListener clickListener;

    private LinearLayout.LayoutParams lps;

	public MyCommentAdapter(Context context, ArrayList<MyCommentInfo> commentInfoList,
                            ArrayList<Boolean> openStatus, MyCommentItemClickListener clickListener){
		this.context = context;
		this.commentInfoList = commentInfoList;
        this.clickListener = clickListener;
        this.openStatus = openStatus;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
        lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.leftMargin = DisplayUtil.dip2px(context, 3);
	}
	
	public void setData(ArrayList<MyCommentInfo> commentInfoList, ArrayList<Boolean> openStatus){

		this.commentInfoList = commentInfoList;
        this.openStatus = openStatus;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return commentInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return commentInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_my_comment_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

        MyCommentInfo commentInfo = commentInfoList.get(position);

        ImageLoader.getInstance().displayImage(commentInfo.getCover_banner(), holder.icon_iv, options);

        holder.title_txt.setText(commentInfo.getBusiness_name());
        holder.money_txt.setText("人均：¥" + commentInfo.getAverage_consump());

        String[] labels = commentInfo.getBusiness_label();
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

        holder.content_txt.setText(commentInfo.getMember_content());

        holder.time_txt.setText("发表于：" + commentInfo.getCreate_time());

        holder.reply_txt.setText(commentInfo.getBusiness_content());

        if (openStatus.get(position)) {
            holder.look_reply_txt.setCompoundDrawables(null, null,
                    context.getResources().getDrawable(R.drawable.open_t),null);
            holder.reply_txt.setVisibility(View.VISIBLE);
        }else {
            holder.look_reply_txt.setCompoundDrawables(null, null,
                    context.getResources().getDrawable(R.drawable.open_b),null);
            holder.reply_txt.setVisibility(View.GONE);
        }

        if (commentInfo.getBusiness_img() != null &&
                commentInfo.getBusiness_img().length > 0) {
            holder.image_grid_view.setVisibility(View.VISIBLE);
            holder.image_grid_view.setAdapter(new CommentImageAdapter(context, commentInfo.getBusiness_img()));
        } else {
            holder.image_grid_view.setVisibility(View.GONE);
        }

        holder.delete_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.onDelete(position);
            }
        });

        holder.look_reply_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.onToggle(position);
            }
        });

		return convertView;
	}


	public class ViewHolder{

		ImageView icon_iv;
        TextView title_txt;
        TextView money_txt;
        LinearLayout label_lay;
        TextView content_txt;
        TextView time_txt;
        TextView delete_txt;
        TextView look_reply_txt;
        TextView reply_txt;
        ScrollGridView image_grid_view;


		public ViewHolder(View view){

            icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
            title_txt = (TextView) view.findViewById(R.id.title_txt);
            money_txt = (TextView) view.findViewById(R.id.money_txt);
            label_lay = (LinearLayout) view.findViewById(R.id.label_lay);
            content_txt = (TextView) view.findViewById(R.id.content_txt);
            time_txt = (TextView) view.findViewById(R.id.time_txt);
            delete_txt = (TextView) view.findViewById(R.id.delete_txt);
            look_reply_txt = (TextView) view.findViewById(R.id.look_reply_txt);
            reply_txt = (TextView) view.findViewById(R.id.reply_txt);
            image_grid_view = (ScrollGridView) view.findViewById(R.id.image_grid_view);
        }
	}


	public interface MyCommentItemClickListener {

        void onDelete(int position);

        void onToggle(int position);
    }

}
