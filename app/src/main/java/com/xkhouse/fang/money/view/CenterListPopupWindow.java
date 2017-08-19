package com.xkhouse.fang.money.view;

import java.util.List;

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


/**
* @Description: 表单类型选择 
* @author wujian  
* @date 2015-9-10 上午8:47:27
 */
public class CenterListPopupWindow extends PopupWindow{

	private Context context;
	private View rootView;

	private ListView type_listview; 
	private TypeAdapter adapter;
	private List<String> types;
	
	private CommonTypeListClickListener typeListClickListener;
	
	public View getView() {
        return rootView;
    }
	
	
	public CenterListPopupWindow(Context context, 
			CommonTypeListClickListener typeListClickListener) {
		 this.context = context;
		 this.typeListClickListener = typeListClickListener;
		 LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 rootView = inflater.inflate(R.layout.view_center_list, null);
		 
		 this.setContentView(rootView);
		 this.setWidth(LayoutParams.MATCH_PARENT);
		 this.setHeight(LayoutParams.MATCH_PARENT);
		 this.setBackgroundDrawable(new BitmapDrawable());
		 initView();
	     setListener();
	}

	 private void initView() {
		 type_listview = (ListView) rootView.findViewById(R.id.type_listview);
		
	 }
	 
	 private void setListener() {
		 rootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CenterListPopupWindow.this.dismiss();
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

			ViewHolder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.item_common_type_list, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.type_name_txt.setText(types.get(position));
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(typeListClickListener != null){
						typeListClickListener.onTypeClick(position);
					}
				}
			});
			
			return convertView;
		}
		 
		public class ViewHolder{
			
			TextView type_name_txt;
			
			public ViewHolder(View view){
				type_name_txt = (TextView) view.findViewById(R.id.type_name_txt);
			}
		}
	 }
	
	 public interface CommonTypeListClickListener {
		 
		 void onTypeClick(int position);
	 }
	 
}
