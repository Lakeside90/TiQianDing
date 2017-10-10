package com.xkhouse.fang.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.booked.entity.AccountInfo;

import java.util.ArrayList;

/**
* 账户明细
 */
public class EmployeeAccountInfoAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<AccountInfo> balanceList;
	private ModelApplication modelApp;

	public EmployeeAccountInfoAdapter(Context context, ArrayList<AccountInfo> balanceList){
		this.context = context;
		this.balanceList = balanceList;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
	}
	
	public void setData(ArrayList<AccountInfo> balanceList){
		this.balanceList = balanceList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return balanceList.size();
	}

	@Override
	public Object getItem(int position) {
		return balanceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_account_info_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		AccountInfo accountInfo = new AccountInfo();

		if ("1".equals(accountInfo.getType())) {
            holder.title_txt.setText("打赏");
			holder.money_txt.setText("+¥" + accountInfo.getMoney());
			holder.money_txt.setTextColor(context.getResources().getColor(R.color.common_green));
        } else if ("2".equals(accountInfo.getType())) {
            holder.title_txt.setText("提现");
			holder.money_txt.setText("-¥" + accountInfo.getMoney());
			holder.money_txt.setTextColor(context.getResources().getColor(R.color.common_red));
        }

        holder.date_txt.setText(accountInfo.getCreate_time());

        holder.content_txt.setVisibility(View.INVISIBLE);

		return convertView;
	}
	
	
	public class ViewHolder{
		
		TextView title_txt;
		TextView date_txt;
		TextView content_txt;
		TextView money_txt;

		public ViewHolder(View view){
			
			title_txt = (TextView) view.findViewById(R.id.title_txt);
			date_txt = (TextView) view.findViewById(R.id.date_txt);
			content_txt = (TextView) view.findViewById(R.id.content_txt);
            money_txt = (TextView) view.findViewById(R.id.money_txt);

		}
	}

}
