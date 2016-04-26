package com.lingzhuo.coolweather01.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.lingzhuo.coolweather01.R;
import com.lingzhuo.coolweather01.bean.CountyWeather;
import com.lingzhuo.coolweather01.db.CoolWeatherDB;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 桌面小组件的控件的实现，规格是4*2
 * Created by Wang on 2016/4/23.
 */
public class MyWedget02 extends AppWidgetProvider {

    private CoolWeatherDB coolWeatherDB;
    private List<CountyWeather> listWeather;
    private Context context;
    private boolean flag=true;


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    private void initDB(Context context) {
        coolWeatherDB = CoolWeatherDB.newInstance(context);
        listWeather = coolWeatherDB.loadCountyWeather();
        this.context = context;
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateMywidget(context, appWidgetManager);
    }

    private void updateMywidget(Context context, AppWidgetManager appWidgetManager) {
        initDB(context);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        rv.setTextViewText(R.id.textView_widget_time, new SimpleDateFormat("hh:mm").format(System.currentTimeMillis()));
        rv.setTextViewText(R.id.textView_widget_location, listWeather.get(0).getCitynm());
        rv.setTextViewText(R.id.textView_widget_avgTemp, ((Integer.parseInt(listWeather.get(0).getTemp_high()) + (Integer.parseInt(listWeather.get(0).getTemp_low()))) / 2) + "℃ " + listWeather.get(0).getWeather());
        rv.setTextViewText(R.id.textView_widget_lowHighTemp, listWeather.get(0).getTemp_low() + "/" + listWeather.get(0).getTemp_high() + "℃");
        String date = listWeather.get(0).getDays().substring(5, 10);
        rv.setTextViewText(R.id.textView_widget_date, date + " " + listWeather.get(0).getWeek());
        String weather;
        if (listWeather.get(0).getWeather().contains("转")) {
            weather = listWeather.get(0).getWeather().substring(0, listWeather.get(0).getWeather().indexOf("转"));
        } else {
            weather = listWeather.get(0).getWeather();
        }

        if (weather.contains("晴")) {
            rv.setImageViewResource(R.id.imageView_widget_weather_ic1, R.mipmap.icon_sunnay);
        } else if (weather.contains("雨")) {
            rv.setImageViewResource(R.id.imageView_widget_weather_ic1, R.mipmap.icon_yu);
        } else if (weather.contains("雪")) {
            rv.setImageViewResource(R.id.imageView_widget_weather_ic1, R.mipmap.icon_xue);
        } else if (weather.contains("霾")) {
            rv.setImageViewResource(R.id.imageView_widget_weather_ic1, R.mipmap.icon_mai);
        } else if (weather.contains("多云")) {
            rv.setImageViewResource(R.id.imageView_widget_weather_ic1, R.mipmap.icon_duoyun);
        } else if (weather.contains("阴")) {
            rv.setImageViewResource(R.id.imageView_widget_weather_ic1, R.mipmap.icon_yintian);
        } else {
            rv.setImageViewResource(R.id.imageView_widget_weather_ic1, R.mipmap.icon_duoyun);
        }
        ComponentName componentName = new ComponentName(context, MyWedget02.class);
        appWidgetManager.updateAppWidget(componentName, rv);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        flag=false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        updateMywidget(context, appWidgetManager);
    }

}
