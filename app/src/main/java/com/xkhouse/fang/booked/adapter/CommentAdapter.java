package com.xkhouse.fang.booked.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.booked.entity.CommentInfo;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.fang.widget.circle.CircleImageView;

import java.util.ArrayList;

/**
 * 评论--商家详情
 */
public class CommentAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CommentInfo> commentInfoList;

	private DisplayImageOptions options;


	public CommentAdapter(Context context, ArrayList<CommentInfo> recommendList){
		this.context = context;
		this.commentInfoList = recommendList;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nopic)   // 加载的图片
				.showImageOnFail(R.drawable.nopic) // 错误的时候的图片
				.showImageForEmptyUri(R.drawable.nopic)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				.cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<CommentInfo> recommendList){
		this.commentInfoList = recommendList;
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
		
		ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		
		CommentInfo commentInfo = commentInfoList.get(position);

		ImageLoader.getInstance().displayImage(commentInfo.getHead_img(), holder.user_icon_iv, options);

		holder.nick_name_txt.setText(commentInfo.getNickname());
		holder.time_txt.setText(commentInfo.getCreate_time());

		int num = Integer.valueOf(commentInfo.getStar_num());
		showStarView(num, holder);

		holder.content_txt.setText(commentInfo.getContent());

		if (commentInfo.getImageUrls() != null &&
				commentInfo.getImageUrls().length > 0) {
			holder.image_grid_view.setVisibility(View.VISIBLE);
			holder.image_grid_view.setAdapter(new CommentImageAdapter(context, commentInfo.getImageUrls()));
		} else {
			holder.image_grid_view.setVisibility(View.GONE);
		}

		return convertView;
	}





	public class ViewHolder{

		CircleImageView user_icon_iv;
		TextView nick_name_txt;
		TextView time_txt;
		ImageView star_one_iv;
		ImageView star_two_iv;
		ImageView star_three_iv;
		ImageView star_four_iv;
		ImageView star_five_iv;
		TextView content_txt;
		ScrollGridView image_grid_view;

		public ViewHolder(View view){

			user_icon_iv = (CircleImageView) view.findViewById(R.id.user_icon_iv);
			nick_name_txt = (TextView) view.findViewById(R.id.nick_name_txt);
			time_txt = (TextView) view.findViewById(R.id.nick_name_txt);
			star_one_iv = (ImageView) view.findViewById(R.id.star_one_iv);
			star_two_iv = (ImageView) view.findViewById(R.id.star_two_iv);
			star_three_iv = (ImageView) view.findViewById(R.id.star_three_iv);
			star_four_iv = (ImageView) view.findViewById(R.id.star_four_iv);
			star_five_iv = (ImageView) view.findViewById(R.id.star_five_iv);
			content_txt = (TextView) view.findViewById(R.id.content_txt);
			image_grid_view = (ScrollGridView) view.findViewById(R.id.image_grid_view);
		}
	}



	/**
	 * 评价星星亮的个数
	 * @param num
	 * @param holder
	 */
	private void showStarView(int num, ViewHolder holder) {

		if (num < 0 || num > 5 || holder == null) return;

		switch (num) {
			case 0:
				holder.star_one_iv.setImageResource(R.drawable.storeup);
				holder.star_two_iv.setImageResource(R.drawable.storeup);
				holder.star_three_iv.setImageResource(R.drawable.storeup);
				holder.star_four_iv.setImageResource(R.drawable.storeup);
				holder.star_five_iv.setImageResource(R.drawable.storeup);
				break;

			case 1:
				holder.star_one_iv.setImageResource(R.drawable.storeup_on);
				holder.star_two_iv.setImageResource(R.drawable.storeup);
				holder.star_three_iv.setImageResource(R.drawable.storeup);
				holder.star_four_iv.setImageResource(R.drawable.storeup);
				holder.star_five_iv.setImageResource(R.drawable.storeup);
				break;

			case 2:
				holder.star_one_iv.setImageResource(R.drawable.storeup_on);
				holder.star_two_iv.setImageResource(R.drawable.storeup_on);
				holder.star_three_iv.setImageResource(R.drawable.storeup);
				holder.star_four_iv.setImageResource(R.drawable.storeup);
				holder.star_five_iv.setImageResource(R.drawable.storeup);
				break;

			case 3:
				holder.star_one_iv.setImageResource(R.drawable.storeup_on);
				holder.star_two_iv.setImageResource(R.drawable.storeup_on);
				holder.star_three_iv.setImageResource(R.drawable.storeup_on);
				holder.star_four_iv.setImageResource(R.drawable.storeup);
				holder.star_five_iv.setImageResource(R.drawable.storeup);
				break;

			case 4:
				holder.star_one_iv.setImageResource(R.drawable.storeup_on);
				holder.star_two_iv.setImageResource(R.drawable.storeup_on);
				holder.star_three_iv.setImageResource(R.drawable.storeup_on);
				holder.star_four_iv.setImageResource(R.drawable.storeup_on);
				holder.star_five_iv.setImageResource(R.drawable.storeup);
				break;

			case 5:
				holder.star_one_iv.setImageResource(R.drawable.storeup_on);
				holder.star_two_iv.setImageResource(R.drawable.storeup_on);
				holder.star_three_iv.setImageResource(R.drawable.storeup_on);
				holder.star_four_iv.setImageResource(R.drawable.storeup_on);
				holder.star_five_iv.setImageResource(R.drawable.storeup_on);
				break;
		}
	}

}
