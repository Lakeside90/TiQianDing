package com.xkhouse.fang.house.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.adapter.NHMultyTypeAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.widget.ScrollGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 新房更多筛选视图
 * Created by wujian on 2016/2/19.
 */
public class NHMultyTypeListPopupWindow  extends PopupWindow implements View.OnClickListener{

    private Context context;
    private View rootView;

    private TextView cancel_txt;
    private TextView reset_txt;
    private TextView confirm_txt;

    private ScrollGridView order_grid;        //排序
    private NHMultyTypeAdapter orderAdapter;
    private List<CommonType> orderList = new ArrayList<>();

    private ScrollGridView houseType_grid;    //户型
    private NHMultyTypeAdapter houseTypeAdapter;
    private List<CommonType> houseTypeList = new ArrayList<>();

    private ScrollGridView space_grid;        //面积
    private NHMultyTypeAdapter spaceAdapter;
    private List<CommonType> spaceList = new ArrayList<>();

    private ScrollGridView feature_grid;      //楼盘特色
    private NHMultyTypeAdapter featureAdapter;
    private List<CommonType> featureList = new ArrayList<>();

    private ScrollGridView developer_grid;     //开发商
    private NHMultyTypeAdapter developerAdapter;
    private List<CommonType> developerList = new ArrayList<>();

    private ScrollGridView saleState_grid;    //销售状态
    private NHMultyTypeAdapter saleStateAdapter;
    private List<CommonType> saleStateList = new ArrayList<>();

    private TextView open_time_txt;
    private ScrollGridView openTime_grid;     //开盘时间
    private NHMultyTypeAdapter openTimeAdapter;
    private List<CommonType> openTimeList = new ArrayList<>();


    private View bg_lay;

    private List<CommonType> parentTypes;

    private HashMap<String, String> selectIndex = new HashMap<String, String>();
    private HashMap<String, String> tempSelectIndex = new HashMap<String, String>();

    private NHMultyTypeListClickListener multyTypeListClickListener;


    public View getView() {
        return rootView;
    }

    public NHMultyTypeListPopupWindow(Context context,
                                      NHMultyTypeListClickListener multyTypeListClickListener) {

        this.context = context;
        this.multyTypeListClickListener = multyTypeListClickListener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.view_newhouse_multy_type_list, null);

        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(R.style.TypePopupAnimation);

        bg_lay = rootView.findViewById(R.id.bg_lay);

        cancel_txt = (TextView) rootView.findViewById(R.id.cancel_txt);
        reset_txt = (TextView) rootView.findViewById(R.id.reset_txt);
        confirm_txt = (TextView) rootView.findViewById(R.id.confirm_txt);

        order_grid = (ScrollGridView) rootView.findViewById(R.id.order_grid);
        feature_grid = (ScrollGridView) rootView.findViewById(R.id.feature_grid);
        houseType_grid = (ScrollGridView) rootView.findViewById(R.id.houseType_grid);
        space_grid = (ScrollGridView) rootView.findViewById(R.id.space_grid);
        developer_grid = (ScrollGridView) rootView.findViewById(R.id.developer_grid);
        saleState_grid = (ScrollGridView) rootView.findViewById(R.id.saleState_grid);
        openTime_grid = (ScrollGridView) rootView.findViewById(R.id.openTime_grid);
        open_time_txt = (TextView) rootView.findViewById(R.id.open_time_txt);


