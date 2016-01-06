package com.chinaway.android.library.locationtracker.sampler;

import android.location.Location;

import java.util.List;

/**
 * 最小二乘法抽样实现.
 */
public class LeastSquareSampler extends AbstractLocationSampler {

    private long mSamplingTimeInterval;
    private long mTime;
    private boolean mFirstFlag;
    private List<Location> mFittingList;

    public LeastSquareSampler(LocationSampling.LocationSamplingCallback callback, long timeInterval) {
        super(callback);
        mSamplingTimeInterval = timeInterval;
    }

    private void updateTime() {
        mTime = System.currentTimeMillis();
    }

    @Override
    public void onStart() {
        mFirstFlag = true;
    }

    @Override
    public void onNewLocation(Location location) {

    }

    @Override
    public void onEnd() {

    }
}
