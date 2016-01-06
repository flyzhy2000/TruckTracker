package com.chinaway.android.library.locationtracker.sampler;

/**
 * 位置抽样算法的抽象类.
 */
public abstract class AbstractLocationSampler implements LocationSampling {

    protected LocationSampling.LocationSamplingCallback mCallback;

    protected AbstractLocationSampler(LocationSamplingCallback callback) {
        mCallback = callback;
    }

    @Override
    public void setLocationSamplingCallback(LocationSamplingCallback callback) {
        mCallback = callback;
    }
}
