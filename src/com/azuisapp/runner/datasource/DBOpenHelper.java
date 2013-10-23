
package com.azuisapp.runner.datasource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库辅助记录类
 * @author hansontian
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    
    public static final String TABLE_RECODER = "RECODER";
   
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "distance_recoder.db3";  
    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_RECODER
            + " (  _id INTEGER PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT, TIME long  ,LATITUDE double ,LONGITUDE double);";
  
    
    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DBOpenHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { 
        Log.e("DataBase", "onCreate");
        try {
            db.execSQL(CREATE_TABLE);           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
