package com.lingzhuo.coolweather01.bean;

/**
 * Created by Wang on 2016/4/20.
 */
public class CountyWeather {
    /**
     <days>2016-04-20</days>
     <week>星期三</week>
     <citynm>北京</citynm>
     <weather>多云转晴</weather>
     <weather_icon>http://api.k780.com:88/upload/weather/d/1.gif</weather_icon>
     <weather_icon1>http://api.k780.com:88/upload/weather/n/0.gif</weather_icon1>
     <wind>无持续风向</wind>
     <winp>微风</winp>
     <temp_high>26</temp_high>
     <temp_low>10</temp_low>
     <humi_high>0</humi_high>
     */
    private String days;
    private String week;
    private String citynm;
    private String temp_low;
    private String temp_high;
    private String weather;
    private String weather_icon;
    private String weather_icon1;
    private String wind;
    private String cityid;
    private String update_time;
    private String winp;

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String time) {
        this.update_time = time;
    }


    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getCitynm() {
        return citynm;
    }

    public void setCitynm(String citynm) {
        this.citynm = citynm;
    }

    public String getTemp_low() {
        return temp_low;
    }

    public void setTemp_low(String temp_low) {
        this.temp_low = temp_low;
    }



    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeather_icon() {
        return weather_icon;
    }

    public void setWeather_icon(String weather_icon) {
        this.weather_icon = weather_icon;
    }

    public String getWeather_icon1() {
        return weather_icon1;
    }

    public void setWeather_icon1(String weather_icon1) {
        this.weather_icon1 = weather_icon1;
    }

    public String getTemp_high() {
        return temp_high;
    }

    public void setTemp_high(String temp_high) {
        this.temp_high = temp_high;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWinp() {
        return winp;
    }

    public void setWinp(String winp) {
        this.winp = winp;
    }
}
