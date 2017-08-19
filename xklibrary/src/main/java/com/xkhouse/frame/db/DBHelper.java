/*
 *
 *@作者 nieshuting
 *@创建日期 2011-6-28上午11:37:20
 *@所有人 CDEL
 *@文件名 DBOpenHelper.java
 *@包名 org.cdel.lib.db
 */

package com.xkhouse.frame.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;

import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.FileUtil;
import com.xkhouse.lib.utils.SDCardUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * DBHelper类用于管理数据库；
 * 
 * @author nieshuting
 * @version 0.2
 */
public class DBHelper {

	/** 数据库实例； */
	protected SQLiteDatabase mDB = null;

	// 上下文；
	protected Context mContext;

	/** 数据库路径； */
	protected String mDBPath;

	/** 数据库所在卡及路径名称； */
	protected String mDbPathName;

	/** 数据库名称； */
	protected String mDBNname;

	/** db是否在SD卡上； */
	public boolean mIsonsd = true;

	private static DBHelper instance;

	private static String TAG = "DBHelper";

	/** 默认db名称 */
	private final static String DEFAULT_DB = "xkhouse.db";

	/** db所有卡的最小可用空间 */
	private static final int MINSIZE_SPACE = 10;

	/**
	 * 私有化构造函数,不让外部创建
	 */
	private DBHelper() {
		Properties property = BaseConfig.getInstance().getConfig();
		if (property != null) {
			mDBPath = property.getProperty("dbpath");
			mDBNname = property.getProperty("dbname");
		}
	}

	/**
	 * 生成单例；
	 * 
	 * @return 返回单例对象
	 */
	public synchronized static DBHelper getInstance() {
		if (instance == null) {
			instance = new DBHelper();
		}
		return instance;
	}

	public void init(Context context) {
		this.mContext = context;
	}

	public String getmDbPathName() {
		return mDbPathName;
	}

	public void setmDbPathName(String mDbPathName) {
		this.mDbPathName = mDbPathName;
	}

	public String getmDbname() {
		return mDBNname;
	}

	public void setmDbname(String mDbname) {
		this.mDBNname = mDbname;
	}

	/**
	 * 获取本次启动时，db实际的存储目录
	 * 
	 * @param @return
	 * @return String
	 */
	public String getmDbpath() {
		return mDbPathName;
	}

	public void setmDbpath(String mDbpath) {
		this.mDbPathName = mDbpath;
	}

	/**
	 * 对外关闭数据库，同时复制至data下
	 * 
	 * @param:
	 * @return: void
	 */
	public void closeDatabase() {
		if (mIsonsd) {// 应用退出，备份数据库到私有目录
			String source = mDbPathName + File.separator + mDBNname;
			String target = mContext.getFilesDir().getAbsolutePath()
					+ File.separator + mDBNname;
			copyDB(source, target);
		}
		mDbPathName = "";
		closedb();
	}

	/**
	 * 关闭数据库
	 * 
	 * @param:
	 * @return: void
	 */
	private void closedb() {
		if (mDB != null && mDB.isOpen()) {
			mDB.close();
			mDB = null;
			Logger.i(TAG, "已关闭数据库!");
		}
	}

