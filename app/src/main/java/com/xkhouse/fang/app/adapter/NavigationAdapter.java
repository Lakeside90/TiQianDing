package com.xkhouse.fang.app.adapter;

import android.app.Activity;
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
import com.xkhouse.fang.app.activity.NewsIndexActivity;
import com.xkhouse.fang.app.entity.XKNavigation;
import com.xkhouse.fang.discount.activity.DiscountActivity;
import com.xkhouse.fang.house.activity.BaikeHouseActivity;
import com.xkhouse.fang.house.activity.BuyAbilityActivity;
import com.xkhouse.fang.house.activity.CustomHouseListActivity;
import com.xkhouse.fang.house.activity.NewHouseListActivity;
import com.xkhouse.fang.house.activity.OldHouseListActivity;
import com.xkhouse.fang.house.activity.RentHouseListActivity;
import com.xkhouse.fang.house.activity.SchoolHouseListActivity;
import com.xkhouse.fang.money.activity.XKLoanIndexActivity;

import java.util.ArrayList;

/** 
 * @Description: 首页功能入口 
 * @author wujian  
 * @date 2015-8-27 下午3:19:49  
 */
public class NavigationAdapter extends BaseAdapter {

	private Context context;
	
	private ArrayList<XKNavigation> moreList;
	private boolean isdefault = false;
	
	public NavigationAdapter(ArrayList<XKNavigation> moreList, Context context){
		this.moreList = moreList;
		this.context = context;
		initData();
	}
	
	private void initData(){
		
		if(moreList == null || moreList.size() < 1){
			isdefault = true;
			moreList = new ArrayList<XKNavigation>();
			
			XKNavigation home = new XKNavigation();
			home.setNavId("11");
			home.setTitle("首页");
			home.setDefaultIcon(R.drawable.menu_index);
			home.setLink("");
			home.setMethod("pushToHomePage");
			moreList.add(home);
			
			XKNavigation newhouse = new XKNavigation();
			newhouse.setNavId("1");
			newhouse.setTitle("新房");
			newhouse.setDefaultIcon(R.drawable.menu_01);
			newhouse.setLink("newhouse");
			newhouse.setMethod("pushToNewHouse");
			moreList.add(newhouse);
			
			XKNavigation oldhouse = new XKNavigation();
			oldhouse.setNavId("2");
			oldhouse.setTitle("二手房");
			oldhouse.setDefaultIcon(R.drawable.menu_02);
			oldhouse.setLink("oldhouse");
			oldhouse.setMethod("pushToOldHouse");
			moreList.add(oldhouse);
			
			XKNavigation renthouse = new XKNavigation();
			renthouse.setNavId("3");
			renthouse.setTitle("租房");
			renthouse.setDefaultIcon(R.drawable.menu_03);
			renthouse.setLink("zufang");
			renthouse.setMethod("pushToRentHouse");
			moreList.add(renthouse);
			
			XKNavigation news = new XKNavigation();
			news.setNavId("4");
			news.setTitle("资讯");
			news.setDefaultIcon(R.drawable.menu_04);
			news.setLink("news");
			news.setMethod("pushToNews");
			moreList.add(news);
			
			XKNavigation earn = new XKNavigation();
			earn.setNavId("5");
			earn.setTitle("荐房赚佣");
			earn.setDefaultIcon(R.drawable.menu_05);
			earn.setLink("");
			earn.setMethod("pushToEarn");
			moreList.add(earn);
			
			XKNavigation findhouse = new XKNavigation();
			findhouse.setNavId("6");
			findhouse.setTitle("帮你找房");
			findhouse.setDefaultIcon(R.drawable.menu_06);
			findhouse.setLink("");
			findhouse.setMethod("pushToFindHouse");
			moreList.add(findhouse);
			
//			XKNavigation price = new XKNavigation();
//			price.setNavId("7");
//			price.setTitle("房价");
//			price.setDefaultIcon(R.drawable.menu_07);
//			price.setLink("");
//			price.setMethod("pushToPrice");
//			moreList.add(price);
			
			XKNavigation hotactivity = new XKNavigation();
			hotactivity.setNavId("12");
			hotactivity.setTitle("热门活动");
			hotactivity.setDefaultIcon(R.drawable.menu_sale);
			hotactivity.setLink("");
			hotactivity.setMethod("pushToActivity");
			moreList.add(hotactivity);
			
			XKNavigation guide = new XKNavigation();
			guide.setNavId("13");
			guide.setTitle("导购");
			guide.setDefaultIcon(R.drawable.menu_buy);
			guide.setLink("");
			guide.setMethod("pushToGuide");
			moreList.add(guide);
			
			XKNavigation furnishing = new XKNavigation();
			furnishing.setNavId("14");
			furnishing.setTitle("家居");
			furnishing.setDefaultIcon(R.drawable.menu_home);
			furnishing.setLink("");
			furnishing.setMethod("pushToFurnishing");
			moreList.add(furnishing);
			
			XKNavigation calculator = new XKNavigation();
			calculator.setNavId("15");
			calculator.setTitle("计算器");
			calculator.setDefaultIcon(R.drawable.menu_calculator);
			calculator.setLink("");
			calculator.setMethod("pushToCalculator");
			moreList.add(calculator);
			
			XKNavigation loan = new XKNavigation();
			loan.setNavId("16");
			loan.setTitle("星空贷");
			loan.setDefaultIcon(R.drawable.menu_loan);
			loan.setLink("");
			loan.setMethod("pushToLoan");
			moreList.add(loan);
			
			XKNavigation usercenter = new XKNavigation();
			usercenter.setNavId("17");
			usercenter.setTitle("个人中心");
			usercenter.setDefaultIcon(R.drawable.menu_me);
			usercenter.setLink("");
			usercenter.setMethod("pushToPersonalCenter");
			moreList.add(usercenter);

            XKNavigation houseBaike = new XKNavigation();
            houseBaike.setNavId("18");
            houseBaike.setTitle("购房百科");
            houseBaike.setDefaultIcon(R.drawable.menu_10);
            houseBaike.setLink("");
            houseBaike.setMethod("pushToEncyclopedias");
            moreList.add(houseBaike);

            XKNavigation buyAbility = new XKNavigation();
            buyAbility.setNavId("19");
            buyAbility.setTitle("购房能力评估");
            buyAbility.setDefaultIcon(R.drawable.menu_09);
            buyAbility.setLink("");
            buyAbility.setMethod("pushToAssessment");
            moreList.add(buyAbility);
		}
	}
	
