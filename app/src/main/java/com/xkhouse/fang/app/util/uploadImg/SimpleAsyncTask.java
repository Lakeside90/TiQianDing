package com.xkhouse.fang.app.util.uploadImg;

import android.annotation.SuppressLint;
import android.os.Handler;

@SuppressLint("HandlerLeak")
public abstract class SimpleAsyncTask<Params, Result> {
	public final static int Default=888;
	public abstract void onPreExecute();

	public abstract void onPostExecute(Result result);

	public abstract Result doInBackground(Params params);

	public void execute(final Params p) {
		onPreExecute();
		new Thread() {
			public void run() {
				Result r = doInBackground(p);
				handler.sendMessage(handler.obtainMessage(Default, r));
			};
		}.start();

	}

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			onPostExecute((Result) msg.obj);
		};
	};
}
