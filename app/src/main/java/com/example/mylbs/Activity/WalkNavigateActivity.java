package com.example.mylbs.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;

public class WalkNavigateActivity extends Activity {

    //百度地图SDK
    private WalkNavigateHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = WalkNavigateHelper.getInstance();
        View view = helper.onCreate(this);
        if(view != null){
            setContentView(view);
        }
        helper.startWalkNavi(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.quit();
    }
}
