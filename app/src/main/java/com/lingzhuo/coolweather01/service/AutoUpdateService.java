package com.lingzhuo.coolweather01.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lingzhuo.coolweather01.activity.MainActivity;
import com.lingzhuo.coolweather01.bean.CountyWeather;
import com.lingzhuo.coolweather01.db.CoolWeatherDB;
import com.lingzhuo.coolweather01.rerceiver.MyTimeChangeReceiver;
import com.lingzhuo.coolweather01.utils.HttpUtils;
import com.lingzhuo.coolweather01.utils.MyWeatherDetialHandler;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 用于后台自动更新天气的服务
 * 同时也注册了系统时间改变的广播事件
 * Created by Wang on 2016/4/21.
 */
public class AutoUpdateService extends Service {

    private List<CountyWeather> listWeather;
    private CoolWeatherDB coolWeatherDB;
    private MyTimeChangeReceiver myTimeChangeReceiver;
    private IntentFilter intentFilter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        coolWeatherDB=CoolWeatherDB.newInstance(getApplicationContext());
        listWeather=coolWeatherDB.loadCountyWeather();
        intentFilter=new IntentFilter();
        myTimeChangeReceiver=new MyTimeChangeReceiver();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        registerReceiver(myTimeChangeReceiver,intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
                Intent intent=new Intent("UPDATE_MAIN");
                sendBroadcast(intent);
            }
        }).start();

        //定时两小时更新数据库天气信息
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int alarmTime=60*2*60*1000;
        long time= System.currentTimeMillis()+alarmTime;
        PendingIntent i=PendingIntent.getBroadcast(this,0,new Intent("START_AlARM"),0);
        manager.set(AlarmManager.RTC_WAKEUP,time,i);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myTimeChangeReceiver);
    }

    /**
     * 更新数据库天气信息的操作
     */
    public void updateWeather(){
        String urls = "http://api.k780.com:88/?app=weather.future&weaid=" + listWeather.get(0).getCityid() + "&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=xml";
        HttpUtils.sendHttpRequest(urls, new HttpUtils.HttpCallbackListener() {
            @Override
            public void onFinish(String responce) {
                if (!TextUtils.isEmpty(responce)) {
                    String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + responce.substring(responce.indexOf("<result>"), responce.lastIndexOf("</root>"));
                    try {
                        coolWeatherDB.deleteWeatherDetial();
                        FileWriter out = new FileWriter(new File(Environment.getExternalStorageDirectory(), "countyWeather.xml"));
                        out.write(str);
                        out.flush();
                        out.close();
                        File file = new File(Environment.getExternalStorageDirectory() + "/countyWeather.xml");
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser parser = factory.newSAXParser();
                        parser.parse(file, new MyWeatherDetialHandler(coolWeatherDB));
                    } catch (Exception e) {
                        onError(e);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

}
