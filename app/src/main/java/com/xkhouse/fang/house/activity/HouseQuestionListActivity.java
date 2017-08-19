package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.house.adapter.HouseQuestionAdapter;
import com.xkhouse.fang.house.entity.HQuestion;
import com.xkhouse.fang.house.task.HouseQuestionListRequest;
import com.xkhouse.fang.user.activity.LoginActivity;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/** 
 * @Description: 楼盘问答列表
 * @author wujian  
 * @date 2016-06-20 上午9:36:29
 */
public class HouseQuestionListActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
    private TextView tv_head_right;

    private ImageView scroll_top_iv;
	private XListView question_listView;
	private HouseQuestionAdapter adapter;

    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

	private HouseQuestionListRequest request;
	private ArrayList<HQuestion> questionList = new ArrayList<HQuestion>();
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private boolean isPullDown = false; // 下拉
	
	private String pid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startListTask(1, true);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_house_question_list);
	}

    @Override
    protected void init() {
        super.init();

        pid = getIntent().getExtras().getString("pid");
    }

    @Override
	protected void findViews() {
		super.findViews();
		
		initTitle();

        question_listView = (XListView) findViewById(R.id.question_listView);
        scroll_top_iv = (ImageView) findViewById(R.id.scroll_top_iv);

        content_lay = (LinearLayout) findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("楼盘问答");
		iv_head_left.setOnClickListener(this);

        tv_head_right = (TextView) findViewById(R.id.tv_head_right);
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText("我要提问");
        tv_head_right.setOnClickListener(this);
	}
	
	@Override
	protected void setListeners() {
		super.setListeners();

        error_lay.setOnClickListener(this);
        scroll_top_iv.setOnClickListener(this);

		question_listView.setPullLoadEnable(true);
		question_listView.setPullRefreshEnable(true);
		question_listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startListTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startListTask(currentPageIndex, false);
            }
        }, R.id.house_listView);

        question_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //滑到底部自动加载下一页数据
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && question_listView.getEnablePullLoad()) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1
                            && !question_listView.getPullLoading()) {
                        question_listView.startLoadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 5) {
                    if (scroll_top_iv.getVisibility() == View.GONE) {
                        scroll_top_iv.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (scroll_top_iv.getVisibility() == View.VISIBLE) {
                        scroll_top_iv.setVisibility(View.GONE);
                    }
                }
            }
        });
	}
	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
            case R.id.iv_head_left:
                finish();
                break;

            case R.id.tv_head_right:
                Bundle data = new Bundle();
                data.putString("pid", pid);

                if(Preference.getInstance().readIsLogin()){
                    Intent intent = new Intent(HouseQuestionListActivity.this, HouseQuestionAddActivity.class);
                    intent.putExtras(data);
                    startActivity(intent);
                }else {
                    Toast.makeText(mContext, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra("classStr", HouseQuestionAddActivity.class);
                    intent.putExtras(data);
                    startActivity(intent);
                }
                break;

            case R.id.scroll_top_iv:
                question_listView.post(new Runnable() {
                    @Override
                    public void run() {
                        question_listView.smoothScrollToPosition(0);
                    }
                });
                break;

            case R.id.error_lay:
                startListTask(1, true);
                break;
		}
	}
	
	private void fillData() {
		if(questionList == null) return;
		if(adapter == null){
			adapter = new HouseQuestionAdapter(mContext, questionList);
			question_listView.setAdapter(adapter);
		}else {
			adapter.setData(questionList);
		}
	}
	
	
	private RequestListener requestListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
            rotate_loading.stop();
            rotate_loading.setVisibility(View.GONE);

            if (isPullDown){
                currentPageIndex = 1;
            }
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
                if(questionList != null && questionList.size()>0){
                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                }else{
                    error_lay.setVisibility(View.VISIBLE);
                    content_lay.setVisibility(View.GONE);
                }
				break;
				
			case Constants.NO_DATA_FROM_NET:
                error_lay.setVisibility(View.GONE);
                content_lay.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
                content_lay.setVisibility(View.VISIBLE);
                error_lay.setVisibility(View.GONE);

				ArrayList<HQuestion> temp = (ArrayList<HQuestion>) message.obj;
                //根据返回的数据量判断是否隐藏加载更多
				if(temp.size() < pageSize){
					question_listView.setPullLoadEnable(false);
				}else{
					question_listView.setPullLoadEnable(true);
				}
				
				//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
				if(isPullDown && questionList != null){
					questionList.clear();
					currentPageIndex = 1;
				}
				questionList.addAll(temp);
				fillData();
                if (currentPageIndex > 1 && message.arg1 == questionList.size()){
                    Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                }
                currentPageIndex++;
				break;
			}
			isPullDown = false;
			question_listView.stopRefresh();
			question_listView.stopLoadMore();
		}
	};

	private void startListTask(int page, boolean showLoading){
		if(NetUtil.detectAvailable(mContext)) {
			if(request == null){
				request = new HouseQuestionListRequest(modelApp.getSite().getSiteId(), "", pid, page, pageSize, requestListener);
			}else {
				request.setData(modelApp.getSite().getSiteId(), "", pid, page, pageSize);
			}
            if(showLoading){
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			request.doRequest();
		}else {
			isPullDown = false;
			question_listView.stopRefresh();
			question_listView.stopLoadMore();

            if (questionList == null || questionList.size() == 0){
                content_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }

		}
	}
	
}
