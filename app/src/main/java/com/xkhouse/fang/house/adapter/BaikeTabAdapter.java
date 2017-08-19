package com.xkhouse.fang.house.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.house.entity.BaikeTab;
import com.xkhouse.fang.widget.fancycoverflow.FancyCoverFlow;
import com.xkhouse.fang.widget.fancycoverflow.FancyCoverFlowAdapter;

import java.util.ArrayList;

/**
 * Created by wujian on 2016/2/26.
 */
public class BaikeTabAdapter extends FancyCoverFlowAdapter {

    private Context mContext;
    private ArrayList<BaikeTab> baikeTabs;

    public BaikeTabAdapter(Context context, ArrayList<BaikeTab> baikeTabs){
        this.mContext = context;
        this.baikeTabs = baikeTabs;
    }

    @Override
    public View getCoverFlowItem(int position, View reusableView, ViewGroup parent) {
        ViewHolder holder = null;
        if(reusableView == null){

            reusableView = LayoutInflater.from(mContext).inflate(R.layout.item_baike_tab_list, null);
            reusableView.setLayoutParams(new FancyCoverFlow.LayoutParams(DisplayUtil.dip2px(mContext, 70),
                    DisplayUtil.dip2px(mContext, 80)));

            holder = new ViewHolder(reusableView);
            reusableView.setTag(holder);
        }else{
            holder = (ViewHolder) reusableView.getTag();
        }

        BaikeTab tab = baikeTabs.get(position);

        holder.tab_icon.setImageResource(tab.getIcon());
        holder.tab_title_txt.setText(tab.getTitle());

        return reusableView;
    }

    @Override
    public int getCount() {
        return baikeTabs.size();
    }

    @Override
    public Object getItem(int position) {
        return baikeTabs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder {
        public ImageView tab_icon;
        public TextView tab_title_txt;

        public ViewHolder(View view){
            tab_icon = (ImageView) view.findViewById(R.id.tab_icon);
            tab_title_txt = (TextView) view.findViewById(R.id.tab_title_txt);
        }
    }

}
