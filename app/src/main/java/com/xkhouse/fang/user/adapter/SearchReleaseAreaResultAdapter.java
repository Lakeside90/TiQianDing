package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.CommonType;

import java.util.ArrayList;

/** 
 * @Description: 房源发布--小区搜索结果
 * @author wujian  
 * @date 2016-04-19
 */
public class SearchReleaseAreaResultAdapter extends BaseAdapter {

	private Context context;

	private ArrayList<CommonType> resultList;

	public SearchReleaseAreaResultAdapter(Context context, ArrayList<CommonType> resultList){
		this.context = context;
		this.resultList = resultList;
	}
	
	public void setData(ArrayList<CommonType> resultList){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_search_project_result_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

        CommonType result = resultList.get(position);
		
		holder.result_key_txt.setText(result.getName());
		

		return convertView;
	}
	
	public class ViewHolder{
		
		TextView result_key_txt;
		TextView result_type_txt;
		
		public ViewHolder(View view){
			
			result_key_txt = (TextView) view.findViewById(R.id.result_key_txt);
			result_type_txt = (TextView) view.findViewById(R.id.result_type_txt);
		}
	}

}
