package com.chinaway.android.library.locationtracker;

import android.content.Context;
import android.text.TextUtils;

import com.chinaway.android.library.locationtracker.data.Constants;
import com.chinaway.android.library.locationtracker.sampler.LeastSquareSampler;
import com.chinaway.android.library.locationtracker.sampler.LocationSampling;
import com.chinaway.android.library.locationtracker.sampler.RandomSampler;
import com.chinaway.android.library.locationtracker.sampler.SampleLocation;
import com.chinaway.android.library.locationtracker.utils.PrefUtils;
import com.chinaway.android.library.locationtracker.utils.TimeUtils;
import com.chinaway.android.library.locationtracker.utils.TrackMath;

/**
 * Created by Huoyunren on 2016/1/11.
 */
public class DefaultLocationNotifier {

    public static final int RANDOM_MODE = 1;
    public static final int LEAST_SQUARE_MODE = 2;

    private static final int DATA_LENGTH = 512;

    private Context mContext;
    private int mMode = LEAST_SQUARE_MODE;
    private long mInterval = 10000;

    private LocationTracker mTracker;
    private String mCsCode;
    private String mDeviceId;
    private SampleLocation mLocation;

    public DefaultLocationNotifier(Context context, String csCode, String deviceId) {
        mContext = context;
        mCsCode = csCode;
        mDeviceId = deviceId;
        init();
    }

    public DefaultLocationNotifier(Context context, int mode, long notifyInterval,
                                   String csCode, String deviceId) {
        mContext = context;
        mMode = mode;
        mInterval = notifyInterval;
        mCsCode = csCode;
        mDeviceId = deviceId;
        init();
    }

    private void init() {
        mTracker = new LocationTracker.Builder(mContext).build();
    }

    public void startTrack() {
        switch (mMode) {
            case LEAST_SQUARE_MODE:
                mTracker.addSampler(new LeastSquareSampler(new LocationSampling.LocationSamplingCallback() {
                    @Override
                    public void onNewSample(SampleLocation location) {
                        if (!filtLocation(location)) {
                            saveUploadLocationData(location);
                            startUploadLocation();
                        }
                    }
                }, mInterval));
            case RANDOM_MODE:
                mTracker.addSampler(new RandomSampler(new LocationSampling.LocationSamplingCallback() {
                    @Override
                    public void onNewSample(SampleLocation location) {
                        if (!filtLocation(location)) {
                            saveUploadLocationData(location);
                            startUploadLocation();
                        }
                    }
                }, mInterval));
        }
    }

    public void stopTrack() {
        mTracker.stop();
    }

    private boolean filtLocation(SampleLocation location) {
        boolean isTooClose = isTooClose(mLocation, location);
        if (!isTooClose) {
            mLocation = location;
        }
        return isTooClose;
    }

    private boolean isTooClose(SampleLocation lastLocation, SampleLocation newLocation) {
        return (TrackMath.distanceBetween(lastLocation.latitude, lastLocation.longitude,
                                    newLocation.latitude, newLocation.longitude) < 10);
    }

    /**
     * 启动位置上传线程.
     */
    private void startUploadLocation() {
        new SimpleSocketUploader(mContext, "123.56.0.101", 2903).start();
    }

    /**
     * 保存要上报的位置信息.
     */
    private void saveUploadLocationData(SampleLocation location) {
        StringBuilder sb = new StringBuilder(DATA_LENGTH);

        if (TextUtils.isEmpty(mCsCode) || TextUtils.isEmpty(mDeviceId)) {
            return;
        }

        sb.append("~").append(mCsCode) // 厂商编码
                .append("&GPSDU&2|").append(mDeviceId).append("|")
                .append(TimeUtils.time(TimeUtils.formatLocationTime(System.currentTimeMillis())))// 时间
                .append("|").append(location.longitude)// 经度
                .append("|").append(location.latitude).append("|")// 纬度
                .append(String.format("%.1f", 0))// 速度
                .append("|").append("0")// 方向
                .append("|{}#");
        String data = PrefUtils.getStringPreferences(mContext, Constants.CONFIG,
                Constants.KEY_STR_UPLOAD_LOCATION_DATA, null);
        if (TextUtils.isEmpty(data)) {
            data = sb.toString();
        } else {
            data += sb.toString();
        }
        PrefUtils.setStringPreferences(mContext, Constants.CONFIG,
                Constants.KEY_STR_UPLOAD_LOCATION_DATA, data);
    }
}
