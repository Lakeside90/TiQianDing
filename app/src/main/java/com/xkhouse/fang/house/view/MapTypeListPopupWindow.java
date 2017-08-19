package com.xkhouse.fang.house.view;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xkhouse.fang.R;


/**
* @Description: 地图找房（户型，租金筛选条件列表）
* @author wujian  
* @date 2015-9-16 上午11:24:01
 */
public class MapTypeListPopupWindow extends PopupWindow{

	private Context context;
	private View rootView;

	private ListView type_listview; 
	private TypeAdapter adapter;
	private List<String> types;
	private MapCommonTypeListClickListener typeListClickListener;
	
	public View getView() {
        return rootView;
    }
	
	
	public MapTypeListPopupWindow(Context context, MapCommonTypeListClickListener typeListClickListener) {
		
		 this.context = context;
		 this.typeListClickListener = typeListClickListener;
		 LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 rootView = inflater.inflate(R.layout.view_map_type_list, null);
		 
		 this.setContentView(rootView);
		 this.setWidth(LayoutParams.WRAP_CONTENT);
		 this.setHeight(LayoutParams.WRAP_CONTENT);
		 this.setBackgroundDrawable(new BitmapDrawable());
//         this.setAnimationStyle(R.style.TypePopupAnimation);
         
		 type_listview = (ListView) rootView.findViewById(R.id.type_listview);
		 setListener();
	}
	
	 
	private void setListener() {
		type_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(typeListClickListener != null){
					typeListClickListener.onTypeClick(position);
				}
			}
		});
	}
	
	
	 public void fillData(List<String> data){
		 this.types = data;
		 if(types == null || types.size() < 1){
			 return;
		 }
		 if(adapter == null){
			 adapter = new TypeAdapter();
		 }
		 type_listview.setAdapter(adapter);
	 }
	 
	 
	 
	 
	 /************************************ 列表内容适配器  ********************************************/
	 
	 class TypeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return types.size();
		}

		@Override
		public Object getItem(int position) {
			return types.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			TypeViewHolder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.item_map_type_list, null);
				holder = new TypeViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (TypeViewHolder) convertView.getTag();
			}
			
			holder.type_name_txt.setText(types.get(position));
			
			return convertView;
		}
		 
		public class TypeViewHolder{
			
			TextView type_name_txt;
			public TypeViewHolder(View view){
				type_name_txt = (TextView) view.findViewById(R.id.type_name_txt);
			}
		}
	 }
	
	 
	 
	 /************************************ 列表点击事件回调  ********************************************/
	 public interface MapCommonTypeListClickListener {
		 
		 void onTypeClick(int position);
	 }
}
