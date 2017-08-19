package com.xkhouse.fang.house.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.CommonType;

import java.util.List;

/**
 * 购房能力评估--区域
 * Created by wujian on 2016/3/3
 */
public class BuyAbilityAreaAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommonType> childTypes;

    public BuyAbilityAreaAdapter(Context context, List<CommonType> childTypes) {
        this.mContext = context;
        this.childTypes = childTypes;
    }

    public void setData(List<CommonType> childTypes){
        this.childTypes = childTypes;
        this.notifyDataSetChanged();
    }


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
    public View getView(int position, View convertView, ViewGroup parent) {

        TypeViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_newhouse_multy_type_list, null);
            holder = new TypeViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (TypeViewHolder) convertView.getTag();
        }

        CommonType type = childTypes.get(position);
        holder.type_name_txt.setText(type.getName());

        if (type.isSelected()) {
            holder.type_name_txt.setTextColor(mContext.getResources().getColor(R.color.common_red_txt));
            holder.type_name_txt.setBackgroundResource(R.drawable.red_border_btn_bg);
        } else{
            holder.type_name_txt.setTextColor(mContext.getResources().getColor(R.color.c_666666));
            holder.type_name_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
        }

        return convertView;
    }

    public class TypeViewHolder{

        TextView type_name_txt;

        public TypeViewHolder(View view){
            type_name_txt = (TextView) view.findViewById(R.id.type_name_txt);
        }
    }


}
