package com.example.mylbs.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;
import com.example.mylbs.R;
import com.example.mylbs.Service.LocationService;
import com.example.mylbs.other.LocationCallback;
import com.example.mylbs.overlay.WalkingRouteOverlay;

public class MarkActivity extends AppCompatActivity implements View.OnClickListener, LocationCallback{

    //百度地图SDK
    private MapView mapView;
    private BaiduMap baiduMap;
    private RoutePlanSearch search;

    //出发位置
    private LatLng startLatLng;
    private double latitude;
    private double longitude;
    //目的位置
    private LatLng endLatLng;

    private LocationService.LocationBinder locationBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationBinder = (LocationService.LocationBinder) service;
            locationBinder.setCallback(MarkActivity.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationBinder.removeCallback(MarkActivity.this);
        }
    };

    public static void actionStart(Context context, double latitude, double longitude){
        Intent intent = new Intent(context, MarkActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mark_layout);
        Intent extra = getIntent();
        latitude = extra.getDoubleExtra("latitude", 0.0f);
        longitude = extra.getDoubleExtra("longitude", 0.0f);
        endLatLng = new LatLng(latitude, longitude);
        initViews();
    }

    private void initViews() {
        Button plan = (Button) findViewById(R.id.plan);
        plan.setOnClickListener(this);
        Button guide = (Button) findViewById(R.id.guide);
        guide.setOnClickListener(this);
        mapView = (MapView) findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.small_map_marker);
        OverlayOptions options = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(marker);
        baiduMap.addOverlay(options);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(endLatLng));

        Intent startLocationService = new Intent(this, LocationService.class);
        startService(startLocationService);
        bindService(startLocationService, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        if(search != null) {
            search.destroy();
        }
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.plan:
                doPlan();
                break;
            case R.id.guide:
                doGuide();
                break;
            default:
                break;
        }
    }

    private void doPlan(){
        search = RoutePlanSearch.newInstance();
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
                if(walkingRouteResult.getRouteLines().size() > 0){
                    overlay.setData(walkingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                }
            }
            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }
            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }
            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }
            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }
            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        };
        search.setOnGetRoutePlanResultListener(listener);
        PlanNode st = PlanNode.withLocation(startLatLng);
        PlanNode et = PlanNode.withLocation(endLatLng);
        search.walkingSearch((new WalkingRoutePlanOption().from(st).to(et)));
    }

    private void doGuide(){
        WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                LatLng startPoint = startLatLng;
                LatLng endPoint = endLatLng;
                WalkRouteNodeInfo st = new WalkRouteNodeInfo();
                st.setLocation(startPoint);
                WalkRouteNodeInfo et = new WalkRouteNodeInfo();
                et.setLocation(endPoint);
                WalkNaviLaunchParam param = new WalkNaviLaunchParam().startNodeInfo(st).endNodeInfo(et);
                WalkNavigateHelper.getInstance().routePlanWithRouteNode(param, new IWRoutePlanListener() {
                    @Override
                    public void onRoutePlanStart() {
                        Toast.makeText(MarkActivity.this, "导航计算开始", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onRoutePlanSuccess() {
                        Intent intent = new Intent(MarkActivity.this, WalkNavigateActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                        Toast.makeText(MarkActivity.this, "导航计算失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void engineInitFail() {
                Toast.makeText(MarkActivity.this, "导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getLocation(BDLocation newLocation) {
        startLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
    }
}
