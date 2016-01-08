package com.chinaway.android.library.locationtracker.sampler;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * 最小二乘法抽样实现.
 */
public class LeastSquareSampler extends AbstractLocationSampler {

    private long mSamplingTimeInterval;
    private long mTime;
    private boolean mFirstFlag;
    private List<Location> mFittingList = new ArrayList<>();

    /**
     * 一次多项式.例子：y = a1*x + a0
     * a0和a1分别表示常量和系数.
     */
    static class PolynomialEquation {
        public double a0;
        public double a1;

        public double getA0() {
            return a0;
        }

        public double getA1() {
            return a1;
        }
    }

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
        updateTime();
    }

    @Override
    public void onNewLocation(Location location) {
        mFittingList.add(location);

        long timeElapse = System.currentTimeMillis() - mTime;
        if (timeElapse >= mSamplingTimeInterval && mFittingList.size() > 0) {
            PolynomialEquation equation = fittingLineToPoints(mFittingList);

            if (mFirstFlag) { // 初次采样，需提交首尾两个点
                SampleLocation start = new SampleLocation();
                start.longitude = getMinLongitude(mFittingList);
                start.latitude = equation.a1 * start.longitude + equation.a0;

                mFirstFlag = false;
                if (!Double.isNaN(start.longitude) && !Double.isNaN(start.latitude)) {
                    mCallback.onNewSample(start);
                } else {
                    return;
                    // no valid fitting location now, let us do the fitting later while having more sample locations.
                }
            }

            SampleLocation end = new SampleLocation();
            end.longitude = getMeanLongitude(mFittingList);
            end.latitude = equation.a1 * end.longitude + equation.a0;

            if (!Double.isNaN(end.longitude) && !Double.isNaN(end.latitude)) {
                mCallback.onNewSample(end);
            } else {
                return;
            }

            updateTime();
            mFittingList.clear();
        }
    }

    @Override
    public void onEnd() {
        if (mFittingList.size() > 0) {
            // 将剩余未拟合的点做处理后给出结束位置.
            PolynomialEquation equation = fittingLineToPoints(mFittingList);

            SampleLocation end = new SampleLocation();
            end.longitude = getMaxLongitude(mFittingList);
            end.latitude = equation.a1 * end.longitude + equation.a0;

            if (!Double.isNaN(end.longitude) && !Double.isNaN(end.latitude)) {
                mCallback.onNewSample(end);
            }

            mFittingList.clear();
        }
    }

    private PolynomialEquation fittingLineToPoints(List<Location> mapPoints) {
        PolynomialEquation lineEquation = new PolynomialEquation();
        if (mapPoints == null || mapPoints.size() < 1) {
            return null;
        }
        double sumX = 0;
        double sumY = 0;
        double sumX2 = 0;
        double sumXY = 0;
        for (Location point : mapPoints) {
            sumX += point.getLongitude();
            sumY += point.getLatitude();
            sumX2 += point.getLongitude() * point.getLongitude();
            sumXY += point.getLongitude() * point.getLatitude();
        }
        double meanX = sumX / mapPoints.size();
        double meanY = sumY / mapPoints.size();
        lineEquation.a1 = (sumXY - sumX * meanY) / (sumX2 - sumX * meanX);
        lineEquation.a0 = meanY - lineEquation.a1 * meanX;

        return lineEquation;
    }

    private double getMinLongitude(List<Location> locationList) {
        double minLongitude = Double.MAX_VALUE;
        for (Location location : locationList) {
            if (location.getLongitude() < minLongitude) {
                minLongitude = location.getLongitude();
            }
        }

        return minLongitude;
    }

    private double getMaxLongitude(List<Location> locationList) {
        double maxLongitude = 0;
        for (Location location : locationList) {
            if (location.getLongitude() > maxLongitude) {
                maxLongitude = location.getLongitude();
            }
        }

        return maxLongitude;
    }

    private double getMeanLongitude(List<Location> mapPoints) {
        if (mapPoints == null || mapPoints.size() < 1) {
            return Double.NaN;
        }
        double sumLongitude = 0;
        for (Location point : mapPoints) {
            sumLongitude += point.getLongitude();
        }

        return sumLongitude / mapPoints.size();
    }
}
