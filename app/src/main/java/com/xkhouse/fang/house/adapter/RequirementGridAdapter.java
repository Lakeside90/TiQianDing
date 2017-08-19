package com.xkhouse.fang.house.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.CommonType;

import java.util.ArrayList;
import java.util.List;

/**
* @Description: 定制需求--特色需求 
* @author wujian  
* @date 2015-9-21 上午11:28:41
 */
public class RequirementGridAdapter extends BaseAdapter {

	private Context context;
	private List<Boolean> status = new ArrayList<Boolean>(); //默认图标
	private List<CommonType> featureList; //名称
    private ArrayList<String> featureIds = new ArrayList<>();

	public RequirementGridAdapter(List<CommonType> featureList, Context context){
		this.featureList = featureList;
		this.context = context;
		initData();
	}
	
	private void initData(){
		status.clear();
		for(int i = 0; i < featureList.size(); i++) {
			status.add(false);
		}
	}

    public RequirementGridAdapter(List<CommonType> featureList, ArrayList<String> featureIds, Context context){
        this.featureList = featureList;
        this.context = context;
        status.clear();
        for(int i = 0; i < featureList.size(); i++) {
            status.add(false);
        }

        if (featureIds != null && featureIds.size() > 0){
            for(int i = 0 ; i < featureIds.size(); i++){
                for (int j=0; j < this.featureList.size(); j++){
                    if(this.featureList.get(j).getId().equals(featureIds.get(i))){
                        status.set(j, true);
                        break;
                    }
                }
            }
        }

    }
	
	@Override
	public int getCount() {
		return featureList.size();
	}

	@Override
	public Object getItem(int position) {
		return featureList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_requirement_grid, null);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(status.get(position)){
						status.set(position, false);
					}else {
						status.set(position, true);
					}
					notifyDataSetChanged();
				}
			});
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		CommonType type = featureList.get(position);
		holder.name_txt.setText(type.getName());
		if(status.get(position)){
			holder.name_txt.setTextColor(context.getResources().getColor(R.color.white));
			holder.name_txt.setBackgroundColor(context.getResources().getColor(R.color.common_green));
		}else{
			holder.name_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
			holder.name_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
		}
		
		return convertView;
	}

	public class ViewHolder{
		TextView name_txt;
		
		public ViewHolder(View view){
			name_txt = (TextView) view.findViewById(R.id.name_txt);
		}
	}
	
	
	public String getfeatureIdsSelect() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < status.size(); i++) {
			if(status.get(i)){
				sb.append(featureList.get(i).getId() + ",");
			}
		}
		if(sb.length() > 1){
			return sb.substring(0, sb.length()-1);
		}else{
			return null;
		}
	}
	
}
