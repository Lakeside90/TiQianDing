package com.xkhouse.fang.money.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.money.entity.XKBArea;
import com.xkhouse.fang.money.entity.XKBHouse;

/**
* @Description: 客户推荐--期望楼盘 
* @author wujian  
* @date 2015-9-9 下午5:14:06
 */
public class ExpectHouseAdapter extends BaseExpandableListAdapter {
	
    private List<XKBArea> groupAreas;
    private HashMap<String, List<XKBHouse>> childHouses;
    
    private Context context;
    private LayoutInflater groupInflater;
    private LayoutInflater childInflater;

    private ExpectHouseItemClickListener clickListener;
 
    
    public ExpectHouseAdapter(Context context, List<XKBArea> groupAreas,
            HashMap<String, List<XKBHouse>> childHouses, ExpectHouseItemClickListener clickListener) {
       this.groupAreas = groupAreas;
       this.childHouses  = childHouses;
       this.clickListener = clickListener;
       
       groupInflater = LayoutInflater.from(context);
       childInflater = LayoutInflater.from(context);
       
       
    }

    @Override
    public int getGroupCount() {
        return groupAreas == null ? 0 : groupAreas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (childHouses.size() == 0) {
            return 0;
        } else {
            List<XKBHouse> contacts =  childHouses.get(groupAreas.get(groupPosition).getId());
            if (contacts == null || contacts.size() == 0) {
                return 0;
            } else {
                return contacts.size();
            }
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupAreas == null ? null : groupAreas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (childHouses.size() == 0) {
            return null;
        } else {
            return childHouses.get(groupAreas.get(groupPosition).getId()).get(childPosition);
        }
        
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;

        if (convertView == null) {
            convertView = groupInflater.inflate(R.layout.item_expect_house_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.area_name_txt = (TextView) convertView.findViewById(R.id.area_name_txt);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        
        
        XKBArea area = groupAreas.get(groupPosition);
        groupViewHolder.area_name_txt.setText(area.getName());
       
        
        return convertView;
    }
    
    

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = childInflater.inflate(R.layout.item_expect_house_child, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.house_name_txt = (TextView) convertView.findViewById(R.id.house_name_txt);
            childViewHolder.house_check_iv = (ImageView) convertView.findViewById(R.id.house_check_iv);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        
        XKBHouse contact = childHouses.get(groupAreas.get(groupPosition).getId()).get(childPosition);
        childViewHolder.house_name_txt.setText(contact.getName());
        
        if(contact.isSelected()){
        	childViewHolder.house_check_iv.setImageResource(R.drawable.checkbox_checked);
	   	}else{
	   		childViewHolder.house_check_iv.setImageResource(R.drawable.checkbox_normal);
	   	}
 
        convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickListener.onHouseClick(groupPosition, childPosition);
			}
		});
        return convertView;
    }
    
    
    
    

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    
    static class GroupViewHolder {
        TextView area_name_txt;
    }

    static class ChildViewHolder {
    	
        TextView house_name_txt;
        ImageView house_check_iv;
        
    }

    
    public void refreshData(List<XKBArea> groupAreas,
            HashMap<String, List<XKBHouse>> childHouses) {
        this.groupAreas = groupAreas;
        this.childHouses = childHouses;
        notifyDataSetChanged();
    }
    
    
    
    public interface ExpectHouseItemClickListener{
    
    	public void onHouseClick(int groupPosition, int childPosition);
    }
}
