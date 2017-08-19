package com.xkhouse.fang.house.view.wheel;

import java.util.List;

import android.content.Context;

import com.xkhouse.fang.widget.wheelview.AbstractWheelTextAdapter;
import com.xkhouse.fang.widget.wheelview.WheelView;

public class YearWheelAdapter extends AbstractWheelTextAdapter {

	private List<String> years;

	public YearWheelAdapter(Context context, List<String> years, WheelView wheelView) {
		super(context);
		this.years = years;
		setWheelView(wheelView);
	}

	@Override
	public int getItemsCount() {
		return years.size();
	}

	@Override
	protected CharSequence getItemText(int index) {
		if (index < years.size()) {
			return  years.get(index) + "年";
		} else {
			return "";
		}
	}
}
