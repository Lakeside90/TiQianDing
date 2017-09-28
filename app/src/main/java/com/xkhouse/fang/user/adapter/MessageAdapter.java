package com.xkhouse.fang.user.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.MSGSystem;
import com.xkhouse.fang.user.entity.MessageInfo;

/**
* @Description: 我的消息
* @author wujian  
* @date 2015-11-4 下午8:36:06
 */
public class MessageAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MessageInfo> messageList;
	
	public MessageAdapter(Context context, ArrayList<MessageInfo> messageList){
		this.context = context;
		this.messageList = messageList;
	}
	
	public void setData(ArrayList<MessageInfo> messageList){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_msg_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		MessageInfo messageInfo = messageList.get(position);

        holder.title_txt.setText(messageInfo.getType());
        holder.content_txt.setText(messageInfo.getContent());
        holder.date_txt.setText(messageInfo.getCreate_time());
		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		TextView title_txt;
		TextView content_txt;
		TextView date_txt;
		
		public ViewHolder(View view){

            title_txt = (TextView) view.findViewById(R.id.title_txt);
			content_txt = (TextView) view.findViewById(R.id.content_txt);
			date_txt = (TextView) view.findViewById(R.id.date_txt);
			
		}
	}

}
