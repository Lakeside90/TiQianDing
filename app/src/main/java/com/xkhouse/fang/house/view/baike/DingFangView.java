package com.xkhouse.fang.house.view.baike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xkhouse.fang.R;

/**
 * 购房百科--订房（请原谅我名字取得这么low）
 * Created by wujian on 2016/3/1.
 */
public class DingFangView implements View.OnClickListener{

    private Context mContext;

    private View rootView;


    public View getView() {
        return rootView;
    }

    public DingFangView(Context context){
        this.mContext = context;
        initView();
        setListener();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_ding_fang, null);



    }

    private void setListener(){

    }


    @Override
    public void onClick(View v) {

    }
}
