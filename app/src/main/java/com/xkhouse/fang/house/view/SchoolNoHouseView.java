package com.xkhouse.fang.house.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.amap.api.maps2d.model.LatLng;
import com.xkhouse.fang.R;
import com.xkhouse.fang.house.adapter.SchoolHouseAdapter;
import com.xkhouse.fang.house.entity.SchoolHouse;

import java.util.ArrayList;

/**
 *
 * 学区房搜索（筛选）结果没数据，显示推荐的学区
 *
 * Created by wujian on 16/8/9.
 */
public class SchoolNoHouseView {

    private Context context;
    private View rootView;

    private ScrollView scrollView;
    private ListView school_listView;
    private SchoolHouseAdapter schoolHouseAdapter;
    private ArrayList<SchoolHouse> schoolHouseList = new ArrayList<SchoolHouse>();

    private LatLng startLatlng; //当前经纬度

    public View getView() {
        return rootView;
    }

    public SchoolNoHouseView(Context context, ArrayList<SchoolHouse> schoolHouseList, LatLng startLatlng) {
        this.context = context;
        this.schoolHouseList = schoolHouseList;
        this.startLatlng = startLatlng;

        initView();

        setListener();

        fillData();
    }

    private void initView() {
        rootView = LayoutInflater.from(context).inflate(R.layout.view_school_no_house, null);
        school_listView = (ListView) rootView.findViewById(R.id.school_listView);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

    }

    private void setListener(){

    }

    public void refreshView(ArrayList<SchoolHouse> schoolHouseList, LatLng startLatlng) {
        this.schoolHouseList = schoolHouseList;
        this.startLatlng = startLatlng;
        fillData();
        scrollView.scrollTo(0, 0);
    }

    private void fillData() {
        if(schoolHouseList == null) return;
        if(schoolHouseAdapter == null){
            schoolHouseAdapter = new SchoolHouseAdapter(context, schoolHouseList, startLatlng);
            school_listView.setAdapter(schoolHouseAdapter);
        }else {
            schoolHouseAdapter.setData(schoolHouseList, startLatlng);
        }
    }


}
