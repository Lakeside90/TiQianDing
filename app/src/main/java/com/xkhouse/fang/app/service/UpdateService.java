package com.xkhouse.fang.app.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xkhouse.frame.db.BaseUpdateDBService;
import com.xkhouse.frame.db.DatabaseUtil;
import com.xkhouse.frame.log.Logger;

/**
 * 数据库升级
 * 
 * @author wujian
 * 
 */
public class UpdateService extends BaseUpdateDBService {


    /**   
	 *
	 */
    public UpdateService(Context context) {
        super(context);

    }

    @Override
    protected void alterTable() throws Exception {
        changUser();
    }

    //增加邀请码字段
    private void changUser() {

       if(!checkColumnExists(DatabaseUtil.getInstance().getDB(), "user", "nuid")){
           DatabaseUtil.getInstance().getDB().execSQL("ALTER TABLE user add column nuid TEXT");
       }

        if(!checkColumnExists(DatabaseUtil.getInstance().getDB(), "user", "member_type")){
            DatabaseUtil.getInstance().getDB().execSQL("ALTER TABLE user add column member_type TEXT");
        }

        if(!checkColumnExists(DatabaseUtil.getInstance().getDB(), "user", "oldhouse_sale_ext_auth")){
            DatabaseUtil.getInstance().getDB().execSQL("ALTER TABLE user add column oldhouse_sale_ext_auth TEXT");
        }

        if(!checkColumnExists(DatabaseUtil.getInstance().getDB(), "user", "oldhouse_hire_ext_auth")){
            DatabaseUtil.getInstance().getDB().execSQL("ALTER TABLE user add column oldhouse_hire_ext_auth TEXT");
        }

        if(!checkColumnExists(DatabaseUtil.getInstance().getDB(), "user", "mobile")){
            DatabaseUtil.getInstance().getDB().execSQL("ALTER TABLE user add column mobile TEXT");
        }
    }


    private boolean checkColumnExists(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false ;
        Cursor cursor = null ;

        try{
            cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName , "%" + columnName + "%"} );
            result = null != cursor && cursor.moveToFirst() ;
        }catch (Exception e){
            Logger.e("", "checkColumnExists..." + e.getMessage()) ;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }

        return result ;
    }

}