        setListener();
        initTempSelectData();
    }


    private void setListener() {
        bg_lay.setOnClickListener(this);
        cancel_txt.setOnClickListener(this);
        reset_txt.setOnClickListener(this);
        confirm_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bg_lay:
            case R.id.cancel_txt:
                NHMultyTypeListPopupWindow.this.dismiss();
                break;

            case R.id.reset_txt:
                resetData();
                break;

            case R.id.confirm_txt:
                setSelectData();
                multyTypeListClickListener.onConfirmClick(selectIndex);
                NHMultyTypeListPopupWindow.this.dismiss();
                break;
        }
    }

    private void initTempSelectData(){
        for (int i = 0; i<7; i++){
            tempSelectIndex.put(String.valueOf(i),"0");
        }
    }

    public void fillData(List<CommonType> commonTypes){
        if (parentTypes == null){
            initSelectData();
        }
        this.parentTypes = commonTypes;
        setTempSelectData();

        if(parentTypes.size() < 7) return;

        orderList.clear();
        orderList.addAll(parentTypes.get(0).getChild());
        if (orderAdapter == null) {
            orderAdapter = new NHMultyTypeAdapter(context, orderList,
                    new NHMultyTypeAdapter.NHMultyTypeAdapterClick() {
                        @Override
                        public void onItemClick(int position) {
                            tempSelectIndex.put("0", String.valueOf(position));
                            orderAdapter.setSelectIndex(position);
                        }
                    });
            order_grid.setAdapter(orderAdapter);
        }
        orderAdapter.setSelectIndex(Integer.valueOf(tempSelectIndex.get("0")));

        houseTypeList.clear();
        houseTypeList.addAll(parentTypes.get(1).getChild());
        if (houseTypeAdapter == null) {
            houseTypeAdapter = new NHMultyTypeAdapter(context, houseTypeList,
                    new NHMultyTypeAdapter.NHMultyTypeAdapterClick() {
                        @Override
                        public void onItemClick(int position) {
                            tempSelectIndex.put("1", String.valueOf(position));
                            houseTypeAdapter.setSelectIndex(position);
                        }
                    });
            houseType_grid.setAdapter(houseTypeAdapter);
        }
        houseTypeAdapter.setSelectIndex(Integer.valueOf(tempSelectIndex.get("1")));

        spaceList.clear();
        spaceList.addAll(parentTypes.get(2).getChild());
        if (spaceAdapter == null) {
            spaceAdapter = new NHMultyTypeAdapter(context, spaceList,
                    new NHMultyTypeAdapter.NHMultyTypeAdapterClick() {
                        @Override
                        public void onItemClick(int position) {
                            tempSelectIndex.put("2", String.valueOf(position));
                            spaceAdapter.setSelectIndex(position);
                        }
                    });
            space_grid.setAdapter(spaceAdapter);
        }
        spaceAdapter.setSelectIndex(Integer.valueOf(tempSelectIndex.get("2")));

        featureList.clear();
        featureList.addAll(parentTypes.get(3).getChild());
        if (featureAdapter == null) {
            featureAdapter = new NHMultyTypeAdapter(context, featureList,
                    new NHMultyTypeAdapter.NHMultyTypeAdapterClick() {
                        @Override
                        public void onItemClick(int position) {
                            tempSelectIndex.put("3", String.valueOf(position));
                            featureAdapter.setSelectIndex(position);
                        }
                    });
            feature_grid.setAdapter(featureAdapter);
        }
        featureAdapter.setSelectIndex(Integer.valueOf(tempSelectIndex.get("3")));

        developerList.clear();
        developerList.addAll(parentTypes.get(4).getChild());
        if (developerAdapter == null) {
            developerAdapter = new NHMultyTypeAdapter(context, developerList,
                    new NHMultyTypeAdapter.NHMultyTypeAdapterClick() {
                        @Override
                        public void onItemClick(int position) {
                            tempSelectIndex.put("4", String.valueOf(position));
                            developerAdapter.setSelectIndex(position);
                        }
                    });
            developer_grid.setAdapter(developerAdapter);
        }
        developerAdapter.setSelectIndex(Integer.valueOf(tempSelectIndex.get("4")));

        saleStateList.clear();
        saleStateList.addAll(parentTypes.get(5).getChild());
        if (saleStateAdapter == null) {
            saleStateAdapter = new NHMultyTypeAdapter(context, parentTypes.get(5).getChild(),
                    new NHMultyTypeAdapter.NHMultyTypeAdapterClick() {
                        @Override
                        public void onItemClick(int position) {
                            tempSelectIndex.put("5", String.valueOf(position));
                            saleStateAdapter.setSelectIndex(position);
                        }
                    });
            saleState_grid.setAdapter(saleStateAdapter);
        }
        saleStateAdapter.setSelectIndex(Integer.valueOf(tempSelectIndex.get("5")));

        if(parentTypes.size() == 7){
            openTimeList.clear();
            openTimeList.addAll(parentTypes.get(6).getChild());
            if (openTimeAdapter == null) {
                openTimeAdapter = new NHMultyTypeAdapter(context, parentTypes.get(6).getChild(),
                        new NHMultyTypeAdapter.NHMultyTypeAdapterClick() {
                            @Override
                            public void onItemClick(int position) {
                                tempSelectIndex.put("6", String.valueOf(position));
                                openTimeAdapter.setSelectIndex(position);
                            }
                        });
                openTime_grid.setAdapter(openTimeAdapter);
            }
            openTimeAdapter.setSelectIndex(Integer.valueOf(tempSelectIndex.get("6")));

            open_time_txt.setVisibility(View.VISIBLE);
            openTime_grid.setVisibility(View.VISIBLE);
        }

    }



    private void resetData() {
        initTempSelectData();

        orderAdapter.setSelectIndex(0);
        featureAdapter.setSelectIndex(0);
        houseTypeAdapter.setSelectIndex(0);
        spaceAdapter.setSelectIndex(0);
        developerAdapter.setSelectIndex(0);
        saleStateAdapter.setSelectIndex(0);
        if(parentTypes.size() == 7){
            openTimeAdapter.setSelectIndex(0);
        }

    }



    private void setSelectData(){
        for(int i = 0; i < tempSelectIndex.size(); i++){
            selectIndex.put(String.valueOf(i), tempSelectIndex.get(String.valueOf(i)));
        }
    }

    private void initSelectData(){
        for (int i = 0; i<7; i++){
            selectIndex.put(String.valueOf(i),"0");
        }
    }

    private void setTempSelectData(){
        for(int i = 0; i < selectIndex.size(); i++){
            tempSelectIndex.put(String.valueOf(i), selectIndex.get(String.valueOf(i)));
        }
    }



    public interface NHMultyTypeListClickListener {

        void onConfirmClick(HashMap<String, String> selectIndex);
    }

}
