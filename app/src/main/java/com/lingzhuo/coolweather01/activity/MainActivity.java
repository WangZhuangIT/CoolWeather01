package com.lingzhuo.coolweather01.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lingzhuo.coolweather01.R;
import com.lingzhuo.coolweather01.adapter.MyPagerAdapter;
import com.lingzhuo.coolweather01.bean.CountyWeather;
import com.lingzhuo.coolweather01.db.CoolWeatherDB;
import com.lingzhuo.coolweather01.fragment.TodayFragment;
import com.lingzhuo.coolweather01.fragment.WeekFragment;
import com.lingzhuo.coolweather01.rerceiver.AutoUpdateReceiver;
import com.lingzhuo.coolweather01.service.AutoUpdateService;
import com.lingzhuo.coolweather01.utils.HttpUtils;
import com.lingzhuo.coolweather01.utils.MyWeatherDetialHandler;
import com.lingzhuo.coolweather01.view.MytitleView;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private MytitleView myTitleView;
    private RelativeLayout relativeLayout_nowPosition;
    private TextView textView_update_time;
    private CoolWeatherDB coolWeatherDB;
    private ViewPager viewPager;
    private TextView textView_nowPosition;
    private List<CountyWeather> listWeather;
    private List<Fragment> dataList;
    private long lastClickTime;
    private ImageView imageView_home, imageView_update;

    private LinearLayout linearLayout_today, linearLayout_week;
    private ImageView imageView_today, imageView_week;
    private TextView textView_today, textView_week;

    private MyPagerAdapter adapter;

    private MyMainReceiver myMainReceiver;
    private IntentFilter myMainFilter;

    private AutoUpdateReceiver autoUpdateReceiver;
    private IntentFilter autoUpdateFilter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, AutoUpdateService.class));
        init();
        initListener();
        registerReceiver(myMainReceiver, myMainFilter);
        registerReceiver(autoUpdateReceiver, autoUpdateFilter);
        textView_nowPosition.setText(listWeather.get(0).getCitynm());
        adapter = new MyPagerAdapter(getSupportFragmentManager(), dataList);
        textView_update_time.setText("最后更新时间:" + listWeather.get(0).getUpdate_time());
        viewPager.setAdapter(adapter);
    }

    private void init() {
        coolWeatherDB = CoolWeatherDB.newInstance(getApplicationContext());
        myTitleView = (MytitleView) findViewById(R.id.myTitleView);
        relativeLayout_nowPosition = (RelativeLayout) myTitleView.findViewById(R.id.relativelayout_nowPosition);
        textView_update_time = (TextView) myTitleView.findViewById(R.id.textView_update_time);
        imageView_home = (ImageView) myTitleView.findViewById(R.id.imageView_home);
        imageView_update = (ImageView) myTitleView.findViewById(R.id.imageView_update);
        listWeather = coolWeatherDB.loadCountyWeather();
        textView_nowPosition = (TextView) myTitleView.findViewById(R.id.textView_nowPosition);
        dataList = new ArrayList<>();
        dataList.add(new TodayFragment(listWeather.get(0), myHandler));
        dataList.add(new WeekFragment(listWeather, myHandler));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        linearLayout_today = (LinearLayout) findViewById(R.id.linearLayout_today);
        linearLayout_week = (LinearLayout) findViewById(R.id.linearLayout_week);
        imageView_today = (ImageView) findViewById(R.id.imageView_today);
        imageView_week = (ImageView) findViewById(R.id.imageView_week);
        textView_today = (TextView) findViewById(R.id.textView_today);
        textView_week = (TextView) findViewById(R.id.textView_week);
        myMainReceiver = new MyMainReceiver();
        myMainFilter = new IntentFilter();
        myMainFilter.addAction("UPDATE_MAIN");
        autoUpdateReceiver = new AutoUpdateReceiver();
        autoUpdateFilter = new IntentFilter();
        autoUpdateFilter.addAction("START_AlARM");
    }

    private void initListener() {
        relativeLayout_nowPosition.setOnClickListener(this);
        imageView_home.setOnClickListener(this);
        imageView_update.setOnClickListener(this);
        linearLayout_today.setOnClickListener(this);
        linearLayout_week.setOnClickListener(this);
        viewPager.setOnPageChangeListener(this);
    }

    /**
     * 接收到天气更新的消息，同时更新UI界面上的各种信息
     */
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dataList.clear();
            listWeather = coolWeatherDB.loadCountyWeather();
            dataList.add(new TodayFragment(listWeather.get(0), myHandler));
            dataList.add(new WeekFragment(listWeather, myHandler));
            adapter.notifyDataSetChanged();
            viewPager.setAdapter(adapter);
            textView_update_time.setText("最后更新时间:" + formatData());
            Toast.makeText(MainActivity.this, "同步天气成功", Toast.LENGTH_SHORT).show();
            sendBroadcast(new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
        }
    };

    public String formatData() {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 hh:mm");
        return format.format(System.currentTimeMillis());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_home:
            case R.id.relativelayout_nowPosition:
                Intent intent = new Intent(MainActivity.this, ChooseAreaActivity.class);
                intent.putExtra("IS_FROM_MAIN", true);
                startActivity(intent);
                break;
            case R.id.linearLayout_today:
                moveToToday();
                viewPager.setCurrentItem(0);
                break;
            case R.id.linearLayout_week:
                moveToWeek();
                viewPager.setCurrentItem(1);
                break;
            case R.id.imageView_update:
                updateWeather();
                break;
        }
    }

    /**
     * 用来更新天气信息的方法
     * 更新天气信息后，发送Message，提醒UI进行及时更新
     */
    private void updateWeather() {
        Toast.makeText(MainActivity.this, "正在同步天气，请稍等...", Toast.LENGTH_SHORT).show();
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
                        Message msg = new Message();
                        myHandler.sendMessage(msg);

                    } catch (Exception e) {
                        onError(e);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "请检查手机网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     *监听ViewPager，使ViewPager和下方的标识当前页面的ImageView联动
     */
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position) {
            case 0:
                moveToToday();
                break;
            case 1:
                moveToWeek();
                break;
        }
    }

    private void moveToWeek() {
        imageView_today.setImageResource(R.mipmap.buttom_not_pic);
        textView_today.setTextColor(Color.rgb(164, 164, 164));
        imageView_week.setImageResource(R.mipmap.buttom__pic);
        textView_week.setTextColor(Color.rgb(57, 171, 255));
    }

    private void moveToToday() {
        imageView_today.setImageResource(R.mipmap.buttom__pic);
        textView_today.setTextColor(Color.rgb(57, 171, 255));
        imageView_week.setImageResource(R.mipmap.buttom_not_pic);
        textView_week.setTextColor(Color.rgb(164, 164, 164));
    }

    /**
     * 接收天气更新的广播，更新天气信息
     */
    public class MyMainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateWeather();
        }
    }


    public void onPageSelected(int position) {
    }

    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onBackPressed() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - lastClickTime < 1000) {
            moveTaskToBack(true);
        } else {
            lastClickTime = nowTime;
            Toast.makeText(MainActivity.this, "再按一次返回键回到桌面", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myMainReceiver);
        unregisterReceiver(autoUpdateReceiver);
    }
}
