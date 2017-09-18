package com.xkhouse.fang.user.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.adapter.TXRecordListAdapter;
import com.xkhouse.fang.user.entity.TXRecord;
import com.xkhouse.fang.user.task.TXRecordListRequest;
import com.xkhouse.fang.widget.ScrollListView;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;


public class TXIndexActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;
    private TextView tv_head_right;

    private TextView tx_bank_txt;
    private TextView tx_zfb_txt;

    private ScrollListView tx_record_listview;
    private TXRecordListAdapter adapter;

    private LinearLayout no_data_lay;


    private ArrayList<TXRecord> records = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startRecordListTask();
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_txindex);
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitle();

        tx_bank_txt = (TextView) findViewById(R.id.tx_bank_txt);
        tx_zfb_txt = (TextView) findViewById(R.id.tx_zfb_txt);

        tx_record_listview = (ScrollListView) findViewById(R.id.tx_record_listview);

        no_data_lay = (LinearLayout) findViewById(R.id.no_data_lay);
    }

    private void initTitle() {
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_right = (TextView) findViewById(R.id.tv_head_right);
        tv_head_title.setText("提现");
        tv_head_right.setText("提现说明");
        tv_head_right.setVisibility(View.VISIBLE);

        tv_head_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(mContext, CashWithdrawDeclareActivity.class));
            }
        });

        iv_head_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();

        tx_bank_txt.setOnClickListener(this);
        tx_zfb_txt.setOnClickListener(this);

        tx_record_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TXRecord record = records.get(position);
                Intent intent;
                if ("0".equals(record.getType())) {
                    intent = new Intent(TXIndexActivity.this, TXZfbActivity.class);
                } else {
                    intent = new Intent(TXIndexActivity.this, TXBankActivity.class);
                }
                Bundle data = new Bundle();
                data.putSerializable("txrecord", record);
                intent.putExtras(data);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){

            case R.id.tx_bank_txt:
                startActivity(new Intent(mContext, TXBankActivity.class));
                break;

            case R.id.tx_zfb_txt:
                startActivity(new Intent(mContext,TXZfbActivity.class));
                break;
        }
    }

    private void fillData(){
        if(adapter == null){
            adapter = new TXRecordListAdapter(mContext, records);
            tx_record_listview.setAdapter(adapter);
        }else{
            adapter.setData(records);
        }
    }

    private void startRecordListTask(){
        if (NetUtil.detectAvailable(mContext)) {

            TXRecordListRequest request = new TXRecordListRequest(modelApp.getUser().getId(), new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            tx_record_listview.setVisibility(View.GONE);
                            no_data_lay.setVisibility(View.VISIBLE);
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            tx_record_listview.setVisibility(View.VISIBLE);
                            no_data_lay.setVisibility(View.GONE);

                            ArrayList<TXRecord> temp = (ArrayList<TXRecord>) message.obj;
                            if (records != null) records.clear();
                            records.addAll(temp);
                            fillData();
                            break;
                    }
                }
            });

            request.doRequest();
        }else{
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }

    }

}
