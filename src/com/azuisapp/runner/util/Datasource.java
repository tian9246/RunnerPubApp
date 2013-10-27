
package com.azuisapp.runner.util;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.azuisapp.runner.datasource.DBOpenHelper;

/**
 * 数据库数据源操作相关
 * 
 * @author hansontian
 */
public class Datasource {

    private static Datasource mInstance;
    public DBOpenHelper dbHelper;
    public SQLiteDatabase db;
    public Context context;

    public static Datasource getInstance() {
        if (mInstance == null)
            mInstance = new Datasource();
        if (mInstance.context != null) {
            if (mInstance.dbHelper == null)
                mInstance.dbHelper = new DBOpenHelper(mInstance.context);
            if (mInstance.db == null)
                mInstance.db = mInstance.dbHelper.getWritableDatabase();
        }
        return mInstance;
    }

    public void initDatabase(Context context) {
        this.context = context;
        dbHelper = new DBOpenHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 返回所有节点数据
     * 
     * @return
     */
    public ArrayList<Location> getAllLocation() {
        ArrayList<Location> locations = new ArrayList<Location>();
        Cursor cursor = null;
        try {
            cursor = this.db.query(DBOpenHelper.TABLE_RECODER, null, null, null, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Location location = new Location("GPS");
                location.setTime(cursor.getLong(cursor.getColumnIndex("TIME")));
                location.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                location.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));
                locations.add(location);
            }
        }
        return locations;
    }

    /**
     * 清除所有节点数据
     */
    public void clearAllLocation() {
        db.delete(DBOpenHelper.TABLE_RECODER, null, null);
    }

    /**
     * 插入节点数据
     * 
     * @param location
     */
    public void insertLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put("TIME", location.getTime());
        values.put("LATITUDE", location.getLatitude());
        values.put("LONGITUDE", location.getLongitude());
        db.insert(DBOpenHelper.TABLE_RECODER, null, values);
    }

    /**
     * 关闭数据库
     */
    public void cleanup() {
        if (this.db != null && db.isOpen()) {
            this.db.close();
            this.db = null;
        }
    }

}
