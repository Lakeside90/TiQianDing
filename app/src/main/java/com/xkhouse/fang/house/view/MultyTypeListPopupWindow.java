package com.xkhouse.fang.house.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.CommonType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
* @Description: 多选类型列表 
* @author wujian  
* @date 2015-6-15 下午4:48:41
 */
public class MultyTypeListPopupWindow extends PopupWindow{

	private Context context;
	private View rootView;

	private View bg_lay;
	
	/***  两级类型   ***/
	private ListView parent_listview;
	private ParentTypeAdapter parentTypeAdapter;
	private ListView child_listview;
	private ChildTypeAdapter childTypeAdapter;
	private List<CommonType> parentTypes;
	private List<CommonType> childTypes;
	private MultyTypeListClickListener multyTypeListClickListener;
	private int parentIndex = 0;  //当前选中的一级分类的索引
	private int childIndex = -1;   //当前选中的二级分类的索引
	
	private HashMap<String, String> selectIndex = new HashMap<String, String>();
	private HashMap<String, String> tempSelectIndex = new HashMap<String, String>();
	
	private LinearLayout multy_btn_lay;
	private Button cancel_btn;
	private Button confirm_btn;

	public View getView() {
        return rootView;
    }
	
	
	/**
	 * 两级类型
	 * @param context
	 * @param multyTypeListClickListener
	 */
	public MultyTypeListPopupWindow(Context context, 
			MultyTypeListClickListener multyTypeListClickListener) {
		 this.context = context;
		 this.multyTypeListClickListener = multyTypeListClickListener;
		 LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 rootView = inflater.inflate(R.layout.view_common_type_child_list, null);
		 
		 this.setContentView(rootView);
		 this.setWidth(LayoutParams.MATCH_PARENT);
		 this.setHeight(LayoutParams.MATCH_PARENT);
		 this.setBackgroundDrawable(new BitmapDrawable());
         this.setAnimationStyle(R.style.TypePopupAnimation);
         
         bg_lay = rootView.findViewById(R.id.bg_lay);
         parent_listview = (ListView) rootView.findViewById(R.id.parent_listview);
         child_listview = (ListView) rootView.findViewById(R.id.child_listview);
         
         cancel_btn = (Button) rootView.findViewById(R.id.cancel_btn);
         confirm_btn = (Button) rootView.findViewById(R.id.confirm_btn);
         multy_btn_lay = (LinearLayout) rootView.findViewById(R.id.multy_btn_lay);
         multy_btn_lay.setVisibility(View.VISIBLE);
         
         setListener();
	}
	
	
	
	 
	 private void setListener() {
		 bg_lay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissWithResetData();
			}
		});
		 
		 cancel_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MultyTypeListPopupWindow.this.dismiss();
				reSetSelectIndex();
				if (multyTypeListClickListener != null) {
					multyTypeListClickListener.onCancelClick();
				}
			}
		});
		 
		 confirm_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MultyTypeListPopupWindow.this.dismiss();
				setSelectIndex();
				reSetTempSelectIndex();
				if (multyTypeListClickListener != null) {
					multyTypeListClickListener.onConfirmClick(selectIndex);
				}
			}
		});
		 
	 }
	 
	 private void reSetTempSelectIndex() {
		 for(int i = 0; i < tempSelectIndex.size(); i++){
			 tempSelectIndex.put(String.valueOf(i), selectIndex.get(String.valueOf(i)));
		 }
		 parentIndex = 0;
	 }
	 
	 private void reSetSelectIndex() {
		 for(int i = 0; i < selectIndex.size(); i++){
			 selectIndex.put(String.valueOf(i), "0");
			 tempSelectIndex.put(String.valueOf(i), "0");
		 }
		 parentIndex = 0;
	 }
	 
	 private void setSelectIndex() {
		 for(int i = 0; i < selectIndex.size(); i++){
			 selectIndex.put(String.valueOf(i), tempSelectIndex.get(String.valueOf(i)));
		 }
	 }
	 
	 /**
	 * @Description: 除了重置和确定外，通过其他方式关闭popupwindow，调用这个方法 
	 * @return void    返回类型
	  */
	 public void dismissWithResetData() {
        MultyTypeListPopupWindow.this.dismiss();
        reSetTempSelectIndex();
    }
	 
	 
	 public void fillParentData(List<CommonType> commonTypes){
		 if(selectIndex.size() != commonTypes.size()){
			 //初始化标记
			 selectIndex.clear();
			 tempSelectIndex.clear();
			 for(int i = 0; i < commonTypes.size(); i++){
				 selectIndex.put(String.valueOf(i), "0");
				 tempSelectIndex.put(String.valueOf(i), "0");
			 }
		 }
		 
		 this.parentTypes = commonTypes;
		 if(parentTypeAdapter == null){
			 parentTypeAdapter = new ParentTypeAdapter();
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

			//该父类中的子类不在第一个就让父类高亮
			if(!"0".equals(tempSelectIndex.get(String.valueOf(position))) || parentIndex == position) {
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
					childTypes = parentTypes.get(position).getChild();
					parentIndex = position;
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
				
				if (String.valueOf(position).equals(tempSelectIndex.get(String.valueOf(parentIndex)))) {
					holder.type_name_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
				} else{
					holder.type_name_txt.setTextColor(context.getResources().getColor(R.color.common_black_txt));
				}
				
				convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						childIndex = position;
						tempSelectIndex.put(String.valueOf(parentIndex), String.valueOf(childIndex));
						
						childTypeAdapter.notifyDataSetChanged();
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
	 public interface MultyTypeListClickListener {
		 
		 void onCancelClick();
		 
		 void onConfirmClick(HashMap<String, String> selectIndex);
	 }
	 
}
