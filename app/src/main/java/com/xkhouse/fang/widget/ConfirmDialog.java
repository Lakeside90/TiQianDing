package com.xkhouse.fang.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author wujian  
 * @date 2015-5-29 下午2:17:36  
 */
public class ConfirmDialog extends Dialog {

    private Context context;
    private String title;
    private String subTitle;
    private String confirmButtonText;
    private String cacelButtonText;
    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {

        public void doConfirm();

        public void doCancel();
    }

    public ConfirmDialog(Context context, String title, String confirmButtonText, String cacelButtonText) {
        super(context, R.style.dialog_untran);
        this.context = context;
        this.title = title;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
    }

    public ConfirmDialog(Context context, String subTitle, String title, String confirmButtonText, String cacelButtonText) {
        super(context, R.style.dialog_untran);
        this.context = context;
        this.subTitle = subTitle;
        this.title = title;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_confirm_dialog, null);
        setContentView(view);

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView subtvTitle = (TextView) view.findViewById(R.id.sub_title);
        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);

        tvTitle.setText(title);
        tvConfirm.setText(confirmButtonText);
        tvCancel.setText(cacelButtonText);

        if (StringUtil.isEmpty(subTitle)){
            subtvTitle.setVisibility(View.GONE);
        }else{
            subtvTitle.setText(subTitle);
            subtvTitle.setVisibility(View.VISIBLE);
        }

        tvConfirm.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());

//        Window dialogWindow = getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
//        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
//        dialogWindow.setAttributes(lp);
        
        WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(
				(WindowManager.LayoutParams) params);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
            case R.id.confirm:
                clickListenerInterface.doConfirm();
                break;
            case R.id.cancel:
                clickListenerInterface.doCancel();
                break;
            }
        }

    };

}