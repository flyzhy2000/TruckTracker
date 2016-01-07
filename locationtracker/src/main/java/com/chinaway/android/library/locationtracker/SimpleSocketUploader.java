package com.chinaway.android.library.locationtracker;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.chinaway.android.library.locationtracker.data.Constants;
import com.chinaway.android.library.locationtracker.utils.PrefUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * 上传定位信息的线程类.
 */
public class SimpleSocketUploader extends Thread {
    private static final String TAG = "SimpleSocketUploader";
    private String mGpsIp; // "123.56.0.101";
    private int mGpsPort; // 2903;
    private static final int TIMEOUT = 30000;
    private Context mContext;

    /**
     * 构造函数.
     *
     * @param ctx  上下文
     * @param ip   目标地址ip
     * @param port 目标地址端口
     */
    public SimpleSocketUploader(Context ctx, String ip, int port) {
        mContext = ctx;
        mGpsIp = ip;
        mGpsPort = port;
    }

    @Override
    public void run() {
        String data = PrefUtils.getStringPreferences(mContext, Constants.CONFIG,
                Constants.KEY_STR_UPLOAD_LOCATION_DATA, null);
        if (TextUtils.isEmpty(data)) {
            return;
        }
        if (TextUtils.isEmpty(mGpsIp)) {
            Log.e(TAG, "GPS ip is empty");
            return;
        }
        Socket socket = null;
        try {
            socket = new Socket();
            InetAddress addr = InetAddress.getByName(mGpsIp);
            socket.connect(new InetSocketAddress(addr, mGpsPort), TIMEOUT);
            OutputStream out = socket.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            PrefUtils.setStringPreferences(mContext, Constants.CONFIG,
                    Constants.KEY_STR_UPLOAD_LOCATION_DATA, null);
            Log.i("Test", "upload = " + data);
        } catch (Exception e) {
            Log.e(TAG, "got Exception when upload location", e);
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "got IOException when close socket", e);
                }
            }
            this.interrupt();
        }
    }


}
