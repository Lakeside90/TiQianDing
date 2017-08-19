package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.user.adapter.SearchReleaseAreaResultAdapter;
import com.xkhouse.fang.user.task.SearchReleaseAreaRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/** 
 * @Description: 搜索页 
 * @author wujian  
 * @date 2016-04-15 上午11:08:29
 */
public class ReleaseStreetSearchActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText search_keys_txt;
	private Button search_edit_btn;
	private ImageView keys_clear_iv; 		//清除搜索输入框中的内容

    private ListView search_result_listview;
    private SearchReleaseAreaResultAdapter adapter;
    private ArrayList<CommonType> resultList;

    private SearchReleaseAreaRequest request;

    public static final int RENT_ACTIVITY = 1;
    public static final int SELL_ACTIVITY = 2;
    public static final int RENT_EDIT_ACTIVITY = 3;
    public static final int SELL_EDIT_ACTIVITY = 4;

    private int activity;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	protected void onResume() {
		super.onResume();

		
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_release_street_search);
		
	}
	
	@Override
	protected void init() {
		super.init();
        activity = getIntent().getExtras().getInt("activity");

	}
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		search_keys_txt = (EditText) findViewById(R.id.search_keys_txt);
		keys_clear_iv = (ImageView) findViewById(R.id.keys_clear_iv);
		search_edit_btn = (Button) findViewById(R.id.search_edit_btn);
        search_result_listview = (ListView) findViewById(R.id.search_result_listview);

	}
	
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("选择小区");
		iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                closeSoftInput();
                //解決黑屏問題
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
	}
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		//监听搜索框的文字变化
		search_keys_txt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
                keys_clear_iv.setVisibility(View.VISIBLE);
			}
		});
		
		search_edit_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               if(!StringUtil.isEmpty(search_keys_txt.getText().toString())){
                   startSearchTask(search_keys_txt.getText().toString());
               }
            }
        });


        keys_clear_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                search_keys_txt.setText("");
			}
		});

        search_result_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent;
                if(activity == SELL_ACTIVITY){
                    intent = new Intent(ReleaseStreetSearchActivity.this, SellReleaseActivity.class);
                    Bundle data  = new Bundle();
                    intent.putExtra("code", 200);
                    data.putSerializable("area", resultList.get(position));
                    intent.putExtras(data);
                    startActivity(intent);

                }else if(activity == SELL_EDIT_ACTIVITY){
                    intent = new Intent(ReleaseStreetSearchActivity.this, SellReleaseEditActivity.class);
                    Bundle data  = new Bundle();
                    intent.putExtra("code", 200);
                    data.putSerializable("area", resultList.get(position));
                    intent.putExtras(data);
                    startActivity(intent);

                }else if(activity == RENT_ACTIVITY){
                    intent = new Intent(ReleaseStreetSearchActivity.this, RentReleaseActivity.class);
                    Bundle data  = new Bundle();
                    intent.putExtra("code", 200);
                    data.putSerializable("area", resultList.get(position));
                    intent.putExtras(data);
                    startActivity(intent);

                }else if(activity == RENT_EDIT_ACTIVITY){
                    intent = new Intent(ReleaseStreetSearchActivity.this, RentReleaseEditActivity.class);
                    Bundle data  = new Bundle();
                    intent.putExtra("code", 200);
                    data.putSerializable("area", resultList.get(position));
                    intent.putExtras(data);
                    startActivity(intent);

                }


            }
        });
	}


    private void fillData(){
        if(resultList == null) return;

        if(adapter == null){
            adapter = new SearchReleaseAreaResultAdapter(this, resultList);
            search_result_listview.setAdapter(adapter);
        }else{
            adapter.setData(resultList);
        }
    }

    private RequestListener listener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            hideLoadingDialog();
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    Toast.makeText(ReleaseStreetSearchActivity.this, R.string.service_error, Toast.LENGTH_SHORT).show();
                    break;

                case Constants.NO_DATA_FROM_NET:
                    Toast.makeText(ReleaseStreetSearchActivity.this, "当前小区不存在", Toast.LENGTH_SHORT).show();
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    ArrayList<CommonType> temp = (ArrayList<CommonType>) message.obj;
                    if(resultList != null){
                        resultList.clear();
                    }else{
                        resultList = new ArrayList<CommonType>();
                    }
                    resultList.addAll(temp);

                    fillData();
                    break;
            }
        }
    };

    private void startSearchTask(String keyword){
        if(NetUtil.detectAvailable(ReleaseStreetSearchActivity.this)){
            if(request == null){
                request = new SearchReleaseAreaRequest(modelApp.getSite().getSiteId(),
                        keyword, listener);
            }else{
                request.setData(modelApp.getSite().getSiteId(), keyword);
            }
            showLoadingDialog(R.string.data_loading);
            request.doRequest();
        }
    }





}
