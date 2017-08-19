package com.xkhouse.frame.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;

/**
 * 数据库统一的CRUD
 * 
 * @author zlx
 * 
 */
public final class DatabaseUtil {
	public String[] columnNames = new String[] { "default" };
	private static DatabaseUtil instance;

	public synchronized static DatabaseUtil getInstance() {
		if (instance == null) {
			instance = new DatabaseUtil();
		}
		return instance;
	}

	private DatabaseUtil() {
		super();
	}

	public SQLiteDatabase getDB() {
		return DBHelper.getInstance().getDatabase();
	}

	public int delete(String table, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = getDB();
		if (db != null&&!db.isReadOnly()) {
			try {
				return db.delete(table, whereClause, whereArgs);
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return 0;
	}

	public long insert(String table, String nullColumnHack, ContentValues values) {
		SQLiteDatabase db = getDB();
		if (db != null&&!db.isReadOnly()) {
			try {
				return db.insert(table, nullColumnHack, values);
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		return 0;
	}

	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		SQLiteDatabase db = getDB();
		if (db != null&&!db.isReadOnly()) {
			try {
				return db.update(table, values, whereClause, whereArgs);
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		return 0;
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		SQLiteDatabase db = getDB();
		if (db != null) {
			try {
				return db.rawQuery(sql, selectionArgs);
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		
		return new MatrixCursor(columnNames);
		
	}

	public void execSQL(String sql, Object[] bindArgs) {
		SQLiteDatabase db = getDB();
		if (db != null&&!db.isReadOnly()) {
			try {
				db.execSQL(sql, bindArgs);
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
	}

	public void execSQL(String sql) {
		SQLiteDatabase db = getDB();
		if (db != null&&!db.isReadOnly()) {
			try {
				db.execSQL(sql);
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
	}

	public void beginTransaction() {
		SQLiteDatabase db = getDB();
		if (db != null) {
			try {
				db.beginTransaction();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public void endTransaction() {
		SQLiteDatabase db = getDB();
		if (db != null) {
			try {
				db.endTransaction();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public void setTransactionSuccessful() {
		SQLiteDatabase db = getDB();
		if (db != null) {
			try {	
				db.setTransactionSuccessful();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public static void destory() {
		if (instance != null) {
			instance.onDestory();
		}
	}

	public void onDestory() {
		instance = null;
	}
}
