package com.xkhouse.fang.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.xkhouse.fang.app.util.DisplayUtil;

/**
 * @Description: 解决scrollview 内部嵌套横向滑动的视图产生的滑动冲突问题
 * @author wujian
 * @date 2015-8-28 上午11:17:09
 */
public class CustomScrollView extends ScrollView {


    private OnBorderListener onBorderListener;
    private View contentView;

	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        doOnBorderListener();
    }

    public void setOnBorderListener(final OnBorderListener onBorderListener) {
        this.onBorderListener = onBorderListener;
        if (onBorderListener == null) {
            return;
        }

        if (contentView == null) {
            contentView = getChildAt(0);
        }
    }

    /**
     * OnBorderListener, Called when scroll to top or bottom
     *
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-22
     */
    public interface OnBorderListener {

        /**
         * Called when scroll to bottom
         */
        public void onBottom();

        /**
         * Called when scroll to top
         */
        public void onTop();

        public void onHideBar();

        public void onShowBar();
    }

    private void doOnBorderListener() {
        if (contentView != null && contentView.getMeasuredHeight() <= getScrollY() + getHeight()) {
            if (onBorderListener != null) {
                onBorderListener.onBottom();
            }
        } else if (getScrollY() == 0) {
            if (onBorderListener != null) {
                onBorderListener.onTop();
            }
        }
        if(getScrollY() < DisplayUtil.dip2px(getContext(),800)){
            if (onBorderListener != null) {
                onBorderListener.onHideBar();
            }
        }else{
            if (onBorderListener != null) {
                onBorderListener.onShowBar();
            }
        }
    }




	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}
}
