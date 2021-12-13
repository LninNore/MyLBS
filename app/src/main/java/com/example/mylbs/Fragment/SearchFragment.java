package com.example.mylbs.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.mylbs.R;
import com.example.mylbs.Service.LocationService;
import com.example.mylbs.other.LocationCallback;
import com.example.mylbs.other.POISearchRecyclerAdapter;

import java.util.List;

public class SearchFragment extends Fragment implements View.OnClickListener, TextWatcher, LocationCallback {

    //暂存页面
    private View view;
    private EditText keyword;
    private Button doSearch;

    private RecyclerView searchResult;
    private POISearchRecyclerAdapter adapter;
    private List<PoiInfo> infoList;

    //当前位置
    private LatLng currentLatLng;

    //百度地图SDK
    private PoiSearch poiSearch;
    private OnGetPoiSearchResultListener myListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            infoList = poiResult.getAllPoi();
            if (infoList == null || infoList.size() <= 0) {
                Toast.makeText(getActivity(), "找不到任何结果", Toast.LENGTH_SHORT).show();
                return;
            }
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
            searchResult.setLayoutManager(manager);
            adapter = new POISearchRecyclerAdapter(infoList);
            searchResult.setAdapter(adapter);
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment, container, false);
        initViews();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        poiSearch.destroy();
        getActivity().unbindService(connection);
    }

    private void initViews() {
        searchResult = (RecyclerView) view.findViewById(R.id.search_result);
        doSearch = (Button) view.findViewById(R.id.do_search);
        doSearch.setOnClickListener(this);
        keyword = (EditText) view.findViewById(R.id.key_word);
        keyword.addTextChangedListener(this);
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(myListener);

        Intent getLocation = new Intent(getActivity(), LocationService.class);
        getActivity().startService(getLocation);
        getActivity().bindService(getLocation, connection, Context.BIND_AUTO_CREATE);
    }

    LocationService.LocationBinder locationBinder;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationBinder = (LocationService.LocationBinder) service;
            locationBinder.setCallback(SearchFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationBinder.removeCallback(SearchFragment.this);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.do_search:
                poiSearch.searchNearby(new PoiNearbySearchOption()
                        .location(currentLatLng)
                        .radius(500)
                        .keyword(keyword.getText().toString())
                        .pageNum(0));
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!keyword.getText().toString().trim().equals("")) {
            doSearch.setVisibility(View.VISIBLE);
        } else {
            doSearch.setVisibility(View.GONE);
        }
    }

    @Override
    public void getLocation(BDLocation newLocation) {
        currentLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
    }
}
