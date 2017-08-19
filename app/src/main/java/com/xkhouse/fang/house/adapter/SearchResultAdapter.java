package com.xkhouse.fang.house.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description: 搜索结果 
 * @author wujian  
 * @date 2015-8-27 下午6:58:09  
 */
public class SearchResultAdapter extends BaseAdapter {

	private Context context;
	
	private ArrayList<CommonType> resultList;
	
	public SearchResultAdapter(Context context, ArrayList<CommonType> resultList){
		this.context = context;
		this.resultList = resultList;
	}

	public void setData(ArrayList<CommonType> resultList) {
		this.resultList = resultList;
		notifyDataSetChanged();
	}
	
	
	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public Object getItem(int position) {
		return resultList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_search_result_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		CommonType result = resultList.get(position);
		
		holder.result_key_txt.setText(result.getName());
		
		if(StringUtil.isEmpty(result.getId())){
			holder.result_num_txt.setText("约0条");
		}else{
			holder.result_num_txt.setText("约" + result.getId() + "条");
		}
		
		
		return convertView;
	}
	
	public class ViewHolder{
		
		TextView result_key_txt;
		TextView result_num_txt;
		
		public ViewHolder(View view){
			
			result_key_txt = (TextView) view.findViewById(R.id.result_key_txt);
			result_num_txt = (TextView) view.findViewById(R.id.result_num_txt);
		}
	}

}
