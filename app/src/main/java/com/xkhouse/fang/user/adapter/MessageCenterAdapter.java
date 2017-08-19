package com.xkhouse.fang.user.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.XKMessage;
import com.xkhouse.lib.utils.DateUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
* @Description: 我的消息
* @author wujian  
* @date 2015-10-10 上午11:58:41
 */
public class MessageCenterAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKMessage> messageList;
	
	public MessageCenterAdapter(Context context, ArrayList<XKMessage> messageList){
		this.context = context;
		this.messageList = messageList;
		
	}
	
	public void setData(ArrayList<XKMessage> messageList){
		this.messageList = messageList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_message_center_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		
		XKMessage message = messageList.get(position);
		
		holder.message_title_txt.setText(message.getTitle());

        if (StringUtil.isEmpty(message.getContent())){
            if ("每日要闻".equals(message.getTitle())){
                holder.message_content_txt.setText("暂无要闻");
            }else if ("最新活动".equals(message.getTitle())){
                holder.message_content_txt.setText("暂无活动");
            }else if ("系统消息".equals(message.getTitle())){
                holder.message_content_txt.setText("暂无消息");
            }
        }else{
            holder.message_content_txt.setText(message.getContent());
        }


		long time = 0;
		try {
			time = Long.parseLong(message.getDate());
		} catch (Exception e) {
		}
		holder.message_date_txt.setText(getDate(time, "MM月dd日"));
		
		holder.message_icon.setImageResource(message.getIconRes());

        if (message.isRead()){
            holder.msg_read_iv.setVisibility(View.VISIBLE);
        }else{
            holder.msg_read_iv.setVisibility(View.INVISIBLE);
        }
		return convertView;
	}
	
	
	public class ViewHolder{
		
		ImageView message_icon;
		ImageView msg_read_iv;
		TextView message_title_txt;
		TextView message_content_txt;
		TextView message_date_txt;
		
		public ViewHolder(View view){
			
			message_icon = (ImageView) view.findViewById(R.id.message_icon);
            msg_read_iv = (ImageView) view.findViewById(R.id.msg_read_iv);

			message_title_txt = (TextView) view.findViewById(R.id.message_title_txt);
			message_content_txt = (TextView) view.findViewById(R.id.message_content_txt);
			message_date_txt = (TextView) view.findViewById(R.id.message_date_txt);
			
		}
	}

	private String getDate(long time, String format){
		String str = "";
		if(time == 0){
			return str;
		}
		try {
			Date date = new Date(time);
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			str = sdf.format(date);
		} catch (Exception e) {
		}
		
		return str;
	}
}
