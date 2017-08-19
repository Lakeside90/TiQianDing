package com.xkhouse.fang.app.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.frame.db.DBHelper;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/** 
 * @Description: 筛选房源条件配置数据数据库操作（行政区域，价格，面积等等）
 * @author wujian  
 * @date 2015-9-18 下午4:21:05  
 */
public class HouseConfigDbService {

	private SQLiteDatabase db;
	
	public HouseConfigDbService() {
		db = DBHelper.getInstance().getDatabase();
	}
	
	/******************  价格数据库表 操作  *******************/
	public void insertPriceList(List<CommonType> priceList, String siteId){
		if(priceList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearPriceList();

            for (CommonType price : priceList) {
                insertPrice(price, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void insertPrice(CommonType price, String siteId) {
        try{
            String sql = "insert into price_range(price_id, price_value, site_id) values (?,?,?)";
            String[] obj = { price.getId(), price.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearPriceList() {
		try {
			String  sql = "delete from price_range";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getPriceListBySite(String siteId){
		ArrayList<CommonType> priceList = new ArrayList<CommonType>();
		String sql = "select * from price_range where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("price_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("price_value")));
                	priceList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return priceList;
	}
	
	
	
	/******************  面积数据库表 操作  *******************/
	public void insertSpaceList(List<CommonType> spaceList, String siteId){
		if(spaceList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearSpaceList();

            for (CommonType space : spaceList) {
                insertSpace(space, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

	}
	
	public void insertSpace(CommonType space, String siteId) {
        try{
            String sql = "insert into space_type(space_id, space_value, site_id) values (?,?,?)";
            String[] obj = { space.getId(), space.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearSpaceList() {
		try {
			String  sql = "delete from space_type";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getSpaceListBySite(String siteId){
		ArrayList<CommonType> spaceList = new ArrayList<CommonType>();
		String sql = "select * from space_type where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("space_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("space_value")));
                	spaceList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return spaceList;
	}
	
	
	
	
	/******************  行政区域数据库表 操作  *******************/
	public void insertAreaList(List<Area> areaList, String siteId){
		if(areaList == null || StringUtil.isEmpty(siteId)) return;
		try {
            db.beginTransaction();

            clearAreaList();

            for (Area area : areaList) {
                insertArea(area, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

	}
	
	public void insertArea(Area area, String siteId) {
        try{
            String sql = "insert into area_type(area_id, area_value, longitude, latitude, site_id) values (?,?,?,?,?)";
            String[] obj = { area.getAreaId(), area.getAreaName(), area.getLongitude(), area.getLatitude(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearAreaList() {
		try {
			String  sql = "delete from area_type";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Area> getAreaListBySite(String siteId){
		ArrayList<Area> areaList = new ArrayList<Area>();
		String sql = "select * from area_type where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	Area area = new Area();
                	area.setAreaId(cursor.getString(cursor.getColumnIndex("area_id")));
                	area.setAreaName(cursor.getString(cursor.getColumnIndex("area_value")));
                	area.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
                	area.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
                	areaList.add(area);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return areaList;
	}
	
	/******************  特色数据库表 操作  *******************/
	public void insertFeatureList(List<CommonType> featureList, String siteId){
		if(featureList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearFeatureList();

            for (CommonType feature : featureList) {
                insertFeature(feature, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void insertFeature(CommonType feature, String siteId) {
        try{
            String sql = "insert into feature_type(feature_id, feature_value, site_id) values (?,?,?)";
            String[] obj = { feature.getId(), feature.getName(), siteId };
            db.execSQL(sql, obj);

        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearFeatureList() {
		try {
			String  sql = "delete from feature_type";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getFeatureListBySite(String siteId){
		ArrayList<CommonType> featureList = new ArrayList<CommonType>();
		String sql = "select * from feature_type where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("feature_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("feature_value")));
                	featureList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return featureList;
	}
	
	
	
	
	/************************  重点学区数据库表 操作  *************************/
	public void insertSchoolList(List<CommonType> schoolList, String siteId){
		if(schoolList == null || StringUtil.isEmpty(siteId)) return;
		try{
            db.beginTransaction();

            clearSchoolList();

            for (CommonType school : schoolList) {
                insertSchool(school, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (Exception e){
            e.printStackTrace();
        }

	}
	
	public void insertSchool(CommonType school, String siteId) {
        try{
            String sql = "insert into school_type(school_id, school_value, site_id) values (?,?,?)";
            String[] obj = { school.getId(), school.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearSchoolList() {
		try {
			String  sql = "delete from school_type";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getSchoolListBySite(String siteId){
		ArrayList<CommonType> schoolList = new ArrayList<CommonType>();
		String sql = "select * from school_type where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("school_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("school_value")));
                	schoolList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return schoolList;
	}
	
	
	/************************  新房--更多--排序  数据库表 操作  *************************/
	public void insertOrderList(List<CommonType> orderList, String siteId){
		if(orderList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearOrderList();

            for (CommonType order : orderList) {
                insertOrder(order, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

	}
	
	public void insertOrder(CommonType order, String siteId) {
        try{
            String sql = "insert into order_type(order_id, order_value, site_id) values (?,?,?)";
            String[] obj = { order.getId(), order.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }

	}
	
	public void clearOrderList() {
		try {
			String  sql = "delete from order_type";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getOrderListBySite(String siteId){
		ArrayList<CommonType> orderList = new ArrayList<CommonType>();
		String sql = "select * from order_type where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("order_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("order_value")));
                	orderList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return orderList;
	}
	
	
	/************************  新房--更多--户型  数据库表 操作  *************************/
	public void insertHouseTypeList(List<CommonType> houseTypeList, String siteId){
		if(houseTypeList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearHouseTypeList();

            for (CommonType houseType : houseTypeList) {
                insertHouseType(houseType, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

	}
	
	public void insertHouseType(CommonType houseType, String siteId) {
        try{
            String sql = "insert into housetype_type(housetype_id, housetype_value, site_id) values (?,?,?)";
            String[] obj = { houseType.getId(), houseType.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearHouseTypeList() {
		try {
			String  sql = "delete from housetype_type";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getHouseTypeListBySite(String siteId){
		ArrayList<CommonType> houseTypeList = new ArrayList<CommonType>();
		String sql = "select * from housetype_type where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("housetype_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("housetype_value")));
                	houseTypeList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return houseTypeList;
	}
	
	
	/************************  新房--更多--装修状态  数据库表 操作  *************************/
	public void insertRenovateList(List<CommonType> renovateList, String siteId){
		if(renovateList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearRenovateList();

            for (CommonType renovate : renovateList) {
                insertRenovate(renovate, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void insertRenovate(CommonType renovate, String siteId) {
        try{
            String sql = "insert into renovate_state(renovate_id, renovate_value, site_id) values (?,?,?)";
            String[] obj = { renovate.getId(), renovate.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearRenovateList() {
		try {
			String  sql = "delete from renovate_state";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getRenovateListBySite(String siteId){
		ArrayList<CommonType> renovateList = new ArrayList<CommonType>();
		String sql = "select * from renovate_state where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("renovate_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("renovate_value")));
                	renovateList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return renovateList;
	}
	
	
	/************************  新房--更多--装修状态  数据库表 操作  *************************/
	public void insertSaleStateList(List<CommonType> saleStateList, String siteId){
		if(saleStateList == null || StringUtil.isEmpty(siteId)) return;

        try {
            db.beginTransaction();

            clearSaleStateList();

            for (CommonType saleState : saleStateList) {
                insertSaleState(saleState, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

	}
	
	public void insertSaleState(CommonType saleState, String siteId) {
        try{
            String sql = "insert into sale_state(sale_id, sale_value, site_id) values (?,?,?)";
            String[] obj = { saleState.getId(), saleState.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearSaleStateList() {
		try {
			String  sql = "delete from sale_state";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getSaleStateListBySite(String siteId){
		ArrayList<CommonType> saleStateList = new ArrayList<CommonType>();
		String sql = "select * from sale_state where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("sale_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("sale_value")));
                	saleStateList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return saleStateList;
	}
	
	
	/************************  新房--类型  数据库表 操作  *************************/
	public void insertBuildList(List<CommonType> propertyList, String siteId){
		if(propertyList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearPropertyList();

            for (CommonType property : propertyList) {
                insertProperty(property, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

	}
	
	public void insertProperty(CommonType property, String siteId) {
        try{
            String sql = "insert into property_type(property_id, property_value, site_id) values (?,?,?)";
            String[] obj = { property.getId(), property.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void clearPropertyList() {
		try {
			String  sql = "delete from property_type";
			if(!db.isOpen()){
				db = DBHelper.getInstance().getDatabase();
			}
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CommonType> getPropertyListBySite(String siteId){
		ArrayList<CommonType> propertyList = new ArrayList<CommonType>();
		String sql = "select * from property_type where site_id = ?";
		Cursor cursor = null;
		try {
			if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                	CommonType type = new CommonType();
                	type.setId(cursor.getString(cursor.getColumnIndex("property_id")));
                	type.setName(cursor.getString(cursor.getColumnIndex("property_value")));
                	propertyList.add(type);
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
		return propertyList;
	}


    /************************  新房--更多--开盘时间  数据库表 操作  *************************/
    public void insertOpenTimeList(List<CommonType> openTimeList, String siteId){
        if(openTimeList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearOpenTimeList();

            for (CommonType openTime : openTimeList) {
                insertOpenTime(openTime, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertOpenTime(CommonType openTime, String siteId) {
        try{
            String sql = "insert into open_time(open_id, open_value, site_id) values (?,?,?)";
            String[] obj = { openTime.getId(), openTime.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearOpenTimeList() {
        try {
            String  sql = "delete from open_time";
            if(!db.isOpen()){
                db = DBHelper.getInstance().getDatabase();
            }
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CommonType> getOpenTimeListBySite(String siteId){
        ArrayList<CommonType> openTimeList = new ArrayList<CommonType>();
        String sql = "select * from open_time where site_id = ?";
        Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CommonType type = new CommonType();
                    type.setId(cursor.getString(cursor.getColumnIndex("open_id")));
                    type.setName(cursor.getString(cursor.getColumnIndex("open_value")));
                    openTimeList.add(type);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return openTimeList;
    }



    /************************  新房--更多--开发商  数据库表 操作  *************************/
    public void insertDeveloperList(List<CommonType> developerList, String siteId){
        if(developerList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearDeveloperList();

            for (CommonType developer : developerList) {
                insertDeveloper(developer, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void insertDeveloper(CommonType developer, String siteId) {
        try{
            String sql = "insert into developer_type(developer_id, developer_value, site_id) values (?,?,?)";
            String[] obj = { developer.getId(), developer.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearDeveloperList() {
        try {
            String  sql = "delete from developer_type";
            if(!db.isOpen()){
                db = DBHelper.getInstance().getDatabase();
            }
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CommonType> getDeveloperListBySite(String siteId){
        ArrayList<CommonType> developerList = new ArrayList<CommonType>();
        String sql = "select * from developer_type where site_id = ?";
        Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CommonType type = new CommonType();
                    type.setId(cursor.getString(cursor.getColumnIndex("developer_id")));
                    type.setName(cursor.getString(cursor.getColumnIndex("developer_value")));
                    developerList.add(type);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return developerList;
    }


    /************************  购房能力评估--贷款年限  数据库表 操作  *************************/
    public void insertLoanTimeList(List<CommonType> loanTimeList, String siteId){
        if(loanTimeList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearLoanTimeList();

            for (CommonType loanTime : loanTimeList) {
                insertLoanTime(loanTime, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void insertLoanTime(CommonType loanTime, String siteId) {
        try{
            String sql = "insert into pinggu_time(time_id, time_value, site_id) values (?,?,?)";
            String[] obj = { loanTime.getId(), loanTime.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearLoanTimeList() {
        try {
            String  sql = "delete from pinggu_time";
            if(!db.isOpen()){
                db = DBHelper.getInstance().getDatabase();
            }
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CommonType> getLoanTimeListBySite(String siteId){
        ArrayList<CommonType> loanTimeList = new ArrayList<CommonType>();
        String sql = "select * from pinggu_time where site_id = ?";
        Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CommonType type = new CommonType();
                    type.setId(cursor.getString(cursor.getColumnIndex("time_id")));
                    type.setName(cursor.getString(cursor.getColumnIndex("time_value")));
                    loanTimeList.add(type);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return loanTimeList;
    }



    /************************  学区房--类型（小学，中学）  数据库表 操作  *************************/

    public void insertSchoolTypeList(List<CommonType> schoolTypeList, String siteId){
        if(schoolTypeList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearSchoolTypeList();

            for (CommonType property : schoolTypeList) {
                insertSchoolType(property, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void insertSchoolType(CommonType schoolType, String siteId) {
        try{
            String sql = "insert into school_type(school_id, school_value, site_id) values (?,?,?)";
            String[] obj = { schoolType.getId(), schoolType.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearSchoolTypeList() {
        try {
            String  sql = "delete from school_type";
            if(!db.isOpen()){
                db = DBHelper.getInstance().getDatabase();
            }
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CommonType> getSchoolTypeListBySite(String siteId){
        ArrayList<CommonType> schoolTypeList = new ArrayList<CommonType>();
        String sql = "select * from school_type where site_id = ?";
        Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CommonType type = new CommonType();
                    type.setId(cursor.getString(cursor.getColumnIndex("school_id")));
                    type.setName(cursor.getString(cursor.getColumnIndex("school_value")));
                    schoolTypeList.add(type);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return schoolTypeList;
    }



    /************************  学区房--是否为重点  数据库表 操作  *************************/

    public void insertSchoolKeyList(List<CommonType> schoolKeyList, String siteId){
        if(schoolKeyList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearSchoolKeyList();

            for (CommonType property : schoolKeyList) {
                insertSchoolKey(property, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void insertSchoolKey(CommonType schoolKey, String siteId) {
        try{
            String sql = "insert into school_key(key_id, key_value, site_id) values (?,?,?)";
            String[] obj = { schoolKey.getId(), schoolKey.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearSchoolKeyList() {
        try {
            String  sql = "delete from school_key";
            if(!db.isOpen()){
                db = DBHelper.getInstance().getDatabase();
            }
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CommonType> getSchoolKeyListBySite(String siteId){
        ArrayList<CommonType> schoolKeyList = new ArrayList<CommonType>();
        String sql = "select * from school_key where site_id = ?";
        Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CommonType type = new CommonType();
                    type.setId(cursor.getString(cursor.getColumnIndex("key_id")));
                    type.setName(cursor.getString(cursor.getColumnIndex("key_value")));
                    schoolKeyList.add(type);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return schoolKeyList;
    }



    /************************  学区房--排序  数据库表 操作  *************************/

    public void insertSchoolOrderList(List<CommonType> schoolOrderList, String siteId){
        if(schoolOrderList == null || StringUtil.isEmpty(siteId)) return;

        try{
            db.beginTransaction();

            clearSchoolOrderList();

            for (CommonType property : schoolOrderList) {
                insertSchoolOrder(property, siteId);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void insertSchoolOrder(CommonType schoolOrder, String siteId) {
        try{
            String sql = "insert into school_order(school_order_id, school_order_value, site_id) values (?,?,?)";
            String[] obj = { schoolOrder.getId(), schoolOrder.getName(), siteId };
            db.execSQL(sql, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearSchoolOrderList() {
        try {
            String  sql = "delete from school_order";
            if(!db.isOpen()){
                db = DBHelper.getInstance().getDatabase();
            }
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CommonType> getSchoolOrderListBySite(String siteId){
        ArrayList<CommonType> schoolOrderList = new ArrayList<CommonType>();
        String sql = "select * from school_order where site_id = ?";
        Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                db = DBHelper.getInstance().getDatabase();
            }
            cursor = db.rawQuery(sql, new String[] { siteId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CommonType type = new CommonType();
                    type.setId(cursor.getString(cursor.getColumnIndex("school_order_id")));
                    type.setName(cursor.getString(cursor.getColumnIndex("school_order_value")));
                    schoolOrderList.add(type);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return schoolOrderList;
    }



}
