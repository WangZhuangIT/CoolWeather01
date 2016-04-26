package com.lingzhuo.coolweather01.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lingzhuo.coolweather01.bean.City;
import com.lingzhuo.coolweather01.bean.County;
import com.lingzhuo.coolweather01.bean.CountyWeather;
import com.lingzhuo.coolweather01.bean.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang on 2016/4/19.
 */
public class CoolWeatherDB {
    //数据库名称
    public static final String DB_NAME = "cool_weather";
    //数据库版本
    public static final int VERSION = 1;

    public static CoolWeatherDB coolWeatherDB;
    public SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
                DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取CoolWeatherDB的实例。
     */
    public synchronized static CoolWeatherDB newInstance(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /**
     * 将Province实例存储到数据库。
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvince_name());
            values.put("province_code", province.getProvince_code());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息。
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db
                .query("province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvince_name(cursor.getString(cursor
                        .getColumnIndex("province_name")));
                province.setProvince_code(cursor.getString(cursor
                        .getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将City实例存储到数据库。
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCity_name());
            values.put("city_code", city.getCity_code());
            values.put("province_id", city.getProvince_id());
            db.insert("city", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息。
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("city", null, "province_id = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCity_name(cursor.getString(cursor
                        .getColumnIndex("city_name")));
                city.setCity_code(cursor.getString(cursor
                        .getColumnIndex("city_code")));
                city.setProvince_id(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }


    /**
     * 将County实例存储到数据库。
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCounty_name());
            values.put("county_code", county.getCounty_code());
            values.put("city_id", county.getCity_id());
            db.insert("county", null, values);
        }
    }

    /**
     * 从数据库读取某城市下所有的县信息。
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("county", null, "city_id = ?",
                new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCounty_name(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCounty_code(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                county.setCity_id(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 获取所有的County的list，这个是用于查询的点击事件的方法
     * @return
     */
    public List<County> loadAllCounties() {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("county", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCounty_name(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCounty_code(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 用来保存我查询的天气的信息
     * @param countyWeather
     */
    public void saveCountyWeather(CountyWeather countyWeather) {
        if (countyWeather != null) {
            ContentValues values = new ContentValues();
            values.put("days", countyWeather.getDays());
            values.put("week", countyWeather.getWeek());
            values.put("citynm", countyWeather.getCitynm());
            values.put("temp_low", countyWeather.getTemp_low());
            values.put("temp_high", countyWeather.getTemp_high());
            values.put("weather", countyWeather.getWeather());
            values.put("weather_icon", countyWeather.getWeather_icon());
            values.put("weather_icon1", countyWeather.getWeather_icon1());
            values.put("wind", countyWeather.getWind());
            values.put("winp", countyWeather.getWinp());
            values.put("cityid", countyWeather.getCityid());
            values.put("update_time", countyWeather.getUpdate_time());
            db.insert("county_weather", null, values);
        }
    }

    /**
     * 用于更新天气的方法，每当更新天气信息，先删除现有的旧的天气数据
     */
    public void deleteWeatherDetial() {
        db.delete("county_weather", null, null);
    }

    /**
     * 用于更新城市列表的操作，分别是清空省、市、县的数据信息
     */
    public void deleteProvince() {
        db.delete("county", null, null);
    }

    public void deleteCity() {
        db.delete("city", null, null);
    }

    public void deleteCounty() {
        db.delete("county", null, null);
    }

    /**
     * 用来获取读取数据库中的天气信息
     * @return
     */
    public List<CountyWeather> loadCountyWeather() {
        List<CountyWeather> list = new ArrayList<>();
        Cursor cursor = db.query("county_weather", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CountyWeather countyWeather = new CountyWeather();
                countyWeather.setDays(cursor.getString(cursor.getColumnIndex("days")));
                countyWeather.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                countyWeather.setTemp_low(cursor.getString(cursor.getColumnIndex("temp_low")));
                countyWeather.setTemp_high(cursor.getString(cursor.getColumnIndex("temp_high")));
                countyWeather.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
                countyWeather.setWeather_icon(cursor.getString(cursor.getColumnIndex("weather_icon")));
                countyWeather.setWeather_icon1(cursor.getString(cursor.getColumnIndex("weather_icon1")));
                countyWeather.setWind(cursor.getString(cursor.getColumnIndex("wind")));
                countyWeather.setCitynm(cursor.getString(cursor.getColumnIndex("citynm")));
                countyWeather.setWinp(cursor.getString(cursor.getColumnIndex("winp")));
                countyWeather.setCityid(cursor.getString(cursor.getColumnIndex("cityid")));
                countyWeather.setUpdate_time(cursor.getString(cursor.getColumnIndex("update_time")));
                list.add(countyWeather);
            } while (cursor.moveToNext());
        }
        return list;
    }


}
