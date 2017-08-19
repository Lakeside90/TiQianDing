package com.xkhouse.fang.widget.imagepicker.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.widget.imagepicker.utils.SDCardImageLoader;
import com.xkhouse.fang.widget.imagepicker.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * PhotoWall中GridView的适配器
 *
 * @author hanj
 */

public class PhotoWallAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imagePathList = null;

    private SDCardImageLoader loader;

    //记录是否被选择
    private SparseBooleanArray selectionMap;

    private int maxCount;

    public PhotoWallAdapter(Context context, ArrayList<String> imagePathList, int maxCount) {
        this.context = context;
        this.imagePathList = imagePathList;
        this.maxCount = maxCount;

        loader = new SDCardImageLoader(ScreenUtils.getScreenW(), ScreenUtils.getScreenH());
        selectionMap = new SparseBooleanArray();

        for (int i = 0; i < imagePathList.size(); i++){
            selectionMap.put(i, false);
        }
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
    public View getView(int position, View convertView, ViewGroup parent) {
        String filePath = (String) getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.photo_wall_item, null);
            holder = new ViewHolder();

            holder.imageView = (ImageView) convertView.findViewById(R.id.photo_wall_item_photo);
            holder.checkBox = (ImageView) convertView.findViewById(R.id.photo_wall_item_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //tag的key必须使用id的方式定义以保证唯一，否则会出现IllegalArgumentException.
        holder.checkBox.setTag(R.id.tag_first, position);
        holder.checkBox.setTag(R.id.tag_second, holder.imageView);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag(R.id.tag_first);
                ImageView image = (ImageView) v.getTag(R.id.tag_second);

                if(selectionMap.get(position)){
                    //取消选中
                    selectionMap.put(position, false);
                    image.setColorFilter(null);
                    holder.checkBox.setImageResource(R.drawable.checkbox_normal);
                }else{
                    //选中
                    int count = 0;
                    for(int i = 0; i < selectionMap.size(); i++){
                        if(selectionMap.get(i)){
                            count++;
                        }
                    }
                    if(count == maxCount){
                        Toast.makeText(context, "最多只能选择8张图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectionMap.put(position, true);
                    image.setColorFilter(context.getResources().getColor(R.color.image_checked_bg));
                    holder.checkBox.setImageResource(R.drawable.checkbox_checked);
                }
            }
        });

        if(selectionMap.get(position)){
            holder.checkBox.setImageResource(R.drawable.checkbox_checked);
        }else{
            holder.checkBox.setImageResource(R.drawable.checkbox_normal);
        }

        holder.imageView.setTag(filePath);
        loader.loadImage(3, filePath, holder.imageView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        ImageView checkBox;
    }

    public SparseBooleanArray getSelectionMap() {
        return selectionMap;
    }

    public void clearSelectionMap() {
        selectionMap.clear();
    }

    public void initSelectionMap() {
        for (int i = 0; i < imagePathList.size(); i++){
            selectionMap.put(i, false);
        }
    }
}
