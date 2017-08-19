package com.xkhouse.fang.house.view.baike;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.BuyAbilityActivity;

/**
 * 购房百科--能力评估
 * Created by wujian on 2016/3/1.
 */
public class BuyAbilityView {

    private Context mContext;

    private View rootView;

    private TextView submit_txt;

    public View getView() {
        return rootView;
    }

    public BuyAbilityView(Context context){
        this.mContext = context;
        initView();
        setListener();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_buy_ability, null);

        submit_txt = (TextView) rootView.findViewById(R.id.submit_txt);

    }

    private void setListener(){
        submit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, BuyAbilityActivity.class));
            }
        });
    }


}
