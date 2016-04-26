package com.lingzhuo.coolweather01.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.lingzhuo.coolweather01.R;
import com.lingzhuo.coolweather01.bean.CountyWeather;
import com.lingzhuo.coolweather01.db.CoolWeatherDB;
import com.lingzhuo.coolweather01.fragment.TodayFragment;
import com.lingzhuo.coolweather01.service.AutoUpdateService;
import com.lingzhuo.coolweather01.utils.HttpUtils;
import com.lingzhuo.coolweather01.utils.MyWeatherDetialHandler;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 开启软件的闪屏的活动
 * 活动的逻辑是每当我开启软件，我就去获取数据库中的天气信息，若获取的天气信息的list为空，我就进入城市选择界面，反之，我就直接进入天气展示界面
 * Created by Wang on 2016/4/21.
 */
public class StartFlashActivity extends BaseActivity {

    private CoolWeatherDB coolWeatherDB;
    private List<CountyWeather> listWeather;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_startflash);
        coolWeatherDB = CoolWeatherDB.newInstance(getApplicationContext());
        listWeather = coolWeatherDB.loadCountyWeather();
        HttpStartUtils.sendHttpRequest(new HttpStartUtils.HttpCallBackListener() {
            @Override
            public void onFinish() {
                if (coolWeatherDB.loadCountyWeather().size() == 0) {
                    Intent intent = new Intent(getApplicationContext(), ChooseAreaActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setAction("FIRST_START");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(StartFlashActivity.this, "程序初始化出错，请重新运行！！！", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 下面这个工具类，纯属是我想给闪屏显示展示的时间，很撇脚，不加的话，会因程序运行很快，来不及展示，就进入下一个活动了
     */
    public static class HttpStartUtils {
        public interface HttpCallBackListener {
            void onFinish();

            void onError(Exception e);
        }

        public static void sendHttpRequest(final HttpCallBackListener httpCallBackListener) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (Exception e) {
                        httpCallBackListener.onError(e);
                    }
                    httpCallBackListener.onFinish();

                }
            }).start();
        }

    }


}
