package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import db.JasonWeatherDB;
import model.City;

/**
 * Created by Jason on 2016/3/13.
 */
public class Utility {

    private static int length=800;
    private static List<City> cityList1=new ArrayList<City>();
    private static List<City> cityList2=new ArrayList<City>();
    private static List<City> cityList3=new ArrayList<City>();
    private static int List1=0;
    private static int List2=length-1;
    private static int List3=length*2-1;


    /*
    * 解析和处理服务器返回的市级数据
    * */
    public static List<City> handleCitiesResponse(String response){
        List<City> cities=new ArrayList<City>();
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("city_info");
            int i;
            for(i=0;i<jsonArray.length();i++){
                JSONObject info= (JSONObject) jsonArray.get(i);
                City city=new City();
                city.setCityName(info.getString("city"));
                city.setCityId(info.getString("id"));
                cities.add(city);
            }
            return cities;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cities;
    }
    /*
    * 根据cityName得到cityId
    * */
    public static City queryCityId(final JasonWeatherDB jasonWeatherDB,String cityName){
        City city=jasonWeatherDB.queryCityId(cityName);
        if(city!=null){
            return city;
        }
        return null;
    }

    /*
    * 解析服务器返回的JSON数据，并将解析出来的数据存储到本地
    * */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray weatherInfo=jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject totalInfo= (JSONObject) weatherInfo.get(0);
            JSONArray daily_forecast= (JSONArray) totalInfo.get("daily_forecast");
            JSONObject daily_forecast0=daily_forecast.getJSONObject(0);
            JSONObject basic=totalInfo.getJSONObject("basic");
            JSONObject update=basic.getJSONObject("update");
            JSONObject tmp=daily_forecast0.getJSONObject("tmp");
            String temp1=tmp.getString("max");
            String temp2=tmp.getString("min");
            JSONObject cond=daily_forecast0.getJSONObject("cond");
            String weatherDesp=cond.getString("txt_d");
            String publishTime=update.getString("loc");
            String cityName=basic.getString("city");
            String cityId=basic.getString("id");

            saveWeatherInfo(context,cityName,cityId,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /*weatherCode=cityId
    * 将服务器返回的所有天气信息存储到SharedPreference文件中
    * */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CANADA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
