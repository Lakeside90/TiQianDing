package com.xkhouse.fang.house.view.baike;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.KanFangDetailActivity;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.KanFang;
import com.xkhouse.fang.app.task.KanFangListRequest;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.house.activity.KanFangActivity;
import com.xkhouse.fang.widget.autoscrollviewpager.AutoScrollViewPager;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 购房百科--看房（请原谅我名字取得这么low）
 * Created by wujian on 2016/3/1.
 */
public class KangFangView {

    private Context mContext;

    private View rootView;

    private LinearLayout kanfang_lay;
    private AutoScrollViewPager kanfang_viewpager;
    private LinearLayout kanfang_point_lay;
    private List<ImageView> pointViews;

    private KanFangListRequest kanFangListRequest;
    private ArrayList<KanFang> kanFangList;

    private TextView submit_txt;

    private ModelApplication modelApp;

    public View getView() {
        return rootView;
    }

    public KangFangView(Context context){
        this.mContext = context;
        modelApp = (ModelApplication) ((Activity) context).getApplication();
        initView();
        setListener();
        startKangFangTask();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_kan_fang, null);

        submit_txt = (TextView) rootView.findViewById(R.id.submit_txt);
        kanfang_viewpager = (AutoScrollViewPager) rootView.findViewById(R.id.kanfang_viewpager);
        kanfang_point_lay = (LinearLayout) rootView.findViewById(R.id.kanfang_point_lay);
        kanfang_lay = (LinearLayout) rootView.findViewById(R.id.kanfang_lay);

    }

    private void setListener(){
        submit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, KanFangActivity.class));
            }
        });

        kanfang_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                for (int i = 0; i < kanFangList.size(); i++) {
                    if (arg0 == i) {
                        pointViews.get(i).setImageResource(R.drawable.cricle_blue_bg);
                    } else {
                        pointViews.get(i).setImageResource(R.drawable.cricle_dark_bg);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }


    private RequestListener kanFangListListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                case Constants.NO_DATA_FROM_NET:
                    kanfang_lay.setVisibility(View.GONE);
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    Bundle data = message.getData();
                    ArrayList<KanFang> temp = (ArrayList<KanFang>) data.getSerializable("kanFangList");

                    if (kanFangList == null) {
                        kanFangList = new ArrayList<KanFang>();
                        kanFangList.addAll(temp);
                    } else {
                        kanFangList.clear();
                        kanFangList.addAll(temp);
                    }
                    if (kanFangList == null || kanFangList.size() == 0 ){
                        kanfang_lay.setVisibility(View.GONE);
                    }else{
                        kanfang_lay.setVisibility(View.VISIBLE);
                        fillKanFangData();
                    }

                    break;
            }
        }
    };

    private void startKangFangTask(){
        if (NetUtil.detectAvailable(mContext)){
            if (kanFangListRequest == null){
                kanFangListRequest = new KanFangListRequest(modelApp.getSite().getSiteId(), kanFangListListener);
            }else{
                kanFangListRequest.setData(modelApp.getSite().getSiteId());
            }
            kanFangListRequest.doRequest();
        }
    }

    private void fillKanFangData(){

        if (kanFangList == null) return;

        pointViews = new ArrayList<ImageView>();
        kanfang_point_lay.removeAllViews();

        LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 6),
                DisplayUtil.dip2px(mContext, 6));
        lps.leftMargin = DisplayUtil.dip2px(mContext, 3);
        for(int i=0; i < kanFangList.size(); i++){
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(R.drawable.cricle_dark_bg);
            kanfang_point_lay.addView(imageView, lps);
            pointViews.add(imageView);
        }
        if(pointViews.size() > 1){
            pointViews.get(0).setImageResource(R.drawable.cricle_blue_bg);
        }



        List<View> views = new ArrayList<View>();
        for (int i = 0; i < kanFangList.size(); i++) {

            View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_kan_fang_list, null);
            TextView kanfang_num_txt = (TextView) convertView.findViewById(R.id.kanfang_num_txt);
            TextView kanfang_title_txt = (TextView) convertView.findViewById(R.id.kanfang_title_txt);
            if (StringUtil.isEmpty(kanFangList.get(i).getApplyNum())) {
                kanfang_num_txt.setText("0");
            } else {
                kanfang_num_txt.setText(kanFangList.get(i).getApplyNum());
            }
            kanfang_title_txt.setText(kanFangList.get(i).getTitle());
            views.add(convertView);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String url = modelApp.getKanFang() + kanFangList.get(kanfang_viewpager.getCurrentItem()).getId() + ".html";
                    Intent intent = new Intent(mContext, KanFangDetailActivity.class);
                    Bundle data = new Bundle();
                    data.putString("url", url);
                    intent.putExtras(data);
                    mContext.startActivity(intent);
                }
            });
        }

        ADPagerAdapter pagerAdapter = new ADPagerAdapter(views);
        kanfang_viewpager.setAdapter(pagerAdapter);

        kanfang_viewpager.setInterval(3000);

        kanfang_viewpager.startAutoScroll();

    }




    class ADPagerAdapter extends PagerAdapter {

        private List<View> listViews;// content

        private int size;// 页数

        public ADPagerAdapter(List<View> views) {// 构造函数
            // 初始化viewpager的时候给的一个页面
            this.listViews = views;
            size = views == null ? 0 : views.size();
        }

        public void setData(List<View> views){
            this.listViews = views;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {// 返回数量
            return size;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
            ((ViewPager) arg0).removeView(listViews.get(arg1 % size));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {// 返回view对象
            try {
                ((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);
            } catch (Exception e) {
                Logger.e("", "exception：" + e.getMessage());
            }
            return listViews.get(arg1 % size);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }


}
