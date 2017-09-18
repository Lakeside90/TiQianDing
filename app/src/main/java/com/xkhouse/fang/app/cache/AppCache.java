package com.xkhouse.fang.app.cache;

import android.os.Environment;

import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.FileUtil;

import java.io.File;
import java.util.Properties;

/**
 * 缓存应用的json数据
 * Created by wujian on 2016/1/25.
 */
public class AppCache {

    private static String TAG = "AppCache";
    //AES 加密key
    private static String key = "tiqianding";


    private static String getCachePath(String siteId){
        String sdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Properties properties = BaseConfig.getInstance().getConfig();
        String path = sdcardDir + File.separator + properties.getProperty("cachepath")
                + File.separator+ siteId;
        return path;
    }

    /**
     * 读取首页轮询图数据
     */
    public static String readHomeAdJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"homead.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入首页轮询图数据
     */
    public static void writeHomeAdJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"homead.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }




    /**
     * 读取首页导航栏目
     */
    public static String readNavigationJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"navigation.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入首页导航栏目
     */
    public static void writeNavigationJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"navigation.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }


    /**
     * 读取首页专题
     */
    public static String readHomeTopicJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"homeTopic.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入首页专题
     */
    public static void writeHomeTopicJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"homeTopic.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }


    /**
     * 读取首页猜你喜欢--楼盘
     */
    public static String readHouseLikeJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"houseLike.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入首页猜你喜欢--楼盘
     */
    public static void writeHouseLikeJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"houseLike.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }


    /**
     * 读取首页推荐预定
     */
    public static String readBookInfoRecommedJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"bookedInfoRecommed.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入首页推荐预定
     */
    public static void writeBookInfoRecommedJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"bookedInfoRecommed.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }

    /**
     * 读取预定列表
     */
    public static String readBookInfoListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"bookedInfoAll.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入预定列表
     */
    public static void writeBookInfoJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"bookedInfoAll.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }




    /**
     * 读取新房楼盘列表缓存--全部楼盘
     * @param siteId
     * @return
     */
    public static String readNewHouseListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"newHouseList.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入新房楼盘列表缓存--全部楼盘
     * @param siteId
     * @param value
     */
    public static void writeNewHouseListJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"newHouseList.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }

    /**
     * 读取新房楼盘列表缓存--最新开盘
     * @param siteId
     * @return
     */
    public static String readNewHouseNListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"newHouseListN.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入新房楼盘列表缓存--最新开盘
     * @param siteId
     * @param value
     */
    public static void writeNewHouseNListJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"newHouseListN.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }

    /**
     * 读取新房楼盘列表缓存--优惠楼盘
     * @param siteId
     * @return
     */
    public static String readNewHouseDListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"newHouseListD.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入新房楼盘列表缓存--优惠楼盘
     * @param siteId
     * @param value
     */
    public static void writeNewHouseDListJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"newHouseListD.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }




    /**
     * 读取新房楼盘户型列表缓存
     * @param siteId
     * @return
     */
    public static String readNewHouseTypeListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"newHouseTypeList.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入新房楼盘户型列表缓存
     * @param siteId
     * @param value
     */
    public static void writeNewHouseTypeListJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"newHouseTypeList.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }


    /**
     * 读取二手房楼盘列表缓存
     * @param siteId
     * @return
     */
    public static String readOldHouseListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"oldHouseList.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入二手房楼盘列表缓存
     * @param siteId
     * @param value
     */
    public static void writeOldHouseListJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"oldHouseList.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }


    /**
     * 读取租房楼盘列表缓存
     * @param siteId
     * @return
     */
    public static String readRentHouseListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"rentHouseList.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入租房楼盘列表缓存
     * @param siteId
     * @param value
     */
    public static void writeRentHouseListJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"rentHouseList.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }



    /**
     * 读取赚佣楼盘列表缓存
     * @param siteId
     * @return
     */
    public static String readMoneyHouseListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"moneyHouseList.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入赚佣楼盘列表缓存
     * @param siteId
     * @param value
     */
    public static void writeMoneyHouseListJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"moneyHouseList.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }


    /**
     * 读取学区房列表缓存
     * @param siteId
     * @return
     */
    public static String readSchoolHouseListJson(String siteId){
        String json = "";
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            json = FileUtil.readFile(path+File.separator+"schoolHouseList.json", key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }

        return json;
    }

    /**
     * 写入学区房列表缓存
     * @param siteId
     * @param value
     */
    public static void writeSchoolHouseListJson(String siteId, String value){
        try{
            String path = getCachePath(siteId);
            FileUtil.createFolder(path);
            FileUtil.writeFile(path+File.separator+"schoolHouseList.json", value, key);
        }catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
    }

}
