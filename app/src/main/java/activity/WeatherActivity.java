package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jason.jasonweather2.R;

import service.AutoUpdateService;
import util.HttpCallBackListener;
import util.HttpUtil;
import util.Utility;

/**
 * Created by Jason on 2016/3/14.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    /*
    * 用于显示城市名
    * */
    private TextView cityNameText;
    /*
    * 用于显示发表时间
    * */
    private TextView publishText;
    /*
    * 用于显示天气描述信息
    * */
    private TextView weatherDespText;
    /*
    * 用于显示气温1
    * */
    private TextView temp1Text;
    /*
    * 用于显示气温2
    * */
    private TextView temp2Text;
    /*
    * 用于显示当前日期
    * */
    private TextView currentDateText;
    /*
    * 切换城市的按钮
    * */

    private Button switchCity;
    /*
    * 更新天气按钮
    * */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //初始化各控件
        bindView();
        String cityId=getIntent().getStringExtra("city_id");
        if(!TextUtils.isEmpty(cityId)){
            //有县城代码时就去查天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.GONE);
            queryWeatherCode(cityId);
        }
        else{
            //没有县城代码就直接显示本地天气
            showWeather();
        }
    }

    /*
    * 查询县级代号对应的天气代号
    * */
    private void queryWeatherCode(String cityId){
        String address="https://api.heweather.com/x3/weather?cityid="+cityId+"&key=3e43d005a4e74218befc786e34308654";
        queryFromServer(address);
    }

    /*
    * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
    * */
    private void queryFromServer(final String address){
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if(!TextUtils.isEmpty(response)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /*
    * 从SharedPreference文件中读取存储的天气信息，并显示到界面上
    * */
    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1","")+"℃");
        temp2Text.setText(prefs.getString("temp2","")+"℃");
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天"+prefs.getString("publish_time",""));
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void bindView(){
        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText= (TextView) findViewById(R.id.city_name);
        publishText= (TextView) findViewById(R.id.publish_text);
        weatherDespText= (TextView) findViewById(R.id.weather_desp);
        temp1Text= (TextView) findViewById(R.id.temp1);
        temp2Text= (TextView) findViewById(R.id.temp2);
        currentDateText= (TextView) findViewById(R.id.current_date);
        switchCity= (Button) findViewById(R.id.switch_city);
        refreshWeather= (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent=new Intent(this,chooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
                String cityId=prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(cityId)){
                    queryWeatherCode(cityId);
                }
                break;
            default:
                break;
        }
    }
}
