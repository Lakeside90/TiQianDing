package com.xkhouse.fang.app.util.uploadImg;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

public class UploadRequest extends StringRequest {

	public UploadRequest(String url, Listener<String> listener,
			ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	@Override
	protected void deliverResponse(String response) {
		String code = "";
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has("code")) {
				code = jsonObject.getString("code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		super.deliverResponse(code);
	}

}
