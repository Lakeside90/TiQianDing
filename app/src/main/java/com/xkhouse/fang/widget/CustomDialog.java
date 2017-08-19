package com.xkhouse.fang.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;

public class CustomDialog extends Dialog {
    private ImageView iv;
    private TextView tv;
    private String text;
    private Animation operatingAnim;


    public CustomDialog(Context context, int theme) {
        super(context, theme);
        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

    }
    public CustomDialog(Context context, String msg, int theme) {
        this(context, theme);
        text = msg;

    }

    public CustomDialog(Context context, int msgId, int theme) {
        this(context, theme);
        text = context.getResources().getString(msgId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialog);
        iv = (ImageView) findViewById(R.id.tips_icon);
        tv = (TextView) findViewById(R.id.tips_msg);
        tv.setText(text);
    }

    @Override
    public void show() {
        super.show();
        iv.startAnimation(operatingAnim);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        iv.clearAnimation();
    }
}
