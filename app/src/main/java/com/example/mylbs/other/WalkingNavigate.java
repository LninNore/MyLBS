package com.example.mylbs.other;

import com.baidu.mapapi.map.BaiduMap;
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
import com.example.mylbs.overlay.WalkingRouteOverlay;

public class WalkingNavigate {

    public static RoutePlanSearch search;

    public static void navigate(BaiduMap baiduMap, LatLng startLatLng, LatLng endLatlng){
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
        PlanNode et = PlanNode.withLocation(endLatlng);
        search.walkingSearch((new WalkingRoutePlanOption().from(st).to(et)));
    }

}
