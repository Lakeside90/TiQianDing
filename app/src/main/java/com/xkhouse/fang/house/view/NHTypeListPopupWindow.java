package com.xkhouse.fang.house.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.CommonType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
* @Description: 类型列表 
* @author wujian  
* @date 2015-6-15 下午4:48:41
 */
public class NHTypeListPopupWindow extends PopupWindow{

	private Context context;
	private View rootView;

	private View bg_lay;

	/***  一级类型   ***/
	private ListView type_listview;
	private TypeAdapter adapter;
	private List<String> types;
	private CommonTypeListClickListener typeListClickListener;
	private int index = 0;   //当前选中的分类的索引

	/***  两级类型   ***/
	private ListView parent_listview;
	private ParentTypeAdapter parentTypeAdapter;
	private ListView child_listview;
	private ChildTypeAdapter childTypeAdapter;
	private List<CommonType> parentTypes;
	private List<CommonType> childTypes;
	private ChildTypeListClickListener childTypeListClickListener;
	private int parentIndex = 0;  //当前选中的一级分类的索引
//	private int childIndex = 0;   //当前选中的二级分类的索引
	private HashMap<String, String> selectIndex = new HashMap<String, String>();

	public View getView() {
        return rootView;
    }

	/**
	 * 只有一级类型
	 * @param context
	 * @param typeListClickListener
	 */
	public NHTypeListPopupWindow(Context context,
                                 CommonTypeListClickListener typeListClickListener) {
		 this.context = context;
		 this.typeListClickListener = typeListClickListener;
		 LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 rootView = inflater.inflate(R.layout.view_newhouse_type_list, null);

		 this.setContentView(rootView);
		 this.setWidth(LayoutParams.MATCH_PARENT);
		 this.setHeight(LayoutParams.MATCH_PARENT);
		 this.setBackgroundDrawable(new BitmapDrawable());
         this.setAnimationStyle(R.style.TypePopupAnimation);

         bg_lay = rootView.findViewById(R.id.bg_lay);
		 type_listview = (ListView) rootView.findViewById(R.id.type_listview);
	     setListener();
	}