	@Override
	public int getCount() {
		return moreList.size();
	}

	@Override
	public Object getItem(int position) {
		return moreList.get(position);
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
					// TODO Auto-generated method stub
					startActivity(position);
				}
			});
			
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		XKNavigation navigation = moreList.get(position);
		
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
	
	private DisplayImageOptions getOptions(int position) {
		
		XKNavigation navigation = moreList.get(position);
		int image = R.drawable.menu_index;
		if("pushToHomePage".equals(navigation.getTitle())) {
			image = R.drawable.menu_index;
		}else if("pushToNewHouse".equals(navigation.getTitle())) {
			image = R.drawable.menu_01;
		}else if("pushToOldHouse".equals(navigation.getTitle())) {
			image = R.drawable.menu_02;
		}else if("pushToRentHouse".equals(navigation.getTitle())) {
			image = R.drawable.menu_03;
		}else if("pushToNews".equals(navigation.getTitle())) {
			image = R.drawable.menu_04;
		}else if("pushToEarn".equals(navigation.getTitle())) {
			image = R.drawable.menu_05;
		}else if("pushToFindHouse".equals(navigation.getTitle())) {
			image = R.drawable.menu_06;
		}else if("pushToPrice".equals(navigation.getTitle())) {
			image = R.drawable.menu_07;
		}else if("pushToActivity".equals(navigation.getTitle())) {
			image = R.drawable.menu_sale;
		}else if("pushToGuide".equals(navigation.getTitle())) {
			image = R.drawable.menu_buy;
		}else if("pushToFurnishing".equals(navigation.getTitle())) {
			image = R.drawable.menu_home;
		}else if("pushToCalculator".equals(navigation.getTitle())) {
			image = R.drawable.menu_calculator;
		}else if("pushToLoan".equals(navigation.getTitle())) {
			image = R.drawable.menu_loan;
		}else if("pushToPersonalCenter".equals(navigation.getTitle())) {
			image = R.drawable.menu_me;
		}else if("pushToEncyclopedias".equals(navigation.getTitle())) {
            image = R.drawable.menu_10;
        }else if("pushToAssessment".equals(navigation.getTitle())) {
            image = R.drawable.menu_09;
        }
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(image)   // 加载的图片
	       .showImageOnFail(image) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
		
		return options;
	}
	
	private void startActivity(int position) {
		XKNavigation navigation = moreList.get(position);
		Intent intent;
		Bundle bundle = new Bundle();
		
		if("pushToHomePage".equals(navigation.getMethod())) {
			((Activity) context).finish();
			
		}else if("pushToNewHouse".equals(navigation.getMethod())) {
			context.startActivity(new Intent(context, NewHouseListActivity.class));
			((Activity) context).finish();
			
		}else if("pushToOldHouse".equals(navigation.getMethod())) {
            context.startActivity(new Intent(context, OldHouseListActivity.class));
			((Activity) context).finish();
			
		}else if("pushToRentHouse".equals(navigation.getMethod())) {
            context.startActivity(new Intent(context, RentHouseListActivity.class));
			((Activity) context).finish();
			
		}else if("pushToNews".equals(navigation.getMethod())) {
			intent = new Intent(context, NewsIndexActivity.class);
			bundle.putInt("currentItem", 0);
			intent.putExtras(bundle);
			context.startActivity(intent);
			((Activity) context).finish();
			
		}else if("pushToEarn".equals(navigation.getMethod())) {
			intent = new Intent(context, MainActivity.class);
            bundle.putInt("fragmentFlag", 2);
			intent.putExtras(bundle);
			context.startActivity(intent);
			((Activity) context).finish();
			
		}else if("pushToFindHouse".equals(navigation.getMethod())) {
			context.startActivity(new Intent(context, CustomHouseListActivity.class));
			((Activity) context).finish();
			
		}else if("pushToPrice".equals(navigation.getMethod())) {

		}else if("pushToActivity".equals(navigation.getMethod())) {
			intent = new Intent(context, DiscountActivity.class);
			context.startActivity(intent);
			((Activity) context).finish();
			
		}else if("pushToGuide".equals(navigation.getMethod())) {
			intent = new Intent(context, NewsIndexActivity.class);
			bundle.putInt("currentItem", 1);
			intent.putExtras(bundle);
			context.startActivity(intent);
			((Activity) context).finish();
			
		}else if("pushToFurnishing".equals(navigation.getMethod())) {
			context.startActivity(new Intent(context, JJIndexActivity.class));
			((Activity) context).finish();
			
		}else if("pushToCalculator".equals(navigation.getMethod())) {
			context.startActivity(new Intent(context, CalculatorActivity.class));
			((Activity) context).finish();

		}else if("pushToLoan".equals(navigation.getMethod())) {
			context.startActivity(new Intent(context, XKLoanIndexActivity.class));
			((Activity) context).finish();
			
		}else if("pushToPersonalCenter".equals(navigation.getMethod())) {
			intent = new Intent(context, MainActivity.class);
            bundle.putInt("fragmentFlag", 3);
			intent.putExtras(bundle);
			context.startActivity(intent);
			((Activity) context).finish();
		}else if("pushToEncyclopedias".equals(navigation.getMethod())){
            context.startActivity(new Intent(context, BaikeHouseActivity.class));
            ((Activity) context).finish();
        }else if("pushToAssessment".equals(navigation.getMethod())){
            context.startActivity(new Intent(context, BuyAbilityActivity.class));
            ((Activity) context).finish();
        }else if("pushToSchoolHouse".equals(navigation.getMethod())){
            context.startActivity(new Intent(context, SchoolHouseListActivity.class));
            ((Activity) context).finish();
        }
	}
	
}
