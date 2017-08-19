package com.xkhouse.fang.house.view.baike;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.CustomHouseListActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.house.activity.NewHouseListActivity;

/**
 * 购房百科--找房（请原谅我名字取得这么low）
 * Created by wujian on 2016/3/1.
 */
public class ZhaoFangView implements View.OnClickListener{

    private Context mContext;

    private View rootView;

    private TextView map_txt;
    private TextView list_txt;
    private TextView custom_txt;
    private TextView submit_txt;

    public View getView() {
        return rootView;
    }

    public ZhaoFangView(Context context){
        this.mContext = context;
        initView();
        setListener();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_zhao_fang, null);

        submit_txt = (TextView) rootView.findViewById(R.id.submit_txt);
        map_txt = (TextView) rootView.findViewById(R.id.map_txt);
        list_txt = (TextView) rootView.findViewById(R.id.list_txt);
        custom_txt = (TextView) rootView.findViewById(R.id.custom_txt);

    }

    private void setListener(){
        map_txt.setOnClickListener(this);
        list_txt.setOnClickListener(this);
        custom_txt.setOnClickListener(this);
        submit_txt.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        Intent intent;
        Bundle data;

        switch (v.getId()){
            case R.id.map_txt:
                data = new Bundle();
                data.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
                intent = new Intent(mContext, MapHousesActivity.class);
                intent.putExtras(data);
                mContext.startActivity(intent);
                break;

            case R.id.list_txt:
                mContext.startActivity(new Intent(mContext, NewHouseListActivity.class));
                break;

            case R.id.custom_txt:
                mContext.startActivity(new Intent(mContext, CustomHouseListActivity.class));
                break;

            case R.id.submit_txt:
                mContext.startActivity(new Intent(mContext, NewHouseListActivity.class));
                break;
        }
    }
}
