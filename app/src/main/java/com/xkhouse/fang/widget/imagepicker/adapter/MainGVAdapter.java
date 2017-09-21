package com.xkhouse.fang.widget.imagepicker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.widget.imagepicker.utils.SDCardImageLoader;
import com.xkhouse.fang.widget.imagepicker.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * 主页面中GridView的适配器
 *
 * @author hanj
 */

public class MainGVAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> imagePathList = null;
    private PhotoOperateListener photoOperateListener;
    private DisplayImageOptions options;
    private SDCardImageLoader loader;


    public MainGVAdapter(Context context, ArrayList<String> imagePathList, PhotoOperateListener photoOperateListener) {
        this.context = context;
        this.imagePathList = imagePathList;
        this.photoOperateListener = photoOperateListener;

        loader = new SDCardImageLoader(ScreenUtils.getScreenW(), ScreenUtils.getScreenH());
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nopic)   // 加载的图片
                .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
                .showImageForEmptyUri(R.drawable.nopic)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();
    }

    @Override
    public int getCount() {
        return imagePathList == null ? 0 : imagePathList.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String filePath = (String) getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_gridview_item, null);
            holder = new ViewHolder();

            holder.imageView = (ImageView) convertView.findViewById(R.id.main_gridView_item_photo);
            holder.item_photo_delete = (ImageView) convertView.findViewById(R.id.item_photo_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setTag(filePath);
        if ("ADD_FLAG".equals(filePath)){
            holder.imageView.setImageResource(R.drawable.pic);
            holder.item_photo_delete.setVisibility(View.GONE);
        }else{
            if (filePath.contains("http://")){
                ImageLoader.getInstance().displayImage(filePath, holder.imageView, options);
            }else{
                loader.loadImage(3, filePath, holder.imageView);
            }
            holder.item_photo_delete.setVisibility(View.VISIBLE);
        }

        holder.item_photo_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoOperateListener.onDelete(position);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("ADD_FLAG".equals(filePath)){
                    photoOperateListener.onAdd();
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        ImageView item_photo_delete;
    }


    public interface PhotoOperateListener {
        void onAdd();
        void onDelete(int position);
    }

}
