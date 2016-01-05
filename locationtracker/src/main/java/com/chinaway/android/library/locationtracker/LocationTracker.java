package com.chinaway.android.library.locationtracker;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

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

    LocationTracker(Context context, int mode, long requestInterval, LocationListener locationListener) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mMode = mode;
        mRequestInterval = requestInterval;
        mLocationListener = locationListener;
    }

    public void start() {
        switch( mMode ) {
            case GPS_MODE:
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        2000, 10, mLocationListener);
                break;
            case NETWORK_MODE:
            case AUTO_MODE:
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        2000, 10, mLocationListener);
        }
    }

    public void stop() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    public static class Builder {
        private Context mContext;
        private int mMode = AUTO_MODE;
        private long mInterval = 1000;
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
