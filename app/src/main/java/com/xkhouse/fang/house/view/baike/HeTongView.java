package com.xkhouse.fang.house.view.baike;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.NewsDetailActivity;

/**
 * 购房百科--合同（请原谅我名字取得这么low）
 * Created by wujian on 2016/3/1.
 */
public class HeTongView implements View.OnClickListener{

    private Context mContext;

    private View rootView;

    private ImageView fanben_iv;
    private ImageView buchong_iv;
    private ImageView xianjing_iv;
    private ImageView qianding_iv;

    public View getView() {
        return rootView;
    }

    public HeTongView(Context context){
        this.mContext = context;
        initView();
        setListener();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_he_tong, null);

        fanben_iv = (ImageView) rootView.findViewById(R.id.fanben_iv);
        buchong_iv = (ImageView) rootView.findViewById(R.id.buchong_iv);
        xianjing_iv = (ImageView) rootView.findViewById(R.id.xianjing_iv);
        qianding_iv = (ImageView) rootView.findViewById(R.id.qianding_iv);

    }

    private void setListener(){
        fanben_iv.setOnClickListener(this);
        buchong_iv.setOnClickListener(this);
        xianjing_iv.setOnClickListener(this);
        qianding_iv.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.fanben_iv:
                startNewsActivity("http://m.hfhouse.com/news/2394312.html", "2394312");
                break;

            case R.id.buchong_iv:
                startNewsActivity("http://m.hfhouse.com/news/2394316.html", "2394316");
                break;

            case R.id.xianjing_iv:
                startNewsActivity("http://m.hfhouse.com/news/2394318.html", "2394318");
                break;

            case R.id.qianding_iv:
                startNewsActivity("http://m.hfhouse.com/news/2394320.html", "2394320");
                break;
        }
    }

    private void startNewsActivity(String url, String newsId){
        Intent intent = new Intent(mContext, NewsDetailActivity.class);
        Bundle data = new Bundle();
        data.putString("url", url);
        data.putString("newsId", newsId);
        intent.putExtras(data);
        mContext.startActivity(intent);
    }

}