	/**
	 * 打开数据库，并根据是否有可用空间，返回读写模式
	 * 
	 * @return SQLiteDatabase
	 */
	public synchronized SQLiteDatabase getDatabase() {
		try {
			if (hasDBPathNameFile()) {
				// 有db文件 ，则直接打开
				openDB();
			} else {
				// 没有db文件，则先关闭则拷贝打开
				closedb();
				if (hasDB()) {
					openDB();
				}
			}
			
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if (mDB == null || !mDB.isOpen()) {
			Logger.e(TAG, "数据库实例为空或未打开");
		}
		return mDB;
	}

	/**
	 * 打开私有目录下的数据库
	 * 
	 * @param
	 * @return void
	 */
	public void openPrivateDatabase() {
		String dir = mContext.getFilesDir().getAbsolutePath();
		mDbPathName = dir + File.separator + mDBNname;
		mIsonsd = false;
		closedb();
		if (hasDB()) {
			openDB();// 重新打开；
		}
		if (mDB != null && mDB.isOpen()) {
			Logger.i(TAG, "已打开data数据库实例");
		}
	}

	/**
	 * 打开SD下的数据库
	 * 
	 * @param
	 * @return void
	 */
	public void openSDDatabase() {
		if (mIsonsd) {
			return;
		}
		mDbPathName = Environment.getExternalStorageDirectory()
				+ File.separator + mDBPath;
		mIsonsd = true;
		FileUtil.createFolder(mDbPathName);
		closedb();
		if (hasDB()) {
			openDB();
		}
		if (mDB != null && mDB.isOpen()) {
			Logger.i(TAG, "已打开SD卡数据库实例");
		}
	}

	/**
	 * 判断并获取数据库完整路径和名称
	 * 
	 * void
	 */
	private boolean hasDBPathNameFile() {
		if (SDCardUtil.detectEXTSDMounted()) {// SD卡挂载
			mDbPathName = Environment.getExternalStorageDirectory()
					+ File.separator + mDBPath;
			FileUtil.createFolder(mDbPathName);
			if (new File(mDbPathName + File.separator + mDBNname).exists()) {// db文件存在
				mIsonsd = true;
				return true;
			} else {
				// db 不存在，但data下有db，则复制
				closedb();
				String source = mContext.getFilesDir().getAbsolutePath()
						+ File.separator + mDBNname;
				String target = mDbPathName + File.separator + mDBNname;
				if (copyDB(source, target)) {
					mIsonsd = true;
					return true;
				}
			}
		} else {// 没有SD卡
			mDbPathName = mContext.getFilesDir().getAbsolutePath();
			if (new File(mDbPathName + File.separator + mDBNname).exists()) {// db文件存在
				mIsonsd = false;
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测是否有数据库,add by huangyu,modified by nieshuting；
	 * 
	 * @return boolean
	 */
	private boolean hasDB() {
		if (StringUtil.isNotNull(mDbPathName)) {
			if (new File(mDbPathName + File.separator + mDBNname).exists()) {
				return true;
			} else {
				return copyDBFromAssets();
			}
		} else {
			return false;
		}
	}

	/**
	 * 打开并返回数据库的引用；
	 * 
	 * @return SQLiteDatabase
	 */
	private void openDB() {
		if (StringUtil.isEmpty(mDbPathName)) {
			return;
		}
		String name = mDbPathName + File.separator + mDBNname;
		long size = SDCardUtil.getDirectorySize(mDbPathName);
		if (size > MINSIZE_SPACE) {// 有可用空间，则以读写模式打开
			if (mDB != null) {
				if (mDB.isReadOnly()) {
					closedb();
					mDB = SQLiteDatabase.openDatabase(name, null,
							SQLiteDatabase.OPEN_READWRITE);
					Logger.i(TAG, "以读写模式打开数据库实例" + name);
				}
			} else {
				mDB = SQLiteDatabase.openDatabase(name, null,
						SQLiteDatabase.OPEN_READWRITE);
				Logger.i(TAG, "以读写模式打开数据库实例" + name);
			}
		} else {// 无可用空间，则以只读模式打开
			if (mDB != null) {
				if (!mDB.isReadOnly()) {
					closedb();
					mDB = SQLiteDatabase.openDatabase(name, null,
							SQLiteDatabase.OPEN_READONLY);
					Logger.i(TAG, "无可用空间，以只读模式打开数据库实例" + name);
				}
			} else {
				mDB = SQLiteDatabase.openDatabase(name, null,
						SQLiteDatabase.OPEN_READONLY);
				Logger.i(TAG, "无可用空间，以只读模式打开数据库实例" + name);
			}
		}

	}

	/**
	 * 拷贝assets的数据库
	 * 
	 * @return boolean
	 */
	private boolean copyDBFromAssets() {
		InputStream is = null;
		boolean isOK = false;
		try {
			if (StringUtil.isNotNull(mDbPathName)) {
				String name = mDbPathName + File.separator + mDBNname;
				String dataPathName = mContext.getFilesDir().getAbsolutePath()
						+ File.separator + mDBNname;
				if (!dataPathName.equals(name)
						&& new File(dataPathName).exists()) {
					is = new FileInputStream(dataPathName);
				} else {
					is = mContext.getClass().getClassLoader()
							.getResourceAsStream(mDBNname);
					if (is == null) {
						is = mContext.getClass().getClassLoader()
								.getResourceAsStream(DEFAULT_DB);
					}
				}
				FileOutputStream fos = new FileOutputStream(name);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				isOK = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.w(TAG, "从assets拷贝数据库失败!");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return isOK;
	}

	/**
	 * 拷贝db
	 * 
	 * @param source
	 * @param target
	 * @return boolean
	 */
	private boolean copyDB(String source, String target) {
		try {
			if (StringUtil.isNotNull(source)) {

				if (!target.equals(source) && new File(source).exists()) {
					InputStream is = new FileInputStream(source);
					FileOutputStream fos = new FileOutputStream(target);
					byte[] buffer = new byte[7168];
					int count = 0;
					while ((count = is.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
					fos.close();
					is.close();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.w(TAG, "备份数据库失败!");
		}
		return false;
	}

}
