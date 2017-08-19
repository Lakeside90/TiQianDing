package com.xkhouse.fang.house.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xkhouse.fang.house.entity.XKSearchResult;
import com.xkhouse.frame.db.DBHelper;

/**
* @Description: 搜索历史记录数据库操作 
* @author wujian  
* @date 2015-9-2 上午11:34:05
 */
public class SearchDbService {

	private SQLiteDatabase db;
	
	public SearchDbService() {
		db = DBHelper.getInstance().getDatabase();
	}
	
	public void saveSearchContent(XKSearchResult result, String uid, String siteId){
		if(result == null) return;
		
		try {
			String table = "search_record";
			String whereClause = "user_id= ? and site_id=? and search_type=? and search_content=?";
			String[] whereArgs = { uid, siteId, result.getType(), result.getProjectName() };
			ContentValues values = new ContentValues();
			values.put("user_id", uid);
			values.put("site_id", siteId);
			values.put("search_type", result.getType());
			values.put("search_content", result.getProjectName());
			values.put("create_time", String.valueOf(System.currentTimeMillis()));
			
			if (!db.isOpen()) {
	            db = DBHelper.getInstance().getDatabase();
	        }
			int count = db.update(table, values, whereClause, whereArgs);
			if(count == 0){
				db.insert(table, "", values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<XKSearchResult> getSearchResult(String uid, String siteId){
		ArrayList<XKSearchResult> resultList = new ArrayList<XKSearchResult>();
		String sql = "select * from search_record where user_id = ? and site_id = ? order by create_time desc";
		String[] arg = { uid, siteId };
		 Cursor cursor = null;
	        try {
	            if (!db.isOpen()) {
	                db = DBHelper.getInstance().getDatabase();
	            }
	            cursor = db.rawQuery(sql, arg);
	            if (cursor != null && cursor.moveToFirst()) {
	                do {
	                	XKSearchResult result = new XKSearchResult();
	                	result.setProjectName(cursor.getString(cursor.getColumnIndex("search_content")));
	                	result.setType(cursor.getString(cursor.getColumnIndex("search_type")));
	                	resultList.add(result);
	                	
	                } while (cursor.moveToNext());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (cursor != null) {
	                cursor.close();
	            }
	        }
		return resultList;
	}
	
	public void clearSearchContent(String uid, String siteId){
		try {
			String  sql = "delete from search_record where user_id = ? and site_id = ?";
			Object[] obj = {uid, siteId};
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
