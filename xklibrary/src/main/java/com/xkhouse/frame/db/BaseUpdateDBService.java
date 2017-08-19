/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title  UpdateDBService.java   
 * @Package com.cdel.frame.db 
 * @Author nieshuting     
 * @Date   2013-12-20 下午4:33:16   
 * @Version V1.0     
 */
package com.xkhouse.frame.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xkhouse.frame.log.Logger;


/**
 * 初始化和更新数据库的表结构，从assets/update_sql.txt读取，实现alterTable即可
 * 
 * @Author nieshuting
 * @ClassName UpdateDBService
 * @Date 2013-12-20下午4:33:16
 */
public abstract class BaseUpdateDBService {

	private static String UPDATESQLPATH = "update_sql.txt";

	private static String TAG = "BaseUpdateDBService";

	protected Context mContext;

	/**
	 * 
	 * @Title UpdateDBService
	 * @param @param context
	 * @throws
	 */
	public BaseUpdateDBService(Context context) {
		this.mContext = context;
	}

	/**
	 * 升级数据库
	 */
	public void updateTable() {
		try {
			DatabaseUtil.getInstance().beginTransaction();
			initTable();
			alterTable();
			DatabaseUtil.getInstance().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(TAG, e.toString());
		} finally {
			DatabaseUtil.getInstance().endTransaction();
		}
	}

	/**
	 * 更新表结构
	 * 
	 * @param
	 * @return void
	 */
	protected abstract void alterTable() throws Exception;

	/**
	 * 初始化建表，执行assest/update_sql.txt的SQL语句
	 * 
	 * @param: @throws Exception
	 * @return: void
	 */
	private void initTable() {
		SQLiteDatabase mDB = DatabaseUtil.getInstance().getDB();
		try {
			InputStream is = mContext.getAssets().open(UPDATESQLPATH);
			if (is != null) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(is));
				String line = "";
				while ((line = in.readLine()) != null) {
					mDB.execSQL(line);
				}
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(TAG, "初始化数据库失败");
		}
	}
}
