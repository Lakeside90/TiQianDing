package com.xkhouse.fang.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.adapter.BaikeTabAdapter;
import com.xkhouse.fang.house.entity.BaikeTab;
import com.xkhouse.fang.house.view.baike.BuyAbilityView;
import com.xkhouse.fang.house.view.baike.DaiKuanView;
import com.xkhouse.fang.house.view.baike.DingFangView;
import com.xkhouse.fang.house.view.baike.FangChanZhengView;
import com.xkhouse.fang.house.view.baike.HeTongView;
import com.xkhouse.fang.house.view.baike.JiaoFangView;
import com.xkhouse.fang.house.view.baike.LuoHuView;
import com.xkhouse.fang.house.view.baike.ZhaoFangView;
import com.xkhouse.fang.widget.fancycoverflow.FancyCoverFlow;

import java.util.ArrayList;

/** 
 * @Description:  活动
 * @author wujian  
 * @date 2015-8-25 下午4:28:25  
 */
public class BaikeFragment extends AppBaseFragment {
	
	
	private View rootView;
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

    private FancyCoverFlow baike_tab;
    private LinearLayout baike_content_lay;

    private ArrayList<BaikeTab> baikeTabs = new ArrayList<>();
    private Handler mHandler = new Handler();

    private BuyAbilityView buyAbilityView;
    private ZhaoFangView zhaoFangView;
//    private KangFangView kangFangView;
    private DingFangView dingFangView;
    private DaiKuanView daiKuanView;
    private HeTongView heTongView;
    private JiaoFangView jiaoFangView;
    private FangChanZhengView fangChanZhengView;
    private LuoHuView luoHuView;



	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.activity_baike_house, container, false);
		findViews();
		setListeners();

        fillData();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

    int index = 0;
	private void setListeners() {
        baike_tab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                scheduleDismissOnScreenControls();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		
	}
	
	private void findViews() {
        initTitle();

        baike_tab = (FancyCoverFlow) rootView.findViewById(R.id.baike_tab);
        baike_content_lay = (LinearLayout) rootView.findViewById(R.id.baike_content_lay);

        baike_tab.setCallbackDuringFling(false);

        baike_tab.setUnselectedAlpha(0.0f);
        //未选中饱和度
        baike_tab.setUnselectedSaturation(1);
        //未选中规模
        baike_tab.setUnselectedScale(0.5f);
        //进入旋转度数
        baike_tab.setMaxRotation(0);
        //下重力
        baike_tab.setScaleDownGravity(0.0f);
        baike_tab.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);

	}




	private void initTitle() {
		iv_head_left = (ImageView) rootView.findViewById(R.id.iv_head_left);
		iv_head_left.setVisibility(View.INVISIBLE);
		
		tv_head_title = (TextView) rootView.findViewById(R.id.tv_head_title);
		tv_head_title.setText("购房百科");

	}


    private void scheduleDismissOnScreenControls() {
        mHandler.removeCallbacks(mDismissOnScreenControlRunner);
        mHandler.postDelayed(mDismissOnScreenControlRunner, 500);
    }

    private Runnable mDismissOnScreenControlRunner = new Runnable() {
        @Override
        public void run() {
            showView();
        }
    };


    private void fillData(){
        BaikeTab tab1 = new BaikeTab();
        tab1.setId(0);
        tab1.setIcon(R.drawable.icon_pinggu);
        tab1.setTitle("能力评估");
        baikeTabs.add(tab1);

        BaikeTab tab2 = new BaikeTab();
        tab2.setId(1);
        tab2.setIcon(R.drawable.icon_zhaofang);
        tab2.setTitle("找房");
        baikeTabs.add(tab2);

        BaikeTab tab3 = new BaikeTab();
        tab3.setId(2);
        tab3.setIcon(R.drawable.icon_kanfang);
        tab3.setTitle("看房");
        baikeTabs.add(tab3);

        BaikeTab tab4 = new BaikeTab();
        tab4.setId(3);
        tab4.setIcon(R.drawable.icon_dingfang);
        tab4.setTitle("订房");
        baikeTabs.add(tab4);

        BaikeTab tab5 = new BaikeTab();
        tab5.setId(4);
        tab5.setIcon(R.drawable.icon_daikuan);
        tab5.setTitle("贷款");
        baikeTabs.add(tab5);

        BaikeTab tab6 = new BaikeTab();
        tab6.setId(5);
        tab6.setIcon(R.drawable.icon_hetong);
        tab6.setTitle("合同");
        baikeTabs.add(tab6);

        BaikeTab tab7 = new BaikeTab();
        tab7.setId(6);
        tab7.setIcon(R.drawable.icon_jiaofang);
        tab7.setTitle("交房");
        baikeTabs.add(tab7);

        BaikeTab tab8 = new BaikeTab();
        tab8.setId(7);
        tab8.setIcon(R.drawable.icon_fangchanzheng);
        tab8.setTitle("房产证");
        baikeTabs.add(tab8);

        BaikeTab tab9 = new BaikeTab();
        tab9.setId(8);
        tab9.setIcon(R.drawable.icon_luohu);
        tab9.setTitle("落户");
        baikeTabs.add(tab9);

        baike_tab.setAdapter(new BaikeTabAdapter(getActivity(), baikeTabs));
    }


    private void showView(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        switch (index){
            case 0:
                if (buyAbilityView == null) buyAbilityView = new BuyAbilityView(getActivity());
                baike_content_lay.removeAllViews();
                baike_content_lay.addView(buyAbilityView.getView(), lp);
                break;

            case 1:
                if (zhaoFangView == null) zhaoFangView = new ZhaoFangView(getActivity());
                baike_content_lay.removeAllViews();
                baike_content_lay.addView(zhaoFangView.getView(), lp);
                break;

            case 2:
//                if (kangFangView == null) kangFangView = new KangFangView(getActivity());
//                baike_content_lay.removeAllViews();
//                baike_content_lay.addView(kangFangView.getView(), lp);
                break;

            case 3:
                if (dingFangView == null) dingFangView = new DingFangView(getActivity());
                baike_content_lay.removeAllViews();
                baike_content_lay.addView(dingFangView.getView(), lp);
                break;

            case 4:
                if (daiKuanView == null) daiKuanView = new DaiKuanView(getActivity());
                baike_content_lay.removeAllViews();
                baike_content_lay.addView(daiKuanView.getView(), lp);
                break;

            case 5:
                if (heTongView == null) heTongView = new HeTongView(getActivity());
                baike_content_lay.removeAllViews();
                baike_content_lay.addView(heTongView.getView(), lp);
                break;

            case 6:
                if (jiaoFangView == null) jiaoFangView = new JiaoFangView(getActivity());
                baike_content_lay.removeAllViews();
                baike_content_lay.addView(jiaoFangView.getView(), lp);
                break;

            case 7:
                if (fangChanZhengView == null) fangChanZhengView = new FangChanZhengView(getActivity());
                baike_content_lay.removeAllViews();
                baike_content_lay.addView(fangChanZhengView.getView(), lp);
                break;

            case 8:
                if (luoHuView == null) luoHuView = new LuoHuView(getActivity());
                baike_content_lay.removeAllViews();
                baike_content_lay.addView(luoHuView.getView(), lp);
                break;
        }
    }



}
