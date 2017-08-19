package com.xkhouse.fang.user.service;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xkhouse.fang.user.entity.User;
import com.xkhouse.frame.db.DBHelper;
import com.xkhouse.lib.utils.StringUtil;


public class UserService {

	private SQLiteDatabase db;

    public UserService() {
        db = DBHelper.getInstance().getDatabase();
    }
    
    public User queryUser(String user_id) {
        User user = null;
        if (StringUtil.isNotNull(user_id)) {
            String sql = "select user_name, real_name, nick_name, email, " +
            		"phone, mobile, age, sex, city, head_photo, nuid, member_type, oldhouse_sale_ext_auth" +
                    ", oldhouse_hire_ext_auth from user where user_id = ?";
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, new String[] { user_id });
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    user = new User();
                    user.setUid(user_id);
                    user.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
//                    user.setPassword(AES.decrypt(Constants.ANDROID_ID, cursor.getString(cursor.getColumnIndex("user_psw"))));
                    user.setRealName(cursor.getString(cursor.getColumnIndex("real_name")));
                    user.setNickName(cursor.getString(cursor.getColumnIndex("nick_name")));
                    user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                    user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                    user.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                    user.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                    user.setCity(cursor.getString(cursor.getColumnIndex("city")));
                    user.setHeadPhoto(cursor.getString(cursor.getColumnIndex("head_photo")));
                    user.setNuid(cursor.getString(cursor.getColumnIndex("nuid")));
                    user.setMemberType(cursor.getString(cursor.getColumnIndex("member_type")));
                    user.setOldhouseHireExtAuth(cursor.getString(cursor.getColumnIndex("oldhouse_hire_ext_auth")));
                    user.setOldhouseSaleExtAuth(cursor.getString(cursor.getColumnIndex("oldhouse_sale_ext_auth")));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return user;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }
        return user;
    }
    
    
    public void insertUser(User user) {
    	if(user == null) return;
        try {
            String sql = null;
            Object[] obj = null;
            if (!hasUser(user.getUid())) {
                sql = "insert into user(user_id, user_name, real_name, nick_name," +
                		" email, phone, mobile, age, sex, city, head_photo, nuid, " +
                        "member_type, oldhouse_sale_ext_auth, oldhouse_hire_ext_auth)  values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                obj = new Object[] { 
                		user.getUid(),
                		user.getUserName(),
//                        AES.encrypt(Constants.ANDROID_ID, user.getPassword()),
                        user.getRealName(),
                        user.getNickName(),
                        user.getEmail(),
                        user.getPhone(), 
                        user.getMobile(),
                        user.getAge(),
                        user.getSex(),
                        user.getCity(),
                        user.getHeadPhoto(),
                        user.getNuid(),
                        user.getMemberType(),
                user.getOldhouseSaleExtAuth(),
                user.getOldhouseHireExtAuth()};

            } else {
                sql = "update user set user_name=?, real_name=?, nick_name=?, email=?, phone=?," +
                		"mobile=?, age=?, sex=?, city=?, head_photo=?, nuid=?, member_type=?," +
                        " oldhouse_sale_ext_auth=?, oldhouse_hire_ext_auth=? where user_id=?";
                obj = new Object[] { 
                		user.getUserName(),
//                        AES.encrypt(Constants.ANDROID_ID, user.getPassword()),
                        user.getRealName(),
                        user.getNickName(),
                        user.getEmail(),
                        user.getPhone(), 
                        user.getMobile(),
                        user.getAge(),
                        user.getSex(),
                        user.getCity(),
                        user.getHeadPhoto(),
                        user.getNuid(),
                        user.getMemberType(),
                        user.getOldhouseSaleExtAuth(),
                        user.getOldhouseHireExtAuth(),
                        user.getUid()
                        };
            }
            db.execSQL(sql, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasUser(String id) {
        if (id == null) {
            id = "";
        }
        boolean has = false;
        String[] arg = new String[] { id };
        String sql = "select user_id from user where user_id = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, arg);
            if (cursor != null && cursor.getCount() > 0) {
                has = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return has;
    }
    
}
