package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.task.BindStatusRequest;
import com.xkhouse.fang.user.task.QQBindRequest;
import com.xkhouse.fang.user.task.SinaBindRequest;
import com.xkhouse.fang.user.task.WXBindRequest;
import com.xkhouse.fang.widget.ConfirmDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * 账户安全
 */
public class AccountEditActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;

    private LinearLayout phone_edit_lay;
    private TextView phone_txt;

    private TextView psw_change_txt;
    private TextView paypsw_change_txt;

    private LinearLayout qq_lay;
    private TextView qq_status_txt;
    private LinearLayout weibo_lay;
    private TextView weibo_status_txt;
    private LinearLayout wx_lay;
    private TextView wx_status_txt;

    private BindStatusRequest qqStatusRequest;
    private BindStatusRequest sinaStatusRequest;
    private BindStatusRequest wxStatusRequest;

    private int qqStatus = 0;   //0:未知， 1：未绑定， 2：绑定
    private int sinaStatus = 0;   //0:未知， 1：未绑定， 2：绑定
    private int wxStatus = 0;   //0:未知， 1：未绑定， 2：绑定



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSinaStatusTask(false);
        startQQStatusTask(false);
        startWXStatusTask(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void init() {
        super.init();
        //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext, "1104857499",
                "ZNVEJATOlvNK33he");
        qqSsoHandler.addToSocialSDK();

        // 添加微信平台
        String appId = "wxa45adeb6ee24dfdb";
        String appSecret = "07e76cac282ff8fca887754f9e3917a6";
        UMWXHandler wxHandler = new UMWXHandler(mContext, appId, appSecret);
        wxHandler.addToSocialSDK();

        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_account_edit);
    }

    @Override
    protected void findViews() {
        super.findViews();
        initTitle();
        phone_edit_lay = (LinearLayout) findViewById(R.id.phone_edit_lay);
        phone_txt = (TextView) findViewById(R.id.phone_txt);

        psw_change_txt = (TextView) findViewById(R.id.psw_change_txt);
        paypsw_change_txt = (TextView) findViewById(R.id.paypsw_change_txt);

        qq_lay = (LinearLayout) findViewById(R.id.qq_lay);
        weibo_lay = (LinearLayout) findViewById(R.id.weibo_lay);
        wx_lay = (LinearLayout) findViewById(R.id.wx_lay);

        qq_status_txt = (TextView) findViewById(R.id.qq_status_txt);
        weibo_status_txt = (TextView) findViewById(R.id.weibo_status_txt);
        wx_status_txt = (TextView) findViewById(R.id.wx_status_txt);
    }

    private void initTitle() {
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("账户安全");
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
        phone_edit_lay.setOnClickListener(this);
        psw_change_txt.setOnClickListener(this);
        paypsw_change_txt.setOnClickListener(this);
        qq_lay.setOnClickListener(this);
        weibo_lay.setOnClickListener(this);
        wx_lay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.phone_edit_lay:
                startActivity(new Intent(mContext, MobileResetActivity.class));
                break;

            case R.id.psw_change_txt:
//                startActivity(new Intent(mContext, ChangePswActivity.class));
                break;

            case R.id.paypsw_change_txt:
                String paystatus = ChangePayPswActivity.PAY_TYPE_CHANGE;
//                if(StringUtil.isEmpty(modelApp.getUser().getPayPassword())){
//                    paystatus = ChangePayPswActivity.PAY_TYPE_SET;
//                }
                Intent intent = new Intent(mContext, ChangePayPswActivity.class);
                Bundle data = new Bundle();
                data.putString("paystatus", paystatus);
                intent.putExtras(data);
                startActivity(intent);
                break;

            case R.id.qq_lay:
                if (qqStatus == 1){
                    showQQBindDialog();
                }else if (qqStatus == 2){
                    showQQUnBindDialog();
                }else {
                    startQQStatusTask(true);
                }
                break;

            case R.id.weibo_lay:
                if (sinaStatus == 1){
                    showSinaBindDialog();
                }else if (sinaStatus == 2){
                    showSinaUnBindDialog();
                }else {
                    startSinaStatusTask(true);
                }
                break;

            case R.id.wx_lay:
                if (wxStatus == 1){
                    showWXBindDialog();
                }else if (wxStatus == 2){
                    showWXUnBindDialog();
                }else {
                    startWXStatusTask(true);
                }
                break;
        }
    }

    private void startQQStatusTask(final boolean isShowDialog) {
        if (NetUtil.detectAvailable(mContext)){
            if (qqStatusRequest == null) {
                qqStatusRequest = new BindStatusRequest(modelApp.getUser().getId(), "1",
                        new RequestListener() {
                            @Override
                            public void sendMessage(Message message) {
                                hideLoadingDialog();
                                switch (message.what) {
                                    case Constants.ERROR_DATA_FROM_NET:
                                        Toast.makeText(mContext, "获取QQ绑定状态失败！", Toast.LENGTH_SHORT).show();
                                        qq_status_txt.setText("");
                                        qqStatus = 0;
                                        break;

                                    case Constants.NO_DATA_FROM_NET:
                                        qq_status_txt.setText("未绑定");
                                        qqStatus = 1;
                                        if (isShowDialog) showQQBindDialog();
                                        break;

                                    case Constants.SUCCESS_DATA_FROM_NET:
                                        qq_status_txt.setText("已绑定");
                                        qqStatus = 2;
                                        if (isShowDialog) showQQUnBindDialog();
                                        break;
                                }
                            }
                        });
            }
            showLoadingDialog(R.string.data_loading);
            qqStatusRequest.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }



    private void startSinaStatusTask(final boolean isShowDialog) {
        if (NetUtil.detectAvailable(mContext)){
            if (sinaStatusRequest == null) {
                sinaStatusRequest = new BindStatusRequest(modelApp.getUser().getId(), "2",
                        new RequestListener() {
                            @Override
                            public void sendMessage(Message message) {
                                hideLoadingDialog();
                                switch (message.what) {
                                    case Constants.ERROR_DATA_FROM_NET:
                                        Toast.makeText(mContext, "获取新浪微博绑定状态失败！", Toast.LENGTH_SHORT).show();
                                        weibo_status_txt.setText("");
                                        sinaStatus = 0;
                                        break;

                                    case Constants.NO_DATA_FROM_NET:
                                        weibo_status_txt.setText("未绑定");
                                        sinaStatus = 1;
                                        if (isShowDialog) showSinaBindDialog();
                                        break;

                                    case Constants.SUCCESS_DATA_FROM_NET:
                                        weibo_status_txt.setText("已绑定");
                                        sinaStatus = 2;
                                        if (isShowDialog) showSinaUnBindDialog();
                                        break;
                                }
                            }
                        });
            }
            showLoadingDialog(R.string.data_loading);
            sinaStatusRequest.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    private void startWXStatusTask(final boolean isShowDialog) {
        if (NetUtil.detectAvailable(mContext)){
            if (wxStatusRequest == null) {
                wxStatusRequest = new BindStatusRequest(modelApp.getUser().getId(), "3",
                        new RequestListener() {
                            @Override
                            public void sendMessage(Message message) {
                                hideLoadingDialog();
                                switch (message.what) {
                                    case Constants.ERROR_DATA_FROM_NET:
                                        Toast.makeText(mContext, "获取微信绑定状态失败！", Toast.LENGTH_SHORT).show();
                                        wx_status_txt.setText("");
                                        wxStatus = 0;
                                        break;

                                    case Constants.NO_DATA_FROM_NET:
                                        wx_status_txt.setText("未绑定");
                                        wxStatus = 1;
                                        if (isShowDialog) showWXBindDialog();
                                        break;

                                    case Constants.SUCCESS_DATA_FROM_NET:
                                        wx_status_txt.setText("已绑定");
                                        wxStatus = 2;
                                        if (isShowDialog) showWXUnBindDialog();
                                        break;
                                }
                            }
                        });
            }
            showLoadingDialog(R.string.data_loading);
            wxStatusRequest.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    //绑定qq
    private void startQQBindTask(String openid){
        if (NetUtil.detectAvailable(mContext)){
            QQBindRequest request = new QQBindRequest(modelApp.getUser().getId(), "1", openid,
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            hideLoadingDialog();
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                    Toast.makeText(mContext, "绑定失败，请重试！", Toast.LENGTH_SHORT).show();
                                    qq_status_txt.setText("");
                                    qqStatus = 0;
                                    break;

                                case Constants.NO_DATA_FROM_NET:
                                    String msg = (String) message.obj;
                                    if(StringUtil.isEmpty(msg)){
                                        Toast.makeText(mContext, "绑定失败，请重试！", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                    }
                                    qq_status_txt.setText("未绑定");
                                    qqStatus = 1;
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    Toast.makeText(mContext, "绑定成功！", Toast.LENGTH_SHORT).show();
                                    qq_status_txt.setText("已绑定");
                                    qqStatus = 2;
                                    break;
                            }
                        }
                    });
            showLoadingDialog(R.string.data_loading);
            request.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    //解绑qq
    private void startQQUnBindTask(){
        if (NetUtil.detectAvailable(mContext)){
            QQBindRequest request = new QQBindRequest(modelApp.getUser().getId(), "2", null,
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            hideLoadingDialog();
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑失败，请重试！", Toast.LENGTH_SHORT).show();
                                    qq_status_txt.setText("");
                                    qqStatus = 0;
                                    break;

                                case Constants.NO_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑失败，请重试！", Toast.LENGTH_SHORT).show();
                                    qq_status_txt.setText("已绑定");
                                    qqStatus = 2;
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑成功！", Toast.LENGTH_SHORT).show();
                                    qq_status_txt.setText("未绑定");
                                    qqStatus = 1;
                                    break;
                            }
                        }
                    });
            showLoadingDialog(R.string.data_loading);
            request.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    //绑定微博
    private void startSinaBindTask(String uid){
        if (NetUtil.detectAvailable(mContext)){
            SinaBindRequest request = new SinaBindRequest(modelApp.getUser().getId(), "1", uid,
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            hideLoadingDialog();
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                    Toast.makeText(mContext, "绑定失败，请重试！", Toast.LENGTH_SHORT).show();
                                    weibo_status_txt.setText("");
                                    sinaStatus = 0;
                                    break;

                                case Constants.NO_DATA_FROM_NET:
                                    String msg = (String) message.obj;
                                    if(StringUtil.isEmpty(msg)){
                                        Toast.makeText(mContext, "绑定失败，请重试！", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                    }
                                    weibo_status_txt.setText("未绑定");
                                    sinaStatus = 1;
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    Toast.makeText(mContext, "绑定成功！", Toast.LENGTH_SHORT).show();
                                    weibo_status_txt.setText("已绑定");
                                    sinaStatus = 2;
                                    break;
                            }
                        }
                    });
            showLoadingDialog(R.string.data_loading);
            request.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    //解绑微博
    private void startSinaUnBindTask(){
        if (NetUtil.detectAvailable(mContext)){
            SinaBindRequest request = new SinaBindRequest(modelApp.getUser().getId(), "2", null,
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            hideLoadingDialog();
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑失败，请重试！", Toast.LENGTH_SHORT).show();
                                    weibo_status_txt.setText("");
                                    sinaStatus = 0;
                                    break;

                                case Constants.NO_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑失败，请重试！", Toast.LENGTH_SHORT).show();
                                    weibo_status_txt.setText("已绑定");
                                    sinaStatus = 2;
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑成功！", Toast.LENGTH_SHORT).show();
                                    weibo_status_txt.setText("未绑定");
                                    sinaStatus = 1;
                                    break;
                            }
                        }
                    });
            showLoadingDialog(R.string.data_loading);
            request.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    //绑定微信
    private void startWXBindTask(String openid){
        if (NetUtil.detectAvailable(mContext)){
            WXBindRequest request = new WXBindRequest(modelApp.getUser().getId(), "1", openid,
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            hideLoadingDialog();
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                    Toast.makeText(mContext, "绑定失败，请重试！", Toast.LENGTH_SHORT).show();
                                    wx_status_txt.setText("");
                                    wxStatus = 0;
                                    break;

                                case Constants.NO_DATA_FROM_NET:
                                    String msg = (String) message.obj;
                                    if(StringUtil.isEmpty(msg)){
                                        Toast.makeText(mContext, "绑定失败，请重试！", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                    }
                                    wx_status_txt.setText("未绑定");
                                    wxStatus = 1;
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    Toast.makeText(mContext, "绑定成功！", Toast.LENGTH_SHORT).show();
                                    wx_status_txt.setText("已绑定");
                                    wxStatus = 2;
                                    break;
                            }
                        }
                    });
            showLoadingDialog(R.string.data_loading);
            request.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    //解绑微信
    private void startWXUnBindTask(){
        if (NetUtil.detectAvailable(mContext)){
            WXBindRequest request = new WXBindRequest(modelApp.getUser().getId(), "2", null,
                    new RequestListener() {
                        @Override
                        public void sendMessage(Message message) {
                            hideLoadingDialog();
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑失败，请重试！", Toast.LENGTH_SHORT).show();
                                    wx_status_txt.setText("");
                                    wxStatus = 0;
                                    break;

                                case Constants.NO_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑失败，请重试！", Toast.LENGTH_SHORT).show();
                                    wx_status_txt.setText("已绑定");
                                    wxStatus = 2;
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    Toast.makeText(mContext, "解绑成功！", Toast.LENGTH_SHORT).show();
                                    wx_status_txt.setText("未绑定");
                                    wxStatus = 1;
                                    break;
                            }
                        }
                    });
            showLoadingDialog(R.string.data_loading);
            request.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }



    //qq绑定对话框
    public void showQQBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "账号未绑定QQ，是否立即绑定！", "绑定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();

                QQLogin();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }

    //qq解绑对话框
    public void showQQUnBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "账号已绑定QQ，是否解绑！", "解绑", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();

                startQQUnBindTask();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }


    //微博绑定对话框
    public void showSinaBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "账号未绑定微博，是否立即绑定！", "绑定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();

                SinaLogin();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }

    //微博解绑对话框
    public void showSinaUnBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "账号已绑定微博，是否解绑！", "解绑", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();

                startSinaUnBindTask();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }


    //微信绑定对话框
    public void showWXBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "账号未绑定微信，是否立即绑定！", "绑定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();

                WXLogin();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }

    //微信解绑对话框
    public void showWXUnBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "账号已绑定微信，是否解绑！", "解绑", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();

                startWXUnBindTask();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }



    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    private void QQLogin(){
        if(!isApkInstalled()){
            Toast.makeText(mContext, "请安装qq客户端!", Toast.LENGTH_SHORT).show();
            return;
        }
        mController.doOauthVerify(mContext, SHARE_MEDIA.QQ, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权完成", Toast.LENGTH_SHORT).show();
                startQQBindTask(value.getString("openid"));
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void SinaLogin(){
        mController.doOauthVerify(mContext, SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权完成", Toast.LENGTH_SHORT).show();

               startSinaBindTask(value.getString("uid"));
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权取消", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void WXLogin(){
        mController.doOauthVerify(mContext, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权开始", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权错误", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权完成", Toast.LENGTH_SHORT).show();
                startWXBindTask(value.getString("openid"));
            }
            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(mContext, "授权取消", Toast.LENGTH_SHORT).show();
            }
        } );
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    public boolean isApkInstalled() {
        try {
            getPackageManager().getApplicationInfo("com.tencent.mobileqq", PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
