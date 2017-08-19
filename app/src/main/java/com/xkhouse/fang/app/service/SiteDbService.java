package com.xkhouse.fang.app.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.frame.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
* @Description: 站点数据库操作  
* @author wujian  
* @date 2015-9-7 下午6:25:16
 */
public class SiteDbService {

	private SQLiteDatabase db;
	
	public SiteDbService() {
		db = DBHelper.getInstance().getDatabase();
	}
	
	
	//插入单个站点
	public void insertSite(Site site) {
        try{
            String sql = "insert into site(site_id, title, domain, area, longitude, latitude, hot) values (?,?,?,?,?,?,?)";
            String[] obj = { site.getSiteId(), site.getTitle(), site.getDomain(),
                    site.getArea(), site.getLongitude(), site.getLatitude(), site.getIsHot()};
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	//插入多个站点
	public void insertSites(List<Site> sites) {
	    try{
            db.beginTransaction();
            clearSites();

            for(Site site : sites){
                insertSite(site);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	
	//站点列表
	public ArrayList<Site> getSiteList(){
		ArrayList<Site> sites = new ArrayList<Site>();
		String sql = "select * from site";
		 Cursor cursor = null;
	        try {
	            if (!db.isOpen()) {
	                db = DBHelper.getInstance().getDatabase();
	            }
	            cursor = db.rawQuery(sql, null);
	            if (cursor != null && cursor.moveToFirst()) {
	                do {
	                	Site site = new Site();
	                	site.setArea(cursor.getString(cursor.getColumnIndex("area")));
	                	site.setDomain(cursor.getString(cursor.getColumnIndex("domain")));
	                	site.setSiteId(cursor.getString(cursor.getColumnIndex("site_id")));
	                	site.setTitle(cursor.getString(cursor.getColumnIndex("title")));
	                	site.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
	                	site.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
	                	site.setIsHot(cursor.getString(cursor.getColumnIndex("hot")));
	                	
	                	sites.add(site);
	                } while (cursor.moveToNext());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (cursor != null) {
	                cursor.close();
	            }
	        }
		return sites;
	}
	
	public Site getSiteByName(String city){
		Site site = null;
		String sql = "select * from site where area = ?";
		Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { city });
            if (cursor != null && cursor.moveToFirst()) {
            	site = new Site();
            	site.setArea(cursor.getString(cursor.getColumnIndex("area")));
            	site.setDomain(cursor.getString(cursor.getColumnIndex("domain")));
            	site.setSiteId(cursor.getString(cursor.getColumnIndex("site_id")));
            	site.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            	site.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
            	site.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
            	site.setIsHot(cursor.getString(cursor.getColumnIndex("hot")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		return site;
	}
	
	
	public void clearSites(){
		try {
			String  sql = "delete from site";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
