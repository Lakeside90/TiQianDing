package com.xkhouse.fang.app.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.widget.pinyin.AssortPinyinList;
import com.xkhouse.fang.widget.pinyin.LanguageComparator_CN;

/**
 * 
* @Description: 切换城市，按首字母排序（ps：热门城市排在前面，所以让其首字为数字，分在"#"组） 
* @author wujian  
* @date 2015-9-22 上午9:41:55
 */
public class CityListAdapter extends BaseExpandableListAdapter {

	// 字符串
	private List<String> strList;

	private AssortPinyinList assort = new AssortPinyinList();

	private Context context;
	private ArrayList<Site> sites;
	private SiteSelectListener siteSelectListener;
	
	// 中文排序
	private LanguageComparator_CN cnSort = new LanguageComparator_CN();

	
	public CityListAdapter(Context context, ArrayList<Site> sites, SiteSelectListener siteSelectListener) {
		super();
		this.context = context;
		this.sites = sites;
		this.siteSelectListener = siteSelectListener;
		// 排序
		sort();
	}

	public void setData(ArrayList<Site> sites) {
		this.sites = sites;
		sort();
	}
	
	private void sort() {
		strList = new ArrayList<String>();
		for (int i = 0; i < sites.size(); i++) {
			strList.add(sites.get(i).getArea());
			if("1".equals(sites.get(i).getIsHot())){
				strList.add("1" + sites.get(i).getArea());
			}
		}
		
		// 分类
		for (String str : strList) {
			assort.getHashList().add(str);
		}
		assort.getHashList().sortKeyComparator(cnSort);
		for (int i = 0, length = assort.getHashList().size(); i < length; i++) {
			Collections.sort((assort.getHashList().getValueListIndex(i)),
					cnSort);
		}

	}

	public Object getChild(int group, int child) {
		return assort.getHashList().getValueIndex(group, child);
	}

	public long getChildId(int group, int child) {
		return child;
	}

	public View getChildView(int group, int child, boolean arg2,
			View contentView, ViewGroup arg4) {
		
		ChildViewHolder holder = null;
		if(contentView == null){
			contentView = LayoutInflater.from(context).inflate(R.layout.item_site_list_child, null);
			holder = new ChildViewHolder(contentView);
			contentView.setTag(holder);
		}else{
			holder = (ChildViewHolder) contentView.getTag();
		}
		
		final String name = assort.getHashList().getValueIndex(group, child);
		if(name.contains("1")){
			holder.site_area_txt.setText(name.replaceAll("1", ""));
		}else{
			holder.site_area_txt.setText(name);
		}
		

		
		contentView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (siteSelectListener != null) {
					if(name.contains("1")){
						siteSelectListener.onSelected(name.replaceAll("1", ""));
					}else{
						siteSelectListener.onSelected(name);
					}
					
				}
			}
		});
		
		return contentView;
	}

	public int getChildrenCount(int group) {
		return assort.getHashList().getValueListIndex(group).size();
	}

	public Object getGroup(int group) {
		return assort.getHashList().getValueListIndex(group);
	}

	public int getGroupCount() {
		return assort.getHashList().size();
	}

	public long getGroupId(int group) {
		return group;
	}

	public View getGroupView(int group, boolean arg1, View contentView,
			ViewGroup arg3) {
		
		GroupViewHolder holder = null;
		if(contentView == null){
			contentView = LayoutInflater.from(context).inflate(R.layout.item_site_list_group, null);
			holder = new GroupViewHolder(contentView);
			contentView.setTag(holder);
		}else{
			holder = (GroupViewHolder) contentView.getTag();
		}
		
		String name_key = assort.getFirstChar(assort.getHashList().getValueIndex(group, 0));
		if("#".equals(name_key)){
			name_key = "热门城市";
		}
		holder.name_key_txt.setText(name_key);

		return contentView;
	}
	
	
	
	public class GroupViewHolder{
		TextView name_key_txt;
		
		public GroupViewHolder(View view){
			name_key_txt = (TextView) view.findViewById(R.id.name_key_txt);
		}
	}
	
	public class ChildViewHolder{
		TextView site_area_txt;
		
		public ChildViewHolder(View view){
			site_area_txt = (TextView) view.findViewById(R.id.site_area_txt);
		}
	}
	

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	public AssortPinyinList getAssort() {
		return assort;
	}

	
	public interface SiteSelectListener {
		public void onSelected(String name);
	}
}
