package com.chinaway.android.library.locationtracker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

/**
 * 检测网络是不是可用.
 */
public class NetWorkDetectionUtils {
    private static InetAddress addr;
    public static final int TYPE_UNKNOWN = -1;
    public static final String NETWORK_TYPE_UNKNOWN = "network_type_unknown";

    public static boolean checkNetworkAvailable(Context context) {
        NetworkInfo info = null;
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            info = connManager.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else {
                return info.isConnected();
            }
        } else {
            return false;
        }
    }

    /**
     * 判断是否有网.
     *
     * @param context
     * @return boolean
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.getTypeName();
            } else {
                return NETWORK_TYPE_UNKNOWN;
            }
        } else {
            return NETWORK_TYPE_UNKNOWN;
        }
    }


    /**
     * 手机连接的网络类型
     */
    public static int getNetWork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.getType();
            } else {
                return TYPE_UNKNOWN;
            }
        } else {
            return TYPE_UNKNOWN;
        }

    }

    /**
     * 网络类型是2g还是3g
     */
    public static int getMobileType(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectManager.getActiveNetworkInfo().getSubtype();
    }


}
