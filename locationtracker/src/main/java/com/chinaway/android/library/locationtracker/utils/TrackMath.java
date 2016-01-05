package com.chinaway.android.library.locationtracker.utils;

import android.location.Location;

/**
 * Created by Huoyunren on 2016/1/5.
 * 根据坐标的变化计算运动的物理量.
 */
public class TrackMath {

    private static final double EARTH_RADIUS = 6378137.0;

    //Android源码中计算距离和方位角的方法
    /**
     * 根据两点坐标计算其距离，单位为米.
     * @param lat1 起点纬度
     * @param lon1 起点经度
     * @param lat2   终点纬度
     * @param lon2  终点经度
     * @param results  用于保存计算结果，results[0]为计算距离的结果, results[1]为计算方位角的结果
     */
    public static void computeDistanceAndBearing(double lat1, double lon1,
                                                 double lat2, double lon2, float[] results) {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)

        int MAXITERS = 20;
        // Convert lat/long to radians
        lat1 *= Math.PI / 180.0;
        lat2 *= Math.PI / 180.0;
        lon1 *= Math.PI / 180.0;
        lon2 *= Math.PI / 180.0;

        double a = 6378137.0; // WGS84 major axis
        double b = 6356752.3142; // WGS84 semi-major axis
        double f = (a - b) / a;
        double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);

        double L = lon2 - lon1;
        double A = 0.0;
        double U1 = Math.atan((1.0 - f) * Math.tan(lat1));
        double U2 = Math.atan((1.0 - f) * Math.tan(lat2));

        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;

        double sigma = 0.0;
        double deltaSigma = 0.0;
        double cosSqAlpha = 0.0;
        double cos2SM = 0.0;
        double cosSigma = 0.0;
        double sinSigma = 0.0;
        double cosLambda = 0.0;
        double sinLambda = 0.0;

        double lambda = L; // initial guess
        for (int iter = 0; iter < MAXITERS; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
            double sinSqSigma = t1 * t1 + t2 * t2; // (14)
            sinSigma = Math.sqrt(sinSqSigma);
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda; // (15)
            sigma = Math.atan2(sinSigma, cosSigma); // (16)
            double sinAlpha = (sinSigma == 0) ? 0.0 :
                    cosU1cosU2 * sinLambda / sinSigma; // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
            cos2SM = (cosSqAlpha == 0) ? 0.0 :
                    cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha; // (18)

            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq; // defn
            A = 1 + (uSquared / 16384.0) * // (3)
                    (4096.0 + uSquared *
                            (-768 + uSquared * (320.0 - 175.0 * uSquared)));
            double B = (uSquared / 1024.0) * // (4)
                    (256.0 + uSquared *
                            (-128.0 + uSquared * (74.0 - 47.0 * uSquared)));
            double C = (f / 16.0) *
                    cosSqAlpha *
                    (4.0 + f * (4.0 - 3.0 * cosSqAlpha)); // (10)
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = B * sinSigma * // (6)
                    (cos2SM + (B / 4.0) *
                            (cosSigma * (-1.0 + 2.0 * cos2SMSq) -
                                    (B / 6.0) * cos2SM *
                                            (-3.0 + 4.0 * sinSigma * sinSigma) *
                                            (-3.0 + 4.0 * cos2SMSq)));

            lambda = L +
                    (1.0 - C) * f * sinAlpha *
                            (sigma + C * sinSigma *
                                    (cos2SM + C * cosSigma *
                                            (-1.0 + 2.0 * cos2SM * cos2SM))); // (11)

            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0e-12) {
                break;
            }
        }

        float distance = (float) (b * A * (sigma - deltaSigma));
        distance = Math.round(distance * 10000) / 10000;
        results[0] = distance;
        if (results.length > 1) {
            float initialBearing = (float) Math.atan2(cosU2 * sinLambda,
                    cosU1 * sinU2 - sinU1 * cosU2 * cosLambda);
            initialBearing *= 180.0 / Math.PI;
            initialBearing = Math.round(initialBearing * 10000) / 10000;
            results[1] = initialBearing;
            if (results.length > 2) {
                float finalBearing = (float) Math.atan2(cosU1 * sinLambda,
                        -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda);
                finalBearing *= 180.0 / Math.PI;
                finalBearing = Math.round(finalBearing * 10000) / 10000;
                results[2] = finalBearing;
            }
        }
    }

    /**
     * 根据两点坐标计算其距离，单位为米.
     * @param startLatitude 起点纬度
     * @param startLongitude 起点经度
     * @param endLatitude   终点纬度
     * @param endLongitude  终点经度
     * @param results       用于保存计算结果，results[0]为计算距离的结果
     */
    public static void distanceBetween(double startLatitude, double startLongitude,
                                       double endLatitude, double endLongitude, float[] results) {
        if (results == null || results.length < 1) {
            throw new IllegalArgumentException("results is null or has length < 1");
        }
        computeDistanceAndBearing(startLatitude, startLongitude,
                endLatitude, endLongitude, results);
    }

    public static float distanceBetween(Location lastLocation, Location newLocation) {
        float distance = 0.0f;
        if (lastLocation != null && newLocation != null) {
            float[] results = new float[2];
            distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    newLocation.getLatitude(), newLocation.getLongitude(), results);
            distance = results[0];
        }
        return distance;
    }

    /**
     * 根据两点的经纬度计算两点的距离.
     *
     * @param lat_a a点纬度值
     * @param lng_a a点经度值
     * @param lat_b b点纬度值
     * @param lng_b b点经度值
     * @return 返回a, b两点的距离，单位为米
     */
    public static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 根据两个定位信息计算速度.
     *
     * @param lastLocation 上一次的定位信息
     * @param newLocation  新的定位信息
     * @return 返回速度值
     */
    public static float getSpeed(Location lastLocation, Location newLocation) {
        float speed = 0.0f;
        if (lastLocation != null && newLocation != null) {
            /*double distance = gps2m(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    newLocation.getLatitude(), newLocation.getLongitude());*/
            float[] results = new float[2];
            distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    newLocation.getLatitude(), newLocation.getLongitude(), results);
            double distance = results[0];
            long timeGap = newLocation.getTime() - lastLocation.getTime();
            if (timeGap > 0) {
                speed = (float) (distance / timeGap) * 3600;
            }
        }
        return speed;
    }
}
