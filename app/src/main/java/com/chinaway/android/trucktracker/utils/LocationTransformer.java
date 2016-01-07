package com.chinaway.android.trucktracker.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Huoyunren on 2016/1/5.
 * 坐标系转换类.
 */
public class LocationTransformer {

    public static final String LONGITUDE = "lon";
    public static final String LATITUDE = "lat";
    private static final double pi = 3.14159265358979324D;// 圆周率
    private static final double a = 6378245.0D;// WGS 长轴半径
    private static final double ee = 0.00669342162296594323D;// WGS 偏心率的平方

    /**
     * 中国坐标内
     *
     * @param lat
     * @param lon
     * @return
     */
    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    // 84->gcj02
    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    public static Map<String, Double> transform(double lon, double lat) {
        HashMap<String, Double> localHashMap = new HashMap<>();
        if (outOfChina(lat, lon)) {
            localHashMap.put(LONGITUDE, lon);
            localHashMap.put(LATITUDE, lat);
            return localHashMap;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        localHashMap.put(LONGITUDE, mgLon);
        localHashMap.put(LATITUDE, mgLat);
        return localHashMap;
    }

    // gcj02-84
    public static Map<String, Double> gcj2wgs(double lon, double lat) {
        Map<String, Double> localHashMap = new HashMap<>();
        double longitude = lon - (transform(lon, lat).get(LONGITUDE) - lon);
        double latitude = (lat - ((transform(lon, lat)).get(LATITUDE) - lat));
        localHashMap.put(LONGITUDE, longitude);
        localHashMap.put(LATITUDE, latitude);
        return localHashMap;
    }


    /**
     * 地球坐标转换为火星坐标
     * World Geodetic System ==> Mars Geodetic System
     *
     * @param wgLat  地球坐标
     * @param wgLon
     *
     * mglat,mglon 火星坐标
     */
    public static void transform2Mars(double wgLat, double wgLon, double[] mars)
    {
        if (outOfChina(wgLat, wgLon))
        {
            mars[0]  = wgLat;
            mars[1] = wgLon;
            return ;
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        mars[0] = wgLat + dLat;
        mars[1] = wgLon + dLon;

    }

    //地球坐标转百度坐标
    public static void transform2bd(double wgLat, double wgLon, double[] bdResult) {
        double[] mars = new double[2];
        transform2Mars(wgLat, wgLon, mars);
        bd_encrypt(mars[0], mars[1], bdResult);
    }

//    public static List<LatLng> transform2bdList(List<LatLng> list) {
//        List<LatLng> dataList = new ArrayList<>();
//        if (list == null || list.size() < 1) {
//            return list;
//        }
//        int size = list.size();
//        for (int i = 0; i < size; i++) {
//            double[] results = new double[2];
//            transform2bd(list.get(i).latitude, list.get(i).longitude, results);
//            LatLng temp = new LatLng(results[0], results[1]);
//            dataList.add(temp);
//        }
//        return dataList;
//    }

    public static void bd_encrypt(double gg_lat, double gg_lon, double[] bdResult) {
        final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        bdResult[0] = z * Math.sin(theta) + 0.006;   //bd_lat
        bdResult[1] = z * Math.cos(theta) + 0.0065;  //bd_lon
    }

    public static void bd_decrypt(double bd_lat, double bd_lon, double[] ggResult) {
        final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        ggResult[0] = z * Math.sin(theta);   //gg_lat
        ggResult[1] = z * Math.cos(theta);  //  gg_lon
    }
}
