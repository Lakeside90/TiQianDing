package com.xkhouse.fang.user.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.XKBank;

/**
* @Description: 星空宝选择银行 
* @author wujian  
* @date 2015-10-22 上午10:47:36
 */
public class CashBankListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKBank> bankList;
	
	public CashBankListAdapter(Context context, ArrayList<XKBank> bankList){
		this.context = context;
		this.bankList = bankList;
		
	}
	
	public void setData(ArrayList<XKBank> bankList){
		this.bankList = bankList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return bankList.size();
	}

	@Override
	public Object getItem(int position) {
		return bankList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_cash_bank_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		
		XKBank bank = bankList.get(position);
		
		holder.bank_name_txt.setText(bank.getBankName());
		ImageLoader.getInstance().displayImage(bank.getBankIcon(), holder.bank_icon);
		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		ImageView bank_icon;
		TextView bank_name_txt;
		
		public ViewHolder(View view){
			
			bank_icon = (ImageView) view.findViewById(R.id.bank_icon);
			bank_name_txt = (TextView) view.findViewById(R.id.bank_name_txt);
		}
	}

}
