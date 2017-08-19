package com.xkhouse.fang.house.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.HQuestion;

import java.util.ArrayList;

/**
* @Description: 楼盘问答列表
* @author wujian  
* @date 2016-6-23
 */
public class HouseQuestionAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<HQuestion> questionList;

	public HouseQuestionAdapter(Context context, ArrayList<HQuestion> questionList){
		this.context = context;
		this.questionList = questionList;
	}
	
	public void setData(ArrayList<HQuestion> questionList) {
		this.questionList = questionList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return questionList.size();
	}

	@Override
	public Object getItem(int position) {
		return questionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_house_question_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

        HQuestion question = questionList.get(position);
        holder.question_txt.setText(question.getQuestion());
        holder.replay_txt.setText(question.getReply());
        holder.replay_time_txt.setText(question.getReplyTime());

		return convertView;
	}
	
	public class ViewHolder{

        TextView question_txt;
        TextView replay_txt;
        TextView replay_time_txt;

		public ViewHolder(View view){
            question_txt = (TextView) view.findViewById(R.id.question_txt);
            replay_txt = (TextView) view.findViewById(R.id.replay_txt);
            replay_time_txt = (TextView) view.findViewById(R.id.replay_time_txt);
		}
	}

}
