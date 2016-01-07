package com.chinaway.android.library.locationtracker.sampler;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huoyunren on 2016/1/6.
 */
public class RandomSampler extends AbstractLocationSampler {

    private long mSamplingTimeInterval;
    private long mTime;
    private boolean mFirstFlag;
    private Location mLocation;

    public RandomSampler(LocationSampling.LocationSamplingCallback callback, long timeInterval) {
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
        if (mFirstFlag) {
            mFirstFlag = false;
            // sample the start location immediately
            mCallback.onNewSample(new SampleLocation(location.getLongitude(), location.getLatitude()));

            updateTime();
            return;
        } else {
            long timeElapse = System.currentTimeMillis() - mTime;
            if (timeElapse >= mSamplingTimeInterval) {
                mCallback.onNewSample(new SampleLocation(location.getLongitude(), location.getLatitude()));
                mLocation = null;
                updateTime();
            } else {
                mLocation = location;  // save the last un-sampled location
            }
        }
    }

    @Override
    public void onEnd() {
        if (mLocation != null) {
            mCallback.onNewSample(new SampleLocation(mLocation.getLongitude(), mLocation.getLatitude()));
        }
    }
}
