package com.xkhouse.fang.widget.imagepicker.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.user.activity.CommentAddActivity;
import com.xkhouse.fang.user.activity.RentReleaseActivity;
import com.xkhouse.fang.user.activity.RentReleaseEditActivity;
import com.xkhouse.fang.user.activity.SellReleaseActivity;
import com.xkhouse.fang.user.activity.SellReleaseEditActivity;
import com.xkhouse.fang.widget.imagepicker.adapter.PhotoAlbumLVAdapter;
import com.xkhouse.fang.widget.imagepicker.model.PhotoAlbumLVItem;
import com.xkhouse.fang.widget.imagepicker.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * 分相册查看SD卡所有图片。
 * Created by hanj on 14-10-14.
 */
public class PhotoAlbumActivity extends AppBaseActivity {

    private ImageView iv_head_left;
    private TextView tv_head_title;
    private TextView tv_head_right;


    private int activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!Utility.isSDcardOK()) {
            Utility.showToast(this, "SD卡不可用。");
            return;
        }

        Intent t = getIntent();
        if (!t.hasExtra("latest_count")) {
            return;
        }

        ListView listView = (ListView) findViewById(R.id.select_img_listView);



        //第二种方式：使用ContentProvider。（效率更高）
        final ArrayList<PhotoAlbumLVItem> list = new ArrayList<PhotoAlbumLVItem>();
        //“最近照片”
        list.add(new PhotoAlbumLVItem(getResources().getString(R.string.latest_image),
                t.getIntExtra("latest_count", -1), t.getStringExtra("latest_first_img")));
        //相册
        list.addAll(getImagePathsByContentProvider());

        PhotoAlbumLVAdapter adapter = new PhotoAlbumLVAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PhotoAlbumActivity.this, PhotoWallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                //第一行为“最近照片”
                if (position == 0) {
                    intent.putExtra("code", 200);
                } else {
                    intent.putExtra("code", 100);
                    intent.putExtra("folderPath", list.get(position).getPathName());
                }
                startActivity(intent);
            }
        });

//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //取消，回到主页面
//                backAction();
//            }
//        });
    }


    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_photo_album);
    }

    @Override
    protected void init() {
        super.init();
        activity = getIntent().getExtras().getInt("activity");
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitle();

    }

    private void initTitle() {
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        iv_head_left.setVisibility(View.INVISIBLE);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText(R.string.select_album);

        tv_head_right = (TextView) findViewById(R.id.tv_head_right);
        tv_head_right.setText(R.string.main_cancel);
        tv_head_right.setVisibility(View.VISIBLE);

        tv_head_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //取消，回到主页面
                backAction();
            }
        });
    }

    /**
     * 点击返回时，回到相册页面
     */
    private void backAction() {
        if (activity == PhotoWallActivity.SELL_ACTIVITY){
            Intent intent = new Intent(this, SellReleaseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(activity == PhotoWallActivity.COMMENT_ACTIVITY){
            Intent intent = new Intent(this, CommentAddActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(activity == PhotoWallActivity.SELL_EDIT_ACTIVITY){
            Intent intent = new Intent(this, SellReleaseEditActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(activity == PhotoWallActivity.RENT_EDIT_ACTIVITY){
            Intent intent = new Intent(this, RentReleaseEditActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 获取目录中图片的个数。
     */
    private int getImageCount(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        for (File file : files) {
            if (Utility.isImage(file)) {
                count++;
            }
        }

        return count;
    }

    /**
     * 获取目录中最新的一张图片的绝对路径。
     */
    private String getFirstImagePath(File folder) {
        File[] files = folder.listFiles();
        for (int i = files.length - 1; i >= 0; i--) {
            File file = files[i];
            Log.d("wj", "file ===  " + file.getAbsolutePath());
            if (Utility.isImage(file)) {
                return file.getAbsolutePath();
            }
        }

        return null;
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    private ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        // 只查询jpg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<PhotoAlbumLVItem> list = null;
        if (cursor != null) {
            if (cursor.moveToLast()) {
                //路径缓存，防止多次扫描同一目录
                HashSet<String> cachePath = new HashSet<String>();
                list = new ArrayList<PhotoAlbumLVItem>();

                while (true) {
                    // 获取图片的路径
                    String imagePath = cursor.getString(0);

                    File parentFile = new File(imagePath).getParentFile();
                    String parentPath = parentFile.getAbsolutePath();

                    //不扫描重复路径
                    if (!cachePath.contains(parentPath)) {
                        int imageCount = getImageCount(parentFile);
                        String imageFile = getFirstImagePath(parentFile);
                        if (imageCount != 0 && imageFile != null){
                            list.add(new PhotoAlbumLVItem(parentPath, imageCount,imageFile));
                        }

                        if (getFirstImagePath(parentFile) == null){
                            Log.d("wj","----- file  " + parentFile.getAbsolutePath());
                        }
                        cachePath.add(parentPath);
                    }

                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }

            cursor.close();
        }

        return list;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

}
