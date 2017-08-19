package com.xkhouse.fang.house.view.wheel;

import java.util.List;

import android.content.Context;

import com.xkhouse.fang.widget.wheelview.AbstractWheelTextAdapter;
import com.xkhouse.fang.widget.wheelview.WheelView;

public class MonthWheelAdapter extends AbstractWheelTextAdapter {

	private List<String> months;

	public MonthWheelAdapter(Context context, List<String> months, WheelView wheelView) {
		super(context);
		this.months = months;
		setWheelView(wheelView);
	}

	@Override
	public int getItemsCount() {
		return months.size();
	}

	@Override
	protected CharSequence getItemText(int index) {
		if (index < months.size()) {
			return  months.get(index) + "月";
		} else {
			return "";
		}
	}

}