	/**
	 * 两级类型
	 * @param context
	 * @param childTypeListClickListener
	 */
	public NHTypeListPopupWindow(Context context, boolean isMoreType,
                                 ChildTypeListClickListener childTypeListClickListener) {
		 this.context = context;
		 this.childTypeListClickListener = childTypeListClickListener;
		 LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 rootView = inflater.inflate(R.layout.view_newhouse_type_child_list, null);
		 
		 this.setContentView(rootView);
		 this.setWidth(LayoutParams.MATCH_PARENT);
		 this.setHeight(LayoutParams.MATCH_PARENT);
		 this.setBackgroundDrawable(new BitmapDrawable());
         this.setAnimationStyle(R.style.TypePopupAnimation);
         
         bg_lay = rootView.findViewById(R.id.bg_lay);
         parent_listview = (ListView) rootView.findViewById(R.id.parent_listview);
         child_listview = (ListView) rootView.findViewById(R.id.child_listview);
         
         setListener();
        
	}
	
	
	
	 
	 private void setListener() {
		 bg_lay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NHTypeListPopupWindow.this.dismiss();
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
	 
	 
	 
	 public void fillParentData(List<CommonType> commonTypes){
		 this.parentTypes = commonTypes;
		 if(parentTypeAdapter == null){
			 parentTypeAdapter = new ParentTypeAdapter();
		 }
		 //初始化选中位置, 每个父类的第一个子类
         for(int i = 0; i < parentTypes.size(); i++){
        	 selectIndex.put(String.valueOf(i), "0");
         }
		 parent_listview.setAdapter(parentTypeAdapter);
		 
		 childTypes = parentTypes.get(0).getChild();
		 if(childTypes == null){
			 childTypes = new ArrayList<CommonType>();
		 }
		 if (childTypeAdapter == null) {
			 childTypeAdapter = new ChildTypeAdapter();
		 }
		 child_listview.setAdapter(childTypeAdapter);
		 
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
				convertView = LayoutInflater.from(context).inflate(R.layout.item_common_type_list, null);
				holder = new TypeViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (TypeViewHolder) convertView.getTag();
			}
			
			holder.type_name_txt.setText(types.get(position));
			if (index == position) {
				holder.type_name_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
				holder.item_bg_lay.setBackgroundColor(context.getResources().getColor(R.color.white));
			}else{
				holder.type_name_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
				holder.item_bg_lay.setBackgroundColor(context.getResources().getColor(R.color.window_bg));
			}
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					index = position;
					adapter.notifyDataSetChanged();
					if(typeListClickListener != null){
						typeListClickListener.onTypeClick(position);
					}
				}
			});
			
			return convertView;
		}
		 
		public class TypeViewHolder{
			
			TextView type_name_txt;
			View item_bg_lay;
			public TypeViewHolder(View view){
				type_name_txt = (TextView) view.findViewById(R.id.type_name_txt);
				item_bg_lay = view.findViewById(R.id.item_bg_lay);
			}
		}
	 }
	
	 
	class ParentTypeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return parentTypes.size();
		}

		@Override
		public Object getItem(int position) {
			return parentTypes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ParentTypeViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.item_common_type_parent_list, null);
				holder = new ParentTypeViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ParentTypeViewHolder) convertView.getTag();
			}
			CommonType type = parentTypes.get(position);
			
			holder.type_name_txt.setText(type.getName());
			if (parentIndex == position) {
				holder.type_name_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
				holder.arrow_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
				holder.item_bg_lay.setBackgroundColor(context.getResources().getColor(R.color.white));
			}else{
				holder.type_name_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
				holder.arrow_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
				holder.item_bg_lay.setBackgroundColor(context.getResources().getColor(R.color.window_bg));
			}
			
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (childTypeListClickListener != null) {
						childTypeListClickListener.onParentClick(position);
					}
					childTypes = parentTypes.get(position).getChild();
					parentIndex = position;
//					childIndex = -1;
					//TODO  
					childTypeAdapter.notifyDataSetChanged();
					parentTypeAdapter.notifyDataSetChanged();
				}
			});

			return convertView;
		}

		public class ParentTypeViewHolder {

			TextView type_name_txt;
			TextView arrow_txt;
			View item_bg_lay;
			
			public ParentTypeViewHolder(View view) {
				type_name_txt = (TextView) view.findViewById(R.id.type_name_txt);
				arrow_txt = (TextView) view.findViewById(R.id.arrow_txt);
				item_bg_lay = view.findViewById(R.id.item_bg_lay);
			}
		}
	}
	 
	 
	 class ChildTypeAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				return childTypes.size();
			}

			@Override
			public Object getItem(int position) {
				return childTypes.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {

				TypeViewHolder holder = null;
				if(convertView == null){
					convertView = LayoutInflater.from(context).inflate(R.layout.item_common_type_child_list, null);
					holder = new TypeViewHolder(convertView);
					convertView.setTag(holder);
				}else{
					holder = (TypeViewHolder) convertView.getTag();
				}
				
				CommonType type = childTypes.get(position);
				holder.type_name_txt.setText(type.getName());
				
				if (String.valueOf(position).equals(selectIndex.get(String.valueOf(parentIndex)))) {
					holder.type_name_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
				}else{
					holder.type_name_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
				}
				
				convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						for(HashMap.Entry<String, String> entry : selectIndex.entrySet()){    
						     entry.setValue("0");   
						}  
						selectIndex.put(String.valueOf(parentIndex), String.valueOf(position));
						childTypeAdapter.notifyDataSetChanged();
						if(childTypeListClickListener != null){
							childTypeListClickListener.onChildClick(parentIndex, position);
						}
						
					}
				});
				
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
	 public interface CommonTypeListClickListener {
		 
		 void onTypeClick(int position);
	 }
	 
	 
	 public interface ChildTypeListClickListener {
		 
		 void onParentClick(int position);
		 
		 void onChildClick(int parent, int child);
	 }
	 
}
