package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import receiver.AutoUpdateReceiver;
import util.HttpCallBackListener;
import util.HttpUtil;
import util.Utility;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long pauseHour=8*60*60*1000;//这是8个小时的毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+pauseHour;
        Intent i=new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String cityId=prefs.getString("weather_code", "");
        String address="https://api.heweather.com/x3/weather?cityid="+cityId+"&key=3e43d005a4e74218befc786e34308654";
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
