package com.chinaway.android.trucktracker;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Huoyunren on 2016/1/8.
 */
public class TruckTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SDKInitializer.initialize(getApplicationContext());
    }
}
