package com.chinaway.android.library.locationtracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.chinaway.android.library.locationtracker.sampler.LocationSampling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huoyunren on 2016/1/5.
 */
public class LocationTracker {

    public static final int GPS_MODE = 0;
    public static final int NETWORK_MODE = 1;
    public static final int AUTO_MODE = 2;

    private LocationManager mLocationManager;
    private int mMode;
    private long mRequestInterval;
    private LocationListener mLocationListener;

    private List<LocationSampling> mSamplers = new ArrayList<>();

    LocationTracker(Context context, int mode, long requestInterval, LocationListener locationListener) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mMode = mode;
        mRequestInterval = requestInterval;

        if (locationListener != null) {
            mLocationListener = locationListener;
        } else {
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    for (LocationSampling sampler : mSamplers) {
                        sampler.onNewLocation(location);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }
    }

    public void start() {
        for (LocationSampling sampler : mSamplers) {
            sampler.onStart();
        }

        switch( mMode ) {
            case GPS_MODE:
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        mRequestInterval, 0, mLocationListener);
                break;
            case NETWORK_MODE:
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        mRequestInterval, 0, mLocationListener);
                break;
            case AUTO_MODE:
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        mRequestInterval, 0, mLocationListener);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        mRequestInterval, 0, mLocationListener);
                break;
        }

    }

    public void stop() {
        mLocationManager.removeUpdates(mLocationListener);

        for (LocationSampling sampler : mSamplers) {
            sampler.onEnd();
        }
    }

    public void addSampler(LocationSampling sampler) {
        mSamplers.add(sampler);
    }

    public static class Builder {
        private Context mContext;
        private int mMode = AUTO_MODE;
        private long mInterval = 2000;
        private LocationListener mLocationListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMode(int mode) {
            mMode = mode;
            return this;
        }

        public Builder setRequestInterval(long millisecond) {
            mInterval = millisecond;
            return this;
        }

        public Builder setLocationListener(LocationListener listener) {
            mLocationListener = listener;
            return this;
        }

        public LocationTracker build() {
            final LocationTracker locationUpdate = new LocationTracker(mContext, mMode, mInterval, mLocationListener);

            return locationUpdate;
        }

    }
}
