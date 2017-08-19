package com.xkhouse.fang.house.view.baike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xkhouse.fang.R;

/**
 * 购房百科--房产证（请原谅我名字取得这么low）
 * Created by wujian on 2016/3/1.
 */
public class FangChanZhengView implements View.OnClickListener{

    private Context mContext;

    private View rootView;

    public View getView() {
        return rootView;
    }

    public FangChanZhengView(Context context){
        this.mContext = context;
        initView();
        setListener();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_fang_chan_zheng, null);



    }

    private void setListener(){

    }


    @Override
    public void onClick(View v) {

    }



}
