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
            String sql = "select token, nickname, realname, head_img, " +
            		"account_balance, activity_num, business_id, gender, phone, interest, is_staff" +
                    " from user where user_id = ?";
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, new String[] { user_id });
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    user = new User();
                    user.setId(user_id);
                    user.setToken(cursor.getString(cursor.getColumnIndex("token")));
                    user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
                    user.setRealname(cursor.getString(cursor.getColumnIndex("realname")));
                    user.setHead_img(cursor.getString(cursor.getColumnIndex("head_img")));
                    user.setAccount_balance(cursor.getString(cursor.getColumnIndex("account_balance")));
                    user.setActivity_num(cursor.getString(cursor.getColumnIndex("activity_num")));
                    user.setBusiness_id(cursor.getString(cursor.getColumnIndex("business_id")));
                    user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                    user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                    user.setInterest(cursor.getString(cursor.getColumnIndex("interest")));
                    user.setIs_staff(cursor.getString(cursor.getColumnIndex("is_staff")));

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
            if (!hasUser(user.getId())) {
                sql = "insert into user(user_id, token, nickname, realname," +
                		" head_img, account_balance, activity_num, business_id, gender, phone, interest, is_staff)" +
                        " values (?,?,?,?,?,?,?,?,?,?,?,?)";
                obj = new Object[] { 
                		user.getId(),
                		user.getToken(),
                        user.getNickname(),
                        user.getRealname(),
                        user.getHead_img(),
                        user.getAccount_balance(),
                        user.getActivity_num(),
                        user.getBusiness_id(),
                        user.getGender(),
                        user.getPhone(),
                        user.getInterest(),
                        user.getIs_staff()
                };

            } else {
                sql = "update user set token=?, nickname=?, realname=?, head_img=?, account_balance=?," +
                		"activity_num=?, business_id=?, gender=?, phone=?, interest=?, is_staff=?" +
                        " where user_id=?";
                obj = new Object[] {
                        user.getToken(),
                        user.getNickname(),
                        user.getRealname(),
                        user.getHead_img(),
                        user.getAccount_balance(),
                        user.getActivity_num(),
                        user.getBusiness_id(),
                        user.getGender(),
                        user.getPhone(),
                        user.getInterest(),
                        user.getIs_staff(),
                        user.getId()
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
