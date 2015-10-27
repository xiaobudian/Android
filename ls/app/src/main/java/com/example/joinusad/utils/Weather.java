package com.example.joinusad.utils;

import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/10/26.
 */
public class Weather{
    private String fileName = "weatherinfo.txt";
    private String encoding = "utf-8";
    private File extDir = Environment.getExternalStorageDirectory();

    private String city;
    private String date;
    private String day;
    private String weather;
    private String quality;
    private String pm25;
    private String pm10;
    private String aqi;
    private String desc;
    private Date update_date;

    private void setaqi(String _aqi){
        aqi = _aqi;
        if (Integer.parseInt(aqi) > 100){
            desc = "今天空气质量糟透了";
        }else{
            desc = "今天空气质量太好了";
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(city+"：\n");
        sb.append(date+"：\n");
        sb.append(day+"：\n");
        sb.append("天气：");
        sb.append(weather+"：\n");
        sb.append("空气质量：");
        sb.append(quality+"：\n");
        sb.append("AQI:");
        sb.append(aqi+"：\n");
        sb.append("PM二点五：");
        sb.append(pm25+"：\n");
        sb.append("PM十：");
        sb.append(pm10+"：\n");
        sb.append(desc+"：\n");
        String result = sb.toString();
        try {
            File file = new File(extDir.getPath() + "/" + fileName);
            FileOutputStream fout = new FileOutputStream(file, false);
            byte[] bytes = result.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setData(String weatherInfo) throws Exception {

        JSONObject obj = new JSONObject(weatherInfo);
        city = obj.getString("city_name");

        JSONObject today = obj.getJSONArray("future").getJSONObject(0);
        date = today.getString("date");
        day = today.getString("day");
        JSONObject now = obj.getJSONObject("now");
        weather = now.getString("text");
        JSONObject air_quality = now.getJSONObject("air_quality");
        JSONObject city = air_quality.getJSONObject("city");

        setaqi(city.getString("aqi"));

        quality = city.getString("quality");
        pm25 = city.getString("pm25");
        pm10 = city.getString("pm10");
    }

    public  boolean expired(String weatherInfo) throws Exception{
        if(weatherInfo.length() == 0)
            return true;
        JSONObject obj = new JSONObject(weatherInfo);
        String last_update = obj.getString("last_update");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date date = sdf.parse(last_update);
        Date now = new Date();
        try {
            if (date.getYear() != now.getYear())
                return true;
            if (date.getMonth() != now.getMonth())
                return true;
            if (date.getDay() != now.getDay())
                return true;
            if (date.getHours() != now.getHours())
                return true;
            if (date.getMinutes() > 30 && now.getMinutes() < 30)
                return true;
            if (date.getMinutes() < 30 && now.getMinutes() > 30)
                return true;
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
