package com.xkhouse.fang.house.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.NewsDetailActivity;
import com.xkhouse.fang.app.entity.XKAd;

import java.util.ArrayList;

/**
 * 新房列表里的广告
 * Created by wujian on 2016/2/19.
 */
public class HouseADListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<XKAd> adList;

    public HouseADListAdapter(Context context, ArrayList<XKAd> adList) {
        this.mContext = context;
        this.adList = adList;
    }

    @Override
    public int getCount() {
        return adList.size();
    }

    @Override
    public Object getItem(int position) {
        return adList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TypeViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_house_ad_list, null);
            holder = new TypeViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (TypeViewHolder) convertView.getTag();
        }

        XKAd ad = adList.get(position);
        holder.ad_name_txt.setText(ad.getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                Bundle data = new Bundle();
                data.putString("url", adList.get(position).getNewsUrl());
                intent.putExtras(data);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public class TypeViewHolder{

        TextView ad_name_txt;

        public TypeViewHolder(View view){
            ad_name_txt = (TextView) view.findViewById(R.id.ad_name_txt);
        }
    }


}
