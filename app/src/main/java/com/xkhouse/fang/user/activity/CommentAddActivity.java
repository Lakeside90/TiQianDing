package com.xkhouse.fang.user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.StorageUtils;
//import com.squareup.okhttp.Call;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.Headers;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.MultipartBuilder;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.CommentInfo;
import com.xkhouse.fang.booked.task.AddressEditRequest;
import com.xkhouse.fang.booked.task.CommentAddRequest;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.user.view.PhotoDialog;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.fang.widget.imagepicker.adapter.MainGVAdapter;
import com.xkhouse.fang.widget.imagepicker.ui.PhotoWallActivity;
import com.xkhouse.fang.widget.imagepicker.utils.ScreenUtils;
import com.xkhouse.fang.widget.imagepicker.utils.Utility;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.FileUtil;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * 评论
 */
public class CommentAddActivity extends AppBaseActivity {

    private View title_view;
	private ImageView iv_head_left;
	private TextView tv_head_title;



    private ImageView star_one_iv;
    private ImageView star_two_iv;
    private ImageView star_three_iv;
    private ImageView star_four_iv;
    private ImageView star_five_iv;

    private EditText comment_txt;

    private TextView commit_txt;




    private ArrayList<ImageView> starViews = new ArrayList<>();

    private CommentAddRequest request;

    private CommentInfo commentInfo;
    private int star = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


        starViews.add(star_one_iv);
        starViews.add(star_two_iv);
        starViews.add(star_three_iv);
        starViews.add(star_four_iv);
        starViews.add(star_five_iv);

        fillData();

        refreshStarView();

