package com.xkhouse.frame.tool;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * 退出应用类
 * 
 * @Author nieshuting
 * @ClassName Exit
 * @Date 2013-12-19上午10:27:09
 */
public class Exit {
	private boolean isExit = false;

	private static int DELAYTIME = 3000;

	private Runnable task = new Runnable() {
		@Override
		public void run() {
			isExit = false;
		}
	};

	public void doExitInOneSecond() {
		isExit = true;
		HandlerThread thread = new HandlerThread("doTask");
		thread.start();
		new Handler(thread.getLooper()).postDelayed(task, DELAYTIME);
	}

	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}
}
