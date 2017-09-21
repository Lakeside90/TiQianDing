package com.xkhouse.fang.widget.imagepicker.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.user.activity.CommentAddActivity;
import com.xkhouse.fang.user.activity.RentReleaseActivity;
import com.xkhouse.fang.user.activity.RentReleaseEditActivity;
import com.xkhouse.fang.user.activity.SellReleaseActivity;
import com.xkhouse.fang.user.activity.SellReleaseEditActivity;
import com.xkhouse.fang.widget.imagepicker.adapter.PhotoWallAdapter;
import com.xkhouse.fang.widget.imagepicker.utils.Utility;

import java.io.File;
import java.util.ArrayList;


/**
 * 选择照片页面
 * Created by hanj on 14-10-15.
 */
public class PhotoWallActivity extends AppBaseActivity {

    private TextView tv_head_left;
    private TextView tv_head_title;
    private TextView tv_head_right;

    private ArrayList<String> list;
    private GridView mPhotoWall;
    private PhotoWallAdapter adapter;

    /**
     * 当前文件夹路径
     */
    private String currentFolder = null;
    /**
     * 当前展示的是否为最近照片
     */
    private boolean isLatest = true;

    public static final int COMMENT_ACTIVITY = 1;
    public static final int SELL_ACTIVITY = 2;
    public static final int RENT_EDIT_ACTIVITY = 3;
    public static final int SELL_EDIT_ACTIVITY = 4;

    private int activity;
    private int maxCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoWall = (GridView) findViewById(R.id.photo_wall_grid);
        list = getLatestImagePaths(100);
        adapter = new PhotoWallAdapter(this, list, maxCount);
        mPhotoWall.setAdapter(adapter);
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.photo_wall);
    }

    @Override
    protected void init() {
        super.init();

        activity = getIntent().getExtras().getInt("activity");
        maxCount = getIntent().getExtras().getInt("maxCount");
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitle();

    }

    private void initTitle() {
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_right = (TextView) findViewById(R.id.tv_head_right);
        tv_head_title.setText(R.string.latest_image);
        tv_head_right.setText(R.string.main_confirm);
        tv_head_right.setVisibility(View.VISIBLE);

        tv_head_left = (TextView) findViewById(R.id.tv_head_left);
        tv_head_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                backAction();
            }
        });

        tv_head_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //选择图片完成,回到起始页面
                ArrayList<String> paths = getSelectImagePaths();

                Intent intent;
                if (activity == SELL_ACTIVITY) {
                    intent = new Intent(PhotoWallActivity.this, SellReleaseActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("code", paths != null ? 100 : 101);
                    intent.putStringArrayListExtra("paths", paths);
                    startActivity(intent);

                } else if (activity == SELL_EDIT_ACTIVITY) {
                    intent = new Intent(PhotoWallActivity.this, SellReleaseEditActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("code", paths != null ? 100 : 101);
                    intent.putStringArrayListExtra("paths", paths);
                    startActivity(intent);

                } else if (activity == COMMENT_ACTIVITY) {
                    intent = new Intent(PhotoWallActivity.this, CommentAddActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("code", paths != null ? 100 : 101);
                    intent.putStringArrayListExtra("paths", paths);
                    startActivity(intent);

                } else if (activity == RENT_EDIT_ACTIVITY) {
                    intent = new Intent(PhotoWallActivity.this, RentReleaseEditActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("code", paths != null ? 100 : 101);
                    intent.putStringArrayListExtra("paths", paths);
                    startActivity(intent);
                }

            }
        });
    }



    /**
     * 第一次跳转至相册页面时，传递最新照片信息
     */
    private boolean firstIn = true;

    /**
     * 点击返回时，跳转至相册页面
     */
    private void backAction() {
        Intent intent = new Intent(this, PhotoAlbumActivity.class);
        Bundle data = new Bundle();
        data.putInt("activity", activity);
        intent.putExtras(data);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        //传递“最近照片”分类信息
        if (firstIn) {
            if (list != null && list.size() > 0) {
                intent.putExtra("latest_count", list.size());
                intent.putExtra("latest_first_img", list.get(0));
            }
            firstIn = false;
        }

        startActivity(intent);
    }

    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 根据图片所属文件夹路径，刷新页面
     */
    private void updateView(int code, String folderPath) {
        list.clear();
        adapter.clearSelectionMap();
        adapter.notifyDataSetChanged();

        if (code == 100) {   //某个相册
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            tv_head_title.setText(folderName);
            list.addAll(getAllImagePathsByFolder(folderPath));
        } else if (code == 200) {  //最近照片
            tv_head_title.setText(R.string.latest_image);
            list.addAll(getLatestImagePaths(100));
        }

        adapter.initSelectionMap();
        adapter.notifyDataSetChanged();
        if (list.size() > 0) {
            //滚动至顶部
            mPhotoWall.smoothScrollToPosition(0);
        }
    }


    /**
     * 获取指定路径下的所有图片文件。
     */
    private ArrayList<String> getAllImagePathsByFolder(String folderPath) {
        File folder = new File(folderPath);
        String[] allFileNames = folder.list();
        if (allFileNames == null || allFileNames.length == 0) {
            return null;
        }

        ArrayList<String> imageFilePaths = new ArrayList<String>();

        for (int i = allFileNames.length - 1; i >= 0; i--) {
            File file = new File(folderPath + File.separator + allFileNames[i]);
            if (Utility.isImage(file)) {
                imageFilePaths.add(folderPath + File.separator + allFileNames[i]);
            }
        }

        return imageFilePaths;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    private ArrayList<String> getLatestImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        // 只查询jpg和png的图片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<String> latestImagePaths = null;
        if (cursor != null) {
            //从最新的图片开始读取.
            //当cursor中没有数据时，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();

                while (true) {
                    // 获取图片的路径
                    String path = cursor.getString(0);
                    latestImagePaths.add(path);

                    if (latestImagePaths.size() >= maxCount || !cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }

        return latestImagePaths;
    }

    //获取已选择的图片路径
    private ArrayList<String> getSelectImagePaths() {
        SparseBooleanArray map = adapter.getSelectionMap();
        if (map.size() == 0) {
            return null;
        }

        ArrayList<String> selectedImageList = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++) {
            if (map.get(i)) {
                selectedImageList.add(list.get(i));
            }
        }

        return selectedImageList;
    }

    //从相册页面跳转至此页
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int code = intent.getIntExtra("code", -1);
        if (code == 100) {
            //某个相册
            String folderPath = intent.getStringExtra("folderPath");
            if (isLatest || (folderPath != null && !folderPath.equals(currentFolder))) {
                currentFolder = folderPath;
                updateView(100, currentFolder);
                isLatest = false;
            }
        } else if (code == 200) {
            //“最近照片”
            if (!isLatest) {
                updateView(200, null);
                isLatest = true;
            }
        }
    }
}
