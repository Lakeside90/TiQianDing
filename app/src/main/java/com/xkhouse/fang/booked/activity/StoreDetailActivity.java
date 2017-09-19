package com.xkhouse.fang.booked.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.booked.adapter.CommentAdapter;
import com.xkhouse.fang.booked.entity.CommentInfo;
import com.xkhouse.fang.booked.entity.StoreDetail;
import com.xkhouse.fang.booked.task.CommentInfoListRequest;
import com.xkhouse.fang.booked.task.StoreDetailRequest;
import com.xkhouse.fang.user.adapter.CJListAdapter;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.task.MessageDetailListRequest;
import com.xkhouse.fang.widget.CustomScrollView;
import com.xkhouse.fang.widget.ScrollListView;
import com.xkhouse.fang.widget.ScrollXListView;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
 * 商户详情
 */
public class StoreDetailActivity extends AppBaseActivity {

    //title
    private ImageView iv_head_left;
    private TextView tv_head_title;
    private ImageView iv_head_right;

    //加载
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private CustomScrollView content_scroll;

    //商家
    private ImageView big_image;
    private TextView b_name_txt;
    private TextView ave_consum_txt;
    private LinearLayout label_lay;
    private ImageView call_iv;
    private TextView address_txt;
    private TextView distance_txt;

    //优惠
    private LinearLayout youhui_lay;
    private TextView youhui_txt;
    private TextView booked_txt;

    private LinearLayout mai_lay;
    private TextView mai_txt;
    private TextView go_mai_txt;

    //抽奖
    private TextView cj_count_txt;
    private ImageView cj_refresh_iv;
    private ScrollListView luck_listview;
    private CJListAdapter luckAdapter;

    //评论
    private TextView comment_count_txt;
    private ScrollXListView comment_listview;
    private CommentAdapter commentAdapter;


    private StoreDetailRequest request;
    private StoreDetail storeDetail;
    private String id;

    private LinearLayout.LayoutParams lps;
    private DisplayImageOptions options;

    private CommentInfoListRequest commentInfoListRequest;
    private ArrayList<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();
    private int currentPageIndex = 1;  //分页索引
    private int pageSize = 10; //每次请求10条数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.leftMargin = DisplayUtil.dip2px(mContext, 3);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nopic)   // 加载的图片
                .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
                .showImageForEmptyUri(R.drawable.nopic)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();

        startTask();

