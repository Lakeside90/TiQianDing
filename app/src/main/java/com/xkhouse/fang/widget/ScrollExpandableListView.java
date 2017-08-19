package com.xkhouse.fang.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/** 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author wujian  
 * @date 2015-9-7 下午4:12:39  
 */
public class ScrollExpandableListView extends ExpandableListView {

	/**
	 * @param context
	 */
	public ScrollExpandableListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ScrollExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
    }
	
	public ScrollExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override      

	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec) {          

	// TODO Auto-generated method stub          

	int expandSpec = MeasureSpec.makeMeasureSpec(  Integer.MAX_VALUE >> 2, 

	MeasureSpec.AT_MOST); 

	super.onMeasure(widthMeasureSpec, expandSpec);

	}
}
