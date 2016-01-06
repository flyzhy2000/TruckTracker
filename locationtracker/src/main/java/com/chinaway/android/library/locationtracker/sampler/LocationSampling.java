package com.chinaway.android.library.locationtracker.sampler;

import android.location.Location;

import java.util.List;

/**
 * 对坐标点进行抽样过滤的类接口定义.
 */
public interface LocationSampling {

    public static interface LocationSamplingCallback {
        public void onNewSample(Location location);
    }

    public void onStart();
    public void onNewLocation(Location location);
    public void onEnd();
    public void setLocationSamplingCallback(LocationSamplingCallback callback);
}