        initImagePickerView();
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_comment_add);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();

        title_view = findViewById(R.id.title_view);

        star_one_iv = (ImageView) findViewById(R.id.star_one_iv);
        star_two_iv = (ImageView) findViewById(R.id.star_two_iv);
        star_three_iv = (ImageView) findViewById(R.id.star_three_iv);
        star_four_iv = (ImageView) findViewById(R.id.star_four_iv);
        star_five_iv = (ImageView) findViewById(R.id.star_five_iv);

        comment_txt = (EditText) findViewById(R.id.comment_txt);

        commit_txt = (TextView) findViewById(R.id.commit_txt);

    }

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("评论");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {

        star_one_iv.setOnClickListener(this);
        star_two_iv.setOnClickListener(this);
        star_three_iv.setOnClickListener(this);
        star_four_iv.setOnClickListener(this);
        star_five_iv.setOnClickListener(this);

        commit_txt.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){

            case R.id.star_one_iv:
                star = 1;
                refreshStarView();
                break;

            case R.id.star_two_iv:
                star = 2;
                refreshStarView();
                break;

            case R.id.star_three_iv:
                star = 3;
                refreshStarView();
                break;

            case R.id.star_four_iv:
                star = 4;
                refreshStarView();
                break;

            case R.id.star_five_iv:
                star = 5;
                refreshStarView();
                break;

            case R.id.commit_txt:
                if (checkData()) {
                    getCommentinfo();
                    startTask();
                }
                break;


            case R.id.take_photo:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                            CAMERA_REQUEST_CODE);
                } else {
                    camera();
                }
                break;

            case R.id.take_img:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    Intent photoIntent = new Intent(CommentAddActivity.this, PhotoWallActivity.class);
                    Bundle photoData = new Bundle();
                    photoData.putInt("activity", PhotoWallActivity.COMMENT_ACTIVITY);
                    photoData.putInt("maxCount", 9-imagePathList.size());
                    photoIntent.putExtras(photoData);
                    startActivity(photoIntent);
                    hidePhotoDialog();
                }
                break;

            case R.id.cancel:
                hidePhotoDialog();
                break;

        }
	}
	
	
	private void fillData(){


	}

	private void refreshStarView() {
        for (int i = 0 ; i < starViews.size(); i++) {
            if (i < star) {
                starViews.get(i).setImageResource(R.drawable.storeup_on);
            }else {
                starViews.get(i).setImageResource(R.drawable.storeup);
            }
        }
    }


    private boolean checkData() {
        if (StringUtil.isEmpty(comment_txt.getText().toString())) {
            Toast.makeText(mContext, "请填写完整", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

	private void getCommentinfo() {

        commentInfo = new CommentInfo();
        commentInfo.setOrder_id("5");
        commentInfo.setContent(comment_txt.getText().toString());
        commentInfo.setStar_num(String.valueOf(star));
        commentInfo.setImageUrls(new String[]{"http://imgsrc.baidu.com/imgad/pic/item/267f9e2f07082838b5168c32b299a9014c08f1f9.jpg",
                "http://imgsrc.baidu.com/imgad/pic/item/267f9e2f07082838b5168c32b299a9014c08f1f9.jpg"});
    }


    private void startTask() {
        if (imagePathList != null && imagePathList.size() > 1){
            commitPhotos();
        }else{
            commitForm();
        }


    }

    private void commitForm() {

//        if(photoUrls != null && photoUrls.size() > 0){
//            String[] urls = new String[photoUrls.size()];
//            for (int i = 0; i < photoUrls.size(); i++){
//                urls[i] = photoUrls.get(i);
//            }
//            commentInfo.setImageUrls(urls);
//        }

        if(NetUtil.detectAvailable(mContext)){
            if(request == null){
                request = new CommentAddRequest(modelApp.getUser().getToken(), commentInfo, requestListener);
            }else {
                request.setData(modelApp.getUser().getToken(), commentInfo);
            }
            showLoadingDialog("处理中...");
            request.doRequest();

        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }





    RequestListener requestListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            hideLoadingDialog();
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                    break;

                case Constants.NO_DATA_FROM_NET:
                    Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };



    /*********************************   图片选择器  *****************************/
    private MainGVAdapter adapter;
    private ArrayList<String> imagePathList;
    private ArrayList<String> imageCachePathList;



    private void initImagePickerView() {
        //获取屏幕像素
        ScreenUtils.initScreen(this);

        ScrollGridView mainGV = (ScrollGridView) findViewById(R.id.main_gridView);

        imagePathList = new ArrayList<String>();
        imagePathList.add(0, "ADD_FLAG");
        adapter = new MainGVAdapter(this, imagePathList, new MainGVAdapter.PhotoOperateListener() {
            @Override
            public void onAdd() {
                //添加照片
                addPhoto();
            }

            @Override
            public void onDelete(int position) {
                imagePathList.remove(position);
                if (!imagePathList.get(imagePathList.size()-1).equals("ADD_FLAG")){
                    imagePathList.add("ADD_FLAG");
                }
                adapter.notifyDataSetChanged();
            }
        });
        mainGV.setAdapter(adapter);
    }

    PhotoDialog photoDialog;
    private void addPhoto() {
        photoDialog = new PhotoDialog(mContext, R.style.takePhotoDialog);
        photoDialog.show();

        photoDialog.takePhoto.setOnClickListener(this);
        photoDialog.takeFromPic.setOnClickListener(this);
        photoDialog.cancel.setOnClickListener(this);
    }
    private void hidePhotoDialog() {
        if (photoDialog != null && photoDialog.isShowing()) {
            photoDialog.dismiss();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int code = intent.getIntExtra("code", -1);

        if (code == 100) {
            //图片相册相关

            ArrayList<String> paths = intent.getStringArrayListExtra("paths");
            //添加，去重
            boolean hasUpdate = false;
            for (String path : paths) {
                if (!imagePathList.contains(path)) {

                    if (imagePathList.size() == 8 && "ADD_FLAG".equals(imagePathList.get(imagePathList.size()-1))) {
                        imagePathList.remove(imagePathList.size()-1);
                    }

                    //最多9张
                    if (imagePathList.size() == 8) {
                        Utility.showToast(this, "最多可添加8张图片。");
                        break;
                    }
                    imagePathList.add(0, path);
                    hasUpdate = true;
                }
            }

            if (hasUpdate) {
                adapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_CAMERA:
                if (!tempPhotoFile.exists()) {
                    return;
                }
                if (!imagePathList.contains(tempPhotoFile.getAbsolutePath())) {

                    if (imagePathList.size() == 8 && "ADD_FLAG".equals(imagePathList.get(imagePathList.size()-1))) {
                        imagePathList.remove(imagePathList.size()-1);
                    }

                    //最多9张
                    if (imagePathList.size() == 8) {
                        Utility.showToast(this, "最多可添加8张图片。");
                        break;
                    }
                    imagePathList.add(0, tempPhotoFile.getAbsolutePath());
                    adapter.notifyDataSetChanged();
                }

                break;
        }
    }



    /***************************** 发布图片  **********************************/
    public static final int PHOTO_CAMERA = 0x1;
    File tempPhotoFile = null;
    //启动拍照
    private void camera() {
        tempPhotoFile = new File(StorageUtils.getCacheDirectory(mContext),
                "XKP" + System.currentTimeMillis() + ".jpg");
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempPhotoFile));
        startActivityForResult(intent, PHOTO_CAMERA);

        hidePhotoDialog();
    }



    private int photos = 0;
    private ArrayList<String> photoUrls = new ArrayList<>();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    break;

                case 1:
                    photoUrls.add((String)msg.obj);
                    break;
            }
            photos++;
            if(photos == imageCachePathList.size()){
                hideLoadingDialog();
                if(photoUrls.size() == 0){
                    Toast.makeText(mContext, "图片上传失败", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "图片上传完成， 上传成功"+photoUrls.size() +
                            "张;上传失败"+(photos-photoUrls.size())+"张", Toast.LENGTH_SHORT).show();
                }

                commitForm();
            }

        }
    };

    private void commitPhotos(){
        photoUrls.clear();
        photos = 0;
        showLoadingDialog("上传图片中");

        new Thread( new Runnable(){
            @Override
            public void run() {
                writeCommitBitmaps();
            }
        }).start();
    }

    private void commitPhoto(File file){

//        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//
//        RequestBody requestBody = new MultipartBuilder()
//                .type(MultipartBuilder.FORM)
//                .addPart(Headers.of(
//                        "Content-Disposition",
//                        "form-data; name=\"upfile\"; filename=\"a.jpg\""), fileBody)
//                .build();
//
//        Request request = new Request.Builder()
//                .url(Constants.UPLOAD_PHOTO_URL)
//                .post(requestBody)
//                .build();
//
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback()
//        {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Message message = new Message();
//                message.what = 0;
//                mHandler.sendMessage(message);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                parse(response.body().string());
//            }
//        });
    }

    private void parse(String result) {
        if (StringUtil.isEmpty(result)) {
            return;
        }

        try {
            if (result != null && result.startsWith("\ufeff")) {
                result = result.substring(1);
            }

            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject != null) {
                if("SUCCESS".equals(jsonObject.optString("state"))){
                    Message message = new Message();
                    message.what = 1;
                    message.obj = "http://upload.xkhouse.com" + jsonObject.optString("url");
                    mHandler.sendMessage(message);
                }else{
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private  Handler bitmapHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 111:
                    for (int i = 0 ; i < imageCachePathList.size(); i++){
                        File file = new File(imageCachePathList.get(i));
                        if (file.exists()){
                            commitPhoto(file);
                        }
                    }
                    break;
            }
        }
    };

    private void writeCommitBitmaps(){
        String sdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Properties properties = BaseConfig.getInstance().getConfig();
        String path = sdcardDir + File.separator + properties.getProperty("imagepath")
                + File.separator+ "photoCache";
        FileUtil.createFolder(path);

        //先清除之前缓存
        clearBitmaopCache();

        int count = imagePathList.size();
        if("ADD_FLAG".equals(imagePathList.get(imagePathList.size()-1))){
            count = imagePathList.size() - 1;
        }

        for (int i = 0; i < count; i++){
            String fileName = path+File.separator+System.currentTimeMillis()+".xkp";
            try{
                saveMyBitmap(fileName, compressImageFromFile(imagePathList.get(i)));
            }catch (Exception e){
                Logger.e("", "图片压缩失败");
            }
        }

        //获取压缩后图片地址
        imageCachePathList = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null && files.length>0){
            for(int i=0; i<files.length; i++){
                imageCachePathList.add(files[i].getAbsolutePath());
            }
        }

        bitmapHandler.sendEmptyMessage(111);
    }

    //清除缓存
    private void clearBitmaopCache(){
        String sdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Properties properties = BaseConfig.getInstance().getConfig();
        String path = sdcardDir + File.separator + properties.getProperty("imagepath")
                + File.separator+ "photoCache";
        File file = new File(path);
        if (file.exists()){
            File files[] = file.listFiles();
            if (files != null){
                for (File f : files) {
                    f.delete();
                }
            }
        }
    }


    //图片压缩
    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 600f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return bitmap;//      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
    }


    //将bitmap写成文件
    public void saveMyBitmap(String fileNmae, Bitmap mBitmap) throws IOException {
        File f = new File(fileNmae);
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int CAMERA_REQUEST_CODE = 11;
    private int READ_EXTERNAL_STORAGE_REQUEST_CODE = 22;

    // 6.0 权限处理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CAMERA_REQUEST_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camera();

            } else {
                //没有取得权限
                if(photoDialog != null) photoDialog.dismiss();
                Snackbar.make(title_view, "提前订没有取得拍照权限，请在设置>应用管理中获取！",
                        Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri packageURI = Uri.parse("package:" + "com.demozi.tqd");
                        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        startActivity(intent);
                    }
                }).show();
            }
        }else if(requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE){
            //相册权限
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent photoIntent = new Intent(CommentAddActivity.this, PhotoWallActivity.class);
                Bundle photoData = new Bundle();
                photoData.putInt("activity", PhotoWallActivity.COMMENT_ACTIVITY);
                photoData.putInt("maxCount", 9-imagePathList.size());
                photoIntent.putExtras(photoData);
                startActivity(photoIntent);
                hidePhotoDialog();

            } else {
                //没有取得权限
                if(photoDialog != null) photoDialog.dismiss();
                Snackbar.make(title_view, "提前订没有取得读取SD卡数据权限，请在设置>应用管理中获取！",
                        Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri packageURI = Uri.parse("package:" + "com.demozi.tqd");
                        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        startActivity(intent);
                    }
                }).show();
            }
        }
    }




}
