package com.lingzhuo.coolweather01.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lingzhuo.coolweather01.R;
import com.lingzhuo.coolweather01.adapter.MyListAdapter;
import com.lingzhuo.coolweather01.bean.City;
import com.lingzhuo.coolweather01.bean.County;
import com.lingzhuo.coolweather01.bean.Province;
import com.lingzhuo.coolweather01.db.CoolWeatherDB;
import com.lingzhuo.coolweather01.utils.HandleResponceUtils;
import com.lingzhuo.coolweather01.utils.HttpUtils;
import com.lingzhuo.coolweather01.utils.MyWeatherDetialHandler;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 选择城市的活动
 * Created by Wang on 2016/4/19.
 */
public class ChooseAreaActivity extends BaseActivity implements View.OnClickListener {
    private EditText editText_search, editText_in;
    private Button button_search, button_in;

    private CoolWeatherDB coolWeatherDB;
    private RelativeLayout relativeLayout;
    private Dialog dialog;
    private Button button_cityList;
    private long lastClickTime;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private List<County> countyAllList;
    private List<String> dataListProvince = new ArrayList<String>();
    private List<String> dataListCity = new ArrayList<String>();
    private List<String> dataListCounty = new ArrayList<String>();
    private List<String> dataListAllCounty = new ArrayList<String>();
    private MyListAdapter adapterProvince;
    private MyListAdapter adapterCity;
    private MyListAdapter adapterCounty;
    private ListView listView_province, listView_city, listView_county;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_choose_area);
        //让键盘不自动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        coolWeatherDB = CoolWeatherDB.newInstance(getApplicationContext());
        init();
        listView_province.setAdapter(adapterProvince);
        listView_city.setAdapter(adapterCity);
        listView_county.setAdapter(adapterCounty);
        initListener();

        queryProvinces();
    }


    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataListProvince.clear();
            for (Province province : provinceList) {
                dataListProvince.add(province.getProvince_name());
            }
            adapterProvince.notifyDataSetChanged();
            listView_province.setSelection(0);
        } else {
            queryFromServer(null, "province", 0);
        }
    }


    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities(int position) {
        cityList = coolWeatherDB.loadCities(provinceList.get(position).getId());
        dataListCounty.clear();
        adapterCounty.notifyDataSetChanged();
        if (cityList.size() > 0) {
            dataListCity.clear();
            for (City city : cityList) {
                dataListCity.add(city.getCity_name());
            }
            adapterCity.notifyDataSetChanged();
            listView_city.setSelection(0);
        } else {
            queryFromServer(provinceList.get(position).getProvince_code(), "city", position);
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties(int position) {
        countyList = coolWeatherDB.loadCounties(cityList.get(position).getId());
        if (countyList.size() > 0) {
            dataListCounty.clear();
            for (County county : countyList) {
                dataListCounty.add(county.getCounty_name());
            }
            adapterCounty.notifyDataSetChanged();
            listView_county.setSelection(0);
        } else {
            queryFromServer(cityList.get(position).getCity_code(), "county", position);
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(final String code, final String type, final int position) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
        HttpUtils.sendHttpRequest(address, new HttpUtils.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = HandleResponceUtils.handleProvincesResponse(coolWeatherDB,
                            response);
                } else if ("city".equals(type)) {
                    result = HandleResponceUtils.handleCitiesResponse(coolWeatherDB,
                            response, provinceList.get(position).getId());
                } else if ("county".equals(type)) {
                    result = HandleResponceUtils.handleCountiesResponse(coolWeatherDB,
                            response, cityList.get(position).getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities(position);
                            } else if ("county".equals(type)) {
                                queryCounties(position);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(ChooseAreaActivity.this, "请检查手机网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void initListener() {
        button_search.setOnClickListener(this);
        button_cityList.setOnClickListener(this);
        button_in.setOnClickListener(this);

        //设置列表的点击事件
        listView_province.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                queryCities(position);
            }
        });

        listView_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                queryCounties(position);
            }
        });

        listView_county.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String county_code = countyList.get(position).getCounty_code();
                findCountyId(county_code);
            }
        });

    }

    /**
     * 当县级的列表被点击或者直接通过搜索栏查询县级城市天气的时候调用此方法
     * 通过相应的县的code查询相应的县的代号，比如北京就是101010100
     * 如果查询城市的代号成功，就根据代号查询城市信息
     *
     * @param county_code
     */
    private void findCountyId(String county_code) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        String address = "http://www.weather.com.cn/data/list3/city" + county_code + ".xml";
        HttpUtils.sendHttpRequest(address, new HttpUtils.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                queryCityWeather(response.substring(response.indexOf("|") + 1, response.length()));
            }


            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(ChooseAreaActivity.this, "请检查手机网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 查询城市代号成功后执行此方法，通过城市代号，我们去查询近期的天气信息，同时处理返回数据，保存到数据库相应的表中
     *
     * @param substring
     */
    private void queryCityWeather(String substring) {
        String urls = "http://api.k780.com:88/?app=weather.future&weaid=" + substring + "&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=xml";
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
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        ActivityCollector.finishAll();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        sendBroadcast(new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
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
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(ChooseAreaActivity.this, "请检查手机网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void init() {
        button_search = (Button) findViewById(R.id.button_search);
        button_in = (Button) findViewById(R.id.button_in);
        button_cityList = (Button) findViewById(R.id.button_cityList);
        editText_search = (EditText) findViewById(R.id.editText_search);
        editText_in = (EditText) findViewById(R.id.editText_in);
        relativeLayout = (RelativeLayout) findViewById(R.id.bg_layout);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        listView_province = (ListView) findViewById(R.id.listView_province);
        listView_city = (ListView) findViewById(R.id.listView_city);
        listView_county = (ListView) findViewById(R.id.listView_county);
        adapterProvince = new MyListAdapter(dataListProvince, getApplicationContext());
        adapterCity = new MyListAdapter(dataListCity, getApplicationContext());
        adapterCounty = new MyListAdapter(dataListCounty, getApplicationContext());
    }


    /**
     * 查询逻辑的实现
     * 获取所有的县级城市的list，判断是否有你所查询的城市
     * 如果查询到，直接调用更具县级城市名获取城市代号，根据代号查询天气等一系列操作
     * 反之，吐司提示用户未查询到
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                dataListAllCounty.clear();
                countyAllList = coolWeatherDB.loadAllCounties();
                if (countyAllList.size() > 0) {
                    for (County county : countyAllList) {
                        dataListAllCounty.add(county.getCounty_name());
                    }
                    if (dataListAllCounty.contains(editText_search.getText().toString())) {
                        editText_in.setText(editText_search.getText().toString());
                    } else {
                        Toast.makeText(ChooseAreaActivity.this, "未查询到相关城市信息", Toast.LENGTH_SHORT).show();
                        editText_search.setText("");
                    }
                } else {
                    Toast.makeText(ChooseAreaActivity.this, "未查询到相关城市信息", Toast.LENGTH_SHORT).show();
                    editText_search.setText("");
                }
                break;
            case R.id.button_cityList:
                //更新数据库的功能
                Toast.makeText(ChooseAreaActivity.this, "城市列表数据已更新", Toast.LENGTH_SHORT).show();
                coolWeatherDB.deleteProvince();
                coolWeatherDB.deleteCity();
                coolWeatherDB.deleteCounty();
                dataListProvince.clear();
                dataListCity.clear();
                dataListCounty.clear();
                adapterProvince.notifyDataSetChanged();
                adapterCity.notifyDataSetChanged();
                adapterCounty.notifyDataSetChanged();
                queryProvinces();
                break;
            case R.id.button_in:
                if (TextUtils.isEmpty(editText_in.getText())) {
                    Toast.makeText(ChooseAreaActivity.this, "请先搜索定位城市", Toast.LENGTH_SHORT).show();
                } else {
                    int index = dataListAllCounty.indexOf(editText_in.getText().toString());
                    String county_code = countyAllList.get(index).getCounty_code();
                    findCountyId(county_code);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - lastClickTime < 1000) {
            moveTaskToBack(true);
        } else {
            lastClickTime = nowTime;
            Toast.makeText(ChooseAreaActivity.this, "再按一次返回键回到桌面", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseImageViews();
    }

    //由于界面包含了一张大图片，所以每次活动销毁，都必须要释放内存，如果不这样做，多次访问页面，就会报错
    private void releaseImageViews() {
        releaseImageView(relativeLayout);
    }

    private void releaseImageView(RelativeLayout relativeLayout) {
        Drawable d = relativeLayout.getBackground();
        if (d != null)
            d.setCallback(null);
        relativeLayout.setBackground(null);
        relativeLayout.setBackgroundDrawable(null);
    }

}
