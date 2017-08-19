package com.xkhouse.fang.user.activity;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.util.BitmapManager;
import com.xkhouse.fang.widget.crop.CropImageView;
import com.xkhouse.frame.activity.BaseActivity;
import com.xkhouse.frame.log.Logger;

public class CropActivity extends BaseActivity {
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    private CropImageView cropImageView;
    private Button cancel;
    private Button ok;

    public static final String FROM = "from";
    private Class fromClass;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	handleMessage();
    }
    
    
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_crop);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void findViews() {
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES,
                DEFAULT_ASPECT_RATIO_VALUES);

        cancel = (Button) findViewById(R.id.crop_cancel);
        ok = (Button) findViewById(R.id.crop_ok);

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    @Override
    protected void setListeners() {

    }

    public static final String CAMERA = "camera";
    public static final String LOCAL = "local";
    public static final String TYPE = "type";
    public static final String PATH = "path";
    private String type;
    private String path;
    private Uri uri;

    Bitmap showBmp;

    private int[] width_height;
    private Intent intent;
    
    protected void handleMessage() {
        intent = getIntent();
        width_height = getScreenWidthHeight(mContext);
        
        type = getIntent().getStringExtra(TYPE);
        
        fromClass = (Class) intent.getSerializableExtra(FROM);
        
        cropImageView.setGuidelines(0);
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES,
                DEFAULT_ASPECT_RATIO_VALUES);
        cropImageView.setFixedAspectRatio(true);
        
        BitmapManager bitmapManager = BitmapManager.getInstance();
        bitmapManager.resize(width_height[0], width_height[1]);

        int degree = 0;
        if (type.equals(CAMERA)) {
            path = intent.getStringExtra(PATH);
            try {
                showBmp = bitmapManager.getFixSizeBitmap(path);
                degree = readPictureDegree(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (type.equals(LOCAL)) {
            uri = intent.getData();

            try {
                showBmp = bitmapManager.getFixSizeBitmap(uri, mContext);
                degree = getDegree(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        cropImageView.setImageBitmap(showBmp);
        if (degree != 0) {
            cropImageView.rotateImage(degree);
        }
        Logger.d("旋转角度", degree + "");
    }

    @Override
    protected void release() {

    }

    public static final int PHOTO_REQUEST_CUT = 8;// 结果
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.crop_cancel:
            finish();
            break;
        case R.id.crop_ok:
            Bitmap bm = cropImageView.getCroppedImage();
            try {
                bm = BitmapManager.getInstance().getFixSizeBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BitmapManager.getInstance().cache(fromClass.toString(), bm, this);
            
            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("data", bm);
//            intent.putExtras(bundle);

            setResult(PHOTO_REQUEST_CUT, intent);
            finish();
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showBmp != null && !showBmp.isRecycled()) {
            showBmp.recycle();
        }
        System.gc();
    }
    
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    } 

    private int getDegree(Uri mImageCaptureUri) {
        int angle = 0;
        // 不管是拍照还是选择图片每张图片都有在数据中存储也存储有对应旋转角度orientation值
        // 所以我们在取出图片是把角度值取出以便能正确的显示图片,没有旋转时的效果观看

        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(mImageCaptureUri, null, null, null, null);// 根据Uri从数据库中找
        
        if (cursor != null) {
            cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
            String orientation = cursor.getString(cursor
                    .getColumnIndex("orientation"));// 获取旋转的角度
            if (orientation != null && !"".equals(orientation)) {  
                angle = Integer.parseInt(orientation);  
            }
            cursor.close();
        }
        
        return angle;
    }
    
    /**
	 * 获取手机屏幕宽高
	 * @param context
	 * @return
	 */
    public int[] getScreenWidthHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new int[] { w_screen, h_screen };
    }
}
