package com.xkhouse.fang.widget.wheelview;

import java.util.List;

import android.content.Context;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 5;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;

	// Values
	private int minValue;
	private int maxValue;

	// format
	private String format;

	private List<String> data;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 */
	public NumericWheelAdapter(Context context) {
		this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 */
	public NumericWheelAdapter(Context context, int minValue, int maxValue) {
		this(context, minValue, maxValue, null);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 * @param date
	 *            the string
	 */
	public NumericWheelAdapter(Context context, int minValue, int maxValue, List<String> data) {
		super(context);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.data = data;
	}

	@Override
	public CharSequence getItemText(int index) {
		if (index > 0 && index < getItemsCount()) {
			int value = minValue + index;
			if (data == null) {
				return Integer.toString(value);
			} else {
				return data.get(index);
			}
		} else if (index == 0) {
			return "保密";
		}
		return null;
	}

	public int getItemsCount() {
		return maxValue - minValue + 1;
	}

	public void setData(List<String> data) {
		this.data = data;
	}
}