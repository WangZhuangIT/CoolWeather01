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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lingzhuo.coolweather01.R;
import com.lingzhuo.coolweather01.bean.CountyWeather;
import com.lingzhuo.coolweather01.view.TempTrendView;

import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于展示一周天气的Fragment
 * Created by Wang on 2016/4/21.
 */
public class WeekFragment extends Fragment {

    private List<CountyWeather> listWeather;
    private Handler myHandler;

    public WeekFragment(List<CountyWeather> listWeather, Handler myHandler) {
        this.listWeather = listWeather;
        this.myHandler = myHandler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_week_weather,null);

        int[] weather_ic1={R.id.week1_icon1,R.id.week2_icon1,R.id.week3_icon1,R.id.week4_icon1,R.id.week5_icon1,R.id.week6_icon1,R.id.week7_icon1};
        int[] weather_ic2={R.id.week1_icon2,R.id.week2_icon2,R.id.week3_icon2,R.id.week4_icon2,R.id.week5_icon2,R.id.week6_icon2,R.id.week7_icon2};
        int[] week_weather={R.id.week1_weather,R.id.week2_weather,R.id.week3_weather,R.id.week4_weather,R.id.week5_weather,R.id.week6_weather,R.id.week7_weather};
        int[] week_temp={R.id.week1_temp,R.id.week2_temp,R.id.week3_temp,R.id.week4_temp,R.id.week5_temp,R.id.week6_temp,R.id.week7_temp};
        int[] week_wind={R.id.week1_wind,R.id.week2_wind,R.id.week3_wind,R.id.week4_wind,R.id.week5_wind,R.id.week6_wind,R.id.week7_wind};
        int[] week_date={R.id.week1_date,R.id.week2_date,R.id.week3_date,R.id.week4_date,R.id.week5_date,R.id.week6_date,R.id.week7_date};
        int[] week_week={R.id.week4_week,R.id.week5_week,R.id.week6_week,R.id.week7_week};

        for (int i=0;i<listWeather.size();i++){

            if (i>=3){
                TextView textView_week= (TextView) view.findViewById(week_week[i-3]);
                textView_week.setText(listWeather.get(i).getWeek());
            }

            ImageView imageView1= (ImageView) view.findViewById(weather_ic1[i]);
            getBitmap(imageView1,listWeather.get(i).getWeather_icon());
            ImageView imageView2= (ImageView) view.findViewById(weather_ic2[i]);
            getBitmap(imageView2,listWeather.get(i).getWeather_icon1());
            TextView textView_weather= (TextView) view.findViewById(week_weather[i]);
            textView_weather.setText(listWeather.get(i).getWeather());
            TextView textView_temp= (TextView) view.findViewById(week_temp[i]);
            textView_temp.setText(listWeather.get(i).getTemp_low()+"/"+listWeather.get(i).getTemp_high()+"℃");
            TextView textView_wind= (TextView) view.findViewById(week_wind[i]);
            textView_wind.setText(listWeather.get(i).getWind());
            TextView textView_date= (TextView) view.findViewById(week_date[i]);
            textView_date.setText(listWeather.get(i).getDays().substring(5,10));

            TempTrendView tempTrendView;
            tempTrendView= (TempTrendView) view.findViewById(R.id.line);
            List<Integer> listTemp=new ArrayList<>();
            List<Integer> listLowTemp=new ArrayList<>();
            for (int j=0;j<listWeather.size();j++){
                int avg=(Integer.parseInt(listWeather.get(j).getTemp_high())+Integer.parseInt(listWeather.get(j).getTemp_low()))/2;
                listTemp.add(Integer.parseInt(listWeather.get(j).getTemp_high()));
                listLowTemp.add(Integer.parseInt(listWeather.get(j).getTemp_low()));
            }
            tempTrendView.setHighTemp((ArrayList<Integer>) listTemp);
            tempTrendView.setLowTemp((ArrayList<Integer>) listLowTemp);

        }

        LinearLayout layout_7= (LinearLayout) view.findViewById(R.id.layout_7);
        if (listWeather.size()<7){
            layout_7.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     *用于根据天气图片的网址，直接加载图片显示在页面上的方法
     */
    private void getBitmap(final ImageView imageView, String weather_icon) {
        HttpMyUtils.sendHttpRequest(weather_icon, new HttpMyUtils.MyHttpCallBackListener() {
            @Override
            public void onFinish(final Bitmap bitmap) {
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
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