        startCommentTask(1);

    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_store_detail);
    }

    @Override
    protected void init() {
        super.init();

        id = getIntent().getExtras().getString("id");
    }

    @Override
    protected void findViews() {
        initTitle();

        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);
        content_scroll = (CustomScrollView) findViewById(R.id.content_scroll);

        big_image = (ImageView) findViewById(R.id.big_image);
        b_name_txt = (TextView) findViewById(R.id.b_name_txt);
        ave_consum_txt = (TextView) findViewById(R.id.ave_consum_txt);
        label_lay = (LinearLayout) findViewById(R.id.label_lay);
        call_iv = (ImageView) findViewById(R.id.call_iv);
        address_txt = (TextView) findViewById(R.id.address_txt);
        distance_txt = (TextView) findViewById(R.id.distance_txt);

        youhui_lay = (LinearLayout) findViewById(R.id.youhui_lay);
        youhui_txt = (TextView) findViewById(R.id.youhui_txt);
        booked_txt = (TextView) findViewById(R.id.booked_txt);

        mai_lay = (LinearLayout) findViewById(R.id.mai_lay);
        mai_txt = (TextView) findViewById(R.id.mai_txt);
        go_mai_txt = (TextView) findViewById(R.id.go_mai_txt);

        cj_count_txt = (TextView) findViewById(R.id.cj_count_txt);
        cj_refresh_iv = (ImageView) findViewById(R.id.cj_refresh_iv);
        luck_listview = (ScrollListView) findViewById(R.id.luck_listview);

        comment_count_txt = (TextView) findViewById(R.id.comment_count_txt);
        comment_listview = (ScrollXListView) findViewById(R.id.comment_listview);
    }


    private void initTitle() {

        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("商户详情");

        iv_head_right = (ImageView) findViewById(R.id.iv_head_right);
        iv_head_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }


    @Override
    protected void setListeners() {
        error_lay.setOnClickListener(this);
        big_image.setOnClickListener(this);
        call_iv.setOnClickListener(this);
        go_mai_txt.setOnClickListener(this);
        booked_txt.setOnClickListener(this);
        cj_refresh_iv.setOnClickListener(this);

        comment_listview.setPullLoadEnable(false);
        comment_listview.setPullRefreshEnable(true);
        comment_listview.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                startCommentTask(currentPageIndex);
            }
        }, R.id.comment_listview);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.error_lay:
                startTask();
                startCommentTask(1);
                break;

            case R.id.big_image:
                Intent intent = new Intent(StoreDetailActivity.this, StoreImageListActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;

            case R.id.call_iv:
                // 检查是否获得了权限（Android6.0运行时权限）
                if (ContextCompat.checkSelfPermission(StoreDetailActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(StoreDetailActivity.this,
                            Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(StoreDetailActivity.this, "请授权！", Toast.LENGTH_LONG).show();

                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent callIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        callIntent.setData(uri);
                        startActivity(callIntent);
                    }else{
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions(StoreDetailActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                }else {
                    // 已经获得授权，可以打电话
                    CallPhone();
                }
                break;

            case R.id.go_mai_txt:

                break;

            case R.id.booked_txt:

                break;

            case R.id.cj_refresh_iv:

                break;
        }
    }


    private void fillData() {
        if (storeDetail == null) return;

        if (storeDetail.getBanner() != null && storeDetail.getBanner().length > 0) {
            ImageLoader.getInstance().displayImage(storeDetail.getBanner()[1], big_image, options);
        }

        b_name_txt.setText(storeDetail.getBusinessName());
        ave_consum_txt.setText("¥" + storeDetail.getAverageConsump() + "/人");

        String[] labels = storeDetail.getBusinessLabel();
        if (labels != null && labels.length > 0) {
            label_lay.setVisibility(View.VISIBLE);
            label_lay.removeAllViews();
            for (String label : labels) {
                TextView textView = new TextView(mContext);
                textView.setPadding(DisplayUtil.dip2px(mContext, 3),
                        DisplayUtil.dip2px(mContext, 2),
                        DisplayUtil.dip2px(mContext, 3),
                        DisplayUtil.dip2px(mContext, 2));
                textView.setTextColor(mContext.getResources().getColor(R.color.common_gray_txt));
                textView.setTextSize(DisplayUtil.dip2px(mContext, 12));
                textView.setBackground(mContext.getResources().getDrawable(R.drawable.gray_border_btn_bg));
                textView.setText(label);
                label_lay.addView(textView, lps);
            }
        } else {
            label_lay.setVisibility(View.GONE);
        }

        address_txt.setText(storeDetail.getAddress());

        distance_txt.setText(""); // TODO: 17/9/9



    }

    private void fillCommentData() {
        if (commentInfoList == null) return;
        if (commentAdapter == null) {
            commentAdapter = new CommentAdapter(mContext, commentInfoList);
            comment_listview.setAdapter(commentAdapter);
        } else {
            commentAdapter.setData(commentInfoList);
        }
    }


    RequestListener detailRequestListener = new RequestListener() {
        @Override
        public void sendMessage(Message message) {
            rotate_loading.stop();
            rotate_loading.setVisibility(View.GONE);
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    if (storeDetail == null) {
                        content_scroll.setVisibility(View.GONE);
                        error_lay.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.NO_DATA_FROM_NET:
                    error_lay.setVisibility(View.GONE);
                    content_scroll.setVisibility(View.VISIBLE);
                    if (storeDetail == null) {
                        content_scroll.setVisibility(View.GONE);
                    }
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    content_scroll.setVisibility(View.VISIBLE);
                    error_lay.setVisibility(View.GONE);

                    storeDetail = (StoreDetail) message.getData().getSerializable("storeDetail");

                    fillData();
                    break;
            }
        }
    };

    private void startTask() {
        if (NetUtil.detectAvailable(mContext)) {
            if (request == null) {
                request = new StoreDetailRequest(id, modelApp.getSite().getSiteId(), detailRequestListener);
            } else {
                request.setData(id, modelApp.getSite().getSiteId());
            }
            content_scroll.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.VISIBLE);
            rotate_loading.start();

            request.doRequest();

        } else {
            if (storeDetail == null) {
                content_scroll.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }


    RequestListener commentRequestListener = new RequestListener() {
        @Override
        public void sendMessage(Message message) {

            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    break;

                case Constants.NO_DATA_FROM_NET:
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    ArrayList<CommentInfo> temp = (ArrayList<CommentInfo>) message.obj;
                    //根据返回的数据量判断是否隐藏加载更多
                    if (temp.size() < pageSize) {
                        comment_listview.setPullLoadEnable(false);
                    } else {
                        comment_listview.setPullLoadEnable(true);
                    }
                    commentInfoList.addAll(temp);

                    fillCommentData();

                    if (currentPageIndex > 1 && message.arg1 == commentInfoList.size()) {
                        Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                    }
                    currentPageIndex++;
                    break;
            }
            comment_listview.stopLoadMore();
        }
    };

    private void startCommentTask(int page) {

        if (NetUtil.detectAvailable(mContext)) {
            if (commentInfoListRequest == null) {
                commentInfoListRequest = new CommentInfoListRequest(id, page, pageSize, commentRequestListener);
            } else {
                commentInfoListRequest.setData(id, page, pageSize);
            }
            commentInfoListRequest.doRequest();
        } else {
            comment_listview.stopRefresh();
            comment_listview.stopLoadMore();
        }

    }


    private void CallPhone() {
        if (storeDetail == null || StringUtil.isEmpty(storeDetail.getPhone())) {
            Toast.makeText(this, "未获取到号码", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(); // 意图对象：动作 + 数据
            intent.setAction(Intent.ACTION_CALL); // 设置动作
            Uri data = Uri.parse("tel:" + storeDetail.getPhone()); // 设置数据
            intent.setData(data);
            startActivity(intent); // 激活Activity组件
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    // 处理权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    CallPhone();
                } else {
                    // 授权失败！
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }

}
