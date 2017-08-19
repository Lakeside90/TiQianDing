package com.xkhouse.fang.house.view.wheel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.widget.wheelview.OnWheelChangedListener;
import com.xkhouse.fang.widget.wheelview.WheelView;

public class SelectDatePopWindow extends PopupWindow implements OnClickListener {

	private Context context;
	
	private WheelView year_wv;
	private WheelView month_wv;
	private WheelView day_wv;
	
	private TextView cancel;
	private TextView confirm;
	
	private YearWheelAdapter yearAdapter;
	private MonthWheelAdapter monthAdapter;
	private DayWheelAdapter dayAdapter;
	
	private OnWheelChangedListener yearChangedListener, monthChangedListener, dayChangedListener;
	
	private List<String> years = new ArrayList<String>();
	private List<String> months = new ArrayList<String>();
	private List<String> days = new ArrayList<String>();
	
	private String currentYear;
	private String currentMonth;
	private String currentDay;

	private int yearIndex;
	private int monthIndex;
	private int dayIndex;
	
	private DateSelectedListener listener;
	
	public SelectDatePopWindow(Context context, 
							   String currentYear, String currentMonth, String currentDay,
							   DateSelectedListener listener) {
		super(context);
		this.context = context;
		this.currentYear = currentYear;
		this.currentMonth = currentMonth;
		this.currentDay = currentDay;
		this.listener = listener;
		setAnimationStyle(R.style.PopupAnimation);
		init();
	}
	
	public void setData(String currentYear, String currentMonth, String currentDay){
		this.currentYear = currentYear;
		this.currentMonth = currentMonth;
		this.currentDay = currentDay;
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		//fill   data 
		setIndex();
		year_wv.setCurrentItem(yearIndex);
		month_wv.setCurrentItem(monthIndex);
		if(currentDay != null){
			day_wv.setCurrentItem(dayIndex);
		}
		
		
		super.showAtLocation(parent, gravity, x, y);
	}

	private void init() {
		
		initData();

		setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
		setOutsideTouchable(false);
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.view_wheel, null);
		setContentView(view);
		setBackgroundDrawable(new BitmapDrawable());

		findViews(view);
		setListener();
		initWheelView();
	}

	private void initData(){
		int yearStart = 1900;
		for (int i = 0; i < 200; i++) {
			years.add(String.valueOf(yearStart+i));
		}
		for(int i = 1; i < 13; i++){
			if(i < 10){
				months.add("0"+String.valueOf((i)));
			}else{
				months.add(String.valueOf((i)));
			}
			
		}
		int max_day = getMaxDays(currentYear, currentMonth);
		
		for(int i = 1; i <= max_day; i++){
			if(i < 10){
				days.add("0"+String.valueOf(i));
			}else{
				days.add(String.valueOf(i));
			}
			
		}
	}
	
	private void setIndex(){
		yearIndex = years.indexOf(currentYear);
		monthIndex = months.indexOf(currentMonth);
		if(currentDay != null){
			dayIndex = days.indexOf(currentDay);
		}
	}
	
	private void findViews(View view) {
		year_wv = (WheelView) view.findViewById(R.id.year_wv);
		month_wv = (WheelView) view.findViewById(R.id.month_wv);
		day_wv = (WheelView) view.findViewById(R.id.day_wv);
		if(currentDay == null){
			day_wv.setVisibility(View.GONE);
		}
		
		cancel = (TextView) view.findViewById(R.id.cancel);
		confirm = (TextView) view.findViewById(R.id.confirm);
	}

	private void setListener() {
		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
		yearChangedListener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (newValue == 0) {
					
				} else {
					currentYear = years.get(newValue);
					yearIndex = newValue;
					days.clear();
					int max_day = getMaxDays(currentYear, currentMonth);
					for(int i = 1; i <= max_day; i++){
						if(i < 10){
							days.add("0" + String.valueOf(i));
						}else{
							days.add(String.valueOf(i));
						}
					}
					if(currentDay != null){
						if(Integer.parseInt(currentDay) > max_day){
							dayIndex = max_day-1;
						}
						day_wv.setCurrentItem(dayIndex);
						currentDay = days.get(dayIndex);
						dayAdapter.notifyDataInvalidatedEvent();
					}
					
				}
				
				
			}
		};
		
		
		monthChangedListener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (newValue == 0) {
					
				} else {
					currentMonth = months.get(newValue);
					monthIndex = newValue;
					days.clear();
					int max_day = getMaxDays(currentYear, currentMonth);
					for(int i = 1; i <= max_day; i++){
						if(i < 10){
							days.add("0" + String.valueOf(i));
						}else{
							days.add(String.valueOf(i));
						}
					}
					if(currentDay != null){
						if(Integer.parseInt(currentDay) > max_day){
							dayIndex = max_day-1;
						}
						day_wv.setCurrentItem(dayIndex);
						currentDay = days.get(dayIndex);
						dayAdapter.notifyDataInvalidatedEvent();
					}
				}
			}
		};
		
		dayChangedListener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (newValue == 0) {
					
				} else {
					currentDay = days.get(newValue);
					dayIndex = newValue;
				}
			}
			
		};
	}

	private void initWheelView() {
		monthAdapter = new MonthWheelAdapter(context, months, month_wv);
		yearAdapter = new YearWheelAdapter(context, years, year_wv);
		dayAdapter = new DayWheelAdapter(context, days, day_wv);

		year_wv.setViewAdapter(yearAdapter);
		year_wv.addChangingListener(yearChangedListener);
		year_wv.setMidTextColor(context.getResources().getColor(R.color.area_wheel_selected));
		year_wv.setOtherTextColor(context.getResources().getColor(R.color.area_wheel_unselect));

		month_wv.setViewAdapter(monthAdapter);
		month_wv.addChangingListener(monthChangedListener);
		month_wv.setMidTextColor(context.getResources().getColor(R.color.area_wheel_selected));
		month_wv.setOtherTextColor(context.getResources().getColor(R.color.area_wheel_unselect));
		
		if(currentDay != null){
			day_wv.setViewAdapter(dayAdapter);
			day_wv.addChangingListener(dayChangedListener);
			day_wv.setMidTextColor(context.getResources().getColor(R.color.area_wheel_selected));
			day_wv.setOtherTextColor(context.getResources().getColor(R.color.area_wheel_unselect));
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			dismiss();
			break;
		case R.id.confirm:
			if(listener != null){
				listener.onSelected(currentYear, currentMonth, currentDay);
			}
			dismiss();
			break;
		default:
			break;
		}
	}


	private int getMaxDays(String year, String month){
		
		int max_day = 0;
		String source = year + "-" + month;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
	    try {
	      Date date = format.parse(source);
	      Calendar calendar = new GregorianCalendar();
	      calendar.setTime(date);
	      max_day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }
		
	    return max_day;
	}
	
	public interface DateSelectedListener{
		public void onSelected(String year, String month, String day);
	}

}
