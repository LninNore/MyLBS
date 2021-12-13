package com.example.mylbs.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.mylbs.R;
import com.example.mylbs.Service.LocationService;
import com.example.mylbs.other.LocationCallback;

public class MapFragment extends Fragment implements View.OnClickListener, LocationCallback {

    //表示此次定位是否是进入了当前页面的第一次
    private boolean ifFirstLocated = true;

    //百度地图SDK
    private MapView mapView;
    private BaiduMap baiduMap;

    //暂存当前页面
    private View view;

    //获取当前位置的服务连接
    private LocationService.LocationBinder locationBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationBinder = (LocationService.LocationBinder) service;
            locationBinder.setCallback(MapFragment.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationBinder.removeCallback(MapFragment.this);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_fragment, container, false);
        initViews();
        initLocation();
        return view;
    }

    private void initViews() {
        mapView = (MapView) view.findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setIndoorEnable(true);
        Button mapTypeNormal = (Button) view.findViewById(R.id.map_type_normal);
        mapTypeNormal.setOnClickListener(this);
        Button mapTypeSatellite = (Button) view.findViewById(R.id.map_type_satellite);
        mapTypeSatellite.setOnClickListener(this);
        CheckBox mapTypeTraffic = (CheckBox) view.findViewById(R.id.map_type_traffic);
        mapTypeTraffic.setOnClickListener(this);
    }

    private void initLocation() {
        Intent startLocationService = new Intent(getActivity(), LocationService.class);
        getActivity().startService(startLocationService);
        getActivity().bindService(startLocationService, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        getActivity().unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "拒绝所有权限将无法使用", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_type_normal:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.map_type_satellite:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.map_type_traffic:
                CheckBox mapTypeTraffic = (CheckBox) v;
                if (mapTypeTraffic.isChecked()) {
                    mapTypeTraffic.setChecked(true);
                    baiduMap.setTrafficEnabled(true);
                } else {
                    mapTypeTraffic.setChecked(false);
                    baiduMap.setTrafficEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void getLocation(BDLocation newLocation) {
        MyLocationData myLocation = new MyLocationData.Builder()
                .accuracy(newLocation.getRadius())
                .direction(newLocation.getDirection())
                .latitude(newLocation.getLatitude())
                .longitude(newLocation.getLongitude())
                .build();
        baiduMap.setMyLocationData(myLocation);
        LatLng newCenter = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        if (ifFirstLocated) {
            ifFirstLocated = false;
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(newCenter));
        }
    }
}
