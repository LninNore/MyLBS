package com.example.mylbs.other;

import com.baidu.location.BDLocation;

public interface LocationCallback {

    //成功定位后的回调函数，返回当前位置。
    void getLocation(BDLocation newLocation);

}
