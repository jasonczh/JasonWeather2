package db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import model.City;

/**
 * Created by Jason on 2016/3/13.
 */
public class JasonWeatherDB {
    /*
    * 数据库名
    * */
    public static final String DB_NAME="jason_weather";

    /*
    * 数据库版本
    * */
    private  static final int VERSION=1;

    private static JasonWeatherDB jasonWeatherDB;
    private SQLiteDatabase db;

    /*
    * 将构造方法私有化  单例设计模式  懒汉式
    * @param context 对应的上下文
    * */
    private JasonWeatherDB(Context context){
        JasonWeatherOpenHelper dbOpenHelper=new JasonWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbOpenHelper.getWritableDatabase();//获得读写实例
    }

    public synchronized static JasonWeatherDB getInstance(Context context){
        if(jasonWeatherDB==null){
            jasonWeatherDB=new JasonWeatherDB(context);
        }
        return jasonWeatherDB;
    }


    /*
    * 将City实例存储到数据库
    * */
    public void saveCity(City city){
        if(city!=null){
            db.execSQL("insert into City(city_name,city_id) values(?,?)",
                    new Object[]{city.getCityName(),city.getCityId()});
        }
    }
    /*
   * 将数据CityList存储到数据库中
   * */
    public void  saveCityList(final List<City> cityList){
        if(cityList.size()>0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(City city:cityList){
                        db.execSQL("insert into City(city_name,city_id) values(?,?)",
                                new Object[]{city.getCityName(),city.getCityId()});
                    }
                }
            }).start();
        }
    }

    /*
    * 从数据库读取某省的所有城市信息
    * */
    public List<City> loadCities(){
        List<City> cities=new ArrayList<City>();
        Cursor cursor=db.query("City",null,null,null,null,null,null);
        /*
        * 判断是否为空
        * */
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
                cities.add(city);
            }
            while(cursor.moveToNext());
        }
        return cities;
    }

    /*
    * 查询City名字返回CityId
    * */
    public City queryCityId(String cityName){
        if(cityName.length()>0){
            Cursor cursor=db.rawQuery("select city_id from City where city_name=?",new String[]{cityName});
            if (cursor.moveToFirst()){
                City city=new City();
                city.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
                city.setCityName(cityName);
                return city;
            }
        }
        return null;
    }

}
