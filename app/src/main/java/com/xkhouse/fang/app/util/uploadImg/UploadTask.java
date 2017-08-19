package com.xkhouse.fang.app.util.uploadImg;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;

public class UploadTask extends SimpleAsyncTask<String, String> {

	// 返回状态
	private int status = 0;
	// 返回提示语
	private String msg = "上传图片失败";
	
	//图片地址
	private String url = "";

	public UploadCallBack callBack;

	public interface UploadCallBack {
		void onSucess(String url);

		void onFail(String url);
	}

	public UploadTask(UploadCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	public void onPreExecute() {

	}

	@Override
	public void onPostExecute(String result) {
		Logger.d("UploadTask", result);
		parse(result);
		if (callBack != null && status == 200) {
			callBack.onSucess(url);
		} else {
			callBack.onFail(url);
		}
	}

	@Override
	public String doInBackground(String path) {

		return UploadUtil.postImage(Constants.UPLOAD_PHOTO_URL,
				getExtras(),
				path);
		
	}

	private Map<String, String> getExtras() {
		Map<String, String> params = new HashMap<String, String>();
//		params.put("action", "uploadimage");
		params.put("file", "upfile");
		return params;
	}

	private void parse(String result) {
		if (StringUtil.isEmpty(result)) {
			return;
		}
		
		try {
			if (result != null && result.startsWith("\ufeff")) {
				result = result.substring(1);
			}

			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject != null) {
				if("SUCCESS".equals(jsonObject.optString("state"))){
					status = 200;
				}
				url = jsonObject.optString("url");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
