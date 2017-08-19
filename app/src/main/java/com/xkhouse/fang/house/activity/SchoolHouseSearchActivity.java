package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.adapter.SearchSchoolResultAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.task.SearchSchoolHouseRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/** 
 * @Description: 学区搜索页
 * @author wujian  
 * @date 2016-6-17 上午11:08:29
 */
public class SchoolHouseSearchActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText search_keys_txt;
	private Button search_edit_btn;
	private ImageView keys_clear_iv; 		//清除搜索输入框中的内容

    private ListView search_result_listview;
    private SearchSchoolResultAdapter adapter;
    private ArrayList<CommonType> resultList;

    private SearchSchoolHouseRequest request;


	private String keyword;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(StringUtil.isEmpty(keyword)){
			search_keys_txt.setText(keyword);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		Bundle data = intent.getExtras();
		if(data != null ){
			keyword = data.getString("keyword");
		}
		if(!StringUtil.isEmpty(keyword)){
			search_keys_txt.setText(keyword);
		}
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_school_house_search);
		
	}
	
	@Override
	protected void init() {
		super.init();
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
		tv_head_title.setText("学区搜索");
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

        search_result_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String keyword = resultList.get(position).getName();
                Intent intent = new Intent(mContext, SchoolHouseListActivity.class);
                Bundle data = new Bundle();
                data.putString("keyword", keyword);
                intent.putExtras(data);
                startActivity(intent);
            }
        });

		//软键盘按下搜索键
		search_keys_txt.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					
					closeSoftInput();
					
					Intent intent = new Intent(mContext, SchoolHouseListActivity.class);
					Bundle data = new Bundle();
					data.putString("keyword", search_keys_txt.getText().toString());
					intent.putExtras(data);
					startActivity(intent);
                    return true;
                }
				return false;
			}
		});
		
		//监听搜索框的文字变化
		search_keys_txt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (search_keys_txt.getText().toString().trim().length() > 0) {
					keys_clear_iv.setVisibility(View.VISIBLE);
					search_edit_btn.setText("搜索");
                    startSearchTask(search_keys_txt.getText().toString());
				} else {
                    if(resultList != null) resultList.clear();
                    fillData();
					keys_clear_iv.setVisibility(View.GONE);
					search_edit_btn.setText("取消");
				}
			}
		});
		
		search_edit_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (search_keys_txt.getText().length() > 0) {
					closeSoftInput();
					
					Intent intent = new Intent(mContext, SchoolHouseListActivity.class);
					Bundle data = new Bundle();
					data.putString("keyword", search_keys_txt.getText().toString());
					intent.putExtras(data);
					startActivity(intent);
				} else {
                    closeSoftInput();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
				}
			}
		});
		
		
		keys_clear_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				search_keys_txt.setText("");
			}
		});
	}



    private void fillData(){
        if(resultList == null) return;

        if(adapter == null){
            adapter = new SearchSchoolResultAdapter(mContext, resultList);
            search_result_listview.setAdapter(adapter);
        }else{
            adapter.setData(resultList);
        }
    }

    private RequestListener listener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                    break;

                case Constants.NO_DATA_FROM_NET:
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
        if(NetUtil.detectAvailable(mContext)){
            if(request == null){
                request = new SearchSchoolHouseRequest(modelApp.getSite().getSiteId(),
                        keyword, listener);
            }else{
                request.setData(modelApp.getSite().getSiteId(), keyword);
            }

            request.doRequest();
        }
    }
}
