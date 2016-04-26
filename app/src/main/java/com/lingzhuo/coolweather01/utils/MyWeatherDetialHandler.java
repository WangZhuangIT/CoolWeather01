package com.lingzhuo.coolweather01.utils;

import android.util.Log;

import com.lingzhuo.coolweather01.bean.County;
import com.lingzhuo.coolweather01.bean.CountyWeather;
import com.lingzhuo.coolweather01.db.CoolWeatherDB;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SAX解析xml方式
 * 用于解析返回的天气信息的工具类
 * 同时将解析的天气信息保存到数据库中
 */
public class MyWeatherDetialHandler extends DefaultHandler{
	private CoolWeatherDB coolWeatherDB;
	private String tag;
	private CountyWeather countyWeather;

	public MyWeatherDetialHandler(CoolWeatherDB coolWeatherDB) {
		this.coolWeatherDB = coolWeatherDB;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		String values=new String(ch,start,length);
		if ("days".equals(tag)) {
			countyWeather.setDays(values);
		}else if ("week".equals(tag)){
			countyWeather.setWeek(values);
		}else if ("citynm".equals(tag)){
			countyWeather.setCitynm(values);
		}else if ("temp_low".equals(tag)){
			countyWeather.setTemp_low(values);
		}else if ("temp_high".equals(tag)){
			countyWeather.setTemp_high(values);
		}else if ("weather".equals(tag)){
			countyWeather.setWeather(values);
		}else if ("weather_icon".equals(tag)){
			countyWeather.setWeather_icon(values);
		}else if ("weather_icon1".equals(tag)){
			countyWeather.setWeather_icon1(values);
		}else if ("wind".equals(tag)){
			countyWeather.setWind(values);
		}else if ("winp".equals(tag)){
			countyWeather.setWinp(values);
			countyWeather.setUpdate_time(formatData());
		}else if ("cityid".equals(tag)){
			countyWeather.setCityid(values);
		}
	}

	public String formatData(){
		SimpleDateFormat format=new SimpleDateFormat("MM月dd日 hh:mm");
		return format.format(System.currentTimeMillis());
	}



	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (qName.contains("item_")){
			coolWeatherDB.saveCountyWeather(countyWeather);
		}
		tag="";
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.contains("item")){
			countyWeather=new CountyWeather();
		}
		tag=qName;
	}
	

}
