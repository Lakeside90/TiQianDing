package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.house.view.SearchHistoryView;
import com.xkhouse.fang.house.view.SearchResultView;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description: 搜索页 
 * @author wujian  
 * @date 2015-9-1 上午11:08:29  
 */
public class SearchActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText search_keys_txt;
	private Button search_edit_btn;
	private ImageView keys_clear_iv; 		//清除搜索输入框中的内容
	private FrameLayout search_container_view;
	
	private SearchHistoryView historyView;
	private SearchResultView resultView;
	
	/** 新房  **/
	public static final String SEARCH_NEW_HOUSE = "newhouse";

	/** 二手房  **/
	public static final String SEARCH_OLD_HOUSE = "oldhouse";
	
	/** 租房 **/
	public static final String SEARCH_RENT_HOUSE = "zufang";
	
	/** 新闻  **/
	public static final String SEARCH_NEWS = "news";
	
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
		if (historyView != null) {
			historyView.fillSearchHistoryData();
		}
        if(!StringUtil.isEmpty(keyword)){
            historyView.getView().setVisibility(View.INVISIBLE);
        }
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
		
		setContentView(R.layout.activity_search);
		
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
		search_container_view = (FrameLayout) findViewById(R.id.search_container_view);
		
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		if (historyView == null) {
			historyView = new SearchHistoryView(this);
			search_container_view.addView(historyView.getView(), lp);
		}
		
		if (resultView == null) {
			resultView = new SearchResultView(this);
			resultView.getView().setVisibility(View.GONE);
			search_container_view.addView(resultView.getView(), lp);
		}
		
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
					
					Intent intent = new Intent(mContext, SearchResultActivity.class);
					Bundle data = new Bundle();
					data.putString("keyWord", search_keys_txt.getText().toString());
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
				// TODO Auto-generated method stub
				if (search_keys_txt.getText().toString().trim().length() > 0) {
					resultView.getView().setVisibility(View.VISIBLE);
					historyView.dismissContentView();
					keys_clear_iv.setVisibility(View.VISIBLE);
					search_edit_btn.setText("搜索");
					resultView.refreshView(search_keys_txt.getText().toString());
				} else {
					resultView.getView().setVisibility(View.GONE);
					historyView.showContentView();
					
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
					
					Intent intent = new Intent(mContext, SearchResultActivity.class);
					Bundle data = new Bundle();
					data.putString("keyWord", search_keys_txt.getText().toString());
					intent.putExtras(data);
					startActivity(intent);
					
				} else {
                    closeSoftInput();
                    //解決黑屏問題
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


}
