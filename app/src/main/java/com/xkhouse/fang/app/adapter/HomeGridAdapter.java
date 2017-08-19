package com.xkhouse.fang.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.CalculatorActivity;
import com.xkhouse.fang.app.activity.JJIndexActivity;
import com.xkhouse.fang.app.activity.MainActivity;
import com.xkhouse.fang.app.activity.NavigationActivity;
import com.xkhouse.fang.app.activity.NewsIndexActivity;
import com.xkhouse.fang.app.entity.XKNavigation;
import com.xkhouse.fang.house.activity.CustomHouseListActivity;
import com.xkhouse.fang.house.activity.OldHouseListActivity;
import com.xkhouse.fang.house.activity.RentHouseListActivity;
import com.xkhouse.fang.house.activity.NewHouseListActivity;
import com.xkhouse.fang.house.activity.SchoolHouseListActivity;
import com.xkhouse.fang.money.activity.XKLoanIndexActivity;

import java.util.ArrayList;

/** 
 * @Description: 首页功能入口 
 * @author wujian  
 * @date 2015-8-27 下午3:19:49  
 */
public class HomeGridAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKNavigation> homeList;
	private ArrayList<XKNavigation> moreList;
	private ArrayList<XKNavigation> appList;
	
	private boolean isdefault = false;
	
	private int[] defaultIcons = 
			{R.drawable.menu_01, R.drawable.menu_02, R.drawable.menu_03,  R.drawable.menu_04,
			R.drawable.menu_05, R.drawable.menu_06, R.drawable.menu_sale, R.drawable.menu_08};
	
	public HomeGridAdapter(ArrayList<XKNavigation> homeList, ArrayList<XKNavigation> moreList,
			ArrayList<XKNavigation> appList, Context context){
		
		this.homeList = homeList;
		this.moreList = moreList;
		this.appList = appList;
		this.context = context;
		initData();
	}
	
	public void setData(ArrayList<XKNavigation> homeList, ArrayList<XKNavigation> moreList,
			ArrayList<XKNavigation> appList){
		this.homeList = homeList;
		this.moreList = moreList;
		this.appList = appList;
		initData();
		notifyDataSetChanged();
	}
	
	private void initData(){
		isdefault = false;

		if(homeList == null || homeList.size() < 1){
			isdefault = true;
			homeList = new ArrayList<XKNavigation>();
			
			XKNavigation newHouse = new XKNavigation();
			newHouse.setNavId("1");
			newHouse.setTitle("新房");
			newHouse.setDefaultIcon(R.drawable.menu_01);
			newHouse.setLink("newhouse");
			newHouse.setMethod("pushToNewHouse");
			homeList.add(newHouse);
			
			XKNavigation oldHouse = new XKNavigation();
			oldHouse.setNavId("2");
			oldHouse.setTitle("二手房");
			oldHouse.setDefaultIcon(R.drawable.menu_02);
			oldHouse.setLink("oldhouse");
			oldHouse.setMethod("pushToOldHouse");
			homeList.add(oldHouse);
			
			XKNavigation rentHouse = new XKNavigation();
			rentHouse.setNavId("3");
			rentHouse.setTitle("租房");
			rentHouse.setDefaultIcon(R.drawable.menu_03);
			rentHouse.setLink("zufang");
			rentHouse.setMethod("pushToRentHouse");
			homeList.add(rentHouse);
			
			XKNavigation news = new XKNavigation();
			news.setNavId("4");
			news.setTitle("资讯");
			news.setDefaultIcon(R.drawable.menu_04);
			news.setLink("news");
			news.setMethod("pushToNews");
			homeList.add(news);
			
			XKNavigation earnHouse = new XKNavigation();
			earnHouse.setNavId("5");
			earnHouse.setTitle("荐房赚佣");
			earnHouse.setDefaultIcon(R.drawable.menu_05);
			earnHouse.setLink("");
			earnHouse.setMethod("pushToEarn");
			homeList.add(earnHouse);
			
			XKNavigation findHouse = new XKNavigation();
			findHouse.setNavId("6");
			findHouse.setTitle("帮你找房");
			findHouse.setDefaultIcon(R.drawable.menu_06);
			findHouse.setLink("");
			findHouse.setMethod("pushToFindHouse");
			homeList.add(findHouse);
			
			XKNavigation priceHouse = new XKNavigation();
			priceHouse.setNavId("12");
			priceHouse.setTitle("热门活动");
			priceHouse.setDefaultIcon(R.drawable.menu_sale);
			priceHouse.setLink("");
			priceHouse.setMethod("pushToActivity");
			homeList.add(priceHouse);
			
			XKNavigation moreHouse = new XKNavigation();
			moreHouse.setNavId("8");
			moreHouse.setTitle("更多");
			moreHouse.setDefaultIcon(R.drawable.menu_08);
			moreHouse.setLink("more");
			moreHouse.setMethod("pushToMore");
			homeList.add(moreHouse);
		}
	}
	
	@Override
	public int getCount() {
		return homeList.size();
	}

	@Override
	public Object getItem(int position) {
		return homeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_home_grid, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startActivity(position);
				}
			});
			
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		XKNavigation navigation = homeList.get(position);
		
		holder.name_txt.setText(navigation.getTitle());
		
		if (isdefault) {
			holder.icon_iv.setImageResource(navigation.getDefaultIcon());
		}else {
			ImageLoader.getInstance().displayImage(navigation.getPhotoUrl(),
					holder.icon_iv, getOptions(position));
		}
		
		
		
		return convertView;
	}

	public class ViewHolder{
		ImageView icon_iv;
		TextView name_txt;
		
		public ViewHolder(View view){
			icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
			name_txt = (TextView) view.findViewById(R.id.name_txt);
		}
	}
	
	private DisplayImageOptions getOptions(int index) {
        if(index < defaultIcons.length){
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(defaultIcons[index])   // 加载的图片
                    .showImageOnFail(defaultIcons[index]) // 错误的时候的图片
                    .showImageForEmptyUri(R.drawable.nopic)
                    .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                    .cacheOnDisk(true).build();

            return options;
        }else{
            return null;
        }

	}
	
	
	private void startActivity(int position){
		
		Intent intent;
		Bundle bundle = new Bundle();
		
		XKNavigation navigation = homeList.get(position);
		String key = navigation.getMethod();
		
		if("pushToNewHouse".equals(key)) {
            context.startActivity(new Intent(context, NewHouseListActivity.class));

		}else if("pushToOldHouse".equals(key)) {
            context.startActivity(new Intent(context, OldHouseListActivity.class));
			
		}else if("pushToRentHouse".equals(key)) {
            context.startActivity(new Intent(context, RentHouseListActivity.class));
			
		}else if("pushToNews".equals(key)) {
			context.startActivity(new Intent(context, NewsIndexActivity.class));
			
		}else if("pushToEarn".equals(key)) {
			((MainActivity) context).selectFragment(2);
			
		}else if("pushToFindHouse".equals(key)) {
			context.startActivity(new Intent(context, CustomHouseListActivity.class));
			
		}else if("pushToActivity".equals(key)) {
			((MainActivity) context).selectFragment(1);
			
		}else if("pushToGuide".equals(key)) {
            intent = new Intent(context, NewsIndexActivity.class);
            bundle.putInt("currentItem", 1);
            intent.putExtras(bundle);
            context.startActivity(intent);

        }else if("pushToFurnishing".equals(key)) {
            context.startActivity(new Intent(context, JJIndexActivity.class));

        }else if("pushToCalculator".equals(key)) {
            context.startActivity(new Intent(context, CalculatorActivity.class));

        }else if("pushToLoan".equals(key)) {
            context.startActivity(new Intent(context, XKLoanIndexActivity.class));

        }else if("pushToMore".equals(key)) {
			intent = new Intent(context, NavigationActivity.class);
			bundle.putSerializable("more", moreList);
			bundle.putSerializable("app", appList);
			intent.putExtras(bundle);
			context.startActivity(intent);

		}else if("pushToSchoolHouse".equals(key)) {
            context.startActivity(new Intent(context, SchoolHouseListActivity.class));
        }


	}
}
