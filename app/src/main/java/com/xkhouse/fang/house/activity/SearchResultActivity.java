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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.activity.NewsSearchActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.adapter.SearchResultAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.XKSearchResult;
import com.xkhouse.fang.house.service.SearchDbService;
import com.xkhouse.fang.house.task.SearchKeyWordRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/** 
 * @Description:  搜索结果页
 * @author wujian  
 * @date 2015-9-2 上午9:26:23  
 */
public class SearchResultActivity extends AppBaseActivity {

	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText search_keys_txt;
	private Button search_edit_btn;
	private ImageView keys_clear_iv; 		//清除搜索输入框中的内容
	
	private LinearLayout result_count_lay;
	private TextView result_count_txt;
	
	//搜索结果
	private ListView result_listview;
	private ArrayList<CommonType> resultList = new ArrayList<CommonType>();
	private SearchResultAdapter resultAdapter;
	private SearchKeyWordRequest request;
	
	//匹配结果
	private ListView match_listview;
	private SearchResultAdapter matchAdapter;
	
	private String keyWord;
	private SearchDbService searchDbService = new SearchDbService();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		search_keys_txt.setText(keyWord);
		startSearchTask();
	}
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_search_result);
	}
	
	@Override
	protected void init() {
		super.init();
		keyWord = getIntent().getExtras().getString("keyWord");
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		search_keys_txt = (EditText) findViewById(R.id.search_keys_txt);
		search_edit_btn = (Button) findViewById(R.id.search_edit_btn);
		keys_clear_iv = (ImageView) findViewById(R.id.keys_clear_iv);
		
		result_count_lay = (LinearLayout) findViewById(R.id.result_count_lay);
		result_count_txt = (TextView) findViewById(R.id.result_count_txt);
		result_listview = (ListView) findViewById(R.id.result_listview);
		match_listview = (ListView) findViewById(R.id.match_listview);
		
		
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("搜索");
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
		
		//软键盘按下搜索键
		search_keys_txt.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					
					closeSoftInput();
					
					startSearchTask();
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
				if (search_keys_txt.getText().length() > 0) {
					keys_clear_iv.setVisibility(View.VISIBLE);
					search_edit_btn.setText("搜索");
				} else {
					keys_clear_iv.setVisibility(View.GONE);
					search_edit_btn.setText("取消");
					resultList.clear();
					result_count_txt.setText("共0个搜索结果，请选择类别查看");
					fillData();
				}
			}
		});
		
		//搜索
		search_edit_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (search_keys_txt.getText().length() > 0) {
					startSearchTask();
					
				} else {
					closeSoftInput();
					finish();
				}
			}
		});
		
		//点击搜索结果
		result_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				String keyword = search_keys_txt.getText().toString();
				Bundle data = new Bundle();
				data.putString("keyword", keyword);
				Intent intent = null;
				
				XKSearchResult result = new XKSearchResult();
				result.setProjectName(keyword);
				
				if("新房".equals(resultList.get(position).getName())){
					intent = new Intent(SearchResultActivity.this, NewHouseListActivity.class);
					result.setType(SearchActivity.SEARCH_NEW_HOUSE);
					
				}else if("二手房".equals(resultList.get(position).getName())){
					intent = new Intent(SearchResultActivity.this, OldHouseListActivity.class);
					result.setType(SearchActivity.SEARCH_OLD_HOUSE);
					
				}else if("租房".equals(resultList.get(position).getName())){
					intent = new Intent(SearchResultActivity.this, RentHouseListActivity.class);
					result.setType(SearchActivity.SEARCH_RENT_HOUSE);
					
				}else if("资讯".equals(resultList.get(position).getName())){
					intent = new Intent(SearchResultActivity.this, NewsSearchActivity.class);
					result.setType(SearchActivity.SEARCH_NEWS);
				}
				
				searchDbService.saveSearchContent(result, "", modelApp.getSite().getSiteId());
				
				if(intent != null){
					intent.putExtras(data);
					startActivity(intent);	
				}
				
			}
		});
		
		//点击匹配结果
		match_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
		
		keys_clear_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				search_keys_txt.setText("");
			}
		});
	}

	private void fillData() {
		if(resultList == null) return;
		
		if(resultAdapter == null){
			resultAdapter = new SearchResultAdapter(mContext, resultList);
			result_listview.setAdapter(resultAdapter);
		}else {
			resultAdapter.setData(resultList);
		}
	}

	private RequestListener listener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			hideLoadingDialog();
			
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.NO_DATA_FROM_NET:
				Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				Bundle data = message.getData();
				CommonType newhouseCount = new CommonType();
				newhouseCount.setName("新房");
				newhouseCount.setId(data.getString("newhouseCount"));
				
				CommonType oldhouseCount = new CommonType();
				oldhouseCount.setName("二手房");
				oldhouseCount.setId(data.getString("oldhouseCount"));
				
				CommonType zufangCount = new CommonType();
				zufangCount.setName("租房");
				zufangCount.setId(data.getString("zufangCount"));
				
				CommonType newsCount = new CommonType();
				newsCount.setName("资讯");
				newsCount.setId(data.getString("newsCount"));
				
				if(resultList != null){
					resultList.clear();
				}else{
					resultList = new ArrayList<CommonType>();
				}
				
				resultList.add(newhouseCount);
				resultList.add(oldhouseCount);
				resultList.add(zufangCount);
				resultList.add(newsCount);
				
				fillData();
				
				try {
					int newhouse = Integer.parseInt(data.getString("newhouseCount"));
					int oldhouse = Integer.parseInt(data.getString("oldhouseCount"));
					int zufang = Integer.parseInt(data.getString("zufangCount"));
					int news = Integer.parseInt(data.getString("newsCount"));
					int count = newhouse + oldhouse + zufang + news;
					
					result_count_txt.setText("共" + count + "个搜索结果，请选择类别查看");
				} catch (Exception e) {
					result_count_txt.setText("共0个搜索结果，请选择类别查看");
				}
				
				break;
			}
		}
	};
	
	/**
	* @Description: 搜索 （按下搜索按钮）
	* @return void    返回类型
	 */
	private void startSearchTask(){
		String keyWord = search_keys_txt.getText().toString();
		if(StringUtil.isEmpty(keyWord.trim())){
			Toast.makeText(mContext, "请输入搜索内容", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(NetUtil.detectAvailable(mContext)){
			if(request == null){
				 request = new SearchKeyWordRequest(modelApp.getSite().getSiteId(), 
						 keyWord, listener);
			}else{
				request.setData(modelApp.getSite().getSiteId(), keyWord);
			}
			showLoadingDialog(R.string.data_loading);
			request.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	} 
	

	
	

}
