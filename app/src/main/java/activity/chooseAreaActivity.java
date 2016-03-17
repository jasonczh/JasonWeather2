package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.jasonweather2.R;

import java.util.ArrayList;
import java.util.List;

import db.JasonWeatherDB;
import model.City;
import model.EditTextWithDel;
import util.HttpCallBackListener;
import util.HttpUtil;
import util.Utility;

/**
 * Created by Jason on 2016/3/13.
 */
public class chooseAreaActivity extends Activity {

    public static final int LEVEL_CITY=1;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private JasonWeatherDB jasonWeatherDB;
    private List<String> dataList=new ArrayList<String>();
    private EditTextWithDel editSearch;
    /*
    * editSearch的点击事件
    * */
    private MotionEvent event;
    /*
    * 记录是否有进行过服务器数据查询
    * */
    private Boolean flag=false;
    /*
   * 市列表
   * */
    private List<City> cityList;
    /*
    * 选中的城市
    * */
    private City selectCity;

    /*
   * 选择的级别
   * */
    private int currentLevel;

    /*
    * 是否从WeatherActivity中跳转过来
    * */
    private boolean isFromWeatherActivity;
    private long currentTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected",false) && !isFromWeatherActivity){
            Intent intent=new Intent(chooseAreaActivity.this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView= (ListView) findViewById(R.id.list_view);
        editSearch= (EditTextWithDel) findViewById(R.id.edit_search);
        titleText= (TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        jasonWeatherDB=JasonWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity = cityList.get(position);
                /*
                * 调用新的Activity 传递 点击的cityId
                * */
                Intent intent=new Intent(chooseAreaActivity.this,WeatherActivity.class);
                intent.putExtra("city_id", selectCity.getCityId());
                startActivity(intent);
                finish();
            }
        });
        queryCities();
        editSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    int eventX= (int) event.getRawX();
                    int eventY= (int) event.getRawY();
                    Rect rect=new Rect();
                    v.getGlobalVisibleRect(rect);
                    rect.left=rect.right-80;
                    System.out.printf("globalRect:" + rect);
                    if(rect.contains(eventX,eventY))
                    {
                        /*
                        * 调用jasonWeatherDb根据输入的editText查询cityId
                        * 并且根据返回的cityId 将值放入intent中 开启 新的Activity显示天气情况
                        * */
                        selectCity=jasonWeatherDB.queryCityId(editSearch.getText().toString());
                        //要判断selectCity是否为null
                        Intent intent=new Intent(chooseAreaActivity.this,WeatherActivity.class);
                        intent.putExtra("city_id",selectCity.getCityId());
                        startActivity(intent);
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });


    }
    private void queryCities(){
        cityList=jasonWeatherDB.loadCities();
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
        }else{
            queryFromServer();
        }
    }

    /*
   * g根据传入的代号和类型从服务器上查询省县市数据
   * */
    private void queryFromServer(){
        String address="";
        address="https://api.heweather.com/x3/citylist?search=allchina&key=3e43d005a4e74218befc786e34308654";
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = true;
                cityList = Utility.handleCitiesResponse(response);
                jasonWeatherDB.saveCityList(cityList);
                if (result) {
                    //通过runOnUiThread方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            showList();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(chooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /*
    * 展现cityList
    * */
    private void showList(){
        if(cityList.size()>0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
        }
    }
    /*
    * 显示进度对话框
    * */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
    * 关闭进度对话框
    * */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /*
    * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
    * */

    @Override
    public void onBackPressed() {
        if(isFromWeatherActivity){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
        }
        else if (System.currentTimeMillis() - currentTime > 2000) {
            currentTime = System.currentTimeMillis();
            Toast.makeText(chooseAreaActivity.this, "再按一次Back键就退出程序", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }

    }
}
