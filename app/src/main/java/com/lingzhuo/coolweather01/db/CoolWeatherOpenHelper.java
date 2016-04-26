package com.lingzhuo.coolweather01.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Wang on 2016/4/19.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    private Context context;
    /**
     *  Province表建表语句
     */
    public static final String CREATE_PROVINCE = "create table province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text)";
    /**
     *  City表建表语句
     */
    public static final String CREATE_CITY = "create table city ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";
    /**
     *  County表建表语句
     */
    public static final String CREATE_COUNTY = "create table county ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";
    public static final String CREATE_COUNTY_WEATHER="create table county_weather(id integer primary key autoincrement," +
            "days text," +
            "week text," +
            "citynm text," +
            "temp_low text," +
            "temp_high text," +
            "weather text," +
            "weather_icon text," +
            "weather_icon1 text," +
            "wind text," +
            "update_time text," +
            "cityid text," +
            "winp text)";


    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
        db.execSQL(CREATE_COUNTY_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
