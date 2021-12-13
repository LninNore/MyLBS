package com.example.mylbs.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.mylbs.other.LocationCallback;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {

    private LocationClient locationClient;

    private List<LocationCallback> callbackList = new ArrayList<>();

    //百度地图SDK
    private class LocationListenerService extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation != null && callbackList.size() > 0) {
                for(LocationCallback callback : callbackList){
                    callback.getLocation(bdLocation);
                }
            }else{
                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setCoorType("bd0911");
        locationClient = new LocationClient(LocationService.this);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new LocationListenerService());
        locationClient.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        stopSelf();
    }

    public LocationService() {
    }

    private LocationBinder binder = new LocationBinder();

    public class LocationBinder extends Binder{
        public void setCallback(LocationCallback callback){
            LocationService.this.callbackList.add(callback);
        }
        public void removeCallback(LocationCallback callback){
            LocationService.this.callbackList.remove(callback);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}