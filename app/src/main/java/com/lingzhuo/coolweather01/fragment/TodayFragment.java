package com.lingzhuo.coolweather01.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingzhuo.coolweather01.R;
import com.lingzhuo.coolweather01.bean.CountyWeather;
import com.lingzhuo.coolweather01.utils.HttpUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * 用来展示当天天气的Fragment类
 * Created by Wang on 2016/4/20.
 */
public class TodayFragment extends Fragment{
    private CountyWeather countyWeather;
    private Handler myHandler;

    public TodayFragment(CountyWeather countyWeather,Handler myHandler) {
        this.countyWeather = countyWeather;
        this.myHandler=myHandler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_today_weather,null);

        //设置今天的平均天气
        TextView textView_avg_degree= (TextView) view.findViewById(R.id.textView_avg_degree);
        int temp=(Integer.parseInt(countyWeather.getTemp_low())+Integer.parseInt(countyWeather.getTemp_high()))/2;
        textView_avg_degree.setText(temp+"℃");

        //当天的温度一览
        TextView textView_degree= (TextView) view.findViewById(R.id.textView_degree);
        textView_degree.setText(countyWeather.getTemp_low()+"/"+countyWeather.getTemp_high()+"℃");

        //设置当天的天气状况
        TextView teView_weather= (TextView) view.findViewById(R.id.teView_weather);
        teView_weather.setText(countyWeather.getWeather());

        //设置当天的风向
        TextView textView_wind_dir= (TextView) view.findViewById(R.id.textView_wind_dir);
        textView_wind_dir.setText(countyWeather.getWind());

        //设置当天的风力
        TextView textView_power= (TextView) view.findViewById(R.id.textView_power);
        textView_power.setText(countyWeather.getWinp());

        //获取天气的图片
        final ImageView imageView1= (ImageView) view.findViewById(R.id.image_weather1);
        String weather_icon=countyWeather.getWeather_icon();
        getBitmap(imageView1, weather_icon);

        ImageView imageView2= (ImageView) view.findViewById(R.id.image_weather2);
        String weather_icon2=countyWeather.getWeather_icon1();
        getBitmap(imageView2,weather_icon2);



        return view;
    }

    /**
     *用于根据天气图片的网址，直接加载图片显示在页面上的方法
     */
    private void getBitmap(final ImageView imageView1, String weather_icon) {
        HttpMyUtils.sendHttpRequest(weather_icon, new HttpMyUtils.MyHttpCallBackListener() {
            @Override
            public void onFinish(final Bitmap bitmap) {
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView1.setImageBitmap(bitmap);
                    }
                },0);
            }

            @Override
            public void onError(Exception e) {
                Log.d("haha","ERR");
            }
        });
    }

    /**
     * 用于发起网络请求，获取相应的天气图片的bitmap工具类
     */
    public static class HttpMyUtils {
         interface MyHttpCallBackListener{
            void onFinish(Bitmap bitmap);
            void onError(Exception e);
        }

        public static void sendHttpRequest(final String urls, final MyHttpCallBackListener httpCallBackListener){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection conn = null;
                    try {
                        URL picUrl = null;

                        picUrl = new URL(urls);
                        Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream());
                        httpCallBackListener.onFinish(pngBM);


                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        httpCallBackListener.onError(e);
                    } finally {
                        if (conn!=null){
                            conn.disconnect();
                        }
                    }
                }
            }).start();
        }

    }


}
