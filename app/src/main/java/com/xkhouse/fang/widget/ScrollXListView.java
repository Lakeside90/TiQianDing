package com.xkhouse.fang.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.xkhouse.fang.widget.xlist.XListView;

/** 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author wujian  
 * @date 2015-11-17 上午10:15:07  
 */
public class ScrollXListView extends XListView {

	public ScrollXListView(Context context){
		super(context);
	}
	
	public ScrollXListView(Context context, AttributeSet attrs) {
		super(context, attrs);
    }
	
	public ScrollXListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, 
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
