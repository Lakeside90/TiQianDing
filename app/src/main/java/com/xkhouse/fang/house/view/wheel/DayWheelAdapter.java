package com.xkhouse.fang.house.view.wheel;

import java.util.List;

import android.content.Context;

import com.xkhouse.fang.widget.wheelview.AbstractWheelTextAdapter;
import com.xkhouse.fang.widget.wheelview.WheelView;

public class DayWheelAdapter extends AbstractWheelTextAdapter {

	private List<String> days;

	public DayWheelAdapter(Context context, List<String> days, WheelView wheelView) {
		super(context);
		this.days = days;
		setWheelView(wheelView);
	}

	@Override
	public int getItemsCount() {
		return days.size();
	}

	@Override
	protected CharSequence getItemText(int index) {
		if (index < days.size()) {
			return  days.get(index) + "日";
		} else {
			return "";
		}
	}

}
