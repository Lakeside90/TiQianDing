package com.xkhouse.fang.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/** 
 * @Description: 解决scrollview 嵌套gridview 显示不全的问题
 * @author wujian  
 * @date 2015-8-27 下午2:37:49  
 */
public class ScrollGridView extends GridView {

	public ScrollGridView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
    }   
  
    public ScrollGridView(Context context) {   
        super(context);   
    }   
  
    public ScrollGridView(Context context, AttributeSet attrs, int defStyle) {   
        super(context, attrs, defStyle);   
    }   
  
    @Override   
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
  
        int expandSpec = MeasureSpec.makeMeasureSpec(   
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
        super.onMeasure(widthMeasureSpec, expandSpec);   
    }
}
