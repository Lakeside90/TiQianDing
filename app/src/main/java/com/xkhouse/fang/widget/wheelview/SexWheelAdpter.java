package com.xkhouse.fang.widget.wheelview;

import android.content.Context;

public class SexWheelAdpter extends AbstractWheelTextAdapter {

	public SexWheelAdpter(Context context) {
		super(context);
	}

	@Override
	public int getItemsCount() {
		return 3;
	}

	@Override
	protected CharSequence getItemText(int index) {
		if (index == 0) {
			return "保密";
		} else if (index == 1) {
			return "男生";
		} else {
			return "女生";
		}
	}

}
