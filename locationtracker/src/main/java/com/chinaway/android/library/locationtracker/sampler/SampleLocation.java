package com.chinaway.android.library.locationtracker.sampler;

import java.util.Calendar;

/**
 * Created by Huoyunren on 2016/1/7.
 */
public class SampleLocation {

    public SampleLocation() { }

    public SampleLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return Calendar.getInstance().getTime().toString() + " | " + longitude + ", " + latitude + "\n";
    }

    double longitude;
    double latitude;
}
